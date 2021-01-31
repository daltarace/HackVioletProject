package com.example.apptesting;

public class Item {
    private String name;
    private String expirationDate;
    public Item(String name, String expirationDate){
        this();
        this.name = name;
        this.expirationDate = expirationDate;
    }
    public Item(){
    }

    public String getName() {
        return name;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}
