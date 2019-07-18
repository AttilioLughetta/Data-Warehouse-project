/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO.Concrete;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
            String sql = "DROP TABLE IF EXISTS "+ name+"; CREATE TABLE "+name+" ();"; 
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
    public static boolean dataDispatch()
    {   
        boolean b = false;
        CallableStatement cstmt;
        try {
            cstmt = connection.prepareCall("{? = CALL finish_inserting()}");
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.executeUpdate();
            b = cstmt.getBoolean(1);
        } catch (SQLException e) {
            System.err.println("impossible to execute finish_inserting() ");
            e.printStackTrace();
        }
        return b;
    }
    public static boolean enableIndexes(boolean flag)
    {   
        boolean b = false;
        CallableStatement cstmt;
        try {
            cstmt = connection.prepareCall("{? = CALL enable_indexes(?)}");
            cstmt.setBoolean(2, flag);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.executeUpdate();
            b = cstmt.getBoolean(1);
        } catch (SQLException e) {
            System.err.println("impossible to execute enable_indexes() ");
            e.printStackTrace();
        }
        return b;
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
        long endTime, startTime, totTime;
        try{
            Statement stmt = connection.createStatement();
            startTime = System.currentTimeMillis();
            String sql = "COPY "+name+" FROM '"+path+"'WITH (format csv, header);";
            stmt.executeUpdate(sql);
            stmt.close();
            dataDispatch();
            endTime = System.currentTimeMillis();
            totTime = endTime - startTime;
            System.out.println("Tempo totale impiegato "+ totTime);
            }
        catch(Exception e)
            {
                System.err.println("CSV not added to DB");
                e.printStackTrace();
                return false;
            }
            return true;
    }

    public static boolean manyCsvToTable(String name, int number, String path)
    {
        String path2 = path.subSequence(0,path.length()-4).toString(); 
        for(int i= 1; i<=number;i++)
            {
<<<<<<< HEAD
                System.out.println("Inserting block "+i+" of "+number);
=======
                System.out.println("Inserimento file csv numero "+i);
>>>>>>> 14ab5f530ec3a4899f1a1b19d7e1b16dd1ef355f
                path = path2+i+".csv";
                if(!csvToTable(name, path))
                    return false;
            }
        return true;
        
    }
}
