package com.sevengreen.ilija.bicyclegallery;
//!!!!!!!!!!!!!!!!
//FTP PASS bicikli123
//http://7green.vacau.com/bikes/

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GalleryAll extends Activity  {
    //access to DB
    DataBaseHelper myDbHelper;
    String selectedItemManufacturer="All", selectedItemType ="All", selectedItemModel, bicycleMapKey;
    ArrayAdapter<String> spinnerArrayAdapterBikeTypes;
    ArrayAdapter<String> spinnerArrayAdapterManufacturers;
    ArrayAdapter<String> spinnerArrayAdapterBikeModelNames;
    Spinner spinnerManufacturers;
    Spinner spinnerBikeTypes;
    Spinner spinnerBikeModelNames;
    TextView textboxBikeManufacturer, textboxBikeType, textboxBikeName;
    ImageView display;
    BicycleLists myBicycleLists = new BicycleLists();
    int currentImagePosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        //open DB
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw new Error("Unable to open database!!!");
        }
    }

    @Override
    protected void onStart () {
        super.onStart();

        setContentView(R.layout.gallery_all);
        display = (ImageView) findViewById(R.id.ImageViewGallery);
        textboxBikeManufacturer = (TextView) findViewById(R.id.textBikeManufacturer);
        textboxBikeType = (TextView) findViewById(R.id.textBikeType);
        textboxBikeName = (TextView) findViewById(R.id.textBikeName);
        //get list of All bikes...
        myDbHelper.getInitialBicycleList(myBicycleLists);

        //setting spinners
        spinnerManufacturers = new Spinner(this);
        spinnerManufacturers = (Spinner) findViewById(R.id.spinnerManufacturers);
        spinnerArrayAdapterManufacturers = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, myBicycleLists.getManufacturers());
        spinnerManufacturers.setAdapter(spinnerArrayAdapterManufacturers);
        spinnerManufacturers.setSelection(0, false);

        spinnerBikeTypes = new Spinner(this);
        spinnerBikeTypes = (Spinner) findViewById(R.id.spinnerBikeTypes);
        spinnerArrayAdapterBikeTypes = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, myBicycleLists.getBikeTypes());
        spinnerBikeTypes.setAdapter(spinnerArrayAdapterBikeTypes);
        spinnerBikeTypes.setSelection(0, false);

        spinnerBikeModelNames = new Spinner(this);
        spinnerBikeModelNames = (Spinner) findViewById(R.id.spinnerBikeModelNames);
        spinnerArrayAdapterBikeModelNames = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, myBicycleLists.getBikeModels());
        spinnerBikeModelNames.setAdapter(spinnerArrayAdapterBikeModelNames);
        spinnerBikeModelNames.setSelection(0, false);

        //initial bike
        updateBikeImage(currentImagePosition);

        spinnerManufacturers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                           @Override
                                                           public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                               //update spinner lists according to manufacturer selection
                                                               selectedItemManufacturer = adapterView.getItemAtPosition(position).toString();
                                                               myDbHelper.updateOnManufacturerClick(myBicycleLists, selectedItemManufacturer, selectedItemType);
                                                               spinnerArrayAdapterBikeTypes.clear();
                                                               spinnerArrayAdapterBikeTypes.addAll(myBicycleLists.getBikeTypes());
                                                               spinnerArrayAdapterBikeTypes.notifyDataSetChanged();
                                                               int spinnerPosition = spinnerArrayAdapterBikeTypes.getPosition(selectedItemType);
                                                               spinnerBikeTypes.setSelection(spinnerPosition);
                                                               spinnerArrayAdapterBikeModelNames.clear();

                                                               spinnerArrayAdapterBikeModelNames.addAll(myBicycleLists.getBikeModels());
                                                               spinnerArrayAdapterBikeModelNames.notifyDataSetChanged();
                                                               if (selectedItemManufacturer.equalsIgnoreCase("All")) {
                                                                   spinnerArrayAdapterManufacturers.clear();
                                                                   spinnerArrayAdapterManufacturers.addAll(myBicycleLists.getManufacturers());
                                                                   spinnerArrayAdapterManufacturers.notifyDataSetChanged();
                                                               }
                                                               updateBikeImage(0);
                                                           }

                                                           public void onNothingSelected(AdapterView<?> adapterView) {
                                                           }
                                                       }
        );

        spinnerBikeTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                           selectedItemType = adapterView.getItemAtPosition(position).toString();
                                                           myDbHelper.updateOnTypeClick(myBicycleLists, selectedItemManufacturer, selectedItemType);
                                                           spinnerArrayAdapterManufacturers.clear();
                                                           spinnerArrayAdapterManufacturers.addAll(myBicycleLists.getManufacturers());
                                                           spinnerArrayAdapterManufacturers.notifyDataSetChanged();
                                                           int spinnerPosition = spinnerArrayAdapterManufacturers.getPosition(selectedItemManufacturer);
                                                           spinnerManufacturers.setSelection(spinnerPosition);
                                                           spinnerArrayAdapterBikeModelNames.clear();
                                                           spinnerArrayAdapterBikeModelNames.addAll(myBicycleLists.getBikeModels());
                                                           spinnerArrayAdapterBikeModelNames.notifyDataSetChanged();
                                                           if (selectedItemType.equalsIgnoreCase("All")) {
                                                               spinnerArrayAdapterBikeTypes.clear();
                                                               spinnerArrayAdapterBikeTypes.addAll(myBicycleLists.getBikeTypes());
                                                               spinnerArrayAdapterBikeTypes.notifyDataSetChanged();
                                                           }
                                                           updateBikeImage(0);
                                                       }


                                                       public void onNothingSelected(AdapterView<?> adapterView) {
                                                       }
                                                   }
        );


        spinnerBikeModelNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                                updateBikeImage(position);
                                                            }

                                                            public void onNothingSelected(AdapterView<?> adapterView) {
                                                            }
                                                        }
        );

        Button buttonNext = (Button) findViewById(R.id.nextGallery);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentImagePosition<myBicycleLists.getListOfChosenBicycles().size()-1)
                    updateBikeImage(currentImagePosition + 1);
            }
        });
        Button buttonPrevious = (Button) findViewById(R.id.backGallery);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentImagePosition>0)
                    updateBikeImage(currentImagePosition - 1);
            }
        });

    }
    void updateBikeImage (int position)
    {
        Bicycle tmpBike=null;
        currentImagePosition=position;
        String tmps="null";
        if(myBicycleLists.getListOfChosenBicycles().containsKey(position)) {
            tmpBike = myBicycleLists.getListOfChosenBicycles().get(position);
            tmps = tmpBike.getLinkToLocalImage().split("\\.")[0];
        }
        display.setImageResource(getBaseContext().getResources().getIdentifier(tmps, "drawable", "com.sevengreen.ilija.bicyclegallery"));
        spinnerBikeModelNames.setSelection(position);
        textboxBikeManufacturer.setText(tmpBike.getBrandName());
        textboxBikeType.setText(tmpBike.getBikeType());
        textboxBikeName.setText(tmpBike.getBikeModelName());
    }

}