package com.syc.user;
import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import com.syc.fortimax.hibernate.entities.imx_tipo_usuario;
import com.syc.fortimax.hibernate.entities.imx_usuario;

public class Usuario extends imx_usuario implements Serializable {

@SuppressWarnings("unused")
private static final Logger log = Logger.getLogger(Usuario.class);

	private static final long serialVersionUID = 6434869952934617551L;

	private int id_gabinete = -1;
	private Date fecha_registro ;
	private Date fecha_inicial_vigencia ;
	private Date fecha_termino_vigencia;
	private String genero ;
	private Date fecha_nacimiento ;
	private String ocupacion;
	private String correo ;
	private int pregunta_secreta = -1;
	private String respuesta_secreta ;
	private String firma_acuerdo ;
	private String autoriza_cortesia ;
	private long bytes_autorizados = 52428800; //el default, 50 MB
	private long bytes_usados = 0L;
	private String verificado_paypal ;
	// Auxiliares
	private boolean PyME = false;
	private int tipo_usuario = 0;

	private imx_usuario imx_usr;
	private String locale_default;

	public int getTipo_usuario()
   {
   	return tipo_usuario;
   }

	public void setTipo_usuario( int tipo_usuario )
   {
   	this.tipo_usuario = tipo_usuario;
   }

	public Usuario(String nombre_usuario) {
		this.setNombreUsuario(nombre_usuario);
	}

	public Usuario(String nombre_usuario, int id_gabinete) {
		this(nombre_usuario);
		this.id_gabinete = id_gabinete;
	}

	public Usuario(
		String nombre_usuario,
		char bandera_conexion,
		String descripcion,
		String codigo,
		char tipo_operacion,
		String cdg,
		String nombre,
		String apellido_paterno,
		String apellido_materno) {
		this(nombre_usuario);

		this.setBanderaConexion(bandera_conexion);
		this.setDescripcion(descripcion);
		this.setCodigo(codigo);
		this.setTipoOperacion(tipo_operacion);
		this.setCdg(cdg);
		this.setNombre(nombre);
		this.setApellidoPaterno(apellido_paterno);
		this.setApellidoMaterno(apellido_materno);
	}

	public Usuario(
		String nombre_usuario,
		char bandera_conexion,
		String descripcion,
		String codigo,
		char tipo_operacion,
		String cdg,
		String nombre,
		String apellido_paterno,
		String apellido_materno,
		int id_gabinete,
		Date fecha_registro,
		Date fecha_inicial_vigencia,
		Date fecha_termino_vigencia,
		String genero,
		Date fecha_nacimiento,
		String ocupacion,
		String correo,
		int pregunta_secreta,
		String respuesta_secreta,
		String firma_acuerdo,
		String autoriza_cortesia,
		int bytes_autorizados,
		int bytes_usados,
		String verificado_paypal,
		String tipo_vignecia) {
		this(
			nombre_usuario,
			bandera_conexion,
			descripcion,
			codigo,
			tipo_operacion,
			cdg,
			nombre,
			apellido_paterno,
			apellido_materno);

		this.id_gabinete = id_gabinete;
		this.fecha_registro = fecha_registro;
		this.fecha_inicial_vigencia = fecha_inicial_vigencia;
		this.fecha_termino_vigencia = fecha_termino_vigencia;
		this.genero = genero;
		this.fecha_nacimiento = fecha_nacimiento;
		this.ocupacion = ocupacion;
		this.correo = correo;
		this.pregunta_secreta = pregunta_secreta;
		this.respuesta_secreta = respuesta_secreta;
		this.firma_acuerdo = firma_acuerdo;
		this.autoriza_cortesia = autoriza_cortesia;
		this.bytes_autorizados = bytes_autorizados;
		this.bytes_usados = bytes_usados;
		this.verificado_paypal = verificado_paypal;
	}

	public int getIdGabinete() {
		return id_gabinete;
	}

	public void setIdGabinete(int id_gabinete) {
		this.id_gabinete = id_gabinete;
	}

	public Date getFechaRegistro() {
		return fecha_registro;
	}

	public void setFechaRegistro(Date fecha_registro) {
		this.fecha_registro = fecha_registro;
	}

	public Date getFechaInicialVigencia() {
		return fecha_inicial_vigencia;
	}

	public void setFechaInicialVigencia(Date fecha_inicial_vigencia) {
		this.fecha_inicial_vigencia = fecha_inicial_vigencia;
	}

