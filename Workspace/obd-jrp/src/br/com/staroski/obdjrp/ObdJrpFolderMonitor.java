package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import br.com.staroski.obdjrp.http.Http;
import br.com.staroski.obdjrp.utils.Lock;

public final class ObdJrpFolderMonitor {

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

	public ObdJrpFolderMonitor() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				stop();
			}
		}, ObdJrpFolderMonitor.class.getSimpleName() + "_ShutdownHook"));
	}

	public void start() {
		if (!scanning) {
			scanning = true;
			scanThread = new Thread(new Runnable() {

				@Override
				public void run() {
					execute();
				}
			}, ObdJrpFolderMonitor.class.getSimpleName() + "_ScanThread");
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
		ObdJrpProperties props = ObdJrpProperties.get();
		String server = props.webServer();
		String folder = props.dataDir().getAbsolutePath();
		System.out.printf("trying to upload data%n  from: \"%s\"%n    to: %s%n", folder, server);
		begin = System.currentTimeMillis();
	}

	private void end() {
		long elapsed = System.currentTimeMillis() - begin;
		if (elapsed < FIVE_MINUTES) {
			long timeToWait = FIVE_MINUTES - elapsed;
			System.out.printf("retrying in %d seconds%n", (timeToWait / 1000));
			LOCK.lock(timeToWait);
		}
	}

	private void execute() {
		do {
			try {
				begin();
				Queue<File> files = getFiles();
				while (scanning) {
					if (files.isEmpty()) {
						System.out.println("no data to upload!");
						break;
					}
					File file = files.poll();
					if (upload(file)) {
						file.delete();
					}
				}
				end();
			} catch (Throwable error) {
				System.out.printf("%s:%s%n", //
						ObdJrpFolderMonitor.class.getSimpleName(), //
						error.getClass().getSimpleName(), //
						error.getMessage());
			}
		} while (scanning);
	}

	private Queue<File> getFiles() {
		Queue<File> files = new LinkedList<>();
		ObdJrpProperties props = ObdJrpProperties.get();
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
			String url = ObdJrpProperties.get().webServer() + "/exec?UploadData";
			boolean accepted = Http.sendPostRequest(url, file);
			System.out.printf("file %s by server%n", accepted ? "accepted" : "rejected");
			return accepted;
		} catch (Exception error) {
			System.out.printf("%s: %s%n", //
					error.getClass().getSimpleName(), //
					error.getMessage());
			return false;
		}
	}
}
