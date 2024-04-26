package com.syc.fortimax.hibernate.entities;

import java.util.HashSet;
import java.util.Set;

public class imx_usuario implements java.io.Serializable {

	private static final long serialVersionUID = 580459366328134865L;

	private String nombreUsuario;

	// Comentado porke se herada desde imx_usuario
	private imx_tipo_usuario imxTipoUsuario;
	private char banderaConexion;
	private String descripcion;
	private String codigo;
	private char tipoOperacion;
	private String cdg;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private char cambioCdg;
	private String intentosAcceso;
	private int activo = 1;
	private int administrador = 0;
	private Set imxPrivilegios = new HashSet(0);

	/**
	 * @return the activo
	 */
	public int getActivo() {
		return activo;
	}

	/**
	 * @param activo
	 *            the activo to set
	 */
	public void setActivo(int activo) {
		this.activo = activo;
	}

	/**
	 * @return the administrador
	 */
	public int getAdministrador() {
		return administrador;
	}

	/**
	 * @param administrador
	 *            the administrador to set
	 */
	public void setAdministrador(int administrador) {
		this.administrador = administrador;
	}

	public imx_usuario() {
	}

	public imx_usuario(String nombreUsuario, char banderaConexion,
			String codigo, char tipoOperacion) {
		this.nombreUsuario = nombreUsuario;
		this.banderaConexion = banderaConexion;
		this.codigo = codigo;
		this.tipoOperacion = tipoOperacion;
	}

	public imx_usuario(String nombreUsuario, imx_tipo_usuario imxTipoUsuario,
			char banderaConexion, String descripcion, String codigo,
			char tipoOperacion, String cdg, String nombre,
			String apellidoPaterno, String apellidoMaterno, char cambioCdg,
			String intentosAcceso, int activo, int administrador,
			Set imxPrivilegios) {
		this.nombreUsuario = nombreUsuario;
		this.imxTipoUsuario = imxTipoUsuario;
		this.banderaConexion = banderaConexion;
		this.descripcion = descripcion;
		this.codigo = codigo;
		this.tipoOperacion = tipoOperacion;
		this.cdg = cdg;
		this.nombre = nombre;
		this.apellidoPaterno = apellidoPaterno;
		this.apellidoMaterno = apellidoMaterno;
		this.cambioCdg = cambioCdg;
		this.intentosAcceso = intentosAcceso;
		this.activo = activo;
		this.administrador = administrador;
		this.imxPrivilegios = imxPrivilegios;

	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public imx_tipo_usuario getImxTipoUsuario() {
		return this.imxTipoUsuario;
	}

	public void setImxTipoUsuario(imx_tipo_usuario imxTipoUsuario) {
		this.imxTipoUsuario = imxTipoUsuario;
	}

	public char getBanderaConexion() {
		return this.banderaConexion;
	}

	public void setBanderaConexion(char banderaConexion) {
		this.banderaConexion = banderaConexion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public char getTipoOperacion() {
		return this.tipoOperacion;
	}

	public void setTipoOperacion(char tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidoPaterno() {
		return this.apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getApellidoMaterno() {
		return this.apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public char getCambioCdg() {
		return this.cambioCdg;
	}

	public void setCambioCdg(char cambioCdg) {
		this.cambioCdg = cambioCdg;
	}

	public String getIntentosAcceso() {
		return this.intentosAcceso;
	}

	public void setIntentosAcceso(String intentosAcceso) {
		this.intentosAcceso = intentosAcceso;
	}

	public Set getImxPrivilegios() {
		return this.imxPrivilegios;
	}

	public void setImxPrivilegios(Set imxPrivilegios) {
		this.imxPrivilegios = imxPrivilegios;
	}

}
