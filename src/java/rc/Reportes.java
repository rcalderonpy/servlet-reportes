/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 *
 * @author Rodrigo
 */
@WebServlet(urlPatterns = {"/reportes"})
public class Reportes extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*ASI SE OBTIENEN LOS PARAMETROS*/
        String plantilla = request.getParameter("plantilla"); //Ej. simons.jrxml
        String archivo_nombre = request.getParameter("archivo_nombre");
        String mes=request.getParameter("mes");//A PARTIR DE ESTE PARAMETRO LOS USO PARA EL JASPER
        String dia=request.getParameter("dia");
        String ano=request.getParameter("ano");
        String fecha=request.getParameter("fecha");
        String cliente_id=request.getParameter("cliente_id");        
        String id_factura=request.getParameter("id_factura");
        String id_documento=request.getParameter("id_documento");
        String user=request.getParameter("user_id");
        
        System.out.println(dia);
        System.out.println(mes);
        System.out.println(ano);
        System.out.println(cliente_id);
        System.out.println(user);
        System.out.println(fecha);
        System.out.println(id_documento);
        
        if (plantilla!=null && plantilla.trim().length()>0) {
            /*PREPARO LA PLANTILLA*/
        //String path_reporte = "/opt/tomcat7/webapps/jasper_plantillas/" + plantilla;
        String path_reporte = "C:\\jasper_plantillas\\" + plantilla;
        /*VERIFICO QUE FORMATO SE REQUIERE, PDF o EXCEL...*/
        if (archivo_nombre.contains(".pdf")) {
            response.setHeader("Content-Disposition", "inline; filename=\"" + archivo_nombre + "\"");
            response.setContentType("application/pdf");
        }
        if (archivo_nombre.contains(".xls")) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + archivo_nombre);
        }
        /*CONECTO CON LA BASE DE DATOS*/
        try {
            Connection con;
            
            //String host = "jdbc:postgresql://localhost:5432/simons";

            String host = "jdbc:mysql://localhost:3306/estucont?autoReconnect=true&useSSL=false";                       
            String uname = "root";
            String upass = "simons123";
            
            //String host = "jdbc:mysql://138.197.220.164:3306/estucont?autoReconnect=true&useSSL=false";           
            //String uname = "rodrigo";
            //String upass = "simons";

            Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("org.postgresql.Driver");

            con = DriverManager.getConnection(host, uname, upass);
            
            JasperReport jr = JasperCompileManager.compileReport(path_reporte);
            Map<String, Object> parameters = new HashMap<String, Object>();
            
            /*AQUI ES DONDE SE SETEAN LOS PARAMETROS PARA EL REPORTE*/
            /*Es importante castear al tipo de dato del parametro que se definió en ireport*/
            
         
            
             if (   plantilla.contains("LibroVentas.jrxml") || 
                    plantilla.contains("LibroVentasOriginal.jrxml") ||
                    plantilla.contains("LibroCompras.jrxml") || 
                    plantilla.contains("LibroComprasOriginal.jrxml") || 
                    plantilla.contains("LibroEgresos.jrxml") || 
                    plantilla.contains("LibroIngresos.jrxml") || 
                    plantilla.contains("RetencionesRecibidas.jrxml")|| 
                    plantilla.contains("reciboCobro.jrxml")|| 
                    plantilla.contains("reciboPago.jrxml")
                ) {
                 parameters.put("cliente_id", Integer.valueOf(cliente_id));
                 parameters.put("mes", Integer.valueOf(mes));
                 parameters.put("ano", Integer.valueOf(ano));
             }
             
             if ( plantilla.contains("LibroMayor.jrxml" ) || 
                    plantilla.contains("ResumenIRPC.jrxml") ||
                     plantilla.contains("BalanceAnalitico.jrxml") ||
                    plantilla.contains("ResultadoAnalitico.jrxml") ||
                    plantilla.contains("LibroDiario.jrxml")
                ) {
                 parameters.put("cliente_id", Integer.valueOf(cliente_id));                 
                 parameters.put("ano", Integer.valueOf(ano));
             }
             
             if ( plantilla.contains("ListadoClientes.jrxml" ) || 
                    plantilla.contains("ListadoProveedores.jrxml")
                ) {
                 parameters.put("cliente_id", Integer.valueOf(cliente_id));                 
                 parameters.put("fecha", fecha);
             }
             
             if (plantilla.contains("Factura.jrxml")) {                 
                 parameters.put("id_factura", Integer.valueOf(id_factura));                 
             }
             
             if (plantilla.contains("ResumenIva.jrxml") ) {
                 parameters.put("cliente_id", Integer.valueOf(cliente_id));
                 parameters.put("mes", Integer.valueOf(mes));
                 parameters.put("ano", Integer.valueOf(ano));
             }
             
             if (plantilla.contains("ResumenCaja.jrxml")) {                 
                 parameters.put("mes", Integer.valueOf(mes));
                 parameters.put("ano", Integer.valueOf(ano));  
                 parameters.put("dia", Integer.valueOf(dia));
                 parameters.put("user", Integer.valueOf(user));  
             }
             
             if (plantilla.contains("Recibo.jrxml")) {                 
                 parameters.put("id_documento", Integer.valueOf(id_documento)); 
             }
                 
             if (plantilla.contains("EstadoCuentaCliente.jrxml") ||
                 plantilla.contains("EstadoCuentaProveedor.jrxml") ||
                 plantilla.contains("EstadoCuentaClienteFecha.jrxml") ||
                 plantilla.contains("EstadoCuentaProveedorFecha.jrxml")) {   
                 parameters.put("fecha", fecha);
                 parameters.put("cliente_id", Integer.valueOf(cliente_id));
                 parameters.put("id_documento", Integer.valueOf(id_documento)); 
             }
             
             
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, con);

            //JasperExportManager.exportReportToPdfFile(jasperPrint, "application.pdf");
            /*PDF*/
            if (archivo_nombre.contains(".pdf")) {
                OutputStream outStream = response.getOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            }
            /*EXCEL*/
            if (archivo_nombre.contains(".xls")) {
                File xlsx = new File(archivo_nombre);
                JRXlsxExporter Xlsxexporter = new JRXlsxExporter();
                Xlsxexporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, true);
                Xlsxexporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                Xlsxexporter.setParameter(JRExporterParameter.OUTPUT_FILE,
                        xlsx);
                Xlsxexporter.exportReport();
                OutputStream out = response.getOutputStream();
                //
                FileInputStream in = new FileInputStream(xlsx);
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        }else{
            System.out.println("Faltan parametros!!! ");
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);           
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
