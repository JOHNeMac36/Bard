package com.chantey.bard;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String SERVER_IP = "[2601:80:4002:ecb0:39e6:3532:c90e:d0a1]:"; //ip address of host machine
        final String PORT = "8080";

        final Button testButton = findViewById(R.id.testButton);

        //set the audio attributes, data source, and event listeners for the MediaPlayer
        final MediaPlayer songPlayer = new MediaPlayer();
        songPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        );

        try{
            songPlayer.setDataSource(SERVER_IP + PORT);
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            System.err.println("Data source could not be set!");
        }

        songPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                System.err.println("An error has occurred with the MediaPlayer");
                return false;
            }
        });

        songPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                songPlayer.start();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    songPlayer.prepareAsync();
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        });
    }
}

