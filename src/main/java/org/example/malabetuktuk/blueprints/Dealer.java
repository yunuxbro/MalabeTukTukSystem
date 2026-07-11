package org.example.malabetuktuk.blueprints;

public class Dealer {
    private String DealerId;
    private String DealerName;
    private int DealerNum;
    private String DealerCity;

    public Dealer(String DealerId, String DealerName, int DealerNum, String DealerCity) {
        this.DealerId = DealerId;
        this.DealerName = DealerName;
        this.DealerNum = DealerNum;
        this.DealerCity = DealerCity;
    }

    public String getDealerId(){
        return DealerId;
    }
    public String getDealerName(){
        return DealerName;
    }
    public int getDealerNum(){
        return DealerNum;

    }
    public String getDealerCity(){
        return DealerCity;
    }
    public void setDealerId(String DealerId){
        this.DealerId = DealerId;
    }
    public void setDealerName(String DealerName){
        this.DealerName = DealerName;
    }
    public void setDealerNum(int DealerNum){
        this.DealerNum = DealerNum;
    }
    public void setDealerCity(String DealerCity){
        this.DealerCity = DealerCity;
    }

}
