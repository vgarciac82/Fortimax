package com.syc.fortimax.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;

import com.syc.fortimax.Historico.Historico;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.imaxfile.CarpetaManager;

public class EventListener implements PreInsertEventListener, PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

	private static final long serialVersionUID = 2086030716998666561L;
	private static Logger log = Logger.getLogger(EventListener.class);

	//TODO: Estos mecanismos son muy rudimentarios, si se seguira maneteniendo este proyecto será necesario migrar parte del esquema que se utiliza en Fortimax_MVC
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		boolean cancelInsert = false;
		log.trace("HibernateEvent - INSERT - "+event.getEntity().getClass().getName());
		try {
			if (event.getEntity() instanceof imx_documento)
				cancelInsert = !CarpetaManager.isValid((imx_documento)event.getEntity());
			else if (event.getEntity() instanceof imx_carpeta)
				cancelInsert = CarpetaManager.preInsert((imx_carpeta)event.getEntity());
		} catch (Exception e) {
			log.error(e,e);
		}
		return cancelInsert;
	}
	
	@Override
	public void onPostInsert(PostInsertEvent event) {
		log.trace("HibernateEvent - INSERT - "+event.getEntity().getClass().getName());
		try {
			if (event.getEntity() instanceof imx_documento)
				Historico.insertarVersion((imx_documento) event.getEntity());
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		log.trace("HibernateEvent - UPDATE - "+event.getEntity().getClass().getName());
		try {
			if (event.getEntity() instanceof imx_documento)
				Historico.insertarVersion((imx_documento) event.getEntity());
		} catch (Exception e) {
			log.error(e,e);
		}
	}

	@Override
	public void onPostDelete(PostDeleteEvent event) {
		log.trace("HibernateEvent - DELETE - "+event.getEntity().getClass().getName());		
		try {
			if (event.getEntity() instanceof imx_documento)
				Historico.insertarVersion((imx_documento) event.getEntity());
		} catch (Exception e) {
			log.error(e,e);
		}
	}

}
