package com.chantey.bard;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

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

                HttpClient httpClient = HttpClients.createDefault();
                HttpPost post = new HttpPost("http://10.0.0.20:80/fileupload"); // ip of port-forwarded port 80 on router

                MultipartEntityBuilder meb = MultipartEntityBuilder.create();
                meb.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

                File songFile = new File(string);
                meb.addBinaryBody("file",
                        new FileInputStream(songFile),
                        ContentType.APPLICATION_OCTET_STREAM,
                        songFile.getName());

                HttpEntity multipart = meb.build();
                post.setEntity(multipart);
                HttpResponse res = httpClient.execute(post);
                HttpEntity responseEntity = res.getEntity();

                BufferedReader in = new BufferedReader(new InputStreamReader
                        (responseEntity.getContent()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.i(TAG, response.toString());
                /*

                URL url = new URL("http://10.0.0.20:80/fileupload"); // ip of port-forwarded port 80 on router

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setConnectTimeout(4000);

                //POST request header
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("Accept-Language", "en-US, en;q=0.5");
                //connection.setRequestProperty("Content-Type", "multipart/form-data");

                connection.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                BufferedReader outputReader = new BufferedReader(new FileReader(string));

                String outputLine;

                while ((outputLine = outputReader.readLine()) != null) {
                    os.writeUTF(outputLine);
                }

                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.i(TAG, "Response code: " + responseCode);

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader
                        (connection.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.i(TAG, response.toString());

                connection.disconnect();

              */

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
