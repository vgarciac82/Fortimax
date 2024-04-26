package com.syc.fortimax.Historico;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.syc.fortimax.FortimaxException;
import com.syc.fortimax.hibernate.EntityManagerException;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.fortimax.hibernate.entities.imx_documento_id;
import com.syc.imaxfile.CarpetaManagerException;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.DocumentoManager;
import com.syc.imaxfile.DocumentoManagerException;
import com.syc.imaxfile.Volumen;
import com.syc.imaxfile.VolumenManager;
import com.syc.imaxfile.VolumenManagerException;
import com.syc.user.Usuario;
import com.syc.user.UsuarioManager;
import com.syc.utils.Json;
import com.syc.zip.MakeZip;

public class Historico {
	
	private static Logger log = Logger.getLogger(Historico.class);

	public static void insertarVersion(imx_documento imx_documento) throws EntityManagerException, IOException, CarpetaManagerException, DocumentoManagerException, VolumenManagerException, FortimaxException {
		/*
		if(imx_documento.getGenerarHistorico())
			insertar(imx_documento);
		*/	
	}

	private static void insertar(imx_documento imx_documento) throws DocumentoManagerException, VolumenManagerException, FortimaxException, IOException, CarpetaManagerException, EntityManagerException {
		imx_documento_id id = imx_documento.getId();
		DocumentoManager dm = new DocumentoManager();
		Documento documento = dm.selectDocumento(id.getTituloAplicacion(),id.getIdGabinete(),id.getIdCarpetaPadre(),id.getIdDocumento());
		String codigo = "his"; //Este es el código que se utilizara para guardar el historico si no se tiene el usuario que realizo la modificación
		if(imx_documento.getUsuarioModificacion()!=null) {
			Usuario usuario = new UsuarioManager().selectUsuario(imx_documento.getUsuarioModificacion());
			codigo = usuario.getCodigo();
		}		
		String filename = dm.getNextFilename(documento, codigo)+".tif";
				
		Volumen v = VolumenManager.getVolumen();
		String volumePath = v.getUnidad() + ("*".equals(v.getRutaBase()) ? "" : v.getRutaBase()) + v.getRutaDirectorio() + v.getVolumen();
		File carpetaVolumen = new File(volumePath);
		carpetaVolumen.mkdir();
		if(!carpetaVolumen.exists())
			throw new FortimaxException(FortimaxException.codeUnknowed,"No se pudo crear la carpeta "+carpetaVolumen.getAbsolutePath());
		File file = new File(carpetaVolumen, filename);
		log.debug("Se guardara el archivo de historico a "+file.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(file, false);
		try {
			MakeZip.writeZip(documento, "/"+documento.getNombreDocumento(), false, fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
		HistoricoModel historico = new HistoricoModel();
		historico.path = file.getAbsolutePath();
		String objetoPlantilla = new Json(historico).returnJson();
		
		//TODO: Reactivar con la nueva implementación de MVC
		//new imx_historico_documento_manager(imx_documento).insertarVersion(imx_documento,objetoPlantilla);	
	}
	
	
}
