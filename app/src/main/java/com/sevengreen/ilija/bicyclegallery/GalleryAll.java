package com.sevengreen.ilija.bicyclegallery;
//!!!!!!!!!!!!!!!!
//FTP PASS bicikli1234
//http://7green.vacau.com/bikes/
//TO DO : insert new db file,
// insert images (resize them before that
//add loading from net in gallery part
// add ads

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;

public class GalleryAll extends Activity  {
    //access to DB
    DataBaseHelper myDbHelper;
    String selectedItemManufacturer="All", selectedItemType ="All";
    ImageLoader imageLoader = ImageLoader.getInstance();
    ProgressBar progressBarGallery;
    Spinner spinnerManufacturers;
    Spinner spinnerBikeTypes;
    Spinner spinnerBikeModelNames;
    TextView textBoxBikeManufacturer, textBoxBikeType, textBoxBikeName;
    BicycleLists myBicycleLists = new BicycleLists();
    Bicycle currentBicycle;
    int spinnerModelPosition=0, spinnerTypePosition=0, spinnerManufacturerPosition=0;
    ImageSwitcher sw;
    Typeface myTypeface;
    private GestureDetector gd;
    SharedPreferences mPrefs;
    Boolean executeOnResume;
    Bitmap loadedImageG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myTypeface = Typeface.createFromAsset(getAssets(), "fonts/scb.ttf");
        mPrefs = getPreferences(MODE_PRIVATE);
        executeOnResume = false;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    protected void onResume (){
        super.onResume();
    }
    protected void onStop() {
        super.onStop();

        File file = new File(getDir("data", MODE_PRIVATE), "map");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(myBicycleLists);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException ex){
            System.out.println("Cannot access memory!");
        }
        executeOnResume = true;

    }
    @Override
    protected void onStart () {
        super.onStart();

        final MySpinnerAdapter spinnerArrayAdapterBikeTypes;
        final MySpinnerAdapter spinnerArrayAdapterManufacturers;
        final MySpinnerAdapter spinnerArrayAdapterBikeModelNames;
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));

        setContentView(R.layout.gallery_all);
        textBoxBikeManufacturer = (TextView) findViewById(R.id.textBikeManufacturer);
        textBoxBikeManufacturer.setTypeface(myTypeface);
        textBoxBikeType = (TextView) findViewById(R.id.textBikeType);
        textBoxBikeType.setTypeface(myTypeface);
        textBoxBikeName = (TextView) findViewById(R.id.textBikeName);
        textBoxBikeName.setTypeface(myTypeface);
        sw = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        progressBarGallery = (ProgressBar) findViewById(R.id.progressBarGallery);

        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                return myView;
            }
        });


        gd=new GestureDetector(this,new MyGuestListner());
        if(executeOnResume) {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            try {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fin);
                BicycleLists obj = (BicycleLists) inputStream.readObject();
                myBicycleLists.setManufacturers(obj.getManufacturers());
                myBicycleLists.setBikeTypes(obj.getBikeTypes());
                myBicycleLists.setBikeModels(obj.getBikeModels());
                myBicycleLists.setListOfChosenBicycles(obj.getListOfChosenBicycles());
            } catch (IOException ex) {
                System.out.println("Cannot read stored data!");
            } catch (ClassNotFoundException cx) {
                System.out.println("Cannot read class!");
            }
        }
        else
        //if(!(executeOnResume))
            myDbHelper.getInitialBicycleList(myBicycleLists);

        //setting spinners
        spinnerManufacturers = new Spinner(this);
        spinnerManufacturers = (Spinner) findViewById(R.id.spinnerManufacturers);

        spinnerArrayAdapterManufacturers = new MySpinnerAdapter(
                this,
                R.layout.simple_spinner_item_gradient,
                myBicycleLists.getManufacturers()
        );
        spinnerManufacturers.setAdapter(spinnerArrayAdapterManufacturers);


        spinnerBikeTypes = new Spinner(this);
        spinnerBikeTypes = (Spinner) findViewById(R.id.spinnerBikeTypes);

        spinnerArrayAdapterBikeTypes = new MySpinnerAdapter(
                this,
                R.layout.simple_spinner_item_gradient,
                myBicycleLists.getBikeTypes()
        );
        spinnerBikeTypes.setAdapter(spinnerArrayAdapterBikeTypes);

        spinnerBikeModelNames = new Spinner(this);
        spinnerBikeModelNames = (Spinner) findViewById(R.id.spinnerBikeModelNames);

        spinnerArrayAdapterBikeModelNames = new MySpinnerAdapter(
                this,
                R.layout.simple_spinner_item_gradient,
                myBicycleLists.getBikeModels()
        );
        spinnerBikeModelNames.setAdapter(spinnerArrayAdapterBikeModelNames);

        //initial bike
        //updateBikeImage(spinnerModelPosition);
        spinnerManufacturers.setSelection(spinnerManufacturerPosition, false);
        spinnerBikeTypes.setSelection(spinnerTypePosition, false);

        spinnerBikeModelNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                updateBikeImage(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerManufacturers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //update spinner lists according to manufacturer selection
                selectedItemManufacturer = adapterView.getItemAtPosition(position).toString();
                myDbHelper.updateOnManufacturerClick(myBicycleLists, selectedItemManufacturer, selectedItemType);
                spinnerManufacturerPosition = position;

                spinnerArrayAdapterBikeTypes.clear();
                spinnerArrayAdapterBikeTypes.addAll(myBicycleLists.getBikeTypes());
                spinnerArrayAdapterBikeTypes.notifyDataSetChanged();
                spinnerTypePosition = spinnerArrayAdapterBikeTypes.getPosition(selectedItemType);
                spinnerBikeTypes.setSelection(spinnerTypePosition);

                spinnerArrayAdapterBikeModelNames.clear();
                spinnerArrayAdapterBikeModelNames.addAll(myBicycleLists.getBikeModels());
                spinnerArrayAdapterBikeModelNames.notifyDataSetChanged();
                if (selectedItemManufacturer.equalsIgnoreCase("All")) {
                    spinnerArrayAdapterManufacturers.clear();
                    spinnerArrayAdapterManufacturers.addAll(myBicycleLists.getManufacturers());
                    spinnerArrayAdapterManufacturers.notifyDataSetChanged();
                }
                spinnerModelPosition=0;
                spinnerBikeModelNames.setSelection(spinnerModelPosition, true);
                updateBikeImage(spinnerModelPosition);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerBikeTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedItemType = adapterView.getItemAtPosition(position).toString();
                myDbHelper.updateOnTypeClick(myBicycleLists, selectedItemManufacturer, selectedItemType);
                spinnerTypePosition=position;

                spinnerArrayAdapterManufacturers.clear();
                spinnerArrayAdapterManufacturers.addAll(myBicycleLists.getManufacturers());
                spinnerArrayAdapterManufacturers.notifyDataSetChanged();
                spinnerManufacturerPosition = spinnerArrayAdapterManufacturers.getPosition(selectedItemManufacturer);
                spinnerManufacturers.setSelection(spinnerManufacturerPosition);

                spinnerArrayAdapterBikeModelNames.clear();
                spinnerArrayAdapterBikeModelNames.addAll(myBicycleLists.getBikeModels());
                spinnerArrayAdapterBikeModelNames.notifyDataSetChanged();
                if (selectedItemType.equalsIgnoreCase("All")) {
                    spinnerArrayAdapterBikeTypes.clear();
                    spinnerArrayAdapterBikeTypes.addAll(myBicycleLists.getBikeTypes());
                    spinnerArrayAdapterBikeTypes.notifyDataSetChanged();
                }
                spinnerModelPosition = 0;
                spinnerBikeModelNames.setSelection(spinnerModelPosition, true);
                updateBikeImage(spinnerModelPosition);
            }


            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerBikeModelNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                updateBikeImage(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Button buttonNext = (Button) findViewById(R.id.nextGallery);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerModelPosition < myBicycleLists.getListOfChosenBicycles().size()-1) {
                    spinnerBikeModelNames.setSelection(spinnerModelPosition + 1,true);
                }
            }
        });
        Button buttonPrevious = (Button) findViewById(R.id.backGallery);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerModelPosition>0)
                    spinnerBikeModelNames.setSelection(spinnerModelPosition - 1,true);
            }
        });

        spinnerBikeModelNames.setSelection(spinnerModelPosition, false);

    }
    void updateBikeImage (int position)
    {
        Animation animationOutL, animationInL , animationOutR, animationInR;
        String pathToLocalImage="null";
        animationOutL = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        animationInR= AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        animationOutR = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        animationInL= AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        if(myBicycleLists.getListOfChosenBicycles().containsKey(position)) {
            currentBicycle = myBicycleLists.getListOfChosenBicycles().get(position);
            pathToLocalImage = currentBicycle.getLinkToLocalImage().split("\\.")[0];
            textBoxBikeManufacturer.setText(currentBicycle.getBrandName());
            textBoxBikeType.setText(currentBicycle.getBikeType());
            textBoxBikeName.setText(currentBicycle.getBikeModelName());
        }

        if(spinnerModelPosition<position) {
            sw.setInAnimation(animationInR);
            sw.setOutAnimation(animationOutL);
            sw.clearAnimation();
        }
        else
        {
            sw.clearAnimation();
            sw.setInAnimation(animationInL);
            sw.setOutAnimation(animationOutR);
        }
        spinnerModelPosition=position;
        loadedImageG = BitmapFactory.decodeResource(getResources(),getBaseContext().getResources().getIdentifier(pathToLocalImage
                , "drawable", "com.sevengreen.ilija.bicyclegallery"));
        sw.setImageResource((getBaseContext().getResources().getIdentifier(pathToLocalImage
                , "drawable", "com.sevengreen.ilija.bicyclegallery")));

        //after small size image is set, download full size image and set it ()
        sw.clearAnimation(); //remove animation
        sw.setInAnimation(null);
        sw.setOutAnimation(null);
        // Get singleton instance
        imageLoader.stop();//stop previous execution
        ImageSize imgSwSize = new ImageSize(sw.getHeight(),sw.getHeight());
        String httpPath = getString(R.string.pathToServer)+currentBicycle.getLinkToRemoteImage();

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.loadImage(httpPath, imgSwSize, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBarGallery.setProgress(0);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBarGallery.setProgress(0);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        loadedImageG = loadedImage;
                        Drawable dw = new BitmapDrawable(getBaseContext().getResources(), loadedImage);
                        sw.clearAnimation();
                        sw.setImageDrawable(dw);
                        progressBarGallery.setProgress(100);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressBarGallery.setProgress(0);
                    }
                },
                new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progressBarGallery.setProgress(current * 100  / total );
                    }
                });

    }

    public class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/scb.ttf");
        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);
        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }
        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

    }

