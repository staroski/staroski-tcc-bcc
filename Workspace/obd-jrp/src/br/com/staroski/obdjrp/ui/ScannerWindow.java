package br.com.staroski.obdjrp.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import br.com.staroski.obdjrp.ObdJrpListener;
import br.com.staroski.obdjrp.data.Package;
import br.com.staroski.obdjrp.data.Scan;
import br.com.staroski.obdjrp.elm.ELM327Error;

public final class ScannerWindow extends JFrame implements ObdJrpListener {

	private static final long serialVersionUID = 1;

	private final ListenerPanel listenerPanel;

	public ScannerWindow() {
		super("OBD-JRP");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImages(loadIcons());
		Container container = getContentPane();
		listenerPanel = new ListenerPanel();
		container.add(listenerPanel, BorderLayout.CENTER);
		pack();
	}

	@Override
	public void onError(ELM327Error error) {
		listenerPanel.onError(error);
	}

	@Override
	public void onFinishPackage(Package dataPackage) {
		listenerPanel.onFinishPackage(dataPackage);
	}

	@Override
	public void onScanned(Scan scannedData) {
		listenerPanel.onScanned(scannedData);
	}

	@Override
	public void onStartPackage(Package dataPackage) {
		listenerPanel.onStartPackage(dataPackage);
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
