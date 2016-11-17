package redes;

import java.util.Scanner;

import javax.swing.JOptionPane;

public class HexaToBin {
		
	public String limpastring(String hexadecimal) {
		int i = 0;
		boolean reswhile = false;
		if (hexadecimal.length() != 17) {
			System.out.println("A string deve ter exatamente 17 caracteres sendo 12 hexadecimais e 5 pontos duplos :. Por exemplo: '00:19:B9:FB:E2:58'");
			String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
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
					System.out.println("Você inseriu um caracter inválido.");
					System.out.println("Os únicos caracteres válidos são: '1234567890AaBbCcDdEeFf:'");
					reswhile = false;
				} else {
					reswhile = true;
				}
			}
		}
		if (reswhile) {
				if (hexadecimal.charAt(0) == ':') {
					System.out.println("endereço mac não pode começar com dois pontos.");
					String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
					limpastring(nexthexa);
				} else {
					if (hexadecimal.charAt(1) == ':') {
						System.out.println("Não é permitido ter dois pontos na posição 01.");
						System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
						String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
						limpastring(nexthexa);
					} else {
						if (hexadecimal.charAt(2) != ':') {
							System.out.println("A posição 02 só aceita dois pontos como resposta.");
							String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
							limpastring(nexthexa);
						} else {
							if (hexadecimal.charAt(3) == ':') {
								System.out.println("Não é permitido ter dois pontos na posição 03.");
								System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
								String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
								limpastring(nexthexa);
							} else {
								if (hexadecimal.charAt(4) == ':') {
									System.out.println("Não é permitido ter dois pontos na posição 04.");
									System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
										String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
									limpastring(nexthexa);
								} else {
									if (hexadecimal.charAt(5) != ':') {
										System.out.println("A posição 05 só aceita dois pontos como resposta.");
										String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
										limpastring(nexthexa);
									} else {
										if (hexadecimal.charAt(6) == ':') {
											System.out.println("Não é permitido ter dois pontos na posição 06.");
											System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
											String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
											limpastring(nexthexa);
										} else {
										if (hexadecimal.charAt(7) == ':') {
												System.out.println("Não é permitido ter dois pontos na posição 07.");
												System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
												String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
												limpastring(nexthexa);
											} else {
												if (hexadecimal.charAt(8) != ':') {
													System.out.println("A posição 08 só aceita dois pontos como resposta.");
													String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
													limpastring(nexthexa);
												} else {
													if (hexadecimal.charAt(9) == ':') {
														System.out.println("Não é permitido ter dois pontos na posição 09.");
														System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
														String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
														limpastring(nexthexa);
													} else {
														if (hexadecimal.charAt(10) == ':') {
															System.out.println("Não é permitido ter dois pontos na posição 10.");
															System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
															String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
															limpastring(nexthexa);
														} else {
															if (hexadecimal.charAt(11) != ':') {
																System.out.println("A posição 11 só aceita dois pontos como resposta.");
																String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																limpastring(nexthexa);
															} else {
																if (hexadecimal.charAt(12) == ':') {
																	System.out.println("Não é permitido ter dois pontos na posição 12.");
																	System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																	String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																	limpastring(nexthexa);
																} else {
																	if (hexadecimal.charAt(13) == ':') {
																		System.out.println("Não é permitido ter dois pontos na posição 13.");
																		System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																		String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																		limpastring(nexthexa);
																	} else {
																		if (hexadecimal.charAt(14) != ':') {
																			System.out.println("A posição 14 só aceita dois pontos como resposta.");
																			String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																			limpastring(nexthexa);
																		} else {
																			if (hexadecimal.charAt(15) == ':') {
																				System.out.println("Não é permitido ter dois pontos na posição 15.");
																				System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																				String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																				limpastring(nexthexa);
																			} else {
																				if (hexadecimal.charAt(16) == ':') {
																					System.out.println("Não é permitido ter dois pontos na posição 16.");
																					System.out.println("O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
																					String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
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
				String nexthexa = JOptionPane.showInputDialog(null,"Endereço MAC inválido. O endereço mac deve ser escrito dessa forma: '00:19:B9:FB:E2:58'");
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