	public Date getFechaTerminoVigencia() {
		return fecha_termino_vigencia;
	}

	public void setFechaTerminoVigencia(Date fecha_termino_vigencia) {
		this.fecha_termino_vigencia = fecha_termino_vigencia;
	}

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	public String getFirmaAcuerdo() {
		return firma_acuerdo;
	}

	public void setFirmaAcuerdo(String firma_acuerdo) {
		this.firma_acuerdo = firma_acuerdo;
	}

	public String getAutorizaCortesia() {
		return autoriza_cortesia;
	}

	public void setAutorizaCortesia(String autoriza_cortesia) {
		this.autoriza_cortesia = autoriza_cortesia;
	}

	public long getBytesAutorizados() {
		return bytes_autorizados;
	}

	public void setBytesAutorizados(long bytes_autorizados) {
		this.bytes_autorizados = bytes_autorizados;
	}

	public long getBytesUsados() {
		return bytes_usados;
	}

	public void setBytesUsados(long bytes_usados) {
		this.bytes_usados = bytes_usados;
	}

	public String getVerificadoPayPal() {
		return verificado_paypal;
	}

	public void setVerificadoPayPal(String verificado_paypal) {
		this.verificado_paypal = verificado_paypal;
	}

	public boolean isPyME() {
		return PyME;
	}

	public boolean getPyME() {
		return PyME;
	}

	public void setPyME(boolean PyME) {
		this.PyME = PyME;
	}

/** Métodos copiados de com.syc.usuario.Usuario */
	public Usuario(String nombre_usuario,char bandera_conexion,String descripcion,String codigo,char tipo_operacion
			,String cdg,String nombre,String apellido_paterno,String apellido_materno,int tipo_usuario,char cambio_cdg){
		super();
		log.debug("Constructor de Usuario.");
		setNombreUsuario(nombre_usuario);
		setBanderaConexion(bandera_conexion);
		setDescripcion(descripcion);
		setCodigo(codigo);
		setTipoOperacion(tipo_operacion);
		setCdg(cdg);
		setNombre(nombre);
		setApellidoPaterno(apellido_paterno);
		setApellidoMaterno(apellido_materno);
		setImxTipoUsuario(new imx_tipo_usuario((short)tipo_usuario, "Normal"));
		setCambioCdg(cambio_cdg);
	}
	
	public imx_usuario ReturnUsuarioEntity(Usuario usr){

		imx_usr = new imx_usuario();
		this.imx_usr.setNombreUsuario(usr.getNombreUsuario());
		this.imx_usr.setBanderaConexion(usr.getBanderaConexion());
		this.imx_usr.setDescripcion(usr.getDescripcion());
		this.imx_usr.setCodigo(usr.getCodigo());
		this.imx_usr.setTipoOperacion(usr.getTipoOperacion());
		this.imx_usr.setCdg(usr.getCdg());
		this.imx_usr.setNombre(usr.getNombre());
		this.imx_usr.setApellidoPaterno(usr.getApellidoPaterno());
		this.imx_usr.setApellidoMaterno(usr.getApellidoMaterno());
		this.imx_usr.setImxTipoUsuario(new imx_tipo_usuario());
		this.imx_usr.setCambioCdg(usr.getCambioCdg());
		return imx_usr;
	}
	

	public void setGenero(String genero){

		this.genero = genero;
	}

	public String getGenero(){

		return genero;
	}

	public void setFechaNacimiento(Date fecha_nacimiento){

		this.fecha_nacimiento = fecha_nacimiento;
	}

	public Date getFechaNacimiento(){

		return fecha_nacimiento;
	}


	public void setCorreo(String correo){

		this.correo = correo;
	}

	public String getCorreo(){

		return correo;
	}

	public void setRespuestaSecreta(String respuesta_secreta){

		this.respuesta_secreta = respuesta_secreta;
	}

	public String getRespuestaSecreta(){

		return respuesta_secreta;
	}

	public void setPreguntaSecreta(int pregunta_secreta){

		this.pregunta_secreta = pregunta_secreta;
	}

	public int getPreguntaSecreta(){

		return pregunta_secreta;
	}

	public void setLocaleDefault(String locale_default){

		this.locale_default = locale_default;
	} // TODO: Verificar si esta variable es necesaria.

	public String getLocaleDefault(){

		return locale_default;
	}
	
	public Usuario(){
	super();
	}
}
