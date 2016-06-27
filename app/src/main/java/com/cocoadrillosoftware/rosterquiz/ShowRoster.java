package com.cocoadrillosoftware.rosterquiz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
        roster = Roster.load(getApplicationContext(),rosterFilename);
        adapter = new RosterAdapter(this, roster);
        studentsTable.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

        final ImageButton newStudentButton = (ImageButton) findViewById(R.id.newStudent);
        newStudentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Student blank_student = new Student();
                blank_student.lastName = "Last Name";
                blank_student.firstName = "First Name";
                currentPosition = -1; // will have to append when we return
                Intent intent = new Intent(ShowRoster.this, ShowStudent.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("student", blank_student); //Optional parameters
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
            if (currentPosition == -1) {
                // back from new student
                roster.add(s);
            }
            else {
                roster.set(currentPosition, s);
            }
            roster.sortStudents();
            adapter.notifyDataSetChanged();
            rosterChanged = true;
        }
    }

}
