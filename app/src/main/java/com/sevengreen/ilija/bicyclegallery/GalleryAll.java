package com.sevengreen.ilija.bicyclegallery;
//!!!!!!!!!!!!!!!!
//FTP PASS bicikli123
//http://7green.vacau.com/bikes/

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
    ImageView display;
    String[] modelsArray;
    Collection<String> vals;
    Collection<Bicycle> valsB;

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
        display	= (ImageView) findViewById(R.id.ImageViewGallery);
        final BicycleLists myBicycleLists = new BicycleLists();
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

        Spinner spinnerBikeModelNames = new Spinner(this);
        spinnerBikeModelNames = (Spinner) findViewById(R.id.spinnerBikeModelNames);
        spinnerArrayAdapterBikeModelNames = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, myBicycleLists.getBikeModels());
        spinnerBikeModelNames.setAdapter(spinnerArrayAdapterBikeModelNames);
        spinnerBikeModelNames.setSelection(0, false);


/*        Spinner spinnerBikeModelNames = new Spinner(this);
        spinnerBikeModelNames = (Spinner) findViewById(R.id.spinnerBikeModelNames);
        spinnerArrayAdapterBikeModelNames = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, myBicycleLists.getBikeModels().values().toArray(new String [0]));
        spinnerBikeModelNames.setAdapter(spinnerArrayAdapterBikeModelNames);
        spinnerBikeModelNames.setSelection(0, false);

       Spinner spinnerBikeModelNames = new Spinner(this);
        spinnerBikeModelNames = (Spinner) findViewById(R.id.spinnerBikeModelNames);
        vals = myBicycleLists.getBikeModels().values();
        modelsArray = vals.toArray(new String[vals.size()]);
        ArrayList<String> modelsList = new ArrayList<String>(Arrays.asList(modelsArray));
        spinnerArrayAdapterBikeModelNames = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, modelsList);
        spinnerBikeModelNames.setAdapter(spinnerArrayAdapterBikeModelNames);
        spinnerBikeModelNames.setSelection(0, false);
        */
/*getAllBikeModels
        //myBicycleLists
        Spinner spinnerBikeModelNames = new Spinner(this);
        spinnerBikeModelNames = (Spinner) findViewById(R.id.spinnerBikeModelNames);
        valsB = myBicycleLists.getListOfChosenBicycles().values();

        //modelsArray = valsB.toArray(new String[valsB.size()]);
        //ArrayList<String> modelsList = new ArrayList<String>(Arrays.asList(modelsArray));
        List<String> modelsList = valsB.strea;

        spinnerArrayAdapterBikeModelNames = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_my, modelsList);
        spinnerBikeModelNames.setAdapter(spinnerArrayAdapterBikeModelNames);
        spinnerBikeModelNames.setSelection(0, false);
*/

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
/*
                vals = myBicycleLists.getBikeModels().values();
                Collection<String> names = CollectionUtils.collect(personList, TransformerUtils.invokerTransformer("getName"));

                modelsArray = vals.toArray(new String[vals.size()]);
                ArrayList<String> modelsList = new ArrayList<String>(Arrays.asList(modelsArray));
*/
                spinnerArrayAdapterBikeModelNames.addAll(myBicycleLists.getBikeModels());
                spinnerArrayAdapterBikeModelNames.notifyDataSetChanged();
                if(selectedItemManufacturer.equalsIgnoreCase("All"))
                {
                    spinnerArrayAdapterManufacturers.clear();
                    spinnerArrayAdapterManufacturers.addAll(myBicycleLists.getManufacturers());
                    spinnerArrayAdapterManufacturers.notifyDataSetChanged();
                }
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
                if(selectedItemType.equalsIgnoreCase("All"))
                {
                    spinnerArrayAdapterBikeTypes.clear();
                    spinnerArrayAdapterBikeTypes.addAll(myBicycleLists.getBikeTypes());
                    spinnerArrayAdapterBikeTypes.notifyDataSetChanged();
                }
            }


            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        }
        );


        spinnerBikeModelNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //selectedItemModel = adapterView.getItemAtPosition(position).toString();
                //bicycleMapKey=selectedItemManufacturer+selectedItemType+selectedItemModel;
                //Log.v("bicycleMapKey", bicycleMapKey);
                String tmps= "author_mtb_trophy.jpg";
                if(myBicycleLists.getListOfChosenBicycles().containsKey(position)) {
                    Bicycle tmpBike = myBicycleLists.getListOfChosenBicycles().get(position);
                    Log.v("Fund", "asdasd");
                    Log.v("tmpBike", tmpBike.toString());
                    Log.v("1", tmpBike.getBikeModelName().toString());
                    Log.v("2", tmpBike.getBikeType().toString());
                    Log.v("3", tmpBike.getBrandName().toString());
                    Log.v("4", tmpBike.getLinkToLocalImage().toString());
                    tmps = tmpBike.getLinkToLocalImage().split("\\.")[0];
                    Log.v("TMP1", tmps);
                   // tmps = tmpBike.getBrandName()+"_"+tmpBike.getBikeType()+"_"+tmpBike.getBikeModelName();
                    //Log.v("TMP2", tmps);
                }



                //display.setImageResource(R.drawable.author_cross_codex);
                display.setImageResource(getBaseContext().getResources().getIdentifier(tmps,"drawable","com.sevengreen.ilija.bicyclegallery"));
                //display.setImageResource(getBaseContext().getResources().getIdentifier(tmps, "drawable", "com.sevengreen.ilija.bicyclegallery"));

            }
            public void onNothingSelected(AdapterView<?> adapterView) {  }
            }
        );





    }
}