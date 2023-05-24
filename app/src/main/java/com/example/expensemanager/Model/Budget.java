package com.example.expensemanager.Model;

public class Budget {
    private String id;
    private int limitAmount;
    private String type;
    private String dateStart;
    private String dateEnd;

    public Budget(){}
    public Budget(String id, int limitAmount, String type, String dateStart, String dateEnd){
        this.id = id;
        this.limitAmount = limitAmount;
        this.type = type;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public int getLimitAmount(){
        return limitAmount;
    }
    public void setLimitAmount(int limitAmount){
        this.limitAmount = limitAmount;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getDateStart(){
        return dateStart;
    }
    public void setDateStart(String dateStart){
        this.dateStart = dateStart;
    }
    public String getDateEnd(){
        return dateEnd;
    }
    public void setDateEnd(String dateEnd){
        this.dateEnd = dateEnd;
    }
}
