package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import br.com.staroski.obdjrp.http.Http;
import br.com.staroski.obdjrp.utils.Lock;
import br.com.staroski.obdjrp.utils.Print;

public final class DataMonitor {

	private static final int FIVE_MINUTES = 300000;

	private static final FileFilter OBD_ONLY = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.isFile() && file.getName().endsWith(".obd");
		}
	};

	private static final FileFilter DIRS_ONLY = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};

	private static final Comparator<File> LAST_MODIFIED = new Comparator<File>() {

		@Override
		public int compare(File o1, File o2) {
			return (int) (o1.lastModified() - o2.lastModified());
		}
	};

	private final Lock LOCK = new Lock();

	private long begin;
	private boolean scanning;
	private Thread scanThread;

	public DataMonitor() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				stop();
			}
		}, "FolderMonitor_ShutdownHook"));
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			scanThread = new Thread(new Runnable() {

				@Override
				public void run() {
					checkDataFolder();
				}
			}, "ObdJrp_FolderMonitor");
			scanThread.start();
		}
	}

	public void stop() {
		scanning = false;
		LOCK.unlock();
		if (scanThread != null && scanThread.isAlive()) {
			try {
				scanThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void begin() {
		String server = Config.get().webServer();
		String folder = Config.get().dataDir().getAbsolutePath();
		System.out.printf("trying to upload data%n  from: \"%s\"%n    to: %s%n", folder, server);
		begin = System.currentTimeMillis();
	}

	private void checkDataFolder() {
		do {
			begin();
			Queue<File> files = getFiles();
			if (files.isEmpty()) {
				System.out.println("no data to upload!");
				continue;
			}
			for (File file : files) {
				try {
					if (upload(file)) {
						file.delete();
					}
				} catch (Throwable error) {
					Print.message(error);
				}
				if (!scanning) {
					return;
				}
			}
			end();
		} while (scanning);
	}

	private void end() {
		long elapsed = System.currentTimeMillis() - begin;
		if (elapsed < FIVE_MINUTES) {
			long timeToWait = FIVE_MINUTES - elapsed;
			System.out.printf("retrying in %d seconds%n", (timeToWait / 1000));
			LOCK.lock(timeToWait);
		}
	}

	private Queue<File> getFiles() {
		Queue<File> files = new LinkedList<>();
		Config props = Config.get();
		File rootFolder = props.dataDir();
		File[] subFolders = rootFolder.listFiles(DIRS_ONLY);
		for (File folder : subFolders) {
			File[] fileArray = folder.listFiles(OBD_ONLY);
			Arrays.sort(fileArray, LAST_MODIFIED);
			files.addAll(Arrays.asList(fileArray));
		}
		return files;
	}

	private boolean upload(File file) {
		System.out.printf("uploading file \"%s\"%n", file.getAbsolutePath());
		try {
			String url = Config.get().webServer() + "/exec?UploadData";
			boolean accepted = Http.sendPostRequest(url, file);
			System.out.printf("file %s by server%n", accepted ? "accepted" : "rejected");
			return accepted;
		} catch (Exception error) {
			Print.message(error);
			return false;
		}
	}
}
