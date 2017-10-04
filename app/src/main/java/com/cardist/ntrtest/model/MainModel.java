package com.cardist.ntrtest.model;

import com.google.gson.annotations.SerializedName;


public class MainModel {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("address")
    private Address address;

    @SerializedName("phone")
    private String phone;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public class Address {

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
}
