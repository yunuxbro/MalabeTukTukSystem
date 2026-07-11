package org.example.malabetuktuk.blueprints;

public class Part {
    private String partId;
    private String partName;
    private String partBrand;
    private double partPrice;
    private int partQuantity;
    private String partType;
    private String addedDate;
    private String imagePath;

    public Part(String partId, String partName, String partBrand, double partPrice, int partQuantity, String partType, String addedDate, String imagePath) {
        this.partId = partId;
        this.partName = partName;
        this.partBrand = partBrand;
        this.partPrice = partPrice;
        this.partQuantity = partQuantity;
        this.partType = partType;
        this.addedDate = addedDate;
        this.imagePath = imagePath;
    }

    public String getPartId() {
        return partId;
    }
    public void setPartId(String partId) {
        this.partId = partId;
    }
    public String getPartName() {
        return this.partName;
    }
    public void setPartName(String partName) {
        this.partName = partName;
    }
    public String getPartBrand() {
        return this.partBrand;
    }
    public void setPartBrand(String partBrand) {
        this.partBrand = partBrand;
    }
    public double getPartPrice() {
        return this.partPrice;
    }
    public void setPartPrice(double partPrice) {
        this.partPrice = partPrice;
    }
    public int getPartQuantity() {
        return this.partQuantity;
    }
    public void setPartQuantity(int partQuantity) {
        this.partQuantity = partQuantity;
    }
    public String getPartType() {
        return this.partType;
    }
    public void setPartType(String partType) {
        this.partType = partType;
    }
    public String getAddedDate() {
        return this.addedDate;
    }
    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }
    public String getImagePath() {
        return this.imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}