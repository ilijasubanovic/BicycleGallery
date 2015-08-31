package com.sevengreen.ilija.bicyclegallery;

/**
 * Created by eilisub on 19.8.2015..
 */
public class Bicycle {
    int id;
    String brandName;
    String bikeType;
    String bikeModelName;
    String bikeModelType;
    String linkToLocalImage;
    String linkToRemoteImage;

    public Bicycle(String brandName) {
        this.brandName = brandName;
    }

    public Bicycle() {
    }

    public Bicycle(int id, String brandName, String bikeType, String bikeModelName, String bikeModelType, String linkToLocalImage, String linkToRemoteImage) {
        this.id = id;
        this.brandName = brandName;
        this.bikeType = bikeType;
        this.bikeModelName = bikeModelName;
        this.bikeModelType = bikeModelType;
        this.linkToLocalImage = linkToLocalImage;
        this.linkToRemoteImage = linkToRemoteImage;
    }

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBikeType() {
        return bikeType;
    }

    public void setBikeType(String bikeType) {
        this.bikeType = bikeType;
    }

    public String getBikeModelName() {
        return bikeModelName;
    }

    public void setBikeModelName(String bikeModelName) {
        this.bikeModelName = bikeModelName;
    }

    public String getBikeModelType() {
        return bikeModelType;
    }

    public void setBikeModelType(String bikeModelType) {
        this.bikeModelType = bikeModelType;
    }

    public String getLinkToLocalImage() {
        return linkToLocalImage;
    }

    public void setLinkToLocalImage(String linkToLocalImage) {
        this.linkToLocalImage = linkToLocalImage;
    }

    public String getLinkToRemoteImage() {
        return linkToRemoteImage;
    }

    public void setLinkToRemoteImage(String linkToRemoteImage) {
        this.linkToRemoteImage = linkToRemoteImage;
    }
}
