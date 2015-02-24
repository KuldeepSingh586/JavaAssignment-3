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
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Kuldeep
 */
@WebServlet("/product")
public class ProductServlets extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method. doGet Method took two arguments
     * It will select data from product table Also Handle Exception
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
     * Handles the HTTP <code>POST</code> method.doGet Method took two arguments
     * It will insert data from product table Also Handle Exception
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
            if (keyValues.contains("ProductID") && keyValues.contains("name") && keyValues.contains("description")
                    && keyValues.contains("quantity")) {
                String ProductID = request.getParameter("ProductID");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                doUpdate("INSERT INTO product (ProductID,name,description,quantity) VALUES (?, ?, ?, ?)", ProductID, name, description, quantity);

            } else {
                output.println("Error: Not data found for this input. Please use a URL of the form /servlet?name=XYZ&age=XYZ");
            }

        } catch (IOException ex) {
            System.err.println("Input Output Issue in doPost Method: " + ex.getMessage());
        }

    }

    /**
     * doPut Method took two Arguments and This method Will update Entries in
     * product Table. This will also catch SQLException and Display an error
     * message.
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {

        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            if (keySet.contains("ProductID") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {
                String ProductID = request.getParameter("ProductID");
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                String quantity = request.getParameter("quantity");
                doUpdate("update product set ProductID = ?, name = ?, description = ?, quantity = ? where ProductID = ?", ProductID, name, description, quantity, ProductID);
            } else {
                out.println("Error: Not data found for this input. Please use a URL of the form /products?id=xx&name=XXX&description=XXX&quantity=xx");
            }
        } catch (IOException ex) {
            System.out.println("Error in writing output: " + ex.getMessage());
        }
    }

    /**
     * doDelete Method took two Arguments and This method Will delete Entries
     * from product Table This will also catch SQLException and Display an error
     * message
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        try (PrintWriter out = response.getWriter()) {
            Connection conn = getConnection();
            if (keySet.contains("ProductID")) {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `product` WHERE `ProductID`=" + request.getParameter("ProductID"));
                try {
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    System.err.println("SQL Exception Error in Update prepared Statement: " + ex.getMessage());
                    out.println("Error in deleting entry.");
                    response.setStatus(500);
                }
            } else {
                out.println("Error: Not enough data in table to delete");
                response.setStatus(500);
            }
        } catch (SQLException ex) {
            System.err.println("SQL Exception Error: " + ex.getMessage());
        }
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

    /**
     * resultMethod accepts two arguments It executes the Query get ProductID,
     * name, description, quantity
     *
     * @param query
     * @param params
     * @throws SQLException
     * @return
     */
    private String resultMethod(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        String jsonString="";
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
//            out.println(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            JSONObject obj = new JSONObject();
            JSONArray list1 = new JSONArray();
             
            while (rs.next()) {

                sb.append(String.format("%s\t%s\t%s\t%s\n", rs.getInt("ProductID"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
                
//                Map m1 = new LinkedHashMap();
//                Map m2 = new HashMap();
//                List l1 = new LinkedList();
//
//                m1.put("ProductID", rs.getInt("ProductID"));
//                m1.put("name", rs.getString("name"));
//                m1.put("description", rs.getString("description"));
//                m1.put("quantity", rs.getInt("quantity"));
//                l1.add(m1);
//                
//               jsonString =l1.toString();
               
            }
            
            
        } catch (SQLException ex) {
            System.err.println("SQL Exception Error: " + ex.getMessage());
        }
        return sb.toString();
    }

    /**
     * doUpdate Method accepts two arguments Update the entries in the table
     * 'product'
     *
     * @param query
     * @param params
     * @return
     */
    private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("SQL EXception in doUpdate Method" + ex.getMessage());
        }
        return numChanges;
    }

}
