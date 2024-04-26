import java.io.BufferedWriter;
import java.io.File;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
 public class UploadFile extends HttpServlet {

	private static final Logger log = Logger.getLogger(UploadFile.class); 


	private static final long serialVersionUID = -6530380811766152355L;
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		doPost(req,res);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
			
		String ruta = req.getParameter("ruta");
		File file = new File(ruta);
		
		File fileParent = new File(file.getParent());
		System.out.println(fileParent.getAbsolutePath());
		if(!fileParent.exists()){
			out.println("No exista la estructura, tratando de crear.<br><br>");
			if(fileParent.mkdirs())
				out.println("Estructura creada con éxito.<br><br>");
			else{
				out.println("No se pudo crear estructura... terminando.<br><br>");
				out.close();
				return; 
			}
		}
		else{
			out.println("La estructura de carpetas si existe.<br><br>");
		}
				
		if(!fileParent.canWrite()){
			out.println("No tengo permisos de escritura... terminando <br><br>");
			out.close();
			return; 
		}
		
		if(!fileParent.canRead()){
			out.println("No tengo permisos de lectura... terminando.<br><br>");
			out.close();
			return;
		}
		
		try{
			File fileEjec = new File(fileParent.getAbsolutePath()+System.getProperty("file.separator")+"test.bat");
			System.out.println(fileEjec.getAbsolutePath());
			BufferedWriter bfw = new BufferedWriter(new FileWriter(fileEjec,false));
			bfw.write("echo hola");
			bfw.newLine();
			bfw.flush();
			bfw.close();
			out.println("Tratando de asignar permisos a archivo de prueba<br><br>");
			out.println("Permisos Lectura<br><br>");
			FilePermission perm = new FilePermission(fileEjec.getAbsolutePath(),"read");
			out.println("Permisos Lectura OK<br><br>");
			out.println("Permisos Escritura<br><br>");
			perm = new FilePermission(fileEjec.getAbsolutePath(),"write");
			out.println("Permisos Escritura OK <br><br>");
			out.println("Permisos Ejecucion<br><br>");
			perm = new FilePermission(fileEjec.getAbsolutePath(),"execute");
			out.println("Permisos Ejecucion OK <br><br>");
			out.println("Permisos Borrado<br><br>");
			perm = new FilePermission(fileEjec.getAbsolutePath(),"delete");
			out.println("Permisos Borrado OK<br><br>");
			
			perm = new FilePermission(fileEjec.getAbsolutePath(),"read,write,execute,delete");
			
			out.println("Permisos del archivo prueba: " + perm.getActions()+"<br><br>");
			Process p = Runtime.getRuntime().exec(fileEjec.getAbsolutePath());
			int r = p.waitFor();
			if(r==0)
				out.println("Archivo ejecutado con exito, permisos de ejecucion OK.<br><br>");
			bfw.close();
			
			out.println("Borrando archivo de prueba automatico<br><br>");
			if(fileEjec.delete()){
				out.println("Borrado de archivo OK<br><br>");
			}
			else{
				fileEjec.deleteOnExit();
				out.println("No se pudo borrar el archivo, se borrara al terminar la VM<br><br>");
			}						
		}
		catch(IOException ioe){	log.error(ioe,ioe);

		}
		catch(InterruptedException ie){	log.error(ie,ie);

		}
						
		out.println("Creando archivo especificado por usuario con texto escrito: <br><br>");
		String contenido = req.getParameter("contenido");
		
		try{
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file,true));
			
			bfw.write(contenido);
			bfw.newLine();
			bfw.flush();
			
			bfw.close();	
			
			out.println("<br><br>Se creo el archivo satisfactoriamente");
		}
		catch(IOException ioe){	log.error(ioe,ioe);

		}
		
		
		out.close();
		
		
	}
}
