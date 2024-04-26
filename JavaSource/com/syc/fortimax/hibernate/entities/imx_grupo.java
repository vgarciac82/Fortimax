package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class imx_grupo implements Serializable {

	private static final long serialVersionUID = 4438788708368694280L;

	private String nombreGrupo;
	private String descripcion;
	private Set<?> imxGrupoUsuarios = new HashSet(0);
	private Set<?> imxGrupoPrivilegios = new HashSet(0);

	public imx_grupo() {
	}

	public imx_grupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public imx_grupo(String nombreGrupo, String descripcion) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.descripcion = descripcion;
	}

	public imx_grupo(String nombreGrupo, String descripcion,
			Set<?> imx_grupoUsuarios, Set<?> imx_grupoPrivilegios) {
		this.nombreGrupo = nombreGrupo;
		this.descripcion = descripcion;
		this.imxGrupoUsuarios = imx_grupoUsuarios;
		this.imxGrupoPrivilegios = imx_grupoPrivilegios;
	}

	public String getNombreGrupo() {
		return this.nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Set<?> getImxGrupoUsuarios() {
		return imxGrupoUsuarios;
	}

	public void setImxGrupoUsuarios(Set<?> imxGrupoUsuarios) {
		this.imxGrupoUsuarios = imxGrupoUsuarios;
	}

	public Set<?> getImxGrupoPrivilegios() {
		return imxGrupoPrivilegios;
	}

	public void setImxGrupoPrivilegios(Set<?> imxGrupoPrivilegios) {
		this.imxGrupoPrivilegios = imxGrupoPrivilegios;
	}
}
