package com.syc.user;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.syc.fortimax.entities.Privilegio;
import com.syc.fortimax.managers.PrivilegioManager;

public class UsuarioPermisos {

	private static Logger log = Logger.getLogger(UsuarioPermisos.class);
	
	 private boolean eliminar = false;
	 private boolean modificar = false;
	 private boolean crear = false;
	 private boolean imprimir = false;
	 private boolean compartir = false;
	 private boolean digitalizar = false;
	 private boolean descargar = false;

	public UsuarioPermisos() {
	}
	
	public UsuarioPermisos(Usuario usuario) {
		//usuario.getAdministrador() //TODO: Los usuarios Administrador quizas deberian tener permisos sobre todo
	}
	 
    public UsuarioPermisos(Usuario usuario, String titulo_aplicacion) {
    	//usuario.getAdministrador() //TODO: Los usuarios Administrador quizas deberian tener permisos sobre todo
        if(usuario!=null)
            try {
                log.trace("Carga de de privilegios de "+usuario.getNombreUsuario()+" en "+titulo_aplicacion);
                ArrayList<Privilegio> privilegios = PrivilegioManager.getPrivilegios(usuario.getNombreUsuario(), titulo_aplicacion);
                log.trace(privilegios);
                eliminar = hayPermiso(privilegios, 1);
                modificar = hayPermiso(privilegios, 2);
                crear = hayPermiso(privilegios, 4);
                //TODO: Nadie sabe cual es el privilegio 8
                imprimir = hayPermiso(privilegios, 16);
                compartir = hayPermiso(privilegios, 32);
                digitalizar = hayPermiso(privilegios, 64);
                descargar = hayPermiso(privilegios, 128);
            } catch (Exception e) {
                log.error(e, e);
            }
    }
    
    private boolean hayPermiso(ArrayList<Privilegio> privilegios, int permiso_requerido) {
        boolean retVal = false;
        try {
            for (Privilegio privilegio : privilegios) {
                retVal = ((permiso_requerido & privilegio.getPrivilegio()) == permiso_requerido);
                if (retVal) {
                    break;
                }
            }
        } catch (Exception se) {
            log.error(se, se);
        }
        return retVal;
    }
    
    public int getPrivilegio() {
    	int privilegio = 0;
    	if(eliminar)
    		privilegio+=1;
    	if(modificar)
    		privilegio+=2;
    	if(crear)
    		privilegio+=4;
    	//TODO: Nadie sabe cual es el privilegio 8
    	if(imprimir)
    		privilegio+=16;
    	if(compartir)
    		privilegio+=32;
    	if(digitalizar)
    		privilegio+=64;
    	if(descargar)
    		privilegio+=128;
    	return privilegio;
    }

	public boolean isEliminar() {
		return eliminar;
	}

	public void setEliminar(boolean eliminar) {
		this.eliminar = eliminar;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public boolean isCrear() {
		return crear;
	}

	public void setCrear(boolean crear) {
		this.crear = crear;
	}

	public boolean isImprimir() {
		return imprimir;
	}

	public void setImprimir(boolean imprimir) {
		this.imprimir = imprimir;
	}

	public boolean isCompartir() {
		return compartir;
	}

	public void setCompartir(boolean compartir) {
		this.compartir = compartir;
	}

	public boolean isDigitalizar() {
		return digitalizar;
	}

	public void setDigitalizar(boolean digitalizar) {
		this.digitalizar = digitalizar;
	}

	public boolean isDescargar() {
		return descargar;
	}

	public void setDescargar(boolean descargar) {
		this.descargar = descargar;
	}

}