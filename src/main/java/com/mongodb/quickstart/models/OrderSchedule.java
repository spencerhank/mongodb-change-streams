package com.mongodb.quickstart.models;

import org.bson.codecs.pojo.annotations.BsonCreator;

import java.util.Objects;

public class OrderSchedule {
    private String scheduleNumber;
    private int quantity;
    private String uom;

    @BsonCreator
    public OrderSchedule(){}
    public OrderSchedule(String scheduleNumber, int quantity, String uom) {
        this.scheduleNumber = scheduleNumber;
        this.quantity = quantity;
        this.uom = uom;
    }

    // Getters and Setters

    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderSchedule that = (OrderSchedule) o;
        return quantity == that.quantity && Objects.equals(scheduleNumber, that.scheduleNumber) && Objects.equals(uom, that.uom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleNumber, quantity, uom);
    }

    @Override
    public String toString() {
        return "OrderSchedule{" +
                "scheduleNumber='" + scheduleNumber + '\'' +
                ", quantity=" + quantity +
                ", uom='" + uom + '\'' +
                '}';
    }
}
