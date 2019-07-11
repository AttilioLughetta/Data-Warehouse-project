package ETL;

public class NapoliDB extends DBHandler{


    public  void initMap()
    {
        MyETL.addMap("Exh_Flow_corr", "exh_flow_corrm3");
        MyETL.addMap("MAF_air_flow_rate_by_ECU", "MAF_air_flow_rate_by_ECU_grams_per_second");
        MyETL.addMap("Vehicle_speed_by_ECU", "Vehicle_speed_by_ECU_Km");
        MyETL.addMap("Engine_fuel_rate_by_ECU", "Engine_fuel_rate_by_ECU");
    }
    //Ignorati
    // 28 36 38 (41 42 43 44 45)
    public void initIgnored()
    {
        MyETL.addIgnore(8);
        MyETL.addIgnore(28);
        MyETL.addIgnore(36);
        MyETL.addIgnore(38);
        MyETL.addIgnore(41);
        MyETL.addIgnore(42);
        MyETL.addIgnore(43);
        MyETL.addIgnore(44);
        MyETL.addIgnore(45);
    }

//Obbligatori
//1 2  15 16 17 32 
    public void initMandatory()
    {
        MyETL.addMandatory(1);
        MyETL.addMandatory(2);
        MyETL.addMandatory(15);
        MyETL.addMandatory(16);
        MyETL.addMandatory(17);
        MyETL.addMandatory(32);
    }


    public String specialAttributesHandler( int num)
    {
        switch(num)
        {
            case 1:
                return "timestamp without time zone ";
            case 2:
                return"integer ";
            case 3:
                return"real ";
            case 27:
                return "real";
            case 29:
                return "real";

        }
        
    return "double precision ";
    }




public static String coordinatecoloumn ( int num, String str)
{
    if(num == 15|| num == 16) {
        Float a  = (Float)MyETL.convertCoordinate(str);
        str = a.toString();
        return str;
    } else
        return str;
}

//Controllo consistenza
//3-4-5-6 -> Positivi

}