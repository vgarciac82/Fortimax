package com.syc.utils;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import com.syc.fortimax.hibernate.HibernateManager;

public class UsedStorageCapacity {

	private static final Logger log = Logger.getLogger(UsedStorageCapacity.class);

	public double bytesAutorizados;
	public double bytesUsadosUsr;
	
	public boolean getQuota(String nombre_usuario) throws Exception {
		boolean retVal = false;

		HibernateManager hm = new HibernateManager();
		try {
			SQLQuery sqlQuery = hm.createSQLQuery("SELECT bytes_autorizados, bytes_usados"
					+ " FROM imx_usuario_expediente"
					+ " WHERE nombre_usuario = ?");
			sqlQuery.setParameter(0, nombre_usuario);
			sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<Map<String,?>> list = (List<Map<String,?>>)sqlQuery.list();
			if(list.size()==0)
				throw new Exception("No se encontro el espacio Autorizado del Usuario: ("+ nombre_usuario + ")");
			
			CaseInsensitiveMap rs = new CaseInsensitiveMap(list.get(0));
			
			bytesAutorizados = Utils.getDouble(rs.get("bytes_autorizados"));
			bytesUsadosUsr = Utils.getDouble(rs.get("bytes_usados"));

			if ( ((bytesAutorizados > 0) && (bytesUsadosUsr < bytesAutorizados)) ) {
				log.info("El usuario :" + nombre_usuario + "  cuenta con espacio Autorizado ");				
				retVal = true;		
			} 		
		} finally {
			hm.close();
		}	
		return retVal;
	}

	public static String conversion(double value) {
		String[] units = { "bytes", "Kb", "Mb", "Gb", "Tb" };
		double bytes = value;
		int i;

		for (i = 0; i < units.length; i++) {
			if (value >= Math.pow(2, i * 10)
					&& (value < Math.pow(2, (i + 1) * 10))) {
				if (i > 0) {
					bytes = value / Math.pow(2, i * 10);
				}
				break;
			}
		}

		if (i == units.length)
			return "0 bytes";

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		return nf.format(bytes) + " " + units[i];
	}
}