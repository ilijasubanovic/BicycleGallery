package com.sevengreen.ilija.bicyclegallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by eilisub on 27.9.2015..
 */
public class FullImagePreview extends Activity {
    ImageView imageFull;
    ProgressBar progressBar;
    ProgressDialog pDialog;
    Bitmap bitmap;
    String bicycleManufacturer, bicycleType, bicycleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onStart () {
        super.onStart();

        setContentView(R.layout.full_image_preview);

        Intent intent = getIntent();
        bicycleManufacturer = intent.getStringExtra("currentBicycleManufacturer");
        bicycleType = intent.getStringExtra("currentBicycleType");
        bicycleName = intent.getStringExtra("currentBicycleName");
        String bicycleLocalImage = intent.getStringExtra("currentBicycleLocalImage");
        String bicycleRemoteImage = intent.getStringExtra("currentBicycleRemoteImage");

        System.out.println("bicycleRemoteImage " + bicycleRemoteImage);

        imageFull = (ImageView) findViewById(R.id.imageViewFull);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLoadingImage);
       // progressBar.dismiss();
        progressBar.setVisibility(View.INVISIBLE);

        String pathToLocalImage = bicycleLocalImage.split("\\.")[0];

        Resources res = getResources();
        int resID = res.getIdentifier(pathToLocalImage, "drawable", getPackageName().toString());
        imageFull.setImageResource(resID);
        String httpPath = getString(R.string.pathToServer)+bicycleRemoteImage;
        System.out.println("PATH    :   " + httpPath);
        new LoadImage().execute(httpPath);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FullImagePreview.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                imageFull.setImageBitmap(image);
                pDialog.dismiss();
                Toast.makeText(FullImagePreview.this, bicycleManufacturer + " - " + bicycleName, Toast.LENGTH_SHORT).show();


            }else{

                pDialog.dismiss();
                Toast.makeText(FullImagePreview.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
