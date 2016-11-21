package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import br.com.staroski.obdjrp.core.ScannerListener;

public final class ScannerWindow extends JFrame {

	private static final long serialVersionUID = 1;

	private final DataPanel dataPanel;

	public ScannerWindow() {
		super("OBD-JRP");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImages(loadIcons());
		Container container = getContentPane();
		dataPanel = new DataPanel();
		container.add(dataPanel, BorderLayout.CENTER);
		pack();
	}

	public ScannerListener getObdJrpListener() {
		return dataPanel;
	}

	private List<Image> loadIcons() {
		List<Image> images = new ArrayList<>();
		try {
			Class<?> clazz = getClass();
			images.add(ImageIO.read(clazz.getResourceAsStream("/obd-jrp_32x32.png")));
			images.add(ImageIO.read(clazz.getResourceAsStream("/obd-jrp_64x64.png")));
			images.add(ImageIO.read(clazz.getResourceAsStream("/obd-jrp_128x128.png")));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return images;
	}
}
