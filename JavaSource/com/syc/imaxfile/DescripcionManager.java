package com.syc.imaxfile;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_descripcion;
import com.syc.utils.Utils;

public class DescripcionManager {

	private static final Logger log = Logger.getLogger(DescripcionManager.class);
	
	@SuppressWarnings("unchecked")
	public Descripcion[] selectDescripcion(String titulo_aplicacion ) {
		
		Descripcion[] desc = null;
		HibernateManager hm = new  HibernateManager();
		try {
			
			String squery ="SELECT COUNT(*) FROM imx_descripcion WHERE titulo_aplicacion = ?";
			Query q = hm.createQuery(squery);		
			q.setString(0, titulo_aplicacion);          			
			Long cantidad_campos = (Long)q.uniqueResult();
			
			if (cantidad_campos < 1)
				return null;
			
			desc = new Descripcion[Utils.getInteger(cantidad_campos)];
			
			squery = "FROM imx_descripcion WHERE titulo_aplicacion = ? ORDER BY posicion_campo";
			
			q = hm.createQuery(squery);		
			q.setString(0, titulo_aplicacion);          
			List<imx_descripcion> campos_descripcion = (List<imx_descripcion>)q.list();
			
			int i = 0;
			for(imx_descripcion campo_descripcion : campos_descripcion){			
				desc[i] = new Descripcion(
						campo_descripcion.getId().getTituloAplicacion(),
						campo_descripcion.getId().getNombreCampo(),
						campo_descripcion.getNombreColumna(),
						campo_descripcion.getPosicionCampo(),
						campo_descripcion.getNombreTipoDatos(),
						campo_descripcion.getIdTipoDatos(),
						campo_descripcion.getLongitudCampo(),
						campo_descripcion.getValorDefCampo(),
						campo_descripcion.getMascaraCampo(),
						campo_descripcion.getNombreIndice(),
						campo_descripcion.getIndiceTipo(), 
						Utils.getString(campo_descripcion.getMultivaluado()),
						Utils.getString(campo_descripcion.getRequerido()), 
						Utils.getString(campo_descripcion.getEditable()), 
						Utils.getString(campo_descripcion.getLista()));
				i++;
			}
		} catch (Exception se) {
			log.error(se, se);

		} finally {
			hm.close();
		}
		return desc;
	}
}
