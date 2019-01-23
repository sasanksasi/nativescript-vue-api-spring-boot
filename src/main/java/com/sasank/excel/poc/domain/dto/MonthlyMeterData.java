package com.sasank.excel.poc.domain.dto;

import java.util.Set;


public class MonthlyMeterData {
    private String region;
    private Set<MonthlyCount> monthlyCount;

    public MonthlyMeterData() {
    }

    public MonthlyMeterData(String region, Set<MonthlyCount> monthlyCount) {
        this.region = region;
        this.monthlyCount = monthlyCount;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Set<MonthlyCount> getMonthlyCount() {
        return monthlyCount;
    }

    public void setMonthlyCount(Set<MonthlyCount> monthlyCount) {
        this.monthlyCount = monthlyCount;
    }

    @Override
    public int hashCode() {
        return region.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MonthlyMeterData)
           return  ((MonthlyMeterData) obj).getRegion().equals(this.getRegion());
        return false;
    }
}
