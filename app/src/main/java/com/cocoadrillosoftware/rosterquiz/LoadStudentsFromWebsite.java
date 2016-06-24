package com.cocoadrillosoftware.rosterquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

import javax.net.ssl.HttpsURLConnection;

public class LoadStudentsFromWebsite extends AppCompatActivity {
    String username, imgFolder, rosterName;
    ArrayList<String> images;

    RosterAdapter adapter;

    Roster roster;
    int loadingErrors;
    final String rostersFolderName = "Rosters";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_load_students_from_website);
        final ListView studentsTable = (ListView) findViewById(R.id.studentsListView);

        // set the incoming data
        Intent intent = getIntent();
        username = intent.getStringExtra("username").replace(" ","%20");
        imgFolder = intent.getStringExtra("imgFolder").replace(" ","%20");
        rosterName = intent.getStringExtra("rosterName");

        roster = new Roster(rosterName);

        // load the names
        LoadStudentNames loadNames = new LoadStudentNames();
        String bodyData = "https://www.eecs.tufts.edu/~cgregg/rosters/cgi-bin/getImageList.cgi";

        bodyData += "?name="+username+"&imgFolder="+imgFolder;
        loadNames.execute(bodyData);

        adapter = new RosterAdapter(this, roster);
        studentsTable.setAdapter(adapter);
        final Button button = (Button) findViewById(R.id.addToRosterButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // save the data to our temporary file
                try {
                    File rostersFolder = getDir(rostersFolderName, Context.MODE_PRIVATE);
                    File outputFile = new File(rostersFolder,roster.toString());
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(roster);
                    oos.close();
                    fos.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
                // send
                Intent intent = new Intent(LoadStudentsFromWebsite.this, MainActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("rosterIncoming", roster.toString()); //Optional parameters
                LoadStudentsFromWebsite.this.startActivity(intent);
            }
        });
    }

    public class LoadStudentNames extends AsyncTask<String, String, String> {

        public LoadStudentNames() {
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                URL url = new URL(urlString);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                in = new BufferedInputStream(urlConnection.getInputStream());


            } catch (Exception e) {
                String s = e.getMessage();
                System.out.println(e.getMessage());

                return e.getMessage();

            }

            resultToDisplay = new Scanner(in,"UTF-8").useDelimiter("\\A").next();

            return resultToDisplay;
        }


        @Override
        protected void onPostExecute(String result) {
            //Update the UI
            System.out.println(result);

            images = new ArrayList<>();

            // split by newline
            String[] lines = result.split("\n");
            for (String line : lines) {
                if (line.endsWith(".jpg")){ // we have a jpg file
                    images.add(line);
                    String[] parts = line.split(".jpg");
                    String nameSp[] = parts[0].split("_");
                    Student s = new Student(nameSp[0],nameSp[1]);
                    roster.add(s);
                }
            }
            adapter.notifyDataSetChanged();
            // now actually load the images
            loadingErrors = 0;
            if (roster.size() > 0) {
                populateImages(0);
            }
        }
    }
    void populateImages(int count) {
        PopulateImages popImages = new PopulateImages(count);

        String bodyData = "https://www.eecs.tufts.edu/~cgregg/rosters/cgi-bin/retrieveImageByFolder.cgi";

        bodyData += "?name="+username+"&imgFolder="+imgFolder+"&imgName="+images.get(count).replace(" ","%20");
        popImages.execute(bodyData);
        // wait 125ms before loading the next one.
        count += 1;
        if (count < images.size()) {
            final int thisCount = count;
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            populateImages(thisCount);
                        }
                    },
                    125 // ms
            );
        }
    }

    public class PopulateImages extends AsyncTask<String, String, SerialBitmap> {
        int count;

        public PopulateImages(int count) {
            this.count = count;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SerialBitmap doInBackground(String... params) {

            String urlString = params[0]; // URL to call

            String resultToDisplay = "";

            InputStream in = null;
            try {
                URL url = new URL(urlString);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                in = new BufferedInputStream(urlConnection.getInputStream());


            } catch (Exception e) {
                String s = e.getMessage();
                System.out.println(e.getMessage());

                //return e.getMessage();
                return null;

            }

            //resultToDisplay = new Scanner(in).useDelimiter("\\A").next();
            SerialBitmap b = new SerialBitmap(in);

            return b;
            //return resultToDisplay;
        }


        @Override
        protected void onPostExecute(SerialBitmap result) {
            //we should have the image data in our string
            if (result != null) {
                roster.get(count).picture = result;
                adapter.notifyDataSetChanged();
            }
        }
    }
}
