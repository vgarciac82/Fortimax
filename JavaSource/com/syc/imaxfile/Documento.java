package com.syc.imaxfile;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.utils.FileStorageCapacity;

public class Documento implements Serializable {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Documento.class);
	private String anotaciones = null;
	private static final long serialVersionUID = -2692758515203282421L;
	private String titulo_aplicacion = null;
	private int id_gabinete = -1;
	private int id_carpeta_padre = -1;
	private int id_documento = -1;
	private String nombre_documento = null;
	private String nombre_usuario = null;
	private String usuario_modificacion = null;
	private int prioridad = -1;
	private int id_tipo_docto = -1;
	private String nombre_tipo_docto = null;
	private Date fh_creacion = new Date(System.currentTimeMillis());
	private Date fh_modificacion = new Date(System.currentTimeMillis());
	private int numero_accesos = 0;
	private int numero_paginas = 0;
	private String titulo = null;
	private String autor = null;
	private String materia = null;
	private String descripcion = null;
	
	private int clase_documento = -1;
	private String estado_documento = "V";
	private double tamano_bytes = 0d;

	private String compartir = "N";
	private String token = "";

	private String extension = "";

	private Pagina[] paginas = new Pagina[0];
	private String dav_path = "";
	private String dateExp = "";
	private String dateShared = "";
	private String houreExp = "";
	
	//BEPM integra OCR
	private String nombreAplicacion = "";
	private String nombreCarpeta = "";
	
	//Integrar permisos de Bajar a documento compartido
	private String ligaPermisoBajar = "N";

	public String getLigaPermisoBajar() {
		return ligaPermisoBajar;
	}

	public void setLigaPermisoBajar(String ligaPermisoBajar) {
		this.ligaPermisoBajar = ligaPermisoBajar;
	}

	public Documento(String titulo_aplicacion, int id_gabinete, int id_carpeta_padre) {
		this.titulo_aplicacion = titulo_aplicacion;
		this.id_gabinete = id_gabinete;
		this.id_carpeta_padre = id_carpeta_padre;
	}

	public Documento(String titulo_aplicacion, int id_gabinete, int id_carpeta_padre, int id_documento) {
		this.titulo_aplicacion = titulo_aplicacion;
		this.id_gabinete = id_gabinete;
		this.id_carpeta_padre = id_carpeta_padre;
		this.id_documento = id_documento;
	}
	
	public Documento( String titulo_aplicacion, int id_gabinete, 
			int id_Carpeta_padre, int id_documento, String nombre_documento )
	{
		//Funcion utilizada por Ifimax, portada desde ADDocumento.
		//super( titulo_aplicacion, id_gabinete, id_Carpeta_padre );
		this.titulo_aplicacion=titulo_aplicacion;
		this.id_gabinete=id_gabinete;
		this.id_carpeta_padre=id_Carpeta_padre;
		this.id_documento=id_documento;
		this.nombre_documento=nombre_documento;
	}
	
	public Documento( Documento d )
	   {	    
		   this(d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento() );
		   this.setIdTipoDocto(d.getIdTipoDocto() );
		   this.setNombreDocumento( d.getNombreDocumento() );
		   this.setNombreUsuario( d.getNombreUsuario() );
		   this.setUsuarioModificacion(d.getUsuarioModificacion());
		   this.setPrioridad( d.getPrioridad() );
		   
	   }

	public Documento(
		String titulo_aplicacion,
		int id_gabinete,
		int id_carpeta_padre,
		int id_documento,
		String nombre_documento,
		String nombre_usuario,
		String usuario_modificacion,
		int prioridad,
		int id_tipo_docto,
		String nombre_tipo_docto,
		Date fh_creacion,
		Date fh_modificacion,
		int numero_accesos,
		int numero_paginas,
		String titulo,
		String autor,
		String materia,
		String descripcion,
		int clase_documento,
		String estado_documento,
		String extension) {
		this(titulo_aplicacion, id_gabinete, id_carpeta_padre, id_documento);
		this.nombre_documento = nombre_documento;
		this.nombre_usuario = nombre_usuario;
		this.usuario_modificacion = usuario_modificacion;
		this.prioridad = prioridad;
		this.id_tipo_docto = id_tipo_docto;
		this.nombre_tipo_docto = nombre_tipo_docto;
		this.fh_creacion = fh_creacion;
		this.fh_modificacion = fh_modificacion;
		this.numero_accesos = numero_accesos;
		this.numero_paginas = numero_paginas;
		this.titulo = titulo;
		this.autor = autor;
		this.materia = materia;
		this.descripcion = descripcion;
		this.clase_documento = clase_documento;
		this.estado_documento = estado_documento;
		this.extension = extension;
	}
	
	public Documento() {
	}

	public Documento(imx_documento imx_documento) {
		this(
		imx_documento.getId().getTituloAplicacion(),
		imx_documento.getId().getIdGabinete(),
		imx_documento.getId().getIdCarpetaPadre(),
		imx_documento.getId().getIdDocumento(),
		imx_documento.getNombreDocumento(),
		imx_documento.getNombreUsuario(),
		imx_documento.getUsuarioModificacion(),
		imx_documento.getPrioridad().intValue(),
		imx_documento.getIdTipoDocumento().intValue(),
		imx_documento.getNombreTipoDocto(),
		new java.sql.Date(imx_documento.getFhCreacion().getTime()),
		new java.sql.Date(imx_documento.getFhModificacion().getTime()),
		imx_documento.getNumeroAccesos(),
		imx_documento.getNumeroPaginas(),
		imx_documento.getTitulo(),
		imx_documento.getAutor(),
		imx_documento.getMateria(),
		imx_documento.getDescripcion(),
		imx_documento.getClaseDocumento().intValue(),
		imx_documento.getEstadoDocumento().toString(),
		""
		);
	}

	public int getId_carpeta_padre() {
		return id_carpeta_padre;
	}

	public void setId_carpeta_padre(int id_carpeta_padre) {
		this.id_carpeta_padre = id_carpeta_padre;
	}

	public int getNumero_paginas() {
		return numero_paginas;
	}

	public void setNumero_paginas(int numero_paginas) {
		this.numero_paginas = numero_paginas;
	}

	public void setTituloAplicacion(String tituloAplicacion) {
		this.titulo_aplicacion = tituloAplicacion;
	}
	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}

	public void setIdGabinete( int idGainete ){
		this.id_gabinete = idGainete;
	}
	
	public int getIdGabinete() {
		return id_gabinete;
	}

	public int getIdCarpetaPadre() {
		return id_carpeta_padre;
	}

	public int getIdDocumento() {
		return id_documento;
	}
	
	public void setAnotaciones( String anotaciones )
	{
		this.anotaciones = anotaciones;
	}
	
	public void setIdDocumento(int id_documento) {
		this.id_documento = id_documento;
	}
	
	public String getAnotaciones()
	{
		return anotaciones;
	}
	
	public String getNombreDocumento() {
		return nombre_documento;
	}

	public void setNombreDocumento(String nombre_documento) {
		this.nombre_documento = nombre_documento;
	}

	public String getNombreUsuario() {
		return nombre_usuario;
	}

	public void setNombreUsuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}
	
	public String getUsuarioModificacion() {
		return usuario_modificacion;
	}

	public void setUsuarioModificacion(String usuario_modificacion) {
		this.usuario_modificacion = usuario_modificacion;
	}

	public int getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(int prioridad) {
		this.prioridad = prioridad;
	}

	public int getIdTipoDocto() {
		return id_tipo_docto;
	}

	public void setIdTipoDocto(int id_tipo_docto) {
		this.id_tipo_docto = id_tipo_docto;
	}

	public String getNombreTipoDocto() {
		return nombre_tipo_docto;
	}
	public String getNombreTipoDocto(int tipo) {
		switch(tipo){
		case -1:nombre_tipo_docto ="SIN_TIPO";
		case 1:nombre_tipo_docto ="EXTERNO";
		case 2:nombre_tipo_docto ="IMAX_FILE";
		default:nombre_tipo_docto ="EXTERNO";
		}		
		return nombre_tipo_docto;
	}

	public void setNombreTipoDocto(String nombre_tipo_docto) {
		this.nombre_tipo_docto = nombre_tipo_docto;
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

	public String getFechaHoraFormateadaModificacion() {
		return (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(fh_modificacion);
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
	
	//TODO: Hay que eliminar los getter/setter duplicados, existen con _ y en camelcase.
	public int getNumeroPaginas() {
		return numero_paginas;
	}

	public void setNumeroPaginas(int numero_paginas) {
		this.numero_paginas = (numero_paginas < 0 ? 0 : numero_paginas);
//		if(this.numero_paginas == 0){
//			this.id_tipo_docto = Documento.SIN_TIPO;
//		}
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getMateria() {
		return materia;
	}

	public void setMateria(String materia) {
		this.materia = materia;
	}

	public String getDescripcion() {
		return (descripcion == null ? "" : descripcion);
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getClaseDocumento() {
		return clase_documento;
	}

	public String getClaseDocumentoTexto() {
		switch (clase_documento) {
			case 0 : // Original (Real)
				return "Fuente";
			case 1 : // Tiene Doctos Virtuales
				return "Virtual";
			case 2 : // En Uso
				return "Base";
			default :
				return "No Asignado";
		}
	}

	public void setClaseDocumento(int clase_documento) {
		this.clase_documento = clase_documento;
	}

	public String getEstadoDocumento() {
		return estado_documento;
	}

	public String getEstadoDocumentoTexto() {
		if ("T".equals(estado_documento))
			return "Transitorio";
		else if ("V".equals(estado_documento))
			return "Vigente";
		else if ("H".equals(estado_documento))
			return "Historico";
		else
			return "No Asignado";
	}

	public void setEstadoDocumento(String estado_documento) {
		this.estado_documento = estado_documento;
	}

	public String getExtension() {
		if(getTipoDocumento()==Documento.IMAX_FILE)
			return "imx";
		else if(getTipoDocumento()==Documento.EXTERNO)
			if(paginas.length>0)
				return paginas[0].getPageExtension();
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = (extension != null) ? extension : "";
	}

	public String getDescripcionPaginas() {
		return numero_paginas
			+ ("IMAX_FILE".equals(nombre_tipo_docto)
				? ((numero_paginas == 1) ? " p\u00E1gina" : " paginas")
				: ((numero_paginas == 1) ? " documento" : " documentos") + " " + extension);
	}

	public String getEstadoDocumentoTipoDocto() {
		return getEstadoDocumentoTexto() + "/" + getNombreTipoDocto();
	}

	public String getFechaDocumentoCreacion() {
		return getFechaFormateadaCreacion() + " a las " + getHoraFormateadaCreacion();
	}

	public String getFechaDocumentoModificacion() {
		return getFechaFormateadaModificacion() + " a las " + getHoraFormateadaModificacion();
	}
	
	public String[] getRealFilesNames() {
		String realFilesNames[] = new String[paginas.length];

		for (int i = 0; i < paginas.length; i++)
			realFilesNames[i] = paginas[i].getNomArchivoOrg();

		return realFilesNames;
	}

	public String[] getFilesNames() {
		String filesNames[] = new String[paginas.length];

		for (int i = 0; i < paginas.length; i++) {
			filesNames[i] =
				nombre_documento + ((id_tipo_docto == 2) ? "_" + (i + 1) : "") + paginas[i].getPageExtension();
		}

		return filesNames;
	}

	public String[] getFullPathFilesNames() {
		String fullPathFilesNames[] = new String[paginas.length];

		for (int i = 0; i < paginas.length; i++) {
			fullPathFilesNames[i] =
				paginas[i].getUnidadDisco()
					+ ("*".equals(paginas[i].getRutaBase())?"":paginas[i].getRutaBase())
					+ paginas[i].getRutaDirectorio()
					+ paginas[i].getNomArchivoVol();
		}

		return fullPathFilesNames;
	}

	public Pagina getPaginaDocumento(int index) {
		return paginas[index];
	}

	public Pagina[] getPaginasDocumento() {
		return paginas;
	}

	public void setPaginasDocumento(Pagina[] paginas) {
		this.paginas = paginas;
	}

	public String toString() {
		return "titulo_aplicacion("
			+ titulo_aplicacion
			+ "), id_gabinete("
			+ id_gabinete
			+ "), id_carpeta_padre("
			+ id_carpeta_padre
			+ "), id_documento("
			+ id_documento
			+ "), nombre_documento("
			+ nombre_documento
			+ "), nombre_usuario("
			+ nombre_usuario
			+ "), usuario_modificacion("
			+ usuario_modificacion
			+ "), prioridad("
			+ prioridad
			+ "), id_tipo_docto("
			+ id_tipo_docto
			+ "), nombre_tipo_docto("
			+ nombre_tipo_docto
			+ "), fh_creacion("
			+ fh_creacion
			+ "), fh_modificacion("
			+ fh_modificacion
			+ "), numero_accesos("
			+ numero_accesos
			+ "), numero_paginas("
			+ numero_paginas
			+ "), titulo("
			+ titulo
			+ "), autor("
			+ autor
			+ "), materia("
			+ materia
			+ "), descripcion("
			+ descripcion
			+ "), clase_documento("
			+ clase_documento
			+ "), estado_documento("
			+ estado_documento
			+ ")";
	}

	public double getTamanoBytes() {
		return tamano_bytes;
	}

	public String getTamanoBytesTexto() {
		return FileStorageCapacity.conversion(tamano_bytes);
	}

	public void setTamanoBytes(double tamano_bytes) {
		this.tamano_bytes = tamano_bytes;
	}

	public String getCompartir() {
		return compartir;
	}
	
	public String getDateExp()
	{
		return dateExp;	
	}

	public String getDateShared()
	{
		if( "".equals( dateShared ) )
			return "NULL";
		else
			return dateShared;
		
	}

	public String getHoureExp()

	{
		return houreExp;
	}
	
	public void setHoureExp( String houreExp )
	{
		this.houreExp = houreExp;
	}
	
	public void setDateExp( String dateExp )
	{
		
		this.dateExp = dateExp;
		
	}
	
	public void setDateShared( String dateShared )
	{
		
		this.dateShared = dateShared;
		
	}
	
	public boolean noEstaCompartido() {
		return !"S".equals(compartir);
	}

	public void setCompartir(String compartir) {
		this.compartir = compartir;
	}

	public String getTokenCompartir() {
		return token;
	}

	public void setTokenCompartir(String token) {
		this.token = token;
	}

	public String getDavPath() {
		return dav_path;
	}

	public void setDavPath(String dav_path) {
		this.dav_path = dav_path;
	}
	
	public String getNombreAplicacion() {
		return nombreAplicacion;
	}

	public void setNombreAplicacion(String nombreAplicacion) {
		this.nombreAplicacion = nombreAplicacion;
	}

	public String getNombreCarpeta() {
		return nombreCarpeta;
	}

	public void setNombreCarpeta(String nombreCarpeta) {
		this.nombreCarpeta = nombreCarpeta;
	}

	public String getNodo() {
		return titulo_aplicacion+"_G"+id_gabinete+"C"+id_carpeta_padre+"D"+id_documento;
	}
	
	public String getTreePath() throws CarpetaManagerException {
		Carpeta carpetaPadre = new Carpeta(titulo_aplicacion,id_gabinete,id_carpeta_padre); 
		carpetaPadre = new CarpetaManager().selectCarpeta(carpetaPadre);
		return carpetaPadre.getTreePath()+getNodo()+":\""+nombre_documento+"\"";
	}
	
	public String getPath() throws CarpetaManagerException {
		Carpeta carpetaPadre = new Carpeta(titulo_aplicacion,id_gabinete,id_carpeta_padre); 
		carpetaPadre = new CarpetaManager().selectCarpeta(carpetaPadre);
		return carpetaPadre.getPath()+nombre_documento;
	}

	public final static int SIN_TIPO = -1;
	//public final static int VACIO = 0;
	public final static int EXTERNO = 1;
	public final static int IMAX_FILE = 2;
	
	public int getTipoDocumento() {
		return id_tipo_docto;
	}
}