package com.het.httpserver.bean;

import java.io.Serializable;

public class LocationBean implements Serializable {

    /**
     * as : AS59257 CMPak Limited
     * city : Karachi
     * country : Pakistan
     * countryCode : PK
     * isp : CMPak Limited
     * lat : 24.9056
     * lon : 67.0822
     * org : CMPak Limited
     * query : 45.116.232.9
     * region : SD
     * regionName : Sindh
     * status : success
     * timezone : Asia/Karachi
     * zip : 12311
     */

    private String as;
    private String city;
    private String country;
    private String countryCode;
    private String isp;
    private double lat;
    private double lon;
    private String org;
    private String query;
    private String region;
    private String regionName;
    private String status;
    private String timezone;
    private String zip;

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
