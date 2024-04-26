package com.syc.imaxfile;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.syc.utils.FileStorageCapacity;

public class Carpeta implements  Serializable {

private static final Logger log = Logger.getLogger(Carpeta.class);

	private static final long serialVersionUID = 9086531727336401770L;
	private String titulo_aplicacion = null;
	private int id_gabinete = -1;
	private int id_carpeta = -1;
	private String nombre_carpeta = null;
	private String nombre_usuario = null;
	private String bandera_raiz = "N";
	private Date fh_creacion = new Date(java.util.Calendar.getInstance().getTime().getTime());
	private Date fh_modificacion = new Date(java.util.Calendar.getInstance().getTime().getTime());
	private int numero_accesos = 0;
	private int numero_carpetas = 0;
	private int numero_documentos = 0;
	private String descripcion = null;
	//private int prioridad = -1;

	private int id_carpeta_hija = -1;
	private String nombre_hija = null;
	private int id_carpeta_padre = -1;

	private double tamanoBytes = 0d;

	private String password = "-1";
	private boolean open = false;

	private String dav_path = "";

	private int prioridad;
	private int profundidad;
	private String nombre_elemento;
	private int posicion_elemento;
	private String nombre_estructura;

	public String getNombre_estructura() {
		return nombre_estructura;
	}

	public void setNombre_estructura(String nombre_estructura) {
		this.nombre_estructura = nombre_estructura;
	}

	public Carpeta(String titulo_aplicacion, int id_gabinete, int id_carpeta) {
		this.titulo_aplicacion = titulo_aplicacion;
		this.id_gabinete = id_gabinete;
		this.id_carpeta = id_carpeta;
	}

	public Carpeta(String titulo_aplicacion, int id_gabinete, int id_carpeta, String descripcion) {
		this(titulo_aplicacion, id_gabinete, id_carpeta);
		this.descripcion = descripcion;
	}

	public Carpeta(
		String titulo_aplicacion,
		int id_gabinete,
		int id_carpeta,
		String nombre_carpeta,
		String nombre_usuario,
		String bandera_raiz,
		Date fh_creacion,
		Date fh_modificacion,
		int numero_accesos,
		int numero_carpetas,
		int numero_documentos,
		String descripcion,
	//int prioridad,
	int id_carpeta_hija, String nombre_hija) {
		this(titulo_aplicacion, id_gabinete, id_carpeta, descripcion);
		this.nombre_carpeta = nombre_carpeta;
		this.nombre_usuario = nombre_usuario;
		this.bandera_raiz = bandera_raiz;
		this.fh_creacion = fh_creacion;
		this.fh_modificacion = fh_modificacion;
		this.numero_accesos = numero_accesos;
		this.numero_carpetas = numero_carpetas;
		this.numero_documentos = numero_documentos;
		//this.prioridad = prioridad;
		this.id_carpeta_hija = id_carpeta_hija;
		this.nombre_hija = nombre_hija;
	}
	
	public Carpeta( String titulo_aplicacion, int idGabinete, int idCarpeta, String nombre_estructura, int posicion_elemento, String nombre_elemento, int profundidad, int prioridad, String descripcion )
	{
		this( titulo_aplicacion, idGabinete, idCarpeta );
		this.nombre_estructura = nombre_estructura;
		this.setPosicion_elemento(posicion_elemento);
		this.setNombre_elemento(nombre_elemento);
		this.setProfundidad(profundidad);
		this.setPrioridad(prioridad);
		this.descripcion = descripcion;
		this.id_carpeta=idCarpeta;
		this.setIdCarpeta( id_carpeta );
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}
	
	public void setTituloAplicacion( String titulo_aplicacion) {
		this.titulo_aplicacion = titulo_aplicacion;
	}
	
	public String getNodo() {
		if(id_carpeta<0)
			id_carpeta=0;
		return titulo_aplicacion+"_G"+id_gabinete+"C"+id_carpeta;
	}
	
	public String getTreePath() {
		return getTreePathPadre()+getNodo()+":\""+getNombreCarpeta()+"\"/";
	}
	
	private String getTreePathPadre() {
		try {
			return getCarpetaPadre().getTreePath();
		} catch (Exception e) {
			return "/";
		}
	}
	
	public String getPath() {
		return getPathPadre()+getNombreCarpeta()+"/";
	}
	
	private String getPathPadre() {
		try {
			return getCarpetaPadre().getPath();
		} catch (Exception e) {
			return "/";
		}
	}

	public int getIdGabinete() {
		return id_gabinete;
	}

	public void setIdGabinete( int id_gabinete) {
		this.id_gabinete = id_gabinete;
	}
	
	public int getIdCarpeta() {
		return id_carpeta;
	}

	public void setIdCarpeta(int id_carpeta) {
		this.id_carpeta = id_carpeta;
	}

	public String getNombreCarpeta() {
		return nombre_carpeta;
	}

