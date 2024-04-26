package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.util.Date;

import com.syc.fortimax.hibernate.Entity;

public class imx_historico_documento extends Entity implements Serializable {

	protected static final long serialVersionUID = -6516428089684984200L;

	private imx_historico_documento_id id;
	
	private Date fechaGeneracion;
	private String nombreDocumento;
	private String descripcion;
	private int numeroPaginas;
	private Long tamano;
	private String usuarioGenerador;
	private String plantilla;
	private imx_volumen imx_volumen;
	private String volumen;
	private String nomArchivoVol;
	private String nomArchivoOrg;

	public imx_historico_documento() {
	}

	public imx_historico_documento(
			imx_historico_documento_id id,
			Date fechaGeneracion,
			String nombreDocumento,
			String descripcion,
			int numeroPaginas,
			Long tamano,
			String usuarioGenerador,
			String objetoPlantilla	
			) {
		super();
		this.id = id;
		this.fechaGeneracion = fechaGeneracion;
		this.nombreDocumento = nombreDocumento;
		this.descripcion = descripcion;
		this.numeroPaginas = numeroPaginas;
		this.tamano = tamano;
		this.usuarioGenerador = usuarioGenerador;
		this.plantilla = objetoPlantilla;
	}

	public imx_historico_documento(imx_documento imx_documento) {
		this(
			new imx_historico_documento_id(imx_documento.getId(),null),
			null,
			imx_documento.getNombreDocumento(),
			imx_documento.getDescripcion(),
			imx_documento.getNumeroPaginas(),
			imx_documento.getTamanoBytes(),
			imx_documento.getUsuarioModificacion(),
			null
		);	
	}

	public imx_historico_documento_id getId() {
		return this.id;
	}

	public void setId(imx_historico_documento_id id) {
		this.id = id;
	}

	public Date getFechaGeneracion() {
		return fechaGeneracion;
	}

	public void setFechaGeneracion(Date fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

	public String getNombreDocumento() {
		return nombreDocumento;
	}

	public void setNombreDocumento(String nombreDocumento) {
		this.nombreDocumento = nombreDocumento;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getNumeroPaginas() {
		return numeroPaginas;
	}

	public void setNumeroPaginas(int numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}

	public Long getTamano() {
		return tamano;
	}

	public void setTamano(Long tamano) {
		this.tamano = tamano;
	}

	public String getUsuarioGenerador() {
		return usuarioGenerador;
	}

	public void setUsuarioGenerador(String usuarioGenerador) {
		this.usuarioGenerador = usuarioGenerador;
	}

	public String getPlantilla() {
		return plantilla;
	}

	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}
	
	public imx_volumen getImx_volumen() {
		return imx_volumen;
	}

	public void setImx_volumen(imx_volumen imx_volumen) {
		this.imx_volumen = imx_volumen;
	}
	
	public String getVolumen(){
		return this.volumen;
	}
	
	public void setVolumen(String volumen){
		this.volumen=volumen;
	}

	public String getNomArchivoVol(){
		return this.nomArchivoVol;
	}
	
	public void setNomArchivoVol(String nomArchivoVol){
		this.nomArchivoVol=nomArchivoVol;
	}
	
	public String getNomArchivoOrg(){
		return this.nomArchivoOrg;
	}
	
	public void setNomArchivoOrg(String nomArchivoOrg){
		this.nomArchivoOrg=nomArchivoOrg;
	}
	
	public String getVolumePath() {
		return getImx_volumen().getAbsolutePath()+getNomArchivoVol();
	}
}
