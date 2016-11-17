package redes;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.CRC32;

public class QuadroEthernet implements Serializable {

	static byte[] preambulo = new byte[7*8]; 	// 7 bits
	static byte[] sd = new byte[8]; 	// 1 bit
	static char[] enddestino = new char[6*8]; // 2 a 6 bits
	static char[] endfonte = new char[6*8]; // 2 a 6 bits
	static byte[] length  = new byte[10]; // 2 bits
	static ArrayList<Integer> dados = new ArrayList<Integer>();
	
	static byte[] pad = new byte[46*8]; // 0 a 46 bits
	static String crc = "";
	
	static ArrayList<Integer> array = new ArrayList<Integer>();
	
	static String macorigem; // converte hexadecimal macorigem para binário endfonte
	static String macdestino; // converte hexadecimal macdestino para binário enddestino

	public byte[] startapreambulo() {
		for (int i = 0; i<7*8; i++){
			byte bit = generatebites();
			preambulo[i] = bit;
		}
		return preambulo;
	}

	public byte[] startasd() {
		for (int i = 0; i<7; i++){
			byte bit = generatebites();
			sd[i] = bit;
		}
		return sd;
	}
	
	public static char[] startaenddestino(String macdestino) {
		
		int i = 0, j = 0;
	    
	    String pedacos = macdestino.substring(0, 2);
	    
	    int decimal = Integer.parseInt(pedacos, 16);      
	    String binary = Integer.toBinaryString(decimal);
		for (i= 0; i < binary.length(); i++) {
	       	enddestino[j] = binary.charAt(i);
	    	j++;
	    }
	    pedacos = macdestino.substring(3,5);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	enddestino[j] = binary.charAt(i);
	    	j++;
	    }   
	    pedacos = macdestino.substring(6,8);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	enddestino[j] = binary.charAt(i);
	    	j++;
	    }
		pedacos = macdestino.substring(9,11);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	enddestino[j] = binary.charAt(i);
	    	j++;
	    }
		pedacos = macdestino.substring(12,14);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	enddestino[j] = binary.charAt(i);
	    	j++;
	    }
		pedacos = macdestino.substring(15,17);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    
	    for (i= 0; i < binary.length(); i++) {
	    	enddestino[j] = binary.charAt(i);
	    	j++;
	    }
		return enddestino;
	}	
	
	public static char[] startaendfonte(String macorigem) {
		int i = 0, j = 0;
	    
	    String pedacos = macorigem.substring(0, 2);
	    
	    int decimal = Integer.parseInt(pedacos, 16);      
	    String binary = Integer.toBinaryString(decimal);
		for (i= 0; i < binary.length(); i++) {
	       	endfonte[j] = binary.charAt(i);
	    	j++;
	    }
	    pedacos = macorigem.substring(3,5);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	endfonte[j] = binary.charAt(i);
	    	j++;
	    }   
	    pedacos = macorigem.substring(6,8);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	endfonte[j] = binary.charAt(i);
	    	j++;
	    }
		pedacos = macorigem.substring(9,11);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	endfonte[j] = binary.charAt(i);
	    	j++;
	    }
		pedacos = macorigem.substring(12,14);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    for (i= 0; i < binary.length(); i++) {
	    	endfonte[j] = binary.charAt(i);
	    	j++;
	    }
		pedacos = macorigem.substring(15,17);
	    decimal = Integer.parseInt(pedacos, 16);      
	    binary = Integer.toBinaryString(decimal);
	    
	    for (i= 0; i < binary.length(); i++) {
	    	endfonte[j] = binary.charAt(i);
	    	j++;
	    }
		return endfonte;
	}			
		//este starta o length.
	public byte[] startalength(int tamanho) {
		String strlength = Integer.toString(tamanho,2);
		System.out.println(strlength);
		byte bit;
		// length tem exatamente 2 bits, sempre
		for (int i = 0; i<strlength.length(); i++){
			bit = (byte) strlength.charAt(i);
			if (bit == 49) {
				bit = 1;
			} else {
				bit = 0;
			}
			length[i] = bit;
		}
		return length;
	}

	//este starta o pad
	public static byte[] startapad() {
		// pad tem de 0 a 46 bytes ou 368 bits
		int tamanho =  (int) ((Math.random() * 736) / 2);
		if (tamanho<0.5) {
			tamanho = 0;
		}
		if (tamanho > 368) {
			tamanho = 368;
		}
		
		for (int i = 0; i<tamanho; i++){
			byte bit = generatebites();
			pad[i] = bit;
		}
		return pad;
	}
				
	public static ArrayList<Integer> startadados(int tamanho) {
		for (int i = 0; i<tamanho; i++){
				byte bit = generatebites();
				dados.add((int) bit);
			}
		return dados;
	}
		
		
	public static byte generatebites() {
		int bit;
		Random gerador = new Random();
		bit = gerador.nextInt();
		bit = (bit*10)/2;
		byte bity;
		if (bit >= 2.4) {
			bity = 1;
		} else {
			bity = 0;
		}
		return bity;
	}
	
	public static String crc() {
		
		//Redes de Computadores: Volume 20 da Série Livros didáticos informática UFRGS
		//Por Alexandre da Silva Carissimi,Juergen Rochol,Lisandro Zambenedetti Granville
		// (...) "CRC-32, calculado considerando os endereços de destino e fonte, o campo de tamanho e área dos dados"
		
		byte [] bArray = null;
        try
        {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream objOstream = new ObjectOutputStream(baos);
                objOstream.writeObject(enddestino);
                objOstream.writeObject(endfonte);
                objOstream.writeObject(length);               
                objOstream.writeObject(dados);
                bArray = baos.toByteArray();
        }
        catch (Exception e)
        {
        	System.out.println("impossível converter arraylist de dados para byte[]");
        }

        CRC32 c = new CRC32();
		c.update(bArray);
		long lcrc = c.getValue();
		crc = Long.toBinaryString(lcrc);
		return crc;
		
	}
	
	public static ArrayList<Integer> arrayfiller(int tamanho4) {
		for (int i = 0; i<7*8;i++) {
			array.add((int) preambulo[i]);
		}
		for (int i = 0; i<7;i++) {
			array.add((int) sd[i]);
		}
		int tamanho = (enddestino.length) + 7;
		int j = 0;
		for (int i = 8; i<tamanho;i++) {
			byte res = (byte) enddestino[j];
			if (res == 49) {
				res = 1;
			} else {
				res = 0;
			}
			array.add((int) res);
			j++;
		}
		j = 0;
		int tamanho2 = (endfonte.length) + tamanho;
		for (int i = tamanho; i<tamanho2;i++) {
			byte res = (byte) endfonte[j];
			if (res == 49) {
				res = 1;
			} else {
				res = 0;
			}
			array.add((int) res);
			j++;
		}
		
		array.add((int) length[0]);
		array.add((int) length[1]);
		
		tamanho2 = tamanho2 + 3;
		int tamanho3 = tamanho2 + tamanho4;
		j = 0;
		byte bit;
		for (int i = tamanho2; i<tamanho3; i++) {
			if (dados.get(j) == 0) {
				bit = 0;
			} else {
				bit = 1;
			}
			array.add((int) bit);
			j++;
		}
		// padding começa aqui
		while (array.size() < 64) {
			array.add(0);
			tamanho3 = tamanho3+1;
		}

		String binary = crc();
		for (int i = 0; i < binary.length(); i++) {
			int hmm = (int) binary.charAt(i);
			if (hmm == 49) {
				hmm = 1;
			} else {
				hmm = 0;
			}
			crc = crc + hmm;
			array.add(hmm);
	    }

		System.out.println("tamanho dos dados: "+tamanho4);				
		System.out.println("tamanho final do quadro ethernet: "+tamanho3);		

		
		//System.out.println("");
		//System.out.print("Array resultante = ");
		//System.out.println(array.toString());
		//System.out.println("");
		//System.out.println("tamanho3: "+tamanho3);
		//System.out.println("arraysize: "+array.size());
		return array;
	}
	
	public static byte[] getPreambulo() {
		return preambulo;
	}

	public static void setPreambulo(byte[] preambulo) {
		QuadroEthernet.preambulo = preambulo;
	}

	public static byte[] getSd() {
		return sd;
	}

	public static void setSd(byte[] sd) {
		QuadroEthernet.sd = sd;
	}

	public static char[] getEnddestino() {
		return enddestino;
	}

	public static void setEnddestino(char[] enddestino) {
		QuadroEthernet.enddestino = enddestino;
	}

	public static char[] getEndfonte() {
		return endfonte;
	}

	public static void setEndfonte(char[] endfonte) {
		QuadroEthernet.endfonte = endfonte;
	}

	public static byte[] getLength() {
		return length;
	}

	public static void setLength(byte[] length) {
		QuadroEthernet.length = length;
	}

	public static ArrayList<Integer> getDados() {
		return dados;
	}

	public static void setDados(ArrayList<Integer> dados) {
		QuadroEthernet.dados = dados;
	}

	public static byte[] getPad() {
		return pad;
	}

	public static void setPad(byte[] pad) {
		QuadroEthernet.pad = pad;
	}

	public static ArrayList<Integer> getArray() {
		return array;
	}

	public static void setArray(ArrayList<Integer> array) {
		QuadroEthernet.array = array;
	}

	public String getMacorigem() {
		return macorigem;
	}

	public void setMacorigem(String macorigem) {
		this.macorigem = macorigem;
	}

	public String getMacdestino() {
		return macdestino;
	}

	public void setMacdestino(String macdestino) {
		this.macdestino = macdestino;
	}
	
	public QuadroEthernet criaquadro(int tamanho, String macorigem, String macdestino) {
		QuadroEthernet q = new QuadroEthernet();
		q.preambulo = startapreambulo();
		q.sd = startasd();;
		q.enddestino = startaenddestino(macdestino);;
		q.endfonte = startaendfonte(macorigem);
		q.length = startalength(tamanho);
		q.dados = startadados(tamanho);
		q.pad = startapad();
		q.crc = crc();
		q.array = arrayfiller(tamanho);
		q.macorigem = macorigem;
		q.macdestino = macdestino;
		return q;
	}

