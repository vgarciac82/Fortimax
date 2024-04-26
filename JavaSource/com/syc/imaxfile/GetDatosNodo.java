package com.syc.imaxfile;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.managers.imx_aplicacion_manager;
import com.syc.fortimax.hibernate.managers.imx_documento_manager;
import com.syc.gavetas.GavetaManager;

public class GetDatosNodo {

private static final Logger log = Logger.getLogger(GetDatosNodo.class);
	
	private final static Pattern patronNodo = Pattern.compile("^([A-Z][A-Z0-9_]*)_(?:G(\\d+))?(?:C(\\d+))?(?:D(\\d+))?(?:P(\\d+))?$");
	private final static Pattern patronGaveta = Pattern.compile("^([A-Z][A-Z0-9_]*)$");
	private String titulo_aplicacion = null;
	private Integer gabinete = null;
	private Integer carpeta = null;
	private Integer documento = null;
	private Integer pagina = null;
	
	public GetDatosNodo(){
		super();
	}
	
	private Integer toInteger(String grupo) {
		if (grupo==null)
			return null;
		else
			return Integer.parseInt(grupo);
	}
	
	public GetDatosNodo(String nodo) {
		Matcher matcherNodo = patronNodo.matcher(nodo);
		if (matcherNodo.matches()) {
			setTitulo_aplicacion(matcherNodo.group(1));
			setGabinete(toInteger(matcherNodo.group(2)));
			setCarpeta(toInteger(matcherNodo.group(3)));
			setDocumento(toInteger(matcherNodo.group(4)));
			setPagina(toInteger(matcherNodo.group(5)));
		} else {
			Matcher matcherGaveta = patronGaveta.matcher(nodo);
			if (matcherGaveta.matches())
				setTitulo_aplicacion(nodo);
			else 
				log.error("Nodo incorrecto: '"+nodo+"'");
		}
	}
	
	public GetDatosNodo(String titulo_aplicacion, Integer gabinete, Integer carpeta, Integer documento, Integer pagina) {
		this(titulo_aplicacion, gabinete, carpeta, documento);
		setPagina(pagina);
	}
	
	public GetDatosNodo(String titulo_aplicacion, Integer gabinete, Integer carpeta, Integer documento) {
		this(titulo_aplicacion, gabinete, carpeta);
		setDocumento(documento);
	}
	
	public GetDatosNodo(String titulo_aplicacion, Integer gabinete, Integer carpeta) {
		this(titulo_aplicacion, gabinete);
		setCarpeta(carpeta);
	}
	
	public GetDatosNodo(String titulo_aplicacion, Integer gabinete) {
		setTitulo_aplicacion(titulo_aplicacion);
		setGabinete(gabinete);
	}
	
	public String getNodo(){
		StringBuilder sbNodo = new StringBuilder();
		if (titulo_aplicacion!=null)
			sbNodo.append(titulo_aplicacion);
		if (gabinete!=null||carpeta!=null||documento!=null||pagina!=null)
			sbNodo.append("_");
		if (gabinete!=null)
			sbNodo.append("G").append(gabinete);
		if (carpeta!=null)
			sbNodo.append("C").append(carpeta);
		if (documento!=null)
			sbNodo.append("D").append(documento);
		if (pagina!=null)
			sbNodo.append("P").append(pagina);
		
		String nodo = sbNodo.toString();
		
		if(patronNodo.matcher(nodo).matches())
			return nodo;
		else if(patronGaveta.matcher(nodo).matches())
			return nodo;
		else {
			log.error("'"+nodo+"' no es un nodo válido");
			return null;
		}
	}
	
	@Override
	public String toString() {
		return getNodo();
	}
	
	public boolean isGaveta(){
		return (titulo_aplicacion!=null&&gabinete==null&&carpeta==null&&documento==null&&pagina==null);
	}
	
	public boolean isExpediente(){
		return (titulo_aplicacion!=null&&gabinete!=null&&carpeta==null&&documento==null&&pagina==null);
	}
	
	public boolean isCarpeta(){
		return (titulo_aplicacion!=null&&gabinete!=null&&carpeta!=null&&documento==null&&pagina==null);
	}
	
	public boolean isDocumento(){
		return (titulo_aplicacion!=null&&gabinete!=null&&carpeta!=null&&documento!=null&&pagina==null);
	}
	
	public boolean isPagina(){
		return (titulo_aplicacion!=null&&gabinete!=null&&carpeta!=null&&documento!=null&&pagina!=null);
	}
	
