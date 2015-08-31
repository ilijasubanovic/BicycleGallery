package com.sevengreen.ilija.bicyclegallery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.sevengreen.ilija.bicyclegallery/databases/";

    private static String DB_NAME = "BicycleList.db";
    private static String DB_TABLE = "bicycle_list";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    //get list of all manufacturers
   /* public void getInitialList(List<Bicycle> BicycleList, List<String> Manufacturers, List<String> BikeTypes, List<String> BikeModels)
    {
        String whereClause = createWhereClause("All","All");
        BicycleList=getBikeObjects(whereClause);
        Manufacturers=getAllManufacturers(whereClause);
        BikeTypes=getAllBikeTypes("All");
        BikeModels=getAllBikeModels(whereClause);
    }*/
    //get list of all manufacturers
    public void getInitialBicycleList(BicycleLists dbBicycleList)
    {
        String whereClause = createWhereClause("All","All");
        dbBicycleList.setListOfChosenBicycles(getBikeObjects(whereClause));
        dbBicycleList.setManufacturers(getAllManufacturers("All"));
        dbBicycleList.setBikeTypes(getAllBikeTypes("All"));
        dbBicycleList.setBikeModels(getAllBikeModels(whereClause));
    }

    //updateOnManufacturerClick
    public void updateOnManufacturerClick(BicycleLists dbBicycleList, String selectedItemManufacturer, String selectedItemType)
    {
        String whereClause = createWhereClause(selectedItemManufacturer,selectedItemType);
        //Update manufacturers list only if Manufacturer was not chosen (equals "All)
        //Log.v("K L I K - MANUFAC", selectedItemManufacturer+"-"+selectedItemType );
        //if(selectedItemManufacturer.equalsIgnoreCase("All"))
        //    dbBicycleList.setManufacturers(getAllManufacturers(selectedItemType));
        //else do nothing. Manufacturer was already chosen and only bike models should be updated
        dbBicycleList.setListOfChosenBicycles(getBikeObjects(whereClause));
        dbBicycleList.setBikeTypes(getAllBikeTypes(selectedItemManufacturer));
        dbBicycleList.setBikeModels(getAllBikeModels(whereClause));
    }

    //updateOnManufacturerClick
    public void updateOnTypeClick(BicycleLists dbBicycleList, String selectedItemManufacturer, String selectedItemType)
    {
        String whereClause = createWhereClause(selectedItemManufacturer,selectedItemType);
        //Log.v("K L I K - Type", selectedItemManufacturer+"-"+selectedItemType );
        //if (selectedItemType.equalsIgnoreCase("All"))
        //    dbBicycleList.setBikeTypes(getAllBikeTypes(selectedItemManufacturer));
        //else do nothing. type was already chosen and only bike models should be updated
        dbBicycleList.setListOfChosenBicycles(getBikeObjects(whereClause));
        dbBicycleList.setManufacturers(getAllManufacturers(selectedItemType));
        dbBicycleList.setBikeModels(getAllBikeModels(whereClause));
    }

       //get list of all manufacturers
    public List<String> getAllManufacturers(String selectedItemType)
    {
        ArrayList localArrayList = new ArrayList();
        String whereClause = " bike_type ";
        if (selectedItemType.equalsIgnoreCase("All"))
            whereClause += " NOTNULL";
        else
            whereClause += "= '" + selectedItemType + "'";
        Object localObject = "SELECT DISTINCT brand_name FROM " + DB_TABLE + " WHERE " + whereClause;
        localObject = getWritableDatabase().rawQuery((String)localObject, null);
        //Add First value "All"
        localArrayList.add("All");
        if (((Cursor)localObject).moveToFirst()) {
            do
            {
                String manufacturerName = ((Cursor)localObject).getString(0);
                //Log.v("MAnufacturer from db",manufacturerName);
               // Bicycle bicycleManufacturer = new Bicycle(manufacturerName);
                localArrayList.add(manufacturerName);
            } while (((Cursor)localObject).moveToNext());
        }
        return localArrayList;
    }


    //getAllBikeTypes
    //get list of all Bike Types
    public List<String> getAllBikeTypes(String selectedItemManufacturer)
    {
        ArrayList localArrayList = new ArrayList();
        String whereClause = "brand_name ";
        if (selectedItemManufacturer.equalsIgnoreCase("All"))
            whereClause += " NOTNULL";
        else
            whereClause += "= '" + selectedItemManufacturer + "'";
        Object localObject = "SELECT DISTINCT bike_type FROM " + DB_TABLE + " WHERE " + whereClause;
        localObject = getWritableDatabase().rawQuery((String)localObject, null);
        //Add First value "All"
        localArrayList.add("All");
        if (((Cursor)localObject).moveToFirst()) {
            do
            {
                String manufacturerName = ((Cursor)localObject).getString(0);
                //Log.v("TypeBike from db",manufacturerName);
                // Bicycle bicycleManufacturer = new Bicycle(manufacturerName);
                localArrayList.add(manufacturerName);
            } while (((Cursor)localObject).moveToNext());
        }
        return localArrayList;
    }

    //getAllBikeModels
    //get list of all Bike Types
    public List<String> getAllBikeModels(String whereClause)
    {
        ArrayList localArrayList = new ArrayList();
        Object localObject = "SELECT bike_model_name FROM " + DB_TABLE + " WHERE " + whereClause;
        String modelKey, modelName;
        localObject = getWritableDatabase().rawQuery((String)localObject, null);
        //Add First value "All"
        //localArrayList.add("All");
        if (((Cursor)localObject).moveToFirst()) {
            do
            {
                modelName = ((Cursor) localObject).getString(0);

                // Bicycle bicycleManufacturer = new Bicycle(manufacturerName);
                localArrayList.add(modelName);

            } while (((Cursor)localObject).moveToNext());
        }
        return localArrayList;
    }    //get list of all Bike Types
    public HashMap<Integer,Bicycle> getBikeObjects(String whereClause)
    {
        HashMap<Integer,Bicycle> localArrayList = new HashMap<Integer, Bicycle>();
        Integer mapKey=0;
        Object localObject = "SELECT brand_name,bike_type,bike_model_name,link_to_local_image FROM " + DB_TABLE + " WHERE " + whereClause;
        localObject = getWritableDatabase().rawQuery((String)localObject, null);
        //Add First value "All"
        //localArrayList.add("All");
        if (((Cursor)localObject).moveToFirst()) {
            do
            {
                Bicycle tmpBikeObject = new Bicycle();
                tmpBikeObject.setBrandName(((Cursor) localObject).getString(0));
              //  mapKey = ((Cursor) localObject).getString(0);
                Log.v("Cursor 0", ((Cursor) localObject).getString(0));

                tmpBikeObject.setBikeType(((Cursor) localObject).getString(1));
               // mapKey += ((Cursor) localObject).getString(1);
                Log.v("Cursor 1", ((Cursor) localObject).getString(1));

                tmpBikeObject.setBikeModelName(((Cursor) localObject).getString(2));
               // mapKey += ((Cursor) localObject).getString(2);
                Log.v("Cursor 2", ((Cursor) localObject).getString(2));

                tmpBikeObject.setLinkToLocalImage(((Cursor) localObject).getString(3));
                Log.v("mapKey", mapKey.toString());



                // Bicycle bicycleManufacturer = new Bicycle(manufacturerName);
                localArrayList.put(mapKey, tmpBikeObject);
                mapKey++;
            } while (((Cursor)localObject).moveToNext());
        }
        Log.v("LOLOLO",localArrayList.toString());
        return localArrayList;
    }

    String createWhereClause (String selectedItemManufacturer, String selectedItemType) {
        String whereClause = "brand_name ";
        if (selectedItemManufacturer.equalsIgnoreCase("All"))
            whereClause += " NOTNULL AND bike_type ";
        else
            whereClause += "= '" + selectedItemManufacturer + "' AND bike_type ";
        if (selectedItemType.equalsIgnoreCase("All"))
            whereClause += " NOTNULL";
        else
            whereClause += "= '" + selectedItemType + "'";

        return whereClause;
    }
    }