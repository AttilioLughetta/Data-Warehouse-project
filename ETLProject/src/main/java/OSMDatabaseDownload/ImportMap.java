/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OSMDatabaseDownload;

import DAO.Concrete.DatabasePostgres;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Utente
 * Importa la mappa contenuta in tool\map.osm nel database
 */
public class ImportMap {
    public static void main(String[] args){
        
        OSMImporter osmimporter=new OSMImporter();
        osmimporter.setDatabase(DatabasePostgres.getDBName());
        osmimporter.setDirectory("tools" + System.getProperty("file.separator") + "osm2pgsql-bin");
        osmimporter.setExecutableName("osm2pgsql.exe");
        osmimporter.setPassword(DatabasePostgres.getPassword());
        osmimporter.setUsername(DatabasePostgres.getUser());
        try {
            osmimporter.importData("target" + System.getProperty("file.separator") + "map.osm");
        } catch (IOException ex) {
            Logger.getLogger(ImportMap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ImportMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
