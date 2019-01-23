package com.sasank.excel.poc.domain.dto;

public class MonthlyCount {
    private String month;
    private double count;

    public MonthlyCount() {

    }

    public MonthlyCount(String month, double count) {
        this.month = month;
        this.count = count;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    @Override
    public int hashCode() {
        return month.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MonthlyCount)
            return  ((MonthlyCount) obj).getMonth().equals(this.getMonth());
        return false;
    }
}
