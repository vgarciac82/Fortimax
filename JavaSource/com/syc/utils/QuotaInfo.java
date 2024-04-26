package com.syc.utils;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.log4j.Logger;

public class QuotaInfo {

private static final Logger log = Logger.getLogger(QuotaInfo.class);

	private UsedStorageCapacity UsdSpcCap = null;

	public boolean GetUsedStorageCapacity(String nombre_usuario)
			throws Exception {

		boolean Resp = false;

		try {
			UsdSpcCap = new UsedStorageCapacity();
			if (UsdSpcCap.getQuota(nombre_usuario)) {
				Resp = true;
			}

		} catch (Exception fsce) {	
			log.error("Error al obtener la cantidad de espacio usuado del usuario: "+ nombre_usuario, fsce);
		}
		return Resp;
	}

	public String GetQuotaTotal() throws Exception {
			log.trace("Cuota Total : " + UsdSpcCap.bytesAutorizados);
			return UsedStorageCapacity.conversion(UsdSpcCap.bytesAutorizados);
	}

	public String GetUsedQuota() throws Exception {
		log.trace("Cuota usada de usuario " + UsdSpcCap.bytesUsadosUsr);

		return UsedStorageCapacity.conversion(UsdSpcCap.bytesUsadosUsr);
	}

	public String GetQuotaPercentFormat() {
		String sPercent = "";

		sPercent = FormatoNum(GetQuotaPercent()) + "%";

		return sPercent;
	}

	private double GetQuotaPercent() {
		return ((UsdSpcCap.bytesUsadosUsr / UsdSpcCap.bytesAutorizados) * 100);
	}

	public String GetColorPercentCuota() {
		String sColor = "";
		double dblPercent = GetQuotaPercent();

		if (dblPercent > 0 && dblPercent < 50)
			sColor = "#70bd7a";
		if (dblPercent > 51 && dblPercent < 89)
			sColor = "#fbc159";
		if (dblPercent > 90 && dblPercent < 100)
			sColor = "#fe0000";

		return sColor;
	}

	private String FormatoNum(double bytes) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(otherSymbols);
		String bytesFormateados = df.format(bytes);
		
		log.trace("Se ha formateado "+bytes+" como "+bytesFormateados);
		return bytesFormateados;
	}
}