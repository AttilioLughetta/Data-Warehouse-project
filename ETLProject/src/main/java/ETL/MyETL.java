package ETL;

import static java.lang.Math.pow;
import java.util.HashMap;
import java.util.HashSet;

public class MyETL{
    private static HashMap<String,String> cMap = new HashMap<String, String>();
    private static HashSet<Integer> ignored = new HashSet<>();
    private static HashSet<Integer> mandatory = new HashSet<>();
    
    public static void addMap(String k, String v){
        cMap.put(k, v);
    }

    public static void addIgnore(int i){
        ignored.add(i);
    }

    public static void addMandatory(int i){
        mandatory.add(i);
    }

    public static String normalizzaValore(String value){
        if(value== null)
            System.out.println("Sto normalizzando una stringa vuota");
        value = value.replaceAll("-", "");
        value = value.replaceAll(" ","_");
        value = value.replaceAll("'", "_");
        value = value.replaceAll("\\.","");
        value = value.replaceAll("/","_");
        value = value.replaceAll("\\(","_");
        value = value.replaceAll("\\)","_");
        return value;
    }

    public static boolean isMandatory(int i){
        return mandatory.contains(i);
    }

    public static boolean isToIgnore(int i){
        return ignored.contains(i);
    }

    public static String nominaColonna(String value){
        if(!cMap.isEmpty()){
            String isIn = cMap.get(value);
            if (isIn!= null){
                cMap.remove(value); 
                value = isIn;                
            }
        }    
        return value;
    }
    
    public static float convertCoordinate(String coordinate){
       float degree=0;
       coordinate=coordinate.substring(1);
       System.out.println(coordinate);
       String[] values = coordinate.split("\\.");
       for(int i=0;i<values.length-1;i++){
           degree=(float) (degree+(Float.parseFloat(values[i])/pow(60,i)));
        }
       return degree;
    }
}