package redes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Receptor implements Serializable {
		
	static Socket ss;
	
	String macorigem = "00:19:B9:FB:E2:58";
	String macdestino = "00:14:A2:EC:D1:42";

	public static String recebedados() {
		try {
			ObjectInputStream input = new ObjectInputStream(ss.getInputStream());
			QuadroEthernet in = (QuadroEthernet) input.readObject();
			
			String res = in.toString();
			System.out.println("Servidor: " + res);
			
			return res;
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Erro! Class not found exception!");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro nos i/o streams!");
		}
		return null;
	}

	public static void enviadados(String res) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(ss.getOutputStream());
			out.writeObject(res);
			
			System.out.println("Dados recebidos com sucesso.\n Resposta enviada com sucesso.");
			
			ss.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro! Pode ser de fechar o server quanto no outputstream");
		}
	}

	public static void main(String[] args) {
		while (true) {
			try {
				int porta = 23345;
				ServerSocket serv = new ServerSocket(porta);
				System.out.println("Aguardando conexões na porta " + porta);
				ss = serv.accept();
				System.out.println("Conexão efetuada com sucesso na porta " + porta);
				String res = "O quadro ethernet recebido pelo servidor foi: " + recebedados();
				enviadados(res);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Erro! Esta porta já está em uso!");
			}

		}
	}


}
