<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.syc.utils.*,org.apache.log4j.Logger"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type"
			content="text/html; charset=UTF-8" />
		<meta name="version" content="4.0" />
		<title>Fortimax - Administrador</title>
		<script type="text/javascript" src="../js/funcionesInterfaz.js"
			language="javascript">
		</script>
		<script type="text/javascript">
		function onLoadPage(){
			document.getElementById("usuario").focus();
			
			habilitaButton();
			
		}
		
		function habilitaButton(){
			if(da_valor('password').trim()!='' && da_valor('usuario').trim()!='') {
				habilita('accept'); 
			}
			else {
				deshabilita('accept');
			}
		}
		</script>
		<link href="../css/estilo-admin.css" rel="stylesheet" type="text/css"
			media="screen" />
	</head>
	<body onload="onLoadPage()">
		<div id="login">
			<div id="logotipos">
				<img src="../imagenes/logotipo-usuario.png" width="170" height="75" />
			</div>
			<form action="../../login">
				<table width="100%">
					<tr>
						<td valign="top">
							<script type="text/javascript">
								new fadeshow(fadeimages, 553, 150, 0, 5000, 1, "R");
							</script>
						</td>
						<td width="250" valign="top">
							<h1 class="letrerologin">
								Administración
							</h1>
							<div class="campo1">
								Usuario:
								<input name="usuario" type="text" class="campo" id="usuario"
									style="margin-left: 39px;"
									onkeyup="habilitaButton()"
									size="15" />
							</div>
							<div class="campo1">
								Contrase&ntilde;a:
								<input name="password" type="password" class="campo"
									id="password" style="margin-left: 17px;"
									onkeyup="habilitaButton()"
									size="15" />
							</div>
							<input id="Admin" name="Admin" value="true" type="hidden" />
							<%
								Logger log = Logger.getLogger(this.getClass());
								try {
									String invalid = request.getParameter("invalid");
									if (invalid != null && invalid.equalsIgnoreCase("true")) {
										out.println("<i style=\"color: red;text-align: center;margin-left:60px\" >Usuario Invalido</i>");
									}
								} catch (Exception e) {
									log.error(e,e);
								}
							%>
							<div class="campo1" >
								<input  type="image" name="accept" id="accept"
									src="../imagenes/btn-ingresar-disabled.png"
									style="margin-left: 63px;margin-top: 8px" disabled="disabled" />
							</div>
				
						</td>
					</tr>
				</table>
			</form>
			<span class="pie">D.R. &copy;2005-2008 S&amp;C Constructores
				de Sistemas S.A. de C.V.| www.fortimax.com</span>
		</div>
	</body>
</html>