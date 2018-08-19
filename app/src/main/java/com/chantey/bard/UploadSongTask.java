package com.chantey.bard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class UploadSongTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UploadFileTask";

    private Context context;

    public UploadSongTask(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings){
        //this asynchronously uploads a file that the user has selected



        for (String string: strings) {

            try{

                URL url = new URL("http://10.0.0.20:80/fileupload"); // ip of port-forwarded port 80 on router

                MultipartUtility multipart = new MultipartUtility(url.toString(), "utf-8");
                multipart.addFilePart("test", new File(string));

                List<String> response = multipart.finish();
                Log.d(TAG, "SERVER REPLIED:");
                for (String line : response) {
                    Log.d(TAG, "Upload Files Response:::" + line);
                }

            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result){
       super.onPostExecute(result);
       String status;
       if (result) {
           status = "Song successfully uploaded";
       } else {
           status = "Song upload fail";
       }
       Toast toast = Toast.makeText(context, status, Toast.LENGTH_SHORT);
       toast.show();
    }

}
