package com.sevengreen.ilija.bicyclegallery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

 public class WriteSDCard extends Activity {

/** Method to check whether external media available and writable. This is adapted from
   http://developer.android.com/guide/topics/data/data-storage.html#filesExternal */

 static boolean checkExternalMedia(){
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
        // Can read and write the media
        return true;
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        // Can only read the media
        return false;
    } else {
        // Can't read or write
        return false;
    }   
}

/** Method to write ascii text characters to file on SD card. Note that you must add a 
   WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
   a FileNotFound Exception because you won't have write permission. */

static String writeToSDFile(File extStorageDirectory, String FileName, Bitmap picture, String status){
		OutputStream outStream = null;
		File file = new File(extStorageDirectory, FileName);
		File path = extStorageDirectory;
	   try {
		    path.mkdirs();
		    outStream = new FileOutputStream(file);
		    picture.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
		    outStream.flush();
		    outStream.close();
		    status = "Saved to" + extStorageDirectory + "/" + FileName;
		   } catch (FileNotFoundException e) {
		    e.printStackTrace();
		    status = e.toString();
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    status = e.toString();
		   }
	   return status;
  
}


}