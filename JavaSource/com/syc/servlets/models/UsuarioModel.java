package com.syc.servlets.models;

public class UsuarioModel {
	private String nombre_usuario;
	private String descripcion;
	private String cdg;
	private String nombre;
	private String apellido_paterno;
	private String apellido_materno;
	private String correo;
	private Long cuota_autorizada;
	private String fecha_nac;
	private String genero;
	private int pregunta_secreta;
	private String respuesta_secreta;
	private String tipo_vigencia;
	private int tipo_usuario;
	private int activo;
	private int administrador;

	public UsuarioModel() {
		super();
		this.nombre_usuario = "";
		this.descripcion = "";
		this.cdg = "";
		this.nombre = "";
		this.apellido_paterno = "";
		this.apellido_materno = "";
		this.correo = "";
		this.cuota_autorizada = -1L;
		this.fecha_nac = "";
		this.genero = "";
		this.pregunta_secreta = -1;
		this.respuesta_secreta = "";
		this.tipo_vigencia = "";
		this.tipo_usuario = -1;
		this.activo = -1;
		this.administrador = -1;
	}

	public String getNombreUsuario() {
		return nombre_usuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombre_usuario = nombreUsuario;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCdg() {
		return cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidoPaterno() {
		return apellido_paterno;
	}

	public void setApellidoPaterno(String apellido_paterno) {
		this.apellido_paterno = apellido_paterno;
	}

	public String getApellidoMaterno() {
		return apellido_materno;
	}

	public void setApellidoMaterno(String apellido_materno) {
		this.apellido_materno = apellido_materno;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Long getCuotaAutorizada() {
		return cuota_autorizada;
	}

	public void setCuotaAutorizada(Long cuota_autorizada) {
		this.cuota_autorizada = cuota_autorizada;
	}

	public String getFechaNac() {
		return fecha_nac;
	}

	public void setFechaNac(String fecha_nac) {
		this.fecha_nac = fecha_nac;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public int getPreguntaSecreta() {
		return pregunta_secreta;
	}

	public void setPreguntaSecreta(int pregunta_secreta) {
		this.pregunta_secreta = pregunta_secreta;
	}

	public String getRespuestaSecreta() {
		return respuesta_secreta;
	}

	public void setRespuestaSecreta(String respuesta_secreta) {
		this.respuesta_secreta = respuesta_secreta;
	}

	public String getTipoVigencia() {
		return tipo_vigencia;
	}

	public void setTipoVigencia(String tipo_vigencia) {
		this.tipo_vigencia = tipo_vigencia;
	}

	public int getTipo_usuario() {
		return tipo_usuario;
	}

	public void setTipo_usuario(int tipo_usuario) {
		this.tipo_usuario = tipo_usuario;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}
	
	public int getAdministrador() {
		return administrador;
	}

	public void setAdministrador(int administrador) {
		this.administrador = administrador;
	}

}