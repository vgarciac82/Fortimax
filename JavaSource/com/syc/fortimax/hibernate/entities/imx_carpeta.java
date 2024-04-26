package com.syc.fortimax.hibernate.entities;
import java.util.Date;

import org.apache.log4j.Logger;


public class imx_carpeta implements java.io.Serializable {

private static final Logger log = Logger.getLogger(imx_carpeta.class);

	private static final long serialVersionUID = -5641841096875149444L;

	private imx_carpeta_id id;
	private String nombreCarpeta;
	private String nombreUsuario;
	private Character banderaRaiz;
	private Date fhCreacion;
	private Date fhModificacion;
	private Integer numeroAccesos;
	private Integer numeroCarpetas;
	private Integer numeroDocumentos;
	private String descripcion;
	private String password;

	public imx_carpeta() {
	}

	public imx_carpeta(imx_carpeta_id id, String nombreCarpeta,
			String nombreUsuario, Character banderaRaiz, Date fhCreacion,
			Date fhModificacion, Integer numeroAccesos, Integer numeroCarpetas,
			Integer numeroDocumentos, String descripcion, String password) {
		super();
		this.id = id;
		this.nombreCarpeta = nombreCarpeta;
		this.nombreUsuario = nombreUsuario;
		this.banderaRaiz = banderaRaiz;
		this.fhCreacion = new Date(fhCreacion.getTime());
		this.fhModificacion = new Date(fhModificacion.getTime());
		this.numeroAccesos = numeroAccesos;
		this.numeroCarpetas = numeroCarpetas;
		this.numeroDocumentos = numeroDocumentos;
		this.descripcion = descripcion;
		this.password = password;
	}

	public imx_carpeta_id getId() {
		return id;
	}

	public void setId(imx_carpeta_id id) {
		this.id = id;
	}

	public String getNombreCarpeta() {
		return nombreCarpeta;
	}

	public void setNombreCarpeta(String nombreCarpeta) {
		this.nombreCarpeta = nombreCarpeta;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public Character getBanderaRaiz() {
		return banderaRaiz;
	}

	public void setBanderaRaiz(Character banderaRaiz) {
		this.banderaRaiz = banderaRaiz;
	}

	public Date getFhCreacion() {
		return new Date  (this.fhCreacion.getTime());
	}

	public void setFhCreacion(Date fhCreacion) {
		this.fhCreacion = new Date( fhCreacion.getTime());
	}

	public Date getFhModificacion() {
		return new Date ( fhModificacion.getTime());
	}

	public void setFhModificacion(Date fhModificacion) {
		this.fhModificacion = new Date ( fhModificacion.getTime());
	}

	public Integer getNumeroAccesos() {
		return numeroAccesos;
	}

	public void setNumeroAccesos(Integer numeroAccesos) {
		this.numeroAccesos = numeroAccesos;
	}

	public Integer getNumeroCarpetas() {
		return numeroCarpetas;
	}

	public void setNumeroCarpetas(Integer numeroCarpetas) {
		this.numeroCarpetas = numeroCarpetas;
	}

	public Integer getNumeroDocumentos() {
		return numeroDocumentos;
	}

	public void setNumeroDocumentos(Integer numeroDocumentos) {
		this.numeroDocumentos = numeroDocumentos;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
