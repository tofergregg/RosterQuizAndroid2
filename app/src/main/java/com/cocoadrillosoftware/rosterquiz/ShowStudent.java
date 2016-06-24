package com.cocoadrillosoftware.rosterquiz;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShowStudent extends AppCompatActivity {

    Student student;
    EditText notesText, lastText, firstText, yearText;
    ImageButton picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_student);

        // set the incoming data
        Intent intent = getIntent();
        student = (Student) intent.getSerializableExtra("student");

        notesText = (EditText) findViewById(R.id.notesText);
        lastText = (EditText) findViewById(R.id.lastText);
        firstText = (EditText) findViewById(R.id.firstText);
        yearText = (EditText) findViewById(R.id.yearText);
        picture = (ImageButton) findViewById(R.id.studentImage);

        if (student.notes != null)
            notesText.setText(student.notes);
        if (student.lastName != null)
            lastText.setText(student.lastName);
        if (student.firstName != null)
            firstText.setText(student.firstName);
        if (student.year != null)
            yearText.setText(student.year);
        if (student.picture != null) {
            picture.setImageDrawable(new BitmapDrawable(getResources(),student.picture.getBitmap()));
        }
    }
}
