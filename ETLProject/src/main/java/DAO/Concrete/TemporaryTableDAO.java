/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO.Concrete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giusy
 */
public class TemporaryTableDAO{
    
    private static Connection connection=null;

    public TemporaryTableDAO() {
        try {
            connection=DatabasePostgres.buildConnection();
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(TemporaryTableDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean dropAndCreateTable(String name){    
        Statement stmt;
        try {
            stmt = connection.createStatement();
            String sql = "DROP TABLE "+ name+"; CREATE TABLE "+name+" ();"; 
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TemporaryTableDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public boolean addColumn(String tablename,String column,String type){
        try {
            String sql= "ALTER TABLE "+tablename+" ADD "+column+" "+type;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TemporaryTableDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public boolean insertValues(String tablename,Queue<String> parameters){
        String sql="INSERT INTO "+tablename+" VALUES ( ";
        PreparedStatement stmt = null ;
        for(String s:parameters){
            sql=sql+s+",";
        }
        sql=sql.substring(0, sql.length()-1)+" )";        
        try {
            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TemporaryTableDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }


    public static boolean csvToTable(String name, String path)
    {
        try{
            Statement stmt = connection.createStatement();
            String sql = "COPY "+name+" FROM '"+path+"'WITH (format csv, header);";
            stmt.executeUpdate(sql);
            stmt.close();
            }
        catch(Exception e)
            {
                System.err.println("Row not added from CSV");
                e.printStackTrace();
                return false;
            }
            return true;
    }
}