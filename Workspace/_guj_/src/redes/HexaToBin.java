package redes;

import java.util.Scanner;

import javax.swing.JOptionPane;

public class HexaToBin {
		
	public String limpastring(String hexadecimal) {
		int i = 0;
		boolean reswhile = false;
		if (hexadecimal.length() != 17) {
			System.out.println("A string deve ter exatamente 17 caracteres sendo 12 hexadecimais e 5 pontos duplos :. Por exemplo: '00:19:B9:FB:E2:58'");
			String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
			limpastring(nexthexa);
		} else {
			while (i<hexadecimal.length()) {				
				if (hexadecimal.charAt(i) != '1' && hexadecimal.charAt(i) != '2' && 
					hexadecimal.charAt(i) != '3' && hexadecimal.charAt(i) != '4' &&
					hexadecimal.charAt(i) != '5' && hexadecimal.charAt(i) != '6' &&
					hexadecimal.charAt(i) != '7' && hexadecimal.charAt(i) != '8' &&
					hexadecimal.charAt(i) != '9' && hexadecimal.charAt(i) != '0' &&
					hexadecimal.charAt(i) != 'A' && hexadecimal.charAt(i) != 'a' &&
					hexadecimal.charAt(i) != 'B' && hexadecimal.charAt(i) != 'b' &&
					hexadecimal.charAt(i) != 'C' && hexadecimal.charAt(i) != 'c' &&
					hexadecimal.charAt(i) != 'D' && hexadecimal.charAt(i) != 'd' &&
					hexadecimal.charAt(i) != 'E' && hexadecimal.charAt(i) != 'e' &&
					hexadecimal.charAt(i) != 'F' && hexadecimal.charAt(i) != 'f' &&
					hexadecimal.charAt(i) != ':') {
					System.out.println("Voc� inseriu um caracter inv�lido.");
					System.out.println("Os �nicos caracteres v�lidos s�o: '1234567890AaBbCcDdEeFf:'");
					reswhile = false;
				} else {
					reswhile = true;
				}
			}
		}
		if (reswhile) {
				if (hexadecimal.charAt(0) == ':') {
					System.out.println("endere�o mac n�o pode come�ar com dois pontos.");
					String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
					limpastring(nexthexa);
				} else {
					if (hexadecimal.charAt(1) == ':') {
						System.out.println("N�o � permitido ter dois pontos na posi��o 01.");
						System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
						String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
						limpastring(nexthexa);
					} else {
						if (hexadecimal.charAt(2) != ':') {
							System.out.println("A posi��o 02 s� aceita dois pontos como resposta.");
							String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
							limpastring(nexthexa);
						} else {
							if (hexadecimal.charAt(3) == ':') {
								System.out.println("N�o � permitido ter dois pontos na posi��o 03.");
								System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
								String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
								limpastring(nexthexa);
							} else {
								if (hexadecimal.charAt(4) == ':') {
									System.out.println("N�o � permitido ter dois pontos na posi��o 04.");
									System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
										String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
									limpastring(nexthexa);
								} else {
									if (hexadecimal.charAt(5) != ':') {
										System.out.println("A posi��o 05 s� aceita dois pontos como resposta.");
										String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
										limpastring(nexthexa);
									} else {
										if (hexadecimal.charAt(6) == ':') {
											System.out.println("N�o � permitido ter dois pontos na posi��o 06.");
											System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
											String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
											limpastring(nexthexa);
										} else {
										if (hexadecimal.charAt(7) == ':') {
												System.out.println("N�o � permitido ter dois pontos na posi��o 07.");
												System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
												String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
												limpastring(nexthexa);
											} else {
												if (hexadecimal.charAt(8) != ':') {
													System.out.println("A posi��o 08 s� aceita dois pontos como resposta.");
													String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
													limpastring(nexthexa);
												} else {
													if (hexadecimal.charAt(9) == ':') {
														System.out.println("N�o � permitido ter dois pontos na posi��o 09.");
														System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
														String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
														limpastring(nexthexa);
													} else {
														if (hexadecimal.charAt(10) == ':') {
															System.out.println("N�o � permitido ter dois pontos na posi��o 10.");
															System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
															String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
															limpastring(nexthexa);
														} else {
															if (hexadecimal.charAt(11) != ':') {
																System.out.println("A posi��o 11 s� aceita dois pontos como resposta.");
																String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																limpastring(nexthexa);
															} else {
																if (hexadecimal.charAt(12) == ':') {
																	System.out.println("N�o � permitido ter dois pontos na posi��o 12.");
																	System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																	String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																	limpastring(nexthexa);
																} else {
																	if (hexadecimal.charAt(13) == ':') {
																		System.out.println("N�o � permitido ter dois pontos na posi��o 13.");
																		System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																		String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																		limpastring(nexthexa);
																	} else {
																		if (hexadecimal.charAt(14) != ':') {
																			System.out.println("A posi��o 14 s� aceita dois pontos como resposta.");
																			String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																			limpastring(nexthexa);
																		} else {
																			if (hexadecimal.charAt(15) == ':') {
																				System.out.println("N�o � permitido ter dois pontos na posi��o 15.");
																				System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																				String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																				limpastring(nexthexa);
																			} else {
																				if (hexadecimal.charAt(16) == ':') {
																					System.out.println("N�o � permitido ter dois pontos na posi��o 16.");
																					System.out.println("O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																					String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																					limpastring(nexthexa);
																				} else {
																					return hexadecimal;
																				}
																			}
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				String nexthexa = JOptionPane.showInputDialog(null,"Endere�o MAC inv�lido. O endere�o mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
				limpastring(nexthexa);
			}
		return hexadecimal;
	}
	
		
	public char[] converte(String hexadecimal) {
	    	char[] endfonte = new char[6*8];
	    	int i = 0, j = 0;
	        
	        String pedacos = hexadecimal.substring(0, 2);
	        
	        int decimal = Integer.parseInt(pedacos, 16);      
	        String binary = Integer.toBinaryString(decimal);
	        //System.out.println("hexa: "+pedacos+". binario: "+binary);

        	//System.out.println(i);
        	for (i= 0; i < binary.length(); i++) {
    	       	endfonte[j] = binary.charAt(i);
	        	j++;
	        }
        	//System.out.println(i);
	        pedacos = hexadecimal.substring(3,5);
	        decimal = Integer.parseInt(pedacos, 16);      
	        binary = Integer.toBinaryString(decimal);
	        //System.out.println("hexa: "+pedacos+". binario: "+binary);
	        for (i= 0; i < binary.length(); i++) {
		        endfonte[j] = binary.charAt(i);
	        	j++;
	        }

        	//System.out.println(i);
	        
	        pedacos = hexadecimal.substring(6,8);
	        decimal = Integer.parseInt(pedacos, 16);      
	        binary = Integer.toBinaryString(decimal);
	        //System.out.println("hexa: "+pedacos+". binario: "+binary);
	        for (i= 0; i < binary.length(); i++) {
		       	endfonte[j] = binary.charAt(i);
	        	j++;
	        }
        	//System.out.println(i);
	        pedacos = hexadecimal.substring(9,11);
	        decimal = Integer.parseInt(pedacos, 16);      
	        binary = Integer.toBinaryString(decimal);
	        //System.out.println("hexa: "+pedacos+". binario: "+binary);
	        for (i= 0; i < binary.length(); i++) {
		        endfonte[j] = binary.charAt(i);
	        	j++;
	        }
        	//System.out.println(i);
	        pedacos = hexadecimal.substring(12,14);
	        decimal = Integer.parseInt(pedacos, 16);      
	        binary = Integer.toBinaryString(decimal);
	        //System.out.println("hexa: "+pedacos+". binario: "+binary);
	        for (i= 0; i < binary.length(); i++) {
		       	endfonte[j] = binary.charAt(i);
	        	j++;
	        }
        	//System.out.println(i);
	        pedacos = hexadecimal.substring(15,17);
	        decimal = Integer.parseInt(pedacos, 16);      
	        binary = Integer.toBinaryString(decimal);
	        //System.out.println("hexa: "+pedacos+". binario: "+binary);
	        //System.out.print(" binary length: "+binary.length()+". binary chars: ");
	        
	        for (i= 0; i < binary.length(); i++) {
	        	endfonte[j] = binary.charAt(i);
		       // System.out.print(binary.charAt(i));
	        	j++;
	        }
			return endfonte;

	        /*
	        System.out.println("");
	        System.out.println("what");
	        for (i = 0; i<endfonte.length; i++) {
	        	System.out.print(endfonte[i]);
	        }
	        */
	    }
}
