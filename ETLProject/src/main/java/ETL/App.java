package ETL;
public class App {
    
    private static String path ="./Tabella.xlsx";
    private static String pathCSV = "C:/Program Files/PostgreSQL/11/data/a.csv";
    private static int page = 0;

public static void main( String[] args ) throws Exception{
       
       NapoliDB Ndb = new NapoliDB();
       TableHandler.CreateTable("Tabella",path,page,Ndb,pathCSV);
        
    }

}  
