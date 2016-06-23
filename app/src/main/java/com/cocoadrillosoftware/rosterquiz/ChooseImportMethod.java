package com.cocoadrillosoftware.rosterquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseImportMethod extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_import_method);
        // set up new roster button
        final Button button = (Button) findViewById(R.id.rosterQuizButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(ChooseImportMethod.this, LoadRosterFromWebsite.class);
                //myIntent.putExtra("key", value); //Optional parameters
                ChooseImportMethod.this.startActivity(myIntent);
            }
        });
    }
}
