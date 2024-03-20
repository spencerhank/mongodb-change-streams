package com.mongodb.quickstart.models;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

public class SalesOrder {
    @BsonId
    private ObjectId id;
    @BsonProperty
    private String salesOrderNumber;
    @BsonProperty
    private String creator;
    @BsonProperty
    private String date;
    @BsonProperty
    private String salesType;
    @BsonProperty
    private String orderType;
    @BsonProperty
    private String salesOrg;
    @BsonProperty
    private String distributionChannel;
    @BsonProperty
    private String division;
    @BsonProperty
    private String netValue;
    @BsonProperty
    private String currency;
    @BsonProperty
    private Customer customer;

    private List<OrderItem> orderItem;

    @BsonCreator
    public SalesOrder() {

    }


    public SalesOrder(ObjectId id, String salesOrderNumber, String creator, String date, String salesType, String orderType, String salesOrg, String distributionChannel, String division, String netValue, String currency, Customer customer, List<OrderItem> orderItem) {
        this.id = id;
        this.salesOrderNumber = salesOrderNumber;
        this.creator = creator;
        this.date = date;
        this.salesType = salesType;
        this.orderType = orderType;
        this.salesOrg = salesOrg;
        this.distributionChannel = distributionChannel;
        this.division = division;
        this.netValue = netValue;
        this.currency = currency;
        this.customer = customer;
        this.orderItem = orderItem;
    }

    // Getters and Setters

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSalesOrderNumber() {
        return salesOrderNumber;
    }

    public void setSalesOrderNumber(String salesOrderNumber) {
        this.salesOrderNumber = salesOrderNumber;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSalesType() {
        return salesType;
    }

    public void setSalesType(String salesType) {
        this.salesType = salesType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public String getDistributionChannel() {
        return distributionChannel;
    }

    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getNetValue() {
        return netValue;
    }

    public void setNetValue(String netValue) {
        this.netValue = netValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesOrder that = (SalesOrder) o;
        return Objects.equals(id, that.id) && Objects.equals(salesOrderNumber, that.salesOrderNumber) && Objects.equals(creator, that.creator) && Objects.equals(date, that.date) && Objects.equals(salesType, that.salesType) && Objects.equals(orderType, that.orderType) && Objects.equals(salesOrg, that.salesOrg) && Objects.equals(distributionChannel, that.distributionChannel) && Objects.equals(division, that.division) && Objects.equals(netValue, that.netValue) && Objects.equals(currency, that.currency) && Objects.equals(customer, that.customer) && Objects.equals(orderItem, that.orderItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salesOrderNumber, creator, date, salesType, orderType, salesOrg, distributionChannel, division, netValue, currency, customer, orderItem);
    }

    @Override
    public String toString() {
        return "SalesOrder{" +
                "_id='" + id + '\'' +
                ", salesOrderNumber='" + salesOrderNumber + '\'' +
                ", creator='" + creator + '\'' +
                ", date='" + date + '\'' +
                ", salesType='" + salesType + '\'' +
                ", orderType='" + orderType + '\'' +
                ", salesOrg='" + salesOrg + '\'' +
                ", distributionChannel='" + distributionChannel + '\'' +
                ", division='" + division + '\'' +
                ", netValue='" + netValue + '\'' +
                ", currency='" + currency + '\'' +
                ", customer=" + customer +
                ", orderItem=" + orderItem +
                '}';
    }
}

