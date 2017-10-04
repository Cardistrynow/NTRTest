package com.cardist.ntrtest.model;

import com.google.gson.annotations.SerializedName;


public class AddressModel {

    @SerializedName("city")
    private String city;

    @SerializedName("street")
    private String street;

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }
}
