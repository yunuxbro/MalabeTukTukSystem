package org.example.malabetuktuk.blueprints;

public class Cartitems {
    private Part part;
    private int orderedQuantity;

    public Cartitems(Part part, int orderedQuantity) {
        this.part = part;
        this.orderedQuantity = orderedQuantity;
    }

    public Part getPart() {
        return part;
    }

    public String getPartId() {
        return part.getPartId();
    }

    public String getPartName() {
        return part.getPartName();
    }

    public double getUnitPrice() {
        return part.getPartPrice();
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }
    public String getpartType() {
        return part.getPartType();
    }



    public double getTotalPrice() {
        return part.getPartPrice() * orderedQuantity;
    }


}