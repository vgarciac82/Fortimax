package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.syc.imaxfile.Documento;

public class imx_documento implements Serializable {

	private static final Logger log = Logger.getLogger(imx_documento.class);
	
	private static final long serialVersionUID = -193854310375657816L;
	
	protected imx_documento_id id;
	protected String nombreDocumento;
	protected String nombreUsuario;
	protected String usuarioModificacion;
	protected Integer prioridad;
	protected Integer idTipoDocumento;
	protected Date fhCreacion;
	protected Date fhModificacion;
	protected Integer numeroAccesos;
	protected Integer numeroPaginas;
	protected String titulo;
	protected String autor;
	protected String materia;
	protected String descripcion;
	protected Integer claseDocumento;
	protected Character estadoDocumento;
	protected Long tamanoBytes;
	protected String compartir;
	protected String tokenCompartir;
	protected Date fechaCompartido;
	protected String diasPermitidos;
	protected Date fechaExpira;

	public imx_documento() {
	}

	public imx_documento(imx_documento_id id, String nombreDocumento,
			String nombreUsuario, String usuarioModificacion, Integer prioridad,
			Integer idTipoDocumento, Date fhCreacion, Date fhModificacion,
			int numeroAccesos, int numeroPaginas, String titulo, String autor,
			String materia, String descripcion, Integer claseDocumento,
			Character estadoDocumento, Long tamanoBytes, String compartir,
			String tokenCompartir, Date fechaCompartido, String diasPermitidos,
			Date fechaExpira) {
		super();
		this.id = id;
		this.nombreDocumento = nombreDocumento;
		this.nombreUsuario = nombreUsuario;
		this.usuarioModificacion = usuarioModificacion;
		this.prioridad = prioridad;
		this.idTipoDocumento = idTipoDocumento;
		this.fhCreacion = fhCreacion;
		this.fhModificacion = fhModificacion;
		this.numeroAccesos = numeroAccesos;
		this.numeroPaginas = numeroPaginas;
		this.titulo = titulo;
		this.autor = autor;
		this.materia = materia;
		this.descripcion = descripcion;
		this.claseDocumento = claseDocumento;
		this.estadoDocumento = estadoDocumento;
		this.tamanoBytes = tamanoBytes;
		this.compartir = compartir;
		this.tokenCompartir = tokenCompartir;
		this.fechaCompartido = fechaCompartido;
		this.diasPermitidos = diasPermitidos;
		this.fechaExpira = fechaExpira;
	}

	public imx_documento(String nombreGrupo) {
		this.nombreDocumento = nombreGrupo;
	}

	public Integer getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(Integer prioridad) {
		this.prioridad = prioridad;
	}

	public Integer getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Integer idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getMateria() {
		return materia;
	}

	public void setMateria(String materia) {
		this.materia = materia;
	}

	public imx_documento_id getId() {
		return this.id;
	}

	public void setId(imx_documento_id id) {
		this.id = id;
	}

	public String getNombreDocumento() {
		return this.nombreDocumento;
	}

	public void setNombreDocumento(String nombreDocumento) {
		this.nombreDocumento = nombreDocumento;
	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}
	
	public String getUsuarioModificacion() {
		return usuarioModificacion;
	}

