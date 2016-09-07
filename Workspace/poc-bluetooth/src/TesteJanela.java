import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import br.com.staroski.obdjrp.bluetooth.Bluetooth;
import br.com.staroski.obdjrp.io.IO;
import br.com.staroski.obdjrp.obd2.ELM327;

public class TesteJanela extends JFrame {

	private static final long serialVersionUID = 1;

	private JTextField textFieldDevice;
	private JTextField textFieldService;
	private JTextField textFieldRpm;
	private JTextField textFieldSpeed;

	private JButton buttonConnect;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TesteJanela frame = new TesteJanela();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TesteJanela() {
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Teste OBD2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 340, 215);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblDevice = new JLabel("Device:");
		lblDevice.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDevice.setBounds(10, 11, 46, 14);
		contentPane.add(lblDevice);

		textFieldDevice = new JTextField();
		textFieldDevice.setText("F8E079DAE781");
		textFieldDevice.setBounds(66, 8, 248, 20);
		contentPane.add(textFieldDevice);
		textFieldDevice.setColumns(10);

		JLabel lblService = new JLabel("Service:");
		lblService.setHorizontalAlignment(SwingConstants.RIGHT);
		lblService.setBounds(10, 36, 46, 14);
		contentPane.add(lblService);

		textFieldService = new JTextField();
		textFieldService.setText("BLT");
		textFieldService.setBounds(66, 33, 248, 20);
		contentPane.add(textFieldService);
		textFieldService.setColumns(10);

		buttonConnect = new JButton("Connect");
		buttonConnect.setBounds(225, 76, 89, 23);
		contentPane.add(buttonConnect);

		JLabel lblRpm = new JLabel("RPM:");
		lblRpm.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRpm.setBounds(10, 122, 46, 14);
		contentPane.add(lblRpm);

		textFieldRpm = new JTextField();
		textFieldRpm.setHorizontalAlignment(SwingConstants.RIGHT);
		textFieldRpm.setBounds(66, 119, 248, 20);
		textFieldRpm.setEditable(false);
		contentPane.add(textFieldRpm);

		JLabel lblSpeed = new JLabel("Speed:");
		lblSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpeed.setBounds(10, 153, 46, 14);
		contentPane.add(lblSpeed);

		textFieldSpeed = new JTextField();
		textFieldSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		textFieldSpeed.setBounds(66, 150, 248, 20);
		textFieldSpeed.setEditable(false);
		contentPane.add(textFieldSpeed);

		registerListeners();
	}

	private void registerListeners() {
		buttonConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buttonConnect.setEnabled(false);
				connect();
			}
		});
	}

	private void connect() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String deviceAddress = textFieldDevice.getText();
					String serviceName = textFieldService.getText();
					IO connection = Bluetooth.connect(deviceAddress, serviceName);

					ELM327 elm327 = new ELM327(connection);

					System.out.println("configurando protocolo automatico"); // ISO 15765-4 CAN == 6
					String result = elm327.exec("AT SP 0");
					System.out.println(result);

					System.out.println("desligando retorno dos cabeçalhos");
					result = elm327.exec("AT H0");
					System.out.println(result);

					System.out.println("requisitando PIDs suportados [01-20]");
					result = elm327.exec("01 00");
					System.out.println(result);
					while (true) {

						System.out.println("requisitando rpm do veiculo");
						result = elm327.exec("01 0C");
						System.out.println(result);
						textFieldRpm.setText(getRPM(result));

						System.out.println("requisitando velocidade do veiculo");
						result = elm327.exec("01 0D");
						System.out.println(result);
						textFieldSpeed.setText(getSpeed(result));

						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(TesteJanela.this, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		});
		thread.start();
	}

	private static String getSpeed(String string) {
		String text = string;
		if (text.startsWith("41")) {
			text = text.substring(4).trim();
		}
		if (text.endsWith(">")) {
			text = text.substring(0, text.length() - 1).trim();
		}
		int speed = Integer.parseInt(text, 16);
		return String.valueOf(speed);
	}

	private static String getRPM(String string) {
		String text = string;
		if (text.startsWith("41")) {
			text = text.substring(4).trim();
		}
		if (text.endsWith(">")) {
			text = text.substring(0, text.length() - 1).trim();
		}
		int a = Integer.parseInt(text, 16);
		int rpm = a / 4;
		return String.valueOf(rpm);
	}
}
