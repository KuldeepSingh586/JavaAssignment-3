/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import DataBaseConnection.Credentials;
import static DataBaseConnection.Credentials.getConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Kuldeep
 */
@WebServlet(name = "ProductServlets", urlPatterns = {"/ProductServlets"})
public class ProductServlets extends HttpServlet {

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
        response.setHeader("Content-Type", "text/plain-text");
        try {
            PrintWriter output = response.getWriter();
            String query = "SELECT * FROM product;";
            if (!request.getParameterNames().hasMoreElements()) {
                output.println(resultMethod(query));
            } else {
                int id = Integer.parseInt(request.getParameter("ProductID"));
                output.println(resultMethod("SELECT * FROM product WHERE ProductID= ?", String.valueOf(id)));
            }

        } catch (IOException ex) {
            System.err.println("Input output Exception: " + ex.getMessage());
        }
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
        Set<String> keyValues = request.getParameterMap().keySet();

        try {
            PrintWriter output = response.getWriter();
            if (keyValues.contains("ProductID") && keyValues.contains("name") && keyValues.contains("description") && keyValues.contains("quantity")) {
                String ProductID = request.getParameter("ProductID");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                resultMethod("INSERT INTO product (ProductID,name,description,quantity) VALUES (?, ?, ?, ?)", ProductID, name, description, quantity);

            } else {
                output.println("Error: Not data found for this input. Please use a URL of the form /servlet?name=XYZ&age=XYZ");
            }

        } catch (IOException ex) {
            System.err.println("Input Output Issue: " + ex.getMessage());
        }

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        int changes = 0;
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("ProductID") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                String ProductID = request.getParameter("ProductID");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                resultMethod("update product set ProductID = ?, name = ?, description = ?, quantity = ? where ProductID = ?", ProductID, name, description, quantity, ProductID);
                if (changes > 0) {
                    response.sendRedirect("http://localhost:8080/CPD4414-Assignment3/products?id=" + ProductID);
                } else {
                    response.setStatus(500);
                }
            } else {
                out.println("Error: Not data found for this input. Please use a URL of the form /products?id=xx&name=XXX&description=XXX&quantity=xx");
            }
        } catch (IOException ex) {
            System.out.println("Error in writing output: " + ex.getMessage());
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
       
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

    private String resultMethod(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("%s\t%s\t%s\t%s\n", rs.getInt("ProductID"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlets.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

}
