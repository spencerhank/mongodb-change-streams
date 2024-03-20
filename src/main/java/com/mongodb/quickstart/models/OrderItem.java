package com.mongodb.quickstart.models;

import org.bson.codecs.pojo.annotations.BsonCreator;

import java.util.List;
import java.util.Objects;

public class OrderItem {
    private String item;
    private String materialType;
    private String itemType;
    private String itemDescription;
    private List<OrderSchedule> orderSchedule;

    @BsonCreator
    public OrderItem() {

    }
    public OrderItem(String item, String materialType, String itemType, String itemDescription, List<OrderSchedule> orderSchedule) {
        this.item = item;
        this.materialType = materialType;
        this.itemType = itemType;
        this.itemDescription = itemDescription;
        this.orderSchedule = orderSchedule;
    }

    // Getters and Setters

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public List<OrderSchedule> getOrderSchedule() {
        return orderSchedule;
    }

    public void setOrderSchedule(List<OrderSchedule> orderSchedule) {
        this.orderSchedule = orderSchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(item, orderItem.item) && Objects.equals(materialType, orderItem.materialType) && Objects.equals(itemType, orderItem.itemType) && Objects.equals(itemDescription, orderItem.itemDescription) && Objects.equals(orderSchedule, orderItem.orderSchedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, materialType, itemType, itemDescription, orderSchedule);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "item='" + item + '\'' +
                ", materialType='" + materialType + '\'' +
                ", itemType='" + itemType + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", orderSchedule=" + orderSchedule +
                '}';
    }
}
