package com.syc.fortimax.hibernate.managers;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_pagina_index;

public class imx_pagina_index_manager {

	private Session sesion;
	private Transaction tx;
	
	
	private void iniciaOperacion() throws HibernateException
	{
		
		sesion=HibernateUtils.getSessionFactory().openSession();
		
		tx=sesion.beginTransaction();
		
	}
	
	
	private void manejaExcepcion(HibernateException he) throws HibernateException
	{
		tx.rollback();
		throw new HibernateException("Ocurrio un error en la capa de acceso a datos" ,he);
		
	}
	
	
	public void guardaDatosIndex(imx_pagina_index DatosIndex)
	{
		
		try
		{
			iniciaOperacion();
			//id=(Long)sesion.save(DatosIndex);
			sesion.save(DatosIndex);
			tx.commit();
			
		}
		catch(HibernateException he)
		{
				manejaExcepcion(he);
				throw he;
				
		}finally
		{
			
			
			//sesion.close();
		}
		
		
	}
	
	public void actualizaDatosIndex(imx_pagina_index DatosIndex) throws HibernateException
	{
		
		try
		{
			iniciaOperacion();
			sesion.update(DatosIndex);
			tx.commit();
		}
		catch(HibernateException he)
		{
			manejaExcepcion(he);
			throw he;
			
		}finally
		{
			sesion.close();
		}
		
		
	}
	
	public void eliminarDatosIndex(imx_pagina_index DatosIndex) throws HibernateException
	{
		
		try
		{
			iniciaOperacion();
			sesion.delete(DatosIndex);
			tx.commit();
			
		}
		catch(HibernateException he)
		{
			manejaExcepcion(he);
			throw he;
			
		}finally
		{
			sesion.close();
		}
	}
	
	
	public imx_pagina_index obtenDatosIndex(long idIndex) throws HibernateException
	{
		imx_pagina_index IndexObtenido=null;
		
		try
		{
			iniciaOperacion();
			IndexObtenido=(imx_pagina_index) sesion.get(imx_pagina_index.class, idIndex );
		}
		finally
		{
			sesion.close();
		}
		

		return IndexObtenido;
		
	}
	
	public List<imx_pagina_index> obtenListaIndex() throws HibernateException
	{
		List<imx_pagina_index> listaIndex =null;
		
		try 
		{
			iniciaOperacion();
			listaIndex=sesion.createQuery("from imx_pagina_index").list();
		}
		finally
		{
			sesion.close();
			
		}
		
		return listaIndex;
		
	}
	

	
	
	
}
