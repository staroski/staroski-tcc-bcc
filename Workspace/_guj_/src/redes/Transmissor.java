package redes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import redes.QuadroEthernet;

public class Transmissor implements Serializable {
	
	public static void main(String[] args) {
		System.out.println("Olá. Eu sou o transmissor.");
		
		String macorigem = "00:19:B9:FB:E2:58"; // mac do transmissor
		String macdestino = "00:14:A2:EC:D1:42"; // mac do receptor
		
		String strtamanho = JOptionPane.showInputDialog(null,"Digite o tamanho do pacote que você deseja enviar.");
		int tamanho = Integer.parseInt(strtamanho);
		while (tamanho < 0 || tamanho > 2000) {
			System.out.println("O tamanho deve ser um número de 1 a 2000.");					
			strtamanho = JOptionPane.showInputDialog(null,"Digite o tamanho do pacote que você deseja enviar.");
			tamanho = Integer.parseInt(strtamanho);
		}
		if (tamanho > 0 || tamanho < 2000) {
			if (tamanho < 1500) {
				QuadroEthernet q1 = new QuadroEthernet();
				QuadroEthernet q = q1.criaquadro(tamanho, macorigem, macdestino);
				enviaerecebe(q);
				System.out.println("Vejamos: " + q.toString());
			} else {
				int metade = tamanho/2;
				QuadroEthernet q1 = new QuadroEthernet();
				QuadroEthernet q3 = q1.criaquadro(metade, macorigem, macdestino);
				enviaerecebe(q3);
				QuadroEthernet q2 = new QuadroEthernet();
				QuadroEthernet q4 = q2.criaquadro(metade, macorigem, macdestino);
				enviaerecebe(q4);
			}
		}
	}
		
	public static String enviaerecebe(QuadroEthernet q) {
		Socket sock = null;
		
		try {
			/* conecta */
			int porta = 23345;
			System.out.println("Tentando se conectar ao servidor na porta " + porta);
			sock = new Socket("localhost", porta);
			System.out.println("Conexão efetuada com sucesso.");
			} catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(null, "Erro! Host desconhecido!");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Erro! Host já está em uso ou não está aberto!");
			}

		try {
			String res = q.toString();
			
			System.out.println("Informação enviada para o servidor com sucesso.");
			System.out.println("O quadro ethernet enviado para o servidor foi: " + res);
			
			/* recebe dados -> Servidor para cliente. */
			ObjectInputStream input = new ObjectInputStream(sock.getInputStream());
			String str = (String) input.readObject();
			System.out.println("A informação recebida do servidor é: " + str);
			
			return str;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erro nos i/o streams!");
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Erro! Classe não encontrada!");
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(null, "Erro! Conexão não foi realizada com sucesso!");
		}
		
		return null;
	}
}

