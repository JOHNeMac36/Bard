package com.chantey.bard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int SELECT_SONG_FILE_REQUEST_CODE = 2;
    //IP address requires 'http://' in front!!!!!
    final String SERVER_IP = "http://[2601:80:4002:ecb0:79f5:9ff6:d2a9:80cd]"; //ip address of host machine
    final String PORT = "6666"; //DO NOT FORWARD THIS PORT
    private GestureDetectorCompat mDetector;
    private boolean isPlaying = false;

    //debug
    private int rot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        //gesture detector for top view gestures
        mDetector = new GestureDetectorCompat(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                        //Tells the device that the intent of the next Activity is to select an item
                        // from the data
                        Intent chooseIntent = new Intent(Intent.ACTION_GET_CONTENT);

                        // show only files that can be opened by the user
                        chooseIntent.addCategory(Intent.CATEGORY_OPENABLE);

                        //filter to show only audio and mp3 files using MIME data type
                        chooseIntent.setType("audio/*");
                        startActivityForResult(chooseIntent, SELECT_SONG_FILE_REQUEST_CODE);
                        return true;
                    }
                });

        final Button testButton = findViewById(R.id.testButton);
        final View topView = findViewById(R.id.top_view);

        //set the audio attributes, data source, and event listeners for the MediaPlayer
        final MediaPlayer songPlayer = new MediaPlayer();
        songPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
        );

        songPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                songPlayer.reset();
                return false;
            }
        });

        getNextSongFromServer(songPlayer, true);

        songPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                songPlayer.start();
            }
        });

        songPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                getNextSongFromServer(songPlayer, false);
                tryToPlayMediaPlayerAsync(songPlayer);
                demoRotation(); //only to be used for demonstration
            }
        });

        //button on-click listeners

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if (!isPlaying) {
                     tryToPlayMediaPlayerAsync(songPlayer);
                     isPlaying = true;
                     testButton.setBackground(getResources().getDrawable(R.drawable.pause, null));
                 } else {
                     songPlayer.stop();
                     songPlayer.reset();
                     getNextSongFromServer(songPlayer, false);
                     isPlaying = false;
                     testButton.setBackground(getResources().getDrawable(R.drawable.play, null));
                 }
            }
        });

        topView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SELECT_SONG_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri songUri;
            if (data != null){
                songUri = data.getData();

                try{
                    String songFilePath = getFilePathFromContentURI(songUri.toString());
                    if (songFilePath != null){
                        new UploadSongTask(getApplicationContext()).execute(
                                Environment.getExternalStorageDirectory() + "/" + songFilePath);
                    } else {
                        throw new IOException();
                    }

                } catch (IOException | NullPointerException e) {
                    Log.e(TAG, "Song write fail");
                    Log.e(TAG, e.toString());
                }

            }

        }
    }

    /*
    this function takes a content uri as a parameter that encodes to a song on a external storage
    (preferably an SD card) and gets the file path on the end of the uri to return the file path
    */
    private String getFilePathFromContentURI(String contentURI) throws IOException {

        String decodedURI = URLDecoder.decode(contentURI, "UTF-8");
        String pattern = "primary:";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(decodedURI);
        String path;

        if(m.find()){
            path = decodedURI.substring(m.end());
            return path;
        } else {
            return null;
        }
    }

    private void getNextSongFromServer(MediaPlayer m, boolean firstSong) {
        if (!firstSong) {
            m.reset();
        }
        try{
            m.setDataSource(SERVER_IP + ":" + PORT + "/song");
        } catch (IllegalArgumentException | IOException e) {
            Log.e(TAG, "Data source could not be set!");
        }
    }

    private void tryToPlayMediaPlayerAsync(MediaPlayer m) {
        try {
            m.prepareAsync();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    /*
    this is a function purely for demonstration that simulates what the UX would be like.
    it is set to delete the bottommost demonstration "song" and replace the current one with the next
    one. A finalized app should do this with actual flexible logic and NOT with a function like this
    one.
    */

    private void demoRotation() {
        ImageView currentAlbumCover = findViewById(R.id.album1);
        ImageView subAlbum1 = findViewById(R.id.album2);
        ImageView subAlbum2 = findViewById(R.id.album3);

        TextView currentSongName = findViewById(R.id.song1);
        TextView subSong1 = findViewById(R.id.song2);
        TextView subSong2 = findViewById(R.id.song3);

        if (rot == 0) {
            //replace album art with new one and delete bottommost song, move new song up
            currentAlbumCover.setImageDrawable(getDrawable(R.drawable.hellfire));
            subAlbum1.setImageDrawable(getDrawable(R.drawable.let_it_die));
            subAlbum2.setImageDrawable(null);

            currentSongName.setText("Devilman no Uta");
            subSong1.setText("Let it Die");
            subSong2.setText(null);

            rot++;
        } else if (rot == 1) {
            currentAlbumCover.setImageDrawable(getDrawable(R.drawable.let_it_die));
            subAlbum1.setImageDrawable(null);

            currentSongName.setText("Let It Die");
            subSong1.setText(null);

            rot++;
        } else if (rot == 2) {
            currentAlbumCover.setImageDrawable(null);

            currentSongName.setText(null);
        }


    }


    //borrowed code from https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }
}