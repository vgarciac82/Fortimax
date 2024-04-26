package com.syc.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class Encripta {

	private static final Logger log = Logger.getLogger(Encripta.class);

	public static String code(String key) throws Exception {
		String sKey = "SYCIMAXFILE";
		String sPass = key;

		String[] a_arrCodigo = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
				"V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6",
				"7", "8", "9" };
		String[] a_arrBase = { "Q00", "A01", "Z02", "W03", "S04", "X05", "E06",
				"D07", "C08", "R09", "F10", "V11", "T12", "G13", "B14", "Y15",
				"H16", "N17", "U18", "J19", "M20", "I21", "K22", "O23", "L24",
				"P25", "Q26", "A27", "Z28", "W29", "S30", "X31", "E32", "D33",
				"C34", "R35" };

		String[][] a_arrMatriz = new String[36][36];
		StringBuffer sPassEncriptado = new StringBuffer("IMAXFL");

		// Crea las 2 dimensiones del arreglo
		int indice = 0;
		for (int i = 0; i < 36; i++) {
			for (int a = 0; a < 36; a++) {
				indice = a + i;
				if (indice > 35)
					indice -= 36;

				a_arrMatriz[i][a] = a_arrBase[indice];
			}
		}

		// Vemos si la longitud de la clave es mayor a la longitud de la llave
		if (sPass.length() > sKey.length())
			sPass = sPass.substring(0, sKey.length());

		int int_posPass = 0;
		int int_posKey = 0;
		for (int s = 1; s <= sPass.length(); s++) {
			// Buscamos el caracter clave, el no...
			for (int a = 0; a < a_arrCodigo.length; a++) {
				if (sPass.substring(s - 1, s).equals(a_arrCodigo[a])) {
					int_posPass = a;
					break;
				}
			}

			// Buscamos el caracter llave, el no...
			for (int a = 0; a < a_arrCodigo.length; a++) {
				if (sKey.substring(s - 1, s).equals(a_arrCodigo[a])) {
					int_posKey = a;
					break;
				}
			}

			// Concatenamos
			sPassEncriptado.append(a_arrMatriz[int_posKey][int_posPass]);
		}

		return (sPassEncriptado.toString());
	}

	public static String code32(String key)  {

		String sPass = key;
		try {
		// encriptamos pass con md5
		byte[] mbyte = sPass.getBytes();
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] md5digest = md5.digest(mbyte);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < md5digest.length; i++) {
			sb.append((Integer.toHexString((md5digest[i] & 0xFF) | 0x100))
					.substring(1, 3));
		}

		sPass = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			log.error(e,e);
		}
		return (sPass.toString());
	}
}
