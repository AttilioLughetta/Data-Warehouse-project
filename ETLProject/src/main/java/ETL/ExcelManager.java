package ETL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public class ExcelManager {
    
    public static FormulaEvaluator eva ;
    
    public static XSSFSheet openSheet(String url, int numero)throws FileNotFoundException, IOException, InvalidFormatException{
        System.out.println( "Opening file" );
        File excel = new File(url);
        //  FileInputStream fis = new FileInputStream(excel);
        XSSFWorkbook book = new XSSFWorkbook(excel);
        XSSFSheet sheet = book.getSheetAt(numero);
        eva = book.getCreationHelper().createFormulaEvaluator();
        return sheet;
    }

    public static void excelToCSV(XSSFSheet sheet, String path, String name, Integer times){
        //HEADER 
      
      List <String> l = new LinkedList<>();
      int size = 0;
      
      //Creating CSV
      CSVPrinter csvPrinter = null;
      try {
            
              BufferedWriter writer = Files.newBufferedWriter(Paths.get(path),StandardOpenOption.APPEND, 
              StandardOpenOption.CREATE);
  
              csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT/*.withHeader((String[])l.toArray())*/);
               
           } catch (Exception e){
                    System.err.println("CSV Not created");
                     return;
           }
        
        Iterator<Row> itr = sheet.iterator();
        if(itr.hasNext()){
            itr.next();
            itr.next();  
        }else{
            System.out.println("Empty Sheet");
            try{
              csvPrinter.close();
            }catch(Exception e)
            {
              e.printStackTrace();
            }
              return;
        }        
        while(itr.hasNext()){
            
            boolean step=false;
            l.clear();
            l.add(times.toString());
            Row riga = itr.next();
            DataFormatter df = new DataFormatter() ;
            String str;
            int num =0 ;
           

               for(int i=0; i<riga.getLastCellNum(); i++) {
                    Cell cell = riga.getCell(i, Row.CREATE_NULL_AS_BLANK);  
                    num++;
                    if(!MyETL.isToIgnore(num)) {
                        if(num==1)
                          {
                            
                            if(cell.getNumericCellValue()==0){
                              System.err.println("Date not added");
                              break;
                            }
                              
                          }
                        if(cell==null||cell.getCellType()==Cell.CELL_TYPE_BLANK)
                          System.err.println("Stepped");
                        else
                        {
                          switch (ExcelManager.eva.evaluateInCell(cell).getCellType()) 
                              {
                              
                              
                              
                              case Cell.CELL_TYPE_BLANK:
                                //if(MyETL.isMandatory(num))
                                    step = true;
                                    System.err.println("Stepped");
                                    break;

                            
                            case Cell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)){    
                                    //Data Case
                                    if (cell.getDateCellValue().toString().trim().length()==0)
                                        {
                                          System.err.println("Stepped");
                                          step = true;
                                          break;
                                        }
                                    java.util.Date d = cell.getDateCellValue();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(d);
                                    c.add(Calendar.DATE,times);
                                    d= c.getTime();
                                    l.add( "'"+"2019"+"-"+(d.getMonth()+1)+ "-"+d.getDate()+" " + d.getHours()+":"+d.getMinutes()+":"+d.getSeconds() +"'");
                                }else{
                                    //Number Case
                                    str = df.formatCellValue(cell).trim();
                                    if(str.length()==0)
                                         step= true;
                                    str=str.replaceAll(",", ".");
                                   l.add(str.toString());
                                }
                                break;

                            case Cell.CELL_TYPE_STRING:
                                if(cell.getStringCellValue().trim().isEmpty())
                                {
                                  System.err.println("Stepped");
                                  step = true;
                                  break;
                                }
                                
                                str = df.formatCellValue(cell);
                                //NapoliDB.
                                if(num == 15|| num == 16)
                                {
                                    Float a  = (Float)MyETL.convertCoordinate(str);
                                    str = a.toString();
                                    l.add(str);
                                }
                                else
                                    l.add("'"+MyETL.normalizzaValore( str ) +"'");
                                break;
                            
                            }
                          }
                        }
                      }
                    
            
            if(!step)
            {
              try {
                if(size==0)
                    size = l.size();
                if(l.size()==size)
                  csvPrinter.printRecord(l);
                else
                  System.err.println("Row  # not added");
        } catch (IOException e) {
          System.err.println("Row not added ");
          e.printStackTrace();
        }
              
            }       
            step= false;             
        }
        try{
          csvPrinter.close();
        }catch(Exception e)
        {
          e.printStackTrace();
        }
          return; 
    
      }

public static void excelToBigCSV(XSSFSheet sheet, String path, String name, int times){
  for(int i=1;i<times;i++)
    ExcelManager.excelToCSV(sheet,path, name, i);

  return;
}

public static void excelToManyCSV(XSSFSheet sheet, String path, String name, int number)
  {
    System.out.println("Creating CSVs");
    String path2 = path.subSequence(0,path.length()-4).toString();
    for( int t=1;t<=number ;t++ )
    {
      path = path2+t+".csv";
      int times = 20;
      //int times = (int) (Math.pow(10, t-1));
      for(int i=1;i<=times;i++)
        ExcelManager.excelToCSV(sheet,path, name, i);
    }
    System.out.println("CSVs Created");
  }



}