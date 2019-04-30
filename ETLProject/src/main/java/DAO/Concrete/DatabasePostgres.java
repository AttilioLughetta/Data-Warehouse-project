/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Utente
 */
package DAO.Concrete;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabasePostgres {
    private static Connection connection=null;
    private static String user="postgres";
    private static String password="bozzo";
    private static String DBName="postgres";
    
    public static String getUser(){
        return user;
    }
    
    public static String getPassword(){
        return password;
    }
    
    public static String getDBName(){
        return DBName;
    }
    
    public static Connection buildConnection() throws InstantiationException, IllegalAccessException, SQLException{
        return buildConnection(user,password,DBName);
    }
    
    public static Connection buildConnection(String u,String p,String db) throws InstantiationException, IllegalAccessException, SQLException{
        if(connection == null){
            try {
                user=u;
                password=p;
                DBName=db;
                String driverName="org.postgresql.Driver";
                //URL di connessione
                String databaseURL= "jdbc:postgresql://localhost:5432/"+db;
                //caricamento della classe nel driver
                Class driverClass= Class.forName(driverName);
                //creazione dell'istanza nel driver
                Driver driver= (Driver) driverClass.newInstance();
                connection= DriverManager.getConnection(databaseURL,u,p);
                createExtensions();
                System.out.println("Connessione stabilita");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return connection;
    }
    
    private static void createExtensions(){
        try{
            Statement st=connection.createStatement();
            st.executeUpdate("CREATE EXTENSION IF NOT EXISTS \"postgis\";\n" +
                        "CREATE EXTENSION IF NOT EXISTS \"fuzzystrmatch\";\n" +
                        "CREATE EXTENSION IF NOT EXISTS \"pgrouting\";\n" +
                        "CREATE EXTENSION IF NOT EXISTS \"plpgsql\";\n" +
                        "CREATE EXTENSION IF NOT EXISTS \"postgis_tiger_geocoder\";\n" +
                        "CREATE EXTENSION IF NOT EXISTS \"postgis_topology\";\n" +
                        "CREATE EXTENSION IF NOT EXISTS \"postgres_fdw\";");
        } catch (SQLException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        try {
            buildConnection();
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(DatabasePostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

