package com.nigeria.locateme.locateme.entities;

import java.io.Serializable;

/**
 * Created by Theophilus on 12/21/2016.
 */
public class NiboUser implements Serializable {

    private String fullName;
    private String phoneNumber;
    private String oldPhoneNumber;
    private String email;
    private String address;
    private String password;
    private String timeCheckin;
    private String lastCheckin;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOldPhoneNumber() {
        return oldPhoneNumber;
    }

    public void setOldPhoneNumber(String oldPhoneNumber) {
        this.oldPhoneNumber = oldPhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastCheckin() {
        return lastCheckin;
    }

    public void setLastCheckin(String lastCheckin) {
        this.lastCheckin = lastCheckin;
    }

    public String getTimeCheckin() {
        return timeCheckin;
    }

    public void setTimeCheckin(String timeCheckin) {
        this.timeCheckin = timeCheckin;
    }












    ///----------------------------------------------------------------------------------------------


    private String houseAddress;
    private String buildingType;
    private String localGovernment;
    private String state;
    private String street;
    private String latLong;


    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }











}
