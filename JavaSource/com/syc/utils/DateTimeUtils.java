package com.syc.utils;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

public class DateTimeUtils {

@SuppressWarnings("unused")
private static final Logger log = Logger.getLogger(DateTimeUtils.class);

	public static final int YEAR = 0;

	public static final int MONTH = 1;

	public static final int MONTH_NAME = 2;

	public static final int DAY = 3;

	public static final int HOUR = 4;

	public static final int MINUTE = 5;

	public static String getElementFromDate(String date, int element) {

		
		String value = "";
		switch (element) {
			case 0:
				
				value = date.substring(0, 4);
				break;
			case 1:
				
				value = date.substring(5, 7);
				break;
			case 2:
				
				value = getMonthName(Integer.parseInt(date.substring(5, 7)));
				break;
			case 3:
				
				value = date.substring(8, 10);
				break;
			case 4:
				
				value = date.substring(11, 13);
				break;
			case 5:
				
				value = date.substring(14, 16);
				break;
			default:
				
				value = "";
				break;

		}

		return value;
	}

	public static String getMonthName(int numberMonth) {
		String monthName = "";
		switch (numberMonth) {
			case 1:
				monthName = "Enero";
				break;
			case 2:
				monthName = "Febrero";
				break;
			case 3:
				monthName = "Marzo";
				break;
			case 4:
				monthName = "Abril";
				break;
			case 5:
				monthName = "Mayo";
				break;
			case 6:
				monthName = "Junio";
				break;
			case 7:
				monthName = "Julio";
				break;
			case 8:
				monthName = "Agosto";
				break;
			case 9:
				monthName = "Septiembre";
				break;
			case 10:
				monthName = "Octubre";
				break;
			case 11:
				monthName = "Noviembre";
				break;
			case 12:
				monthName = "Diciembre";
				break;

		}
		
		return monthName;
	}

	public static String transformFromSQLDateTime(java.sql.Date date, java.sql.Time time) {

		if (time != null && date != null) {

			return genDateExpiration(DateTimeUtils.transformFromSQLDate(date), DateTimeUtils.transformFromSQLTime(time));

		}

		return "";

	}

	public static String transformFromSQLTime(java.sql.Time time) {
		if (time != null) {
			
			return time.toString().substring(0, 5);

		}
		return "";
	}

	public static String transformFromSQLDate(java.sql.Date date) {

		if (date != null) {
			return date.toString().replace('-', '/');
		}

		return "";

	}

	public static String transformToMillis(String date) {

		if (date.length() > 0) {
			Calendar calendar = new GregorianCalendar();

			int year = Integer.parseInt(date.substring(0, 4));
			int month = Integer.parseInt(date.substring(5, 7)) - 1;
			int day = Integer.parseInt(date.substring(8, 10));
			int hour = Integer.parseInt(date.substring(11, 13));
			int minute = Integer.parseInt(date.substring(14, 16));

			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.MILLISECOND, 0);
			return "" + calendar.getTimeInMillis();
		} else
			return "NULL";

	}

	public static String genDateExpiration(String date, String hr) {

		date = date.replace('-', '/');
		String nwDate = date + ":" + (hr.length() > 5 ? hr.substring(0, 5) : hr);
		return nwDate;
	}

	public static String truncDate(String dateC, String delim) {
		if (dateC.length() > 0) {
			String y = dateC.substring(0, 4);
			String m = dateC.substring(5, 7);
			String d = dateC.substring(8, 10);

			return y + delim + m + delim + d;
		} else
			return "";
	}

	public static String genHourExpiration(int hrs) {
		String hrS, /*mnt, d,*/ mi;
		Calendar calendar = new GregorianCalendar();
		java.util.Date trialTime = new java.util.Date();
		calendar.setTime(trialTime);
		int min = calendar.get(Calendar.MINUTE);
		int carry = 0;

		if (60 - min < 30) {
			min = 00;
			carry = 1;
		} else {
			min = 30;
			carry = 0;
		}

		Calendar calendar2 = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
				.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY) + hrs + carry, min);
		if (calendar2.get(Calendar.HOUR_OF_DAY) < 10)
			hrS = "0" + calendar2.get(Calendar.HOUR_OF_DAY);
		else
			hrS = "" + calendar2.get(Calendar.HOUR_OF_DAY);
		if (calendar2.get(Calendar.MINUTE) < 10)
			mi = "0" + calendar2.get(Calendar.MINUTE);
		else
			mi = "" + calendar2.get(Calendar.MINUTE);
		String dte = hrS + ":" + mi;
		return dte;

	}

	public static String getLastDayOfYear() {

		Calendar calendar = new GregorianCalendar();
		java.util.Date trialTime = new java.util.Date();
		calendar.setTime(trialTime);

		int year;
		year = calendar.get(Calendar.YEAR);

		if ((calendar.get(Calendar.MONTH) + 1) == 12)
			year++;

		String dte = year + ",12,31";

		return dte;

	}

	public static String addHouresToDate(int hrs) {

		Calendar calendar = new GregorianCalendar();
		java.util.Date trialTime = new java.util.Date();
		calendar.setTime(trialTime);

		int min = calendar.get(Calendar.MINUTE);
		int carry = 0;

		if (60 - min < 30) {
			min = 00;
			carry = 1;
		} else {
			min = 30;
			carry = 0;
		}

		Calendar calendar2 = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
				.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY) + hrs + carry, min);

		String dte = calendar2.get(Calendar.YEAR) + "/" + formatedOut(calendar2.get(Calendar.MONTH) + 1) + "/"
				+ formatedOut(calendar2.get(Calendar.DAY_OF_MONTH)) + ":"
				+ formatedOut(calendar2.get(Calendar.HOUR_OF_DAY))

				+ ":" + formatedOut(calendar2.get(Calendar.MINUTE));

		return dte;
	}

	/**
	 * Devuelve la fecha del d�a actual en el formato yyyy/mm/dd regresando la
	 * cadena con el delimitador especificado, por ejemplo si <code>delim</code>
	 * es '-' regresa la cadena yyyy-mm-dd
	 * 
	 * @return La fecha del dia, separando los elementos por <code>
	 *delim</code>.
	 * @param delim
	 *            Delimitador de los elementos de la fecha.
	 */
	public static String getHoy(String delim) {

		Calendar calendar = new GregorianCalendar();
		java.util.Date trialTime = new java.util.Date();
		calendar.setTime(trialTime);
		String dia = "";
		String mes = "";
		String hoy = "";

		if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
			dia = "0" + calendar.get(Calendar.DAY_OF_MONTH);
		else
			dia = "" + calendar.get(Calendar.DAY_OF_MONTH);

		if (calendar.get(Calendar.MONTH) + 1 < 10)
			mes = "0" + (calendar.get(Calendar.MONTH) + 1);
		else
			mes = "" + (calendar.get(Calendar.MONTH) + 1);
		String anio = "" + calendar.get(Calendar.YEAR);

		hoy = anio + delim + mes + delim + dia;
		return hoy;

	}

	public static String formatedOut(int value) {
		if (value < 10)
			return "0" + value;
		else
			return "" + value;
	}
}
