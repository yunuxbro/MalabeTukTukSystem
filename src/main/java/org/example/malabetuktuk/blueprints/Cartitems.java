package org.example.malabetuktuk.blueprints;

public class Cartitems {
    public class cartitem{
        private Part part;
        private int quantity;

        public cartitem(Part part, int quantity){
            this.part = part;
            this.quantity = quantity;
        }

        public Part getPart(){
            return this.part;
        }
        public int getQuantity(){
            return this.quantity;
        }

        public void setQuantity(int quantity){
            this.quantity = quantity;
        }

        public double getTotalPrice(){
            return part.getPartPrice()*quantity;
        }
    }

}

