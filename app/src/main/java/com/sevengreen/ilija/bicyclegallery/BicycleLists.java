package com.sevengreen.ilija.bicyclegallery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eilisub on 21.8.2015..
 */
public class BicycleLists {
    List<String> Manufacturers = null; //myDbHelper.getAllManufacturers("");
    List<String> BikeTypes = null; //myDbHelper.getAllBikeTypes();
    List<String> BikeModels = null; //myDbHelper.getAllBikeModels();
    HashMap<Integer,Bicycle> ListOfChosenBicycles=null;

    public BicycleLists(List<String> manufacturers, List<String> bikeTypes, List<String> bikeModels, HashMap<Integer,Bicycle> listOfChosenBicycles) {
        Manufacturers = manufacturers;
        BikeTypes = bikeTypes;
        BikeModels = bikeModels;
        ListOfChosenBicycles = listOfChosenBicycles;
    }

    public BicycleLists() {
    }

    public List<String> getManufacturers() {
        return Manufacturers;
    }

    public void setManufacturers(List<String> manufacturers) {
        Manufacturers = manufacturers;
    }

    public List<String> getBikeTypes() {
        return BikeTypes;
    }

    public void setBikeTypes(List<String> bikeTypes) {
        BikeTypes = bikeTypes;
    }

    public List<String> getBikeModels() {
        return BikeModels;
    }

    public void setBikeModels(List<String> bikeModels) {
        BikeModels = bikeModels;
    }

    public HashMap<Integer,Bicycle> getListOfChosenBicycles() {
        return ListOfChosenBicycles;
    }

    public void setListOfChosenBicycles(HashMap<Integer,Bicycle> listOfChosenBicycles) {
        ListOfChosenBicycles = listOfChosenBicycles;
    }
}
