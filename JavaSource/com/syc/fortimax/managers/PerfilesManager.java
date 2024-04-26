package com.syc.fortimax.managers;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.syc.fortimax.hibernate.HibernateManager;
import com.syc.fortimax.hibernate.entities.imx_catalogo_privilegios;
import com.syc.fortimax.hibernate.entities.imx_grupo_privilegio;
import com.syc.fortimax.hibernate.entities.imx_perfiles_privilegios;
import com.syc.fortimax.hibernate.entities.imx_usuario_privilegio;


public class PerfilesManager {
	private static Logger log = Logger.getLogger(PerfilesManager.class);
	
	@SuppressWarnings("unchecked")
	public ArrayList<imx_perfiles_privilegios> listPerfiles(){
		ArrayList<imx_perfiles_privilegios> lista=null;
		try{
			HibernateManager _hm=new HibernateManager();
			lista=(ArrayList<imx_perfiles_privilegios>) _hm.list(imx_perfiles_privilegios.class);
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return lista;
	}
	public imx_perfiles_privilegios getPerfil(String nombre, int valor){
		try{
			Conjunction conjunction = Restrictions.conjunction();
			if(nombre!=""&&nombre!=null)
				conjunction.add(Restrictions.eq("Nombre", nombre));
			else
				conjunction.add(Restrictions.eq("Valor", valor));
			
			HibernateManager _hm=new HibernateManager();
			_hm.setCriterion(conjunction);
			imx_perfiles_privilegios p=(imx_perfiles_privilegios)_hm.uniqueResult(imx_perfiles_privilegios.class);
			return p;
		}
		catch(Exception e){
			log.error(e.toString());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public ArrayList<imx_catalogo_privilegios> listPrivilegiosPerfiles(String perfil){
		ArrayList<imx_catalogo_privilegios> lista=null;
		try{
			HibernateManager _hm=new HibernateManager();
			lista=(ArrayList<imx_catalogo_privilegios>)_hm.list(imx_catalogo_privilegios.class);
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return lista;
	}
	public boolean creaPerfil(imx_perfiles_privilegios perfil){
		Boolean result=false;
		try{
			HibernateManager _hm=new HibernateManager();
			_hm.save(perfil);
			result=true;
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return result;
	}
	@SuppressWarnings({ "unchecked" })
	public boolean actualizaPerfil(imx_perfiles_privilegios perfil){
		Boolean result=false;
		try{
			HibernateManager _hm=new HibernateManager();
			List<Object> datosEditar=new ArrayList<Object>();
			_hm.setCriterion(Restrictions.eq("nombreNivel", perfil.getNombre()));
			List<imx_usuario_privilegio> usuarios=(List<imx_usuario_privilegio>)_hm.list(imx_usuario_privilegio.class);
			List<imx_grupo_privilegio> grupos=(List<imx_grupo_privilegio>)_hm.list(imx_grupo_privilegio.class);
			//List<Object> datosEditar=new ArrayList<Object>();
			
			List<imx_usuario_privilegio> usuariosEditados=new ArrayList<imx_usuario_privilegio>();
			List<imx_grupo_privilegio> gruposEditados=new ArrayList<imx_grupo_privilegio>();
			for(imx_usuario_privilegio u:usuarios){
				u.setPrivilegio(privilegiosPerfiles(perfil.getNombre(),u.getPrivilegio(),true));
				usuariosEditados.add(u);
			}
			for(imx_grupo_privilegio g:grupos){
				g.setPrivilegio(privilegiosPerfiles(perfil.getNombre(),g.getPrivilegio(),true));
				gruposEditados.add(g);
			}	
			//datosEditar.add(perfil);
			_hm.update(perfil);
			
			for(imx_usuario_privilegio us:usuariosEditados){
				us.setPrivilegio(privilegiosPerfiles(perfil.getNombre(),us.getPrivilegio(),false));
				datosEditar.add(us);
			}
			for(imx_grupo_privilegio gr:gruposEditados){
				gr.setPrivilegio(privilegiosPerfiles(perfil.getNombre(),gr.getPrivilegio(),false));
				datosEditar.add(gr);
			}
			_hm.update(datosEditar);
			result=true;
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return result;
	}
	public boolean borrarPerfil(imx_perfiles_privilegios perfil){
		Boolean result=false;
		try{
			HibernateManager _hm=new HibernateManager();
			_hm.delete(perfil);
			result=true;
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return result;
	}
	@SuppressWarnings({ "unchecked" })
	public List<String> obtenerUsuariosPerfil(String perfil,Boolean disponibles){
		try{
			Conjunction conjunction = Restrictions.conjunction();
			if(disponibles){
				conjunction.add(Restrictions.eq("nombreNivel", "PERSONALIZADO"));
				conjunction.add(Restrictions.ne("id.tituloAplicacion", "USR_GRALES"));
			}
			else
				conjunction.add(Restrictions.eq("nombreNivel", perfil));
			
			HibernateManager hm=new HibernateManager();
			hm.setCriterion(conjunction);
			hm.setProjection(Projections.distinct(Projections.property("id.nombreUsuario")));
			List<String> usuarios=(List<String>)hm.list(imx_usuario_privilegio.class);
			return usuarios;	
		}
		catch(Exception e){
			log.error(e.toString());
			return null;
		}
	}
	@SuppressWarnings({ "unchecked" })
	public List<String> obtenerGruposPerfil(String perfil,Boolean disponibles){
		try{
			Conjunction conjunction = Restrictions.conjunction();
			if(disponibles){
				conjunction.add(Restrictions.eq("nombreNivel", "PERSONALIZADO"));
				conjunction.add(Restrictions.ne("id.tituloAplicacion", "USR_GRALES"));
			}
			else
				conjunction.add(Restrictions.eq("nombreNivel", perfil));
			HibernateManager _hm=new HibernateManager();
			_hm.setCriterion(conjunction);
			_hm.setProjection(Projections.distinct(Projections.property("id.nombreGrupo")));
			List<String> grupos=(List<String>)_hm.list(imx_grupo_privilegio.class);
			return grupos;
			
		}
		catch(Exception e){
			log.error(e.toString());
			return null;
		}
	}
	public int privilegiosPerfiles(String perfil,int privilegios,Boolean restar){
		try{
			HibernateManager hm=new HibernateManager();
			hm.setCriterion(Restrictions.eq("Nombre", perfil));
			imx_perfiles_privilegios p=(imx_perfiles_privilegios)hm.uniqueResult(imx_perfiles_privilegios.class);
			int valorPerfil=p.getValor();
			
			String _b1=new StringBuilder(Integer.toBinaryString(privilegios)).reverse().toString();
			String _b2=new StringBuilder(Integer.toBinaryString(valorPerfil)).reverse().toString();
			BitSet b1=new BitSet();
			BitSet b2=new BitSet();
			for(int i=0;i<_b1.length();i++)
				if(_b1.substring(i, i+1).equals("1"))
					b1.set(i);
			
			for(int i=0;i<_b2.length();i++)
				if(_b2.substring(i, i+1).equals("1"))
					b2.set(i);
			if(restar){
				b1.or(b2);
				b1.xor(b2);
			}
			else
				b1.or(b2);
				
			int resultado = 0 ;
		    for(int i = 0 ; i < b1.length() ; i++)
		        if(b1.get(i))
		            resultado |= (1 << i);
		            
		    resultado &= Integer.MAX_VALUE;
			return resultado;
		}
		catch(Exception e){
			log.error(e.toString());
			return 0;
		}
	}
	@SuppressWarnings("unchecked")
	public Boolean eliminarPerfilGruposUsuarios(List<String> usuarios, List<String> grupos,String perfil){
		Boolean resultado=false;
		try{
			HibernateManager hm=new HibernateManager();
			hm.setCriterion(Restrictions.eq("nombreNivel", perfil));
			List<imx_grupo_privilegio> listaGrupos=(List<imx_grupo_privilegio>) hm.list(imx_grupo_privilegio.class);
			
			List<imx_grupo_privilegio> gruposEditados=new ArrayList<imx_grupo_privilegio>();
			for(imx_grupo_privilegio g:listaGrupos){
				if(grupos.indexOf(g.getId().getNombreGrupo())==-1&&g.getId().getTituloAplicacion()!="USR_GRALES"){
					g.setNombreNivel("PERSONALIZADO");
					g.setPrivilegio(privilegiosPerfiles(perfil,g.getPrivilegio(),true));
					gruposEditados.add(g);
				}
			}
			
			List<imx_usuario_privilegio> listaUsuarios=(List<imx_usuario_privilegio>) hm.list(imx_usuario_privilegio.class);
			
			List<imx_usuario_privilegio> usuariosEditados=new ArrayList<imx_usuario_privilegio>();
			for(imx_usuario_privilegio u:listaUsuarios){
				if(usuarios.indexOf(u.getId().getNombreUsuario())==-1&&u.getId().getTituloAplicacion()!="USR_GRALES"){
					u.setNombreNivel("PERSONALIZADO");
					u.setPrivilegio(privilegiosPerfiles(perfil,u.getPrivilegio(),true));
					usuariosEditados.add(u);
				}
			}
			List<Object> datos=new ArrayList<Object>();
			datos.addAll(usuariosEditados);
			datos.addAll(gruposEditados);
			hm.update(datos);
			resultado=true;
			
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public Boolean asignaUsuariosGruposPerfil(List<String> usuarios, List<String> grupos, String perfil){
		Boolean resultado=false;
		try{
			Conjunction criterionsUsuarios = Restrictions.conjunction();
			Disjunction disjunctionUsuarios=Restrictions.disjunction();
			criterionsUsuarios.add(Restrictions.ne("id.tituloAplicacion", "USR_GRALES"));
			for(String us:usuarios)
				disjunctionUsuarios.add(Restrictions.eq("id.nombreUsuario", us));

			Conjunction criterionsGrupos = Restrictions.conjunction();
			Disjunction disjunctionGrupos=Restrictions.disjunction();
			for(String gr:grupos)
				disjunctionGrupos.add(Restrictions.eq("id.nombreGrupo", gr));
			
			criterionsUsuarios.add(disjunctionUsuarios);
			criterionsGrupos.add(disjunctionGrupos);
			HibernateManager hm=new HibernateManager();
			List<Object> datos=new ArrayList<Object>();
			if(usuarios.size()>0){
				hm.setCriterion(criterionsUsuarios);
				List<imx_usuario_privilegio> listaUsuarios=(List<imx_usuario_privilegio>) hm.list(imx_usuario_privilegio.class);
				List<imx_usuario_privilegio> usuariosEditados=new ArrayList<imx_usuario_privilegio>();
				for(imx_usuario_privilegio u:listaUsuarios){
					u.setNombreNivel(perfil);
					u.setPrivilegio(privilegiosPerfiles(perfil,u.getPrivilegio(),false));
					usuariosEditados.add(u);
				}
				datos.addAll(usuariosEditados);
			}
			hm.clear();
			if(grupos.size()>0){
				hm.setCriterion(criterionsGrupos);
				List<imx_grupo_privilegio> listaGrupos=(List<imx_grupo_privilegio>) hm.list(imx_grupo_privilegio.class);
				List<imx_grupo_privilegio> gruposEditados=new ArrayList<imx_grupo_privilegio>();
				for(imx_grupo_privilegio g:listaGrupos){
					g.setNombreNivel(perfil);
					g.setPrivilegio(privilegiosPerfiles(perfil,g.getPrivilegio(),false));
					gruposEditados.add(g);
				}
				datos.addAll(gruposEditados);
			}
			hm.update(datos);
			resultado=true;
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public Boolean perfilVacio(String nombrePerfil){
		Boolean resultado=false;
		try{
			HibernateManager hm=new HibernateManager();
			hm.setCriterion(Restrictions.eq("nombreNivel", nombrePerfil));
			List<imx_usuario_privilegio> u=(List<imx_usuario_privilegio>)hm.list(imx_usuario_privilegio.class);
			List<imx_grupo_privilegio> p=(List<imx_grupo_privilegio>)hm.list(imx_grupo_privilegio.class);
			if(u.size()>0||p.size()>0)
				resultado=false;
			else
				resultado=true;			
		}
		catch(Exception e){
			log.error(e.toString());
		}
		return resultado; 
	}
}