	public String getName() throws EntityManagerException, CarpetaManagerException {
		Object object = getHibernateObject();
		if(object instanceof imx_documento)
			return ((imx_documento)object).getNombreDocumento();
		else if(object instanceof Carpeta)
			return ((Carpeta)object).getNombreCarpeta();
		return getNodo();
	}
	
	public Object getObject() throws CarpetaManagerException, Exception, DocumentoManagerException {
		if(isGaveta()) {
			return new GavetaManager().selectGaveta(titulo_aplicacion);
		} else if(isDocumento()) {
			return new DocumentoManager().selectDocumento(titulo_aplicacion, gabinete, carpeta, documento);
		} else if (isCarpeta()) {
			Carpeta c = new Carpeta(titulo_aplicacion, gabinete, carpeta);
			return new CarpetaManager().selectCarpeta(c);
		} else {
			HashMap<String, Object> expediente = new ExpedienteManager().selectExpediente(titulo_aplicacion, gabinete);
			return expediente;
		}
	}
	
	public Object getHibernateObject() throws EntityManagerException, CarpetaManagerException {
		//Solo devolver objetos de la forma imx_objeto en esta función, y devolver objetos completos en getObject();
		if(isGaveta()) {
			imx_aplicacion imx_aplicacion = imx_aplicacion_manager.select(titulo_aplicacion);
			return imx_aplicacion;
		} else if(isDocumento()) {
			imx_documento_manager idm = new imx_documento_manager();
			idm.select(titulo_aplicacion, gabinete, carpeta, documento);
			imx_documento imx_documento = idm.uniqueResult();
			return imx_documento;
		} else if (isCarpeta()) {
			Carpeta c = new Carpeta(titulo_aplicacion, gabinete, carpeta);
			return new CarpetaManager().selectCarpeta(c);
		} else if (isExpediente()){
			HashMap<String, Object> expediente = new ExpedienteManager().selectExpediente(titulo_aplicacion, gabinete);
			return expediente;
		} else {
			return null;
		}
	}
	
	public Integer getIdDocumento(){
		return documento;
	}
	
	public Integer getIdCarpeta(){
		return carpeta;
	}
	
	public Integer getGabinete(){
		return gabinete;
	}
	
	public String getGaveta(){
		return titulo_aplicacion;
	}

	public void setTitulo_aplicacion(String titulo_aplicacion) {
		if(titulo_aplicacion == null)
			this.titulo_aplicacion = null;
		else if(!titulo_aplicacion.isEmpty()){
			this.titulo_aplicacion = titulo_aplicacion;
		} else {
			log.error("El nombre de la gaveta no puede ser la cadena vacía.");
		}
	}

	public void setGabinete(Integer gabinete) {
		if(gabinete == null)
			this.gabinete = null;
		else if(gabinete > 0){
			this.gabinete = gabinete;
		} else {
			log.error("El id del gabinete debe ser mayor a 0.");
		}
	}

	public void setCarpeta(Integer carpeta) {
		if(carpeta == null)
			this.carpeta = null;
		else if(carpeta >= 0) {
			this.carpeta = carpeta;
		} else {
			log.error("El id de carpeta debe ser mayor o igual a 0.");
		}
	}

	public void setDocumento(Integer documento) {
		if(documento == null)
			this.documento = null;
		else if(documento >= 0){
			this.documento = documento;
		} else {
			log.error("El id de documento debe ser mayor a 0.");
		}
	}
	
	public void setPagina(Integer pagina) {
		if(pagina == null)
			this.pagina = null;
		else if(pagina > 0){
			this.pagina = pagina;
		} else {
			log.error("El id de pagina debe ser mayor a 0.");
		}
	}
	
	public void eliminarDocumento(){
		if(isDocumento()) {
			documento = null;
		} else {
			log.error("No es un nodo de documento.");
		}
	}
	
	public void eliminarCarpeta(){
		if(isCarpeta()) {
			carpeta = null;
		} else {
			log.error("No es un nodo de carpeta.");
		}
	}

	public Integer getPagina() {
		return pagina;
	}

	public void separaDatosGabinete() {
		// TODO Auto-generated method stub
		
	}

	public void separaDatosDocumento() {
		// TODO Auto-generated method stub
		
	}

	public void creaNodo() {
		// TODO Auto-generated method stub
		
	}

	public void separaDatosCarpeta() {
		// TODO Auto-generated method stub
		
	}

	public void separaDatosPagina() {
		// TODO Auto-generated method stub
		
	}
}