/*
	public void startatudo(int tamanho, String macorigem, String macdestino) {
		startapreambulo();
		startasd();
		startaenddestino(macdestino);
		startaendfonte(macorigem);
		startalength(tamanho);
		startapad();
		startadados(tamanho);
		crc();
		arrayfiller(tamanho);
	}
*/
	public String toString() {
		String strpreambulo = "";
		for (int i = 0; i<preambulo.length; i++) {
			strpreambulo = strpreambulo + preambulo[i];
		}
		String strsd = "";
		for (int i = 0; i<sd.length; i++) {
			strsd = strsd + sd[i];
		}
		String strenddestino = "";
		for (int i = 0; i<enddestino.length; i++) {
			strenddestino = strenddestino + enddestino[i];
		}
		String strendfonte = "";
		for (int i = 0; i<endfonte.length; i++) {
			strendfonte = strendfonte + endfonte[i];
		}
		String strlength = "";
		for (int i = 0; i<length.length; i++) {
			strlength = strlength + length[i];
		}
		String strdados = "";
		for (int i = 0; i<dados.size(); i++) {
			strdados = strdados + dados.get(i);
		}

		String strcrc = crc();

		return "QuadroEthernet [preambulo=" + strpreambulo + ", sd=" + strsd + ", enddestino=" + strenddestino +
				", endfonte=" + strendfonte + ", length=" + strlength + ", dados=" + strdados +", crc="+ strcrc+"]";
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

}
