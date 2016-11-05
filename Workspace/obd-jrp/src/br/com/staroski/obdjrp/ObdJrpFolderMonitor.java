package br.com.staroski.obdjrp;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import br.com.staroski.obdjrp.io.HttpPost;

public final class ObdJrpFolderMonitor {

	private static final int ONE_MINUTE = 60000;

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

	private boolean scanning;

	private Thread scanThread;

	private ObdJrpProperties properties;

	private long begin_scan;

	private final Object LOCK = new Object();

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
		synchronized (LOCK) {
			LOCK.notify();
		}
		if (scanThread != null && scanThread.isAlive()) {
			try {
				scanThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void begin_scan() {
		begin_scan = System.currentTimeMillis();
	}

	private void end_scan() {
		long elapsed = System.currentTimeMillis() - begin_scan;
		if (elapsed < ONE_MINUTE) {
			synchronized (LOCK) {
				try {
					LOCK.wait(ONE_MINUTE - elapsed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void execute() {
		while (scanning) {
			try {
				properties = ObdJrpProperties.get();
				begin_scan();
				{
					Queue<File> files = getFiles();
					while (scanning && !files.isEmpty()) {
						File file = files.poll();
						if (upload(file)) {
							file.delete();
						}
					}
				}
				end_scan();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private Queue<File> getFiles() {
		Queue<File> files = new LinkedList<>();
		File rootFolder = properties.getPackageDir();
		File[] subFolders = rootFolder.listFiles(DIRS_ONLY);
		for (File folder : subFolders) {
			File[] fileArray = folder.listFiles(OBD_ONLY);
			Arrays.sort(fileArray, LAST_MODIFIED);
			files.addAll(Arrays.asList(fileArray));
		}
		return files;
	}

	private boolean upload(File file) {
		System.out.printf("uploading file \"%s\"...", file.getAbsolutePath());
		String requestURL = properties.getWebServer() + "/send-data";
		try {
			HttpPost multipart = new HttpPost(requestURL);
			multipart.addFilePart("fileUpload", file);
			List<String> response = multipart.finish();
			boolean ok = response.size() == 1 && "OK".equals(response.get(0));
			System.out.println(ok ? " OK" : " ERROR");
			return ok;
		} catch (IOException e) {
			System.out.println(" ERROR: " + e.getMessage());
			return false;
		}
	}
}
