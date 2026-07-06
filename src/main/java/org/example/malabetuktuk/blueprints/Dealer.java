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

    public String getDealerId() {
        return this.DealerId;
    }
    public void setDealerId(String DealerId) {
        this.DealerId = DealerId;
    }
    public String getDealerName() {
        return this.DealerName;
    }
    public void setDealerName(String DealerName) {
        this.DealerName = DealerName;
    }
    public int getDealerNum() {
        return this.DealerNum;
    }
    public void setDealerNum(int DealerNum) {
        this.DealerNum = DealerNum;
    }
    public String getDealerCity() {
        return this.DealerCity;
    }
    public void setDealerCity(String DealerCity) {
        this.DealerCity = DealerCity;
    }
}
