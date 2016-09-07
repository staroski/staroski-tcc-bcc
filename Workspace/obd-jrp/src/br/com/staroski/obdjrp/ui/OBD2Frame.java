package br.com.staroski.obdjrp.ui;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JFrame;

public class OBD2Frame extends JFrame {

	private static final long serialVersionUID = 1;

	private CardLayout cardLayout;

	public OBD2Frame() {
		super("OBD-JRP");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Container panel = getContentPane();
		cardLayout = new CardLayout();
		panel.setLayout(cardLayout);
		panel.add(new ConnectionPanel());
		pack();
	}
}
