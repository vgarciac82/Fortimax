package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;
import java.util.HashMap;

public class imx_grupo_privilegio implements Serializable {

	private static final long serialVersionUID = 1241419553521261570L;

	private imx_grupo_privilegio_id id;
	private imx_grupo imxGrupo;
	private String nombreNivel;
	private int privilegio;

	public imx_grupo_privilegio() {
		super();
	}

	public imx_grupo_privilegio(imx_grupo_privilegio_id id,
			imx_grupo imx_grupo, String nombreNivel, int privilegio) {
		super();
		this.id = id;
		this.imxGrupo = imx_grupo;
		this.nombreNivel = nombreNivel;
		this.privilegio = privilegio;
	}

	public imx_grupo_privilegio(imx_grupo_privilegio_id id, String nombreNivel,
			int privilegio) {
		super();
		this.id = id;
		this.nombreNivel = nombreNivel;
		this.privilegio = privilegio;
	}

	public imx_grupo_privilegio_id getId() {
		return this.id;
	}

	public void setId(imx_grupo_privilegio_id id) {
		this.id = id;
	}

	public imx_grupo getImxGrupo() {
		return imxGrupo;
	}

	public void setImxGrupo(imx_grupo imxGrupo) {
		this.imxGrupo = imxGrupo;
	}

	public String getNombreNivel() {
		return this.nombreNivel;
	}

	public void setNombreNivel(String nombreNivel) {
		this.nombreNivel = nombreNivel;
	}

	public int getPrivilegio() {
		return this.privilegio;
	}

	public void setPrivilegio(int privilegio) {
		this.privilegio = privilegio;
	}
	
	private enum Acciones
	{
	    Eliminar,Modificar,Crear,Imprimir,Compartir,Digitalizar,Descargar,NoValida;
	    public static Acciones toAccion(String str)
	    {
	        try {
	            return valueOf(str);
	        } 
	        catch (Exception ex) {
	            return NoValida;
	        }
	    }   
	}
	
	public HashMap<String,Boolean> getPrivilegioMap() {
		HashMap<String,Boolean> privilegioMap = new HashMap<String,Boolean>();
		privilegioMap.put("Eliminar",((getPrivilegio()&1)==1));
		privilegioMap.put("Modificar",((getPrivilegio()&2)==2));
		privilegioMap.put("Crear",((getPrivilegio()&4)==4));
		privilegioMap.put("Imprimir",((getPrivilegio()&16)==16));
		privilegioMap.put("Compartir",((getPrivilegio()&32)==32));
		privilegioMap.put("Digitalizar",((getPrivilegio()&64)==64));
		privilegioMap.put("Descargar",((getPrivilegio()&128)==128));
		return privilegioMap;
	}
	
	public void setPrivilegio(String accion,boolean permitido) {
		int newPrivilegio = getPrivilegio();
		switch (Acciones.toAccion(accion))
		{
		    case Eliminar:    newPrivilegio+=-(getPrivilegioMap().get("Eliminar")?1:0)+(permitido?1:0); break;               
		    case Modificar:	  newPrivilegio+=-(getPrivilegioMap().get("Modificar")?2:0)+(permitido?2:0); break;   
		    case Crear:       newPrivilegio+=-(getPrivilegioMap().get("Crear")?4:0)+(permitido?4:0); break;
		    case Imprimir:    newPrivilegio+=-(getPrivilegioMap().get("Imprimir")?16:0)+(permitido?16:0); break;
		    case Compartir:   newPrivilegio+=-(getPrivilegioMap().get("Compartir")?32:0)+(permitido?32:0); break;
		    case Digitalizar: newPrivilegio+=-(getPrivilegioMap().get("Digitalizar")?64:0)+(permitido?64:0); break;
		    case Descargar:   newPrivilegio+=-(getPrivilegioMap().get("Descargar")?128:0)+(permitido?128:0); break;
		    default:
		}	
		setPrivilegio(newPrivilegio);
	}
}
