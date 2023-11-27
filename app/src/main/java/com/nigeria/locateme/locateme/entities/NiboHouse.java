package com.nigeria.locateme.locateme.entities;

import java.io.Serializable;

/**
 * Created by Theophilus on 12/21/2016.
 */
public class NiboHouse implements Serializable {

    private String houseAddress;
    private String localGovernment;
    private String state;
    private String street;
    private String latLong;
    private String mapper;
    private String nearestBusstop;
    private String niboCode;
    private String houseNumber;
    private String city;
    private String mysqlId;

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getLocalGovernment() {
        return localGovernment;
    }

    public void setLocalGovernment(String localGovernment) {
        this.localGovernment = localGovernment;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(String mapper) {
        this.mapper = mapper;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNearestBustop() {
        return nearestBusstop;
    }

    public void setNearestBusstop(String nearestBusstop) {
        this.nearestBusstop = nearestBusstop;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getMysqlId() {
        return mysqlId;
    }

    public void setMysqlId(String mysqlId) {
        this.mysqlId = mysqlId;
    }

    public String getNiboCode() {
        return niboCode;
    }

    public void setNiboCode(String niboCode) {
        this.niboCode = niboCode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