@Override
public boolean onTouchEvent(MotionEvent event) {
    // TODO Auto-generated method stub
    return gd.onTouchEvent(event);
}
    class MyGuestListner extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (velocityX < 0) {
                if(spinnerModelPosition<myBicycleLists.getListOfChosenBicycles().size()-1)
                    spinnerBikeModelNames.setSelection(spinnerModelPosition + 1,true);
            } else {
                if(spinnerModelPosition>0)
                    spinnerBikeModelNames.setSelection(spinnerModelPosition - 1,true);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {

            //openOptionsMenu();
            PopupMenu popup = new PopupMenu(GalleryAll.this, sw);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_gallery, popup.getMenu());
           // popup.setGravity(Gravity.CENTER_HORIZONTAL);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_full_screen:
                            Intent i = new Intent("com.sevengreen.ilija.bicyclegallery.FullImagePreview");
                            i.putExtra("currentBicycleManufacturer",currentBicycle.getBrandName());
                            i.putExtra("currentBicycleType",currentBicycle.getBikeModelType());
                            i.putExtra("currentBicycleName",currentBicycle.getBikeModelName());
                            i.putExtra("currentBicycleLocalImage", currentBicycle.getLinkToLocalImage());
                            i.putExtra("currentBicycleRemoteImage",currentBicycle.getLinkToRemoteImage());
                            startActivity(i);
                            return true;

                        case R.id.menu_save: {
                            String status = "Unable to save image to device";
                            if (WriteSDCard.checkExternalMedia()) {
                                String fileName = currentBicycle.getLinkToRemoteImage();
                                File extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                status = WriteSDCard.writeToSDFile(extStorageDirectory, fileName, loadedImageG, status);
                                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                                return true;
                            }
                            else {
                                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                        case R.id.menu_background:
                            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                            try {
                                myWallpaperManager.setBitmap(loadedImageG);

                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            Toast showtoast=Toast.makeText(getApplicationContext(), "Wallpaper Set", Toast.LENGTH_SHORT);
                            showtoast.setGravity(Gravity.CENTER, 0, 0);
                            showtoast.show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();

         return super.onSingleTapConfirmed(e);
        }
    }
}