package com.cocoadrillosoftware.rosterquiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by chrisgregg on 6/21/16.
 */
public class Roster extends ArrayList<Student> {
    private String name;

    Roster(String name) {
        this.name = name;
    }

    void sortStudents(){
        Collections.sort(this, new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.getLastName().compareTo(o2.getLastName());
            }
        });
    }

    void printRoster(){
        for (Student student : this) {
            student.printStudent();
        }
    }

    public String toString() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }
}
