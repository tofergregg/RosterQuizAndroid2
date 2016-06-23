package com.cocoadrillosoftware.rosterquiz;

import android.graphics.drawable.Drawable;

/**
 * Created by chrisgregg on 6/21/16.
 */
public class Student {
    String firstName;
    String lastName;
    String year;
    String notes;
    Drawable picture;

    Student() {

    }

    Student(String last, String first) {
        lastName = last;
        firstName = first;
    }

    void addDetails(String last, String first, String yr, String nts, Drawable pic){
        firstName = first;
        lastName = last;
        year = yr;
        notes = notes;
        picture = pic;
    }

    public void printStudent(){
        System.out.println("Name: " + lastName + " " + firstName + ", year:" + year);
    }

    public String toString() {
        return lastName + ", " + firstName;
    }

    public String commaName() {
        return lastName + ", " + firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
