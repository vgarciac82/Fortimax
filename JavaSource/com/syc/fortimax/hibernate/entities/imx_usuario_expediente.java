package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.util.Date;

public class imx_usuario_expediente implements Serializable {

	private static final long serialVersionUID = -7713302530363515977L;

	private imx_usuario_expediente_id id;
	private Date fecha_registro;
	private Date fecha_inicial_vigencia;
	private Date fecha_termino_vigencia;
	private String genero;
	private Date fecha_nacimiento;
	private String ocupacion;
	private String correo;
	private Integer pregunta_secreta;
	private String respuesta_secreta;
	private String firma_acuerdo;
	private String autoriza_cortesia;
	private long bytes_autorizados;
	private long bytes_usados;
	private String verificado_paypal;
	private String beneficiario;
	private Date fecha_ultimo_acceso;

	public imx_usuario_expediente() {
		super();
	}

	public imx_usuario_expediente(imx_usuario_expediente r) {
		super();
		this.id = r.id;
		this.fecha_registro = r.fecha_registro;
		this.fecha_inicial_vigencia = r.fecha_inicial_vigencia;
		this.fecha_termino_vigencia = r.fecha_termino_vigencia;
		this.genero= r.genero;
		this.fecha_nacimiento = r.fecha_nacimiento;
		this.ocupacion = r.ocupacion;
		this.correo = r.correo;
		this.pregunta_secreta = r.pregunta_secreta;
		this.respuesta_secreta = r.respuesta_secreta;
		this.firma_acuerdo = r.firma_acuerdo;
		this.autoriza_cortesia = r.autoriza_cortesia;
		this.bytes_autorizados = r.bytes_autorizados;
		this.bytes_usados = r.bytes_usados;
		this.verificado_paypal = r.verificado_paypal;
		this.beneficiario = r.beneficiario;
		this.fecha_ultimo_acceso = r.fecha_ultimo_acceso;
	}
	
	public imx_usuario_expediente(imx_usuario_expediente_id id, Date fecha_registro, Date fecha_inicial_vigencia,
			Date fecha_termino_vigencia, String genero, Date fecha_nacimiento, String ocupacion, String correo,
			Integer pregunta_secreta, String respuesta_secreta, String firma_acuerdo, String autoriza_cortesia,
			Integer bytes_autorizados, Integer bytes_usados, String verificado_paypal, String beneficiario, Date fecha_ultimo_acceso) {
		super();
		this.id = id;
		this.fecha_registro = fecha_registro;
		this.fecha_inicial_vigencia = fecha_inicial_vigencia;
		this.fecha_termino_vigencia = fecha_termino_vigencia;
		this.genero= genero;
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
		this.beneficiario = beneficiario;
		this.fecha_ultimo_acceso = fecha_ultimo_acceso;
	}
	
	public imx_usuario_expediente_id getId() {
		return id;
	}

	public void setId(imx_usuario_expediente_id id) {
		this.id = id;
	}

	public Date getFecha_registro() {
		return fecha_registro;
	}

	public void setFecha_registro(Date fecha_registro) {
		this.fecha_registro = fecha_registro;
	}

	public Date getFecha_inicial_vigencia() {
		return fecha_inicial_vigencia;
	}

	public void setFecha_inicial_vigencia(Date fecha_inicial_vigencia) {
		this.fecha_inicial_vigencia = fecha_inicial_vigencia;
	}

	public Date getFecha_termino_vigencia() {
		return fecha_termino_vigencia;
	}

	public void setFecha_termino_vigencia(Date fecha_termino_vigencia) {
		this.fecha_termino_vigencia = fecha_termino_vigencia;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public Date getFecha_nacimiento() {
		return fecha_nacimiento;
	}

	public void setFecha_nacimiento(Date fecha_nacimiento) {
		this.fecha_nacimiento = fecha_nacimiento;
	}

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Integer getPregunta_secreta() {
		return pregunta_secreta;
	}

	public void setPregunta_secreta(Integer pregunta_secreta) {
		this.pregunta_secreta = pregunta_secreta;
	}

	public String getRespuesta_secreta() {
		return respuesta_secreta;
	}

	public void setRespuesta_secreta(String respuesta_secreta) {
		this.respuesta_secreta = respuesta_secreta;
	}

	public String getFirma_acuerdo() {
		return firma_acuerdo;
	}

	public void setFirma_acuerdo(String firma_acuerdo) {
		this.firma_acuerdo = firma_acuerdo;
	}

	public String getAutoriza_cortesia() {
		return autoriza_cortesia;
	}

	public void setAutoriza_cortesia(String autoriza_cortesia) {
		this.autoriza_cortesia = autoriza_cortesia;
	}

	public long getBytes_autorizados() {
		return bytes_autorizados;
	}

	public void setBytes_autorizados(long bytes_autorizados) {
		this.bytes_autorizados = bytes_autorizados;
	}

	public long getBytes_usados() {
		return bytes_usados;
	}

	public void setBytes_usados(long bytes_usados) {
		this.bytes_usados = bytes_usados;
	}

	public String getVerificado_paypal() {
		return verificado_paypal;
	}

	public void setVerificado_paypal(String verificado_paypal) {
		this.verificado_paypal = verificado_paypal;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public Date getFecha_ultimo_acceso() {
		return fecha_ultimo_acceso;
	}

	public void setFecha_ultimo_acceso(Date fecha_ultimo_acceso) {
		this.fecha_ultimo_acceso = fecha_ultimo_acceso;
	}
	
	@Override
	public String toString() {
		return "imx_usuario_expediente [id=" + id + ", fecha_registro="
				+ fecha_registro + ", fecha_inicial_vigencia="
				+ fecha_inicial_vigencia + ", fecha_termino_vigencia="
				+ fecha_termino_vigencia + ", genero=" + genero
				+ ", fecha_nacimiento=" + fecha_nacimiento + ", ocupacion="
				+ ocupacion + ", correo=" + correo + ", pregunta_secreta="
				+ pregunta_secreta + ", respuesta_secreta=" + respuesta_secreta
				+ ", firma_acuerdo=" + firma_acuerdo + ", autoriza_cortesia="
				+ autoriza_cortesia + ", bytes_autorizados="
				+ bytes_autorizados + ", bytes_usados=" + bytes_usados
				+ ", verificado_paypal=" + verificado_paypal
				+ ", beneficiario=" + beneficiario + ", fecha_ultimo_acceso="
				+ fecha_ultimo_acceso + "]";
	}
}

