package com.cocoadrillosoftware.rosterquiz;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        // set up new roster button
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowStudent.this, ShowRoster.class);
                // just go back, don't start new activity
                intent.setAction(Intent.ACTION_ATTACH_DATA);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Student s = new Student();
                s.lastName = lastText.getText().toString();
                s.firstName = firstText.getText().toString();
                s.notes = notesText.getText().toString();
                s.year = yearText.getText().toString();
                s.setPictureFromDrawable(picture.getDrawable());

                intent.putExtra("student", s); //Send back student
                intent.putExtra("sender","ShowStudent");
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        //Save state here
    }
}
