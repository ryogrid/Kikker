package jp.ryo.informationPump.server.data;

public class SortBoxForWebData {
    public StoreBoxForWebData webdata;
    public Double simirality;
    
    public SortBoxForWebData(StoreBoxForWebData webdata,Double simirality) {
        this.webdata = webdata;
        this.simirality = simirality;
    }
}
