package com.cocoadrillosoftware.rosterquiz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ShowRoster extends AppCompatActivity {
    Roster roster;
    RosterAdapter adapter;
    final String rostersFolderName = "Rosters";
    int currentPosition;
    boolean rosterChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_roster);
        final ListView studentsTable = (ListView) findViewById(R.id.studentsListView);

        Intent i = getIntent();
        String rosterFilename = i.getStringExtra("rosterFilename");
        // read roster from temp file
        try {
            File rostersFolder = getDir(rostersFolderName, Context.MODE_PRIVATE);
            File tempFile = new File(rostersFolder,rosterFilename);
            FileInputStream fis = new FileInputStream(tempFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                roster = (Roster) ois.readObject();
                adapter = new RosterAdapter(this, roster);
                studentsTable.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
            fis.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        studentsTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                currentPosition = position;
                final Student student = roster.get(position);
                System.out.println("Clicked on "+student.toString());
                // save the roster to transfer it

                // send
                Intent intent = new Intent(ShowRoster.this, ShowStudent.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("student", student); //Optional parameters
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (rosterChanged) {
            Roster.save(getApplicationContext(),roster);
        }
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String sender = intent.getStringExtra("sender");
        if (sender != null && sender.equals("ShowStudent")) {
            System.out.println("Back from Students with student to save.");
            Student s = (Student) intent.getSerializableExtra("student");
            roster.set(currentPosition,s);
            rosterChanged = true;
        }
    }

}
