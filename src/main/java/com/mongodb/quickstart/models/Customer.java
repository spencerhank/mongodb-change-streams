package com.mongodb.quickstart.models;

import org.bson.codecs.pojo.annotations.BsonCreator;

import java.util.List;
import java.util.Objects;

public class Customer {
    private String customerId;
    private String customerName;
    private String zipCode;
    private String street;
    private String phone;
    private String country;
    private String city;
    private List<EmailAddress> emailAddress;

    @BsonCreator
    public Customer() {}

    public Customer(String customerId, String customerName, String zipCode, String street, String phone, String country, String city, List<EmailAddress> emailAddress) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.zipCode = zipCode;
        this.street = street;
        this.phone = phone;
        this.country = country;
        this.city = city;
        this.emailAddress = emailAddress;
    }

    // Getters and Setters

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<EmailAddress> getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(List<EmailAddress> emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) && Objects.equals(customerName, customer.customerName) && Objects.equals(zipCode, customer.zipCode) && Objects.equals(street, customer.street) && Objects.equals(phone, customer.phone) && Objects.equals(country, customer.country) && Objects.equals(city, customer.city) && Objects.equals(emailAddress, customer.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerName, zipCode, street, phone, country, city, emailAddress);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", street='" + street + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", emailAddress=" + emailAddress +
                '}';
    }
}
