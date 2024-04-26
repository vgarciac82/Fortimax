package com.syc.fortimax.hibernate.managers;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;

public class imx_catalogo_tipo_documento_manager {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(imx_catalogo_tipo_documento_manager.class);

	//TO-DO: Muchos metodos redundantes, basarse en imx_tipos_documentos_index_manager.java para usar Criteria y arreglar esto.
	
	@SuppressWarnings("unchecked")
	public ArrayList<imx_catalogo_tipo_documento> getTiposDocumento() {
		Session sess = HibernateUtils.getSession();
		ArrayList<imx_catalogo_tipo_documento> tiposDocumento = new ArrayList<imx_catalogo_tipo_documento>();
		String hquery = "FROM imx_catalogo_tipo_documento";
		Query q = sess.createQuery(hquery);
		tiposDocumento = (ArrayList<imx_catalogo_tipo_documento>) q.list();
		return tiposDocumento;
	}
	
	public imx_catalogo_tipo_documento getTipoDocumento(int id) {
		Session sess = HibernateUtils.getSession();
		imx_catalogo_tipo_documento tipoDocumento = null;
		String hquery = "FROM imx_catalogo_tipo_documento WHERE id = :id";
		Query q = sess.createQuery(hquery);
		q.setInteger("id", id);
		tipoDocumento = (imx_catalogo_tipo_documento) q.uniqueResult();
		return tipoDocumento;
	}
	
	public imx_catalogo_tipo_documento getTipoDocumento(String nombre) {
		Session sess = HibernateUtils.getSession();
		imx_catalogo_tipo_documento tipoDocumento = null;
		String hquery = "FROM imx_catalogo_tipo_documento WHERE NOMBRE=:NOMBRE";
		Query q = sess.createQuery(hquery);
		q.setParameter("NOMBRE", nombre);
		tipoDocumento = (imx_catalogo_tipo_documento) q.uniqueResult();
		return tipoDocumento;
	}

	public void createTipoDocumento(imx_catalogo_tipo_documento tipoDocumento) {
		Session sess = HibernateUtils.getSession();
		Transaction transaction = sess.beginTransaction();
		try {	
			sess.save(tipoDocumento);
			transaction.commit();		
		} catch(HibernateException e) {
			transaction.rollback();
			throw e;
		}
	}
	
	public void updateTipoDocumento(imx_catalogo_tipo_documento tipoDocumento) {
		Session sess = HibernateUtils.getSession();
		Transaction transaction = sess.beginTransaction();
		try {	
			sess.update(tipoDocumento);
			transaction.commit();		
		} catch(HibernateException e) {
			transaction.rollback();
			throw e;
		}
	}
	
	public boolean deleteTipoDocumento(imx_catalogo_tipo_documento tipoDocumento) throws EntityManagerException {
		Session sess = HibernateUtils.getSession();
		Transaction transaction = sess.beginTransaction();
		boolean success = false;
		try {	
			sess.delete(tipoDocumento);
			transaction.commit();
			imx_tipos_documentos_index_manager indexManager = new imx_tipos_documentos_index_manager();
			indexManager.delete(tipoDocumento);
			success = true;
		} catch(HibernateException e) {
			transaction.rollback();
			throw e;
		}
		return success;
	}

}
