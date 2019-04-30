package ETL;

import DAO.Concrete.TemporaryTableDAO;

import java.sql.Statement;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import java.sql.Connection;


public  class TableHandler{
    
    private static TemporaryTableDAO tt=null;

    public static void CreateTable(String name, String path, int page, DBHandler db, String pathCSV) throws Exception
    {

        db.initAll();
        
        XSSFSheet s= null;
        s= ExcelManager.openSheet(path, page);
        tt=new TemporaryTableDAO();
        tt.dropAndCreateTable(name);
        

        //ExcelManager.excelToCSV(s,pathCSV, name);
        System.out.println("Table Created");
        addColoumn(s,name,db);
        System.out.println("Coloumns Added, filling Table");
        if(tt.csvToTable(name,pathCSV))
            System.out.println("Table correctly filled");
        //fieldingTable(s, c, name);
        
       
        
        
    }


    public static void addColoumn(XSSFSheet sheet, String name, DBHandler db){
        String tipo = " VARCHAR(50)";     
        Iterator<Row> itr = sheet.iterator();
        
        if(itr.hasNext()){
            Row riga = itr.next();
            itr.next();
            Row tmpr = itr.next();
            int num = 0;
                //itero le colonne
            Iterator<Cell> Citr= riga.cellIterator();
            Iterator<Cell> tmpc = tmpr.cellIterator();
            while(Citr.hasNext()){
                    if(!Citr.hasNext()){
                        System.err.println("Citr");
                        System.exit(104);
                    }                        
                    if(!tmpc.hasNext()){
                        System.err.println("tmpc");
                        System.exit(104);
                    }
                    Cell cell = Citr.next();
                    Cell tmpCell = tmpc.next();
                    num++;
                    if(!MyETL.isToIgnore(num)){
                        tipo = db.specialAttributesHandler(num);
                        String value = MyETL.normalizzaValore(cell.getStringCellValue());
                        value = MyETL.nominaColonna(value);
                        tt.addColumn(name, value, tipo);
                    }         
            }
        }
    }

    public static void fieldingTable(XSSFSheet sheet, String name)throws Exception{
        int numx=0;
        Iterator<Row> itr = sheet.iterator();
        if(itr.hasNext()){
            itr.next();
            itr.next();  
        }else{
            System.out.println("Empty Sheet");
            return;
        }        
        while(itr.hasNext()){
            
            boolean step=false;
            Queue<String> parameters=new LinkedList<>();
            
            Row riga = itr.next();
            DataFormatter df = new DataFormatter() ;
            String str=null;
            Iterator<Cell> Citr= riga.cellIterator();
            int num =0 ;
            numx++;
            while(Citr.hasNext()&& !step){
                Cell cell = Citr.next();
                num++;
                if(!MyETL.isToIgnore(num)) {

                    switch (ExcelManager.eva.evaluateInCell(cell).getCellType()) {

                        case Cell.CELL_TYPE_BLANK:
                            if(MyETL.isMandatory(num))
                                step = true;
                            break;

                        case Cell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)){    
                                //Data Case
                                java.util.Date d = cell.getDateCellValue();
                                parameters.add("'"+"2019"+"-"+d.getMonth()+ "-15"+" " + d.getHours()+":"+d.getMinutes()+":"+d.getSeconds() +"'");
                            }else{
                                //Number Case
                                str = df.formatCellValue(cell).trim();
                                if(str.length()==0)
                                     step= true;
                                str=str.replaceAll(",", ".");
                                parameters.add(str.toString());
                            }
                            break;

                        case Cell.CELL_TYPE_STRING:
                            //qui
                            str = df.formatCellValue(cell);
                            parameters.add("'"+MyETL.normalizzaValore( str ) +"'");
                            break;

                    }

                }
            }
            
            if(!step){
                if(!tt.insertValues(name, parameters)){
                    System.out.println("Row not added  : #"+numx/*sql*/);
                }
                step= false;
            }                
        }
    }

    public static boolean csvToTable(String name, String path, Connection c)
    {
        try{
            Statement stmt = c.createStatement();
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