	public void setUsuarioModificacion(String usuarioModificacion) {
		this.usuarioModificacion = usuarioModificacion;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public Date getFhCreacion() {
		return fhCreacion;
	}

	public void setFhCreacion(Date fhCreacion) {
		this.fhCreacion = fhCreacion;
	}

	public Date getFhModificacion() {
		return fhModificacion;
	}

	public void setFhModificacion(Date fhModificacion) {
		this.fhModificacion = fhModificacion;
	}

	public int getNumeroAccesos() {
		return this.numeroAccesos;
	}

	public void setNumeroAccesos(int numeroAccesos) {
		this.numeroAccesos = numeroAccesos;
	}

	public int getNumeroPaginas() {
		return this.numeroPaginas;
	}

	public void setNumeroPaginas(int numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return this.autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getClaseDocumento() {
		return this.claseDocumento;
	}

	public void setClaseDocumento(Integer claseDocumento) {
		this.claseDocumento = claseDocumento;
	}

	public Character getEstadoDocumento() {
		return this.estadoDocumento;
	}

	public void setEstadoDocumento(Character estadoDocumento) {
		this.estadoDocumento = estadoDocumento;
	}

	public Long getTamanoBytes() {
		return this.tamanoBytes;
	}

	public void setTamanoBytes(Long tamanoBytes) {
		this.tamanoBytes = tamanoBytes;
	}

	public String getCompartir() {
		return this.compartir;
	}

	public void setCompartir(String compartir) {
		this.compartir = compartir;
	}

	public String getTokenCompartir() {
		return this.tokenCompartir;
	}

	public void setTokenCompartir(String tokenCompartir) {
		this.tokenCompartir = tokenCompartir;
	}

	public Date getFechaCompartido() {
		return this.fechaCompartido;
	}

	public void setFechaCompartido(Date fechaCompartido) {
		this.fechaCompartido = fechaCompartido;
	}

	public String getDiasPermitidos() {
		return this.diasPermitidos;
	}

	public void setDiasPermitidos(String diasPermitidos) {
		this.diasPermitidos = diasPermitidos;
	}

	public Date getFechaExpira() {
		return this.fechaExpira;
	}

	public void setFechaExpira(Date fechaExpira) {
		this.fechaExpira = fechaExpira;
	}

	// TODO: Esta variable no pertenece a la tabla, pero se necesita, para k fortimax
	// sigua funcionando como lo hace hasta ahora (mal)
	
	protected String nombreTipoDocto;

	public String getNombreTipoDocto() {
		return nombreTipoDocto;
	}

	public void setNombreTipoDocto(String nombreTipoDocto) {
		this.nombreTipoDocto = nombreTipoDocto;
	}
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd:HH:mm");
	
	public imx_documento(Documento d) {
		super();
		this.id = new imx_documento_id(d.getTituloAplicacion(), d.getIdGabinete(), d.getIdCarpetaPadre(), d.getIdDocumento());
		this.nombreDocumento = d.getNombreDocumento();
		this.nombreUsuario = d.getNombreUsuario();
		this.usuarioModificacion = d.getUsuarioModificacion();
		this.prioridad = d.getPrioridad();
		this.idTipoDocumento = d.getIdTipoDocto();
		this.fhCreacion = d.getFechaCreacion();
		this.fhModificacion = d.getFechaModificacion();
		this.numeroAccesos = d.getNumeroAccesos();
		this.numeroPaginas = d.getNumeroPaginas();
		this.titulo = d.getTitulo();
		this.autor = d.getAutor();
		this.materia = d.getMateria();
		this.descripcion = d.getDescripcion();
		this.claseDocumento = d.getClaseDocumento();
		this.estadoDocumento = d.getEstadoDocumento().charAt(0);
		this.tamanoBytes = (long) d.getTamanoBytes();
		this.compartir = "null".equals(d.getCompartir()) ? "N" : d.getCompartir(); 
		this.tokenCompartir = d.getTokenCompartir();
		this.nombreTipoDocto = d.getNombreTipoDocto();
		try {
			this.fechaExpira = sdf.parse(d.getDateExp()+":"+d.getHoureExp());
		} catch (ParseException e) {
			log.trace("No se pudo procesar la fecha de Expiración \""+d.getDateExp()+":"+d.getHoureExp()+"\"");
		}
		try {
			if("CURRENT_DATE".equals(d.getDateShared()))
				this.fechaCompartido = new Date(System.currentTimeMillis());
			else if("NULL".equals(d.getDateShared()))
				;
			else
				this.fechaCompartido = sdf.parse(d.getDateShared());
		} catch (ParseException e) {
			log.trace("No se pudo procesar la fecha de Compartición \""+d.getDateShared()+"\"");
		}
		
		//TODO: Lo siguiente parece que no sé usa para nada:
		//this.diasPermitidos = ???
	}
	
	@Override
	public String toString() {
		return "imx_documento [id=" + id + ", nombreDocumento="
				+ nombreDocumento + ", nombreUsuario=" + nombreUsuario
				+ ", usuarioModificacion=" + usuarioModificacion
				+ ", prioridad=" + prioridad + ", idTipoDocumento="
				+ idTipoDocumento + ", fhCreacion=" + fhCreacion
				+ ", fhModificacion=" + fhModificacion + ", numeroAccesos="
				+ numeroAccesos + ", numeroPaginas=" + numeroPaginas
				+ ", titulo=" + titulo + ", autor=" + autor + ", materia="
				+ materia + ", descripcion=" + descripcion
				+ ", claseDocumento=" + claseDocumento + ", estadoDocumento="
				+ estadoDocumento + ", tamanoBytes=" + tamanoBytes
				+ ", compartir=" + compartir + ", tokenCompartir="
				+ tokenCompartir + ", fechaCompartido=" + fechaCompartido
				+ ", diasPermitidos=" + diasPermitidos + ", fechaExpira="
				+ fechaExpira+"]";
	}
}
