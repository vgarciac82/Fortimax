package com.syc.utils;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;

public class linkshared {
	private static final Logger log=Logger.getLogger(linkshared.class);
	
	private synchronized String generarToken() {
		int time = (int)System.currentTimeMillis();
		String hex = Integer.toHexString(time);
		if(hex.length()%2 != 0)
			hex = "0"+hex;
		byte[] bytes = DatatypeConverter.parseHexBinary(hex);
		String token = new String(Base64.encodeBase64(bytes)).replace('+','-').replace('/','_');
		return token;
	};
	
	public Documento  comparteDocumento(String identificadorDocumento){
		Documento d=null;
		try{
				DocumentoManager dmr = new DocumentoManager();
				d= dmr.selectDocumento(identificadorDocumento);
			if(d.noEstaCompartido()){
				//d.setCompartir("S");			
				d.setTokenCompartir(generarToken());
				d.setDateExp(DateTimeUtils.addHouresToDate(8));
				d.setHoureExp(DateTimeUtils.genHourExpiration(8));
				d.setDateShared("CURRENT_DATE");
				//d.setLigaPermisoBajar("N");
				//dmr.updateDocumento(d, true);
				//d=dmr.selectDocumento(identificadorDocumento);
			}
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return d;
	}
	public Boolean cancelarCompartir(String identificadorDocumento){
		Boolean result=false;
		Documento d=null;
		try{
				DocumentoManager dmr=new DocumentoManager();
				d=dmr.selectDocumento(identificadorDocumento);
				if(!d.noEstaCompartido()){
					d.setCompartir("N");
					d.setTokenCompartir("");
					d.setDateExp("");
					d.setHoureExp("NULL");
					d.setLigaPermisoBajar("N");
					dmr.updateDocumento(d, true);
				}
				result=true;
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return result;
	}
	public Boolean modificarExpiracion(String identificadorDocumento,String fecha,String hora,String permiso,Boolean recompartir){
		Boolean result=false;
		Documento d=null;
		try{
			DocumentoManager dmr=new DocumentoManager();
			d=dmr.selectDocumento(identificadorDocumento);
			if(!recompartir){
				d.setCompartir("S");
				d.setTokenCompartir(generarToken());
				d.setDateExp(fecha+":"+hora);
				d.setHoureExp(hora);
				d.setDateShared("CURRENT_DATE");
				d.setLigaPermisoBajar(permiso);
				dmr.updateDocumento(d, true);
			}
			else{
				d.setCompartir("S");
				d.setTokenCompartir(generarToken());
				d.setDateExp(DateTimeUtils.addHouresToDate(8));
				d.setHoureExp(DateTimeUtils.genHourExpiration(8));
				d.setDateShared("CURRENT_DATE");
				d.setLigaPermisoBajar(permiso);
				dmr.updateDocumento(d, true);
			}
			result=true;
		}
		catch(Exception ex){
			log.error(ex.toString());
		}
		return result;
	}
	public Documento  obtenerDocumentoCompartido(String identificadorDocumento){
		Documento d=null;
		try{
			
				DocumentoManager dmr = new DocumentoManager();
				d= dmr.selectDocumento(identificadorDocumento);
			if(d!=null){
					if(d.noEstaCompartido()){
						d.setTokenCompartir(generarToken());
						d.setDateExp(DateTimeUtils.addHouresToDate(8));
						d.setHoureExp(DateTimeUtils.genHourExpiration(8));
						d.setDateShared("CURRENT_DATE");

					}
				}
			else{
				log.debug("El documento: "+identificadorDocumento+" no existe");
			}
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return d;
	}
}
