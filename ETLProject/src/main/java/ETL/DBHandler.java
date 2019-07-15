package ETL;

public  abstract class DBHandler{
    
    
    public  void initAll(){
        initMap();
        initIgnored();
        initMandatory();
        }
    
    public  abstract void initMap();
    public abstract void initIgnored();
    public abstract String specialAttributesHandler(int n);
    public abstract void initMandatory();

}