	public void setNombreCarpeta(String nombre_carpeta) {
		this.nombre_carpeta = nombre_carpeta;
	}

	public String getNombreUsuario() {
		return nombre_usuario;
	}

	public void setNombreUsuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}

	public String getBanderaRaiz() {
		return bandera_raiz;
	}

	public void setBanderaRaiz(String bandera_raiz) {
		this.bandera_raiz = bandera_raiz;
	}

	public Date getFechaCreacion() {
		return fh_creacion;
	}

	public String getFechaFormateadaCreacion() {
		return (new SimpleDateFormat("dd-MM-yyyy")).format(fh_creacion);
	}

	public String getHoraFormateadaCreacion() {
		return (new SimpleDateFormat("HH:mm:ss")).format(fh_creacion);
	}

	public String getFechaHoraFormateadaCreacion() {
		return (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(fh_creacion);
	}

	public void setFechaCreacion(Date fh_creacion) {
		this.fh_creacion = fh_creacion;
	}

	public Date getFechaModificacion() {
		return fh_modificacion;
	}

	public String getFechaFormateadaModificacion() {
		return (new SimpleDateFormat("dd-MM-yyyy")).format(fh_modificacion);
	}

	public String getHoraFormateadaModificacion() {
		return (new SimpleDateFormat("HH:mm:ss")).format(fh_modificacion);
	}

	public void setFechaModificacion(Date fh_modificacion) {
		this.fh_modificacion = fh_modificacion;
	}

	public int getNumeroAccesos() {
		return numero_accesos;
	}

	public void setNumeroAccesos(int numero_accesos) {
		this.numero_accesos = numero_accesos;
	}

	public int getNumeroCarpetas() {
		return numero_carpetas;
	}

	public void setNumeroCarpetas(int numero_carpetas) {
		this.numero_carpetas = numero_carpetas;
	}

	public int getNumeroDocumentos() {
		return numero_documentos;
	}

	public void setNumeroDocumentos(int numero_documentos) {
		this.numero_documentos = numero_documentos;
	}

	public String getDescripcion() {
		return (descripcion == null ? "" : descripcion);
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/*
		public int getPrioridad() {
			return prioridad;
		}
	
		public void setPrioridad(int prioridad) {
			this.prioridad = prioridad;
		}
	*/

	public int getIdCarpetaHija() {
		return id_carpeta_hija;
	}

	public void setIdCarpetaHija(int id_carpeta_hija) {
		this.id_carpeta_hija = id_carpeta_hija;
	}

	public String getNombreHija() {
		return nombre_hija;
	}

	public void setNombreHija(String nombre_hija) {
		this.nombre_hija = nombre_hija;
	}

	public int getIdCarpetaPadre() {
		return id_carpeta_padre;
	}
	
	public Carpeta getCarpetaPadre() throws CarpetaManagerException {
		Carpeta carpetaPadre = this;
		carpetaPadre.setIdCarpeta(getIdCarpetaPadre());
		return new CarpetaManager().selectCarpeta(carpetaPadre);
	}

	public void setIdCarpetaPadre(int id_carpeta_padre) {
		this.id_carpeta_padre = id_carpeta_padre;
	}

	public String getContenidoCarpeta() {
		String folder = (getNumeroCarpetas() == 1) ? " Carpeta " : " Carpetas ";
		String docto = (getNumeroDocumentos() == 1) ? " Documento" : " Documentos";
		return getNumeroCarpetas() + folder + getNumeroDocumentos() + docto;
	}

	public String getFechaCarpetaCreacion() {
		return getFechaFormateadaCreacion() + " a las " + getHoraFormateadaCreacion();
	}

	public double getTamanoBytes() {
		return tamanoBytes;
	}

	public String getTamanoBytesTexto() {
		return FileStorageCapacity.conversion(tamanoBytes);
	}

	public void setTamanoBytes(double tamanoBytes) {
		this.tamanoBytes = tamanoBytes;
	}

	public String getPassword() {
		return password;
	}

	public boolean isProtected() {
		boolean bReturn = !((password == null) || "-1".equals(password));
		return bReturn;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getDavPath() {
		return (dav_path.replaceAll("&#39;", "'"));
	}

	public void setDavPath(String dav_path) {
		this.dav_path = dav_path;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(int prioridad) {
		this.prioridad = prioridad;
	}

	public int getProfundidad() {
		return profundidad;
	}

	public void setProfundidad(int profundidad) {
		this.profundidad = profundidad;
	}

	public String getNombre_elemento() {
		return nombre_elemento;
	}

	public void setNombre_elemento(String nombre_elemento) {
		this.nombre_elemento = nombre_elemento;
	}

	public int getPosicion_elemento() {
		return posicion_elemento;
	}

	public void setPosicion_elemento(int posicion_elemento) {
		this.posicion_elemento = posicion_elemento;
	}
}
