package com.chantey.bard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private URL SERVER_URL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String SERVER_IP = "[2601:80:4002:ecb0:39e6:3532:c90e:d0a1]:"; //ip address of host machine
        final String PORT = "8080";

        final Button testButton = findViewById(R.id.testButton);
        final TextView testView = findViewById(R.id.testText);

        try {
            SERVER_URL = new URL("http://" + SERVER_IP + PORT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Failed to construct SERVER_URL");
        }

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SERVER_URL != null){
                    testView.setText(getTestStringFromServer(SERVER_URL));
                } else {
                    System.err.println("Failed to setText; SERVER_URL is null");
                }
            }
        });
    }

    //contact the server and returns whatever string it obtains from the server
    // proof-of-concept test method
    private String getTestStringFromServer(URL url){
        String serverString;

        try{

        } catch(Exception e) {

        }
        //return the String that was requested from the server
        return "test";
    }
}

