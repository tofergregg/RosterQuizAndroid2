package com.cocoadrillosoftware.rosterquiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class LoadRosterFromWebsite extends AppCompatActivity {
    final String sentinel = "_S__S_";

    String username;

    public class RosterInfo {
        String imgFolder;

        RosterInfo(String ifolder) {
            imgFolder = ifolder;
        }

        String getName(){
            return imgFolder.split(sentinel)[0];
        }

        String getFilename() {
            return imgFolder.split(sentinel)[1];
        }

        @Override public String toString() {
            return getName() + "(" + getFilename() + ")";
        }
    }

    ArrayAdapter adapter;

    ArrayList<RosterInfo> rosterNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_roster_from_website);
        final ListView rosterTable = (ListView) findViewById(R.id.rosterListView);


        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, rosterNames);
        rosterTable.setAdapter(adapter);
        registerForContextMenu(rosterTable);

        rosterTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                // Open activity to load the students
                Intent myIntent = new Intent(LoadRosterFromWebsite.this, LoadStudentsFromWebsite.class);
                //myIntent.putExtra("key", value); //Optional parameters
                myIntent.putExtra("username",username);
                myIntent.putExtra("imgFolder",rosterNames.get(position).imgFolder);
                myIntent.putExtra("rosterName",rosterNames.get(position).getName());
                LoadRosterFromWebsite.this.startActivity(myIntent);

            }
        });

        final Button button = (Button) findViewById(R.id.showRostersButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText nameEditText = (EditText)(findViewById(R.id.nameText));
                EditText pwEditText = (EditText)(findViewById(R.id.pwText));
                username = nameEditText.getText().toString().replace(" ","%20");
                String pwText = pwEditText.getText().toString().replace(" ","%20");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pwEditText.getWindowToken(), 0);

                CallAPI sendPost = new CallAPI();
                String bodyData = "https://www.eecs.tufts.edu/~cgregg/rosters/cgi-bin/list_rosters.cgi";


                bodyData += "?name="+username+"&pw="+pwText;
                sendPost.execute(bodyData);
                //rosterNames.add("Next");
                //adapter.notifyDataSetChanged();
            }
        });
    }

    public class CallAPI extends AsyncTask<String, String, String> {

        public CallAPI() {
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
            if (result.contains("User name and password do not match!")) {
                new AlertDialog.Builder(LoadRosterFromWebsite.this)
                        .setTitle("Error!")
                        .setMessage(result)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // nothing to do
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else {
                // split by newline
                String[] lines = result.split("\n");
                for (String line : lines) {
                    RosterInfo rosterInfo = new RosterInfo(line);
                    rosterNames.add(rosterInfo);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}