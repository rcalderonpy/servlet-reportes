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
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

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
        String anho=request.getParameter("anho");
        String fecha=request.getParameter("fecha");
        String cliente_id=request.getParameter("cliente_id");        
        String id_factura=request.getParameter("id_factura");
        String id_documento=request.getParameter("id_documento");
        String fdesde=request.getParameter("fdesde");
        String fhasta=request.getParameter("fhasta");
        String user=request.getParameter("user_id");
        String sucursal=request.getParameter("sucursal");
        String renta_id=request.getParameter("renta_id");        
        String id=request.getParameter("id");
        String empresa_id=request.getParameter("empresa_id");
        String estudio_id=request.getParameter("estudio_id");
        String periodo=request.getParameter("periodo");
        String reporte_id=request.getParameter("reporte_id");    

        
        if (plantilla!=null && plantilla.trim().length()>0) {
            /*PREPARO LA PLANTILLA*/
            String path_reporte = "/usr/local/tomcat/webapps/jasper_plantillas/" + plantilla;
            //String path_reporte = "C:\\jasper_plantillas\\" + plantilla;
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

                String host = "";
                String uname = System.getenv("DATABASE_USER");
                String upass = System.getenv("DATABASE_PASSWORD");;
                StringBuilder hostSb = new StringBuilder();
                hostSb.append("jdbc:mysql://").append(System.getenv("DATABASE_HOST")).append(":").append(System.getenv("DATABASE_PORT"));
                if ( plantilla.contains("rh_" )) {
                    host = hostSb.append("/pypoldb?ssl-mode=REQUIRED").toString();                
                } else if(plantilla.contains("sf_" )){
                    host = hostSb.append("/factura?ssl-mode=REQUIRED").toString();                
                } else if(plantilla.contains("admin_" )){
                    host = hostSb.append("/admin_back?ssl-mode=REQUIRED").toString();                    
                } else {
                    host = hostSb.append("/estucont?ssl-mode=REQUIRED").toString();                
                }

                Class.forName("com.mysql.jdbc.Driver");
                //Class.forName("org.postgresql.Driver");

                con = DriverManager.getConnection(host, uname, upass);

                JasperReport jr = JasperCompileManager.compileReport(path_reporte);
                Map<String, Object> parameters = new HashMap<String, Object>();

                /*AQUI ES DONDE SE SETEAN LOS PARAMETROS PARA EL REPORTE*/
                /*Es importante castear al tipo de dato del parametro que se definió en ireport*/


                if(cliente_id != null) parameters.put("cliente_id", Integer.valueOf(cliente_id));
                if(estudio_id != null) parameters.put("estudio_id", Integer.valueOf(estudio_id));
                if(empresa_id != null) parameters.put("empresa_id", Integer.valueOf(empresa_id));
                if(renta_id != null) parameters.put("renta_id", Integer.valueOf(renta_id));
                if(sucursal != null) parameters.put("sucursal_id", Integer.valueOf(sucursal));
                if(mes != null) parameters.put("mes", Integer.valueOf(mes));
                if(ano != null) parameters.put("ano", Integer.valueOf(ano));
                if(anho != null) parameters.put("anho", Integer.valueOf(anho));
                if(dia != null) parameters.put("dia", Integer.valueOf(dia));
                if(user != null) parameters.put("user", Integer.valueOf(user));
                if(fdesde != null) parameters.put("inicioIrp", fdesde);
                if(fhasta != null) parameters.put("inicioIrp", fhasta);
                if(fecha != null) parameters.put("fecha", fecha);
                if(id_factura != null) parameters.put("id_factura", Integer.valueOf(id_factura));
                if(id_documento != null) parameters.put("id_documento", Integer.valueOf(id_documento));
                if(id != null) parameters.put("id", Integer.valueOf(id));
                if(periodo != null) parameters.put("periodo", periodo);
                if(reporte_id != null) parameters.put("reporte_id", reporte_id);
                
                System.out.println("***************************");
                System.out.println(plantilla);
                System.out.println("***************************");
                System.out.println(parameters);

                //System.out.println(System.getenv("LOGNAME"));
                //System.out.println(request.toString());    



                 java.util.Locale locale=new Locale("es", "PY");
                 parameters.put( JRParameter.REPORT_LOCALE, locale);

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
        System.out.println("ingresó en el método Get");
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
