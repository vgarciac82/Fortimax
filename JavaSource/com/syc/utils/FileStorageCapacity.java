package com.syc.utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

import com.syc.fortimax.hibernate.HibernateManager;

public class FileStorageCapacity {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(FileStorageCapacity.class);

	public boolean isNotMeetQuota(String nombre_usuario, double bytes) throws FileStorageCapacityException {
		boolean retVal = false;
		
		HibernateManager hm = new HibernateManager();
		try {
			SQLQuery sqlQuery = hm.createSQLQuery("SELECT bytes_autorizados, bytes_usados"
					+ " FROM imx_usuario_expediente"
					+ " WHERE nombre_usuario = '" + nombre_usuario + "'");
			sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			@SuppressWarnings("unchecked")
			List<HashMap<String,?>> list = (List<HashMap<String,?>>)sqlQuery.list();
			if(list.size()==0)
				throw new FileStorageCapacityException("No se encontró el usuario("+nombre_usuario+")");
			
			Map<String,?> map = new CaseInsensitiveMap(list.get(0));
			double bytesAutorizados = Utils.getDouble(map.get("bytes_autorizados"));
			double bytesUsadosUsr = Utils.getDouble(map.get("bytes_usados"));

			if ((bytesAutorizados>0) && (bytesUsadosUsr>bytesAutorizados)) {
				throw new FileStorageCapacityException("Se ha excedido el espacio autorizado del Usuario "+nombre_usuario);
			} 
			
			hm.executeSQLQuery("UPDATE imx_usuario_expediente" +
					  " SET bytes_usados = " + bytesUsadosUsr + 
					  " WHERE nombre_usuario = '" + nombre_usuario + "'");
			retVal=true;	
		} catch (FileStorageCapacityException e) {
			throw e;
		} catch (Exception e) {
			throw new FileStorageCapacityException(e.getMessage());
		} finally {
			hm.close();
		}	
		return retVal;
	}

	public boolean decrementQuota(String nombre_usuario, double bytes) throws FileStorageCapacityException {
		boolean retVal = false;
		
		HibernateManager hm = new HibernateManager();
		try {
			List<?> list = hm.createSQLQuery("SELECT bytes_usados"
						   + " FROM imx_usuario_expediente"
						   + " WHERE nombre_usuario = '" + nombre_usuario + "'").list();
			if(list.size()==0)
				throw new FileStorageCapacityException("No se encontró el usuario(" + nombre_usuario + ")");
			double bytes_usados = Utils.getDouble(list.get(0));
			double bytesUsados = (bytes_usados - bytes) < 0 ? 0 : bytes_usados - bytes;
			
			hm.executeSQLQuery("UPDATE imx_usuario_expediente" +
					  " SET bytes_usados = " + bytesUsados + 
					  " WHERE nombre_usuario = '" + nombre_usuario + "'");
			
			retVal=true;
		} catch (FileStorageCapacityException e) {
			throw e;
		} catch (Exception e) {
			throw new FileStorageCapacityException(e.getMessage());
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