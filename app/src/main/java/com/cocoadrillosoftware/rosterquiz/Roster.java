package com.cocoadrillosoftware.rosterquiz;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by chrisgregg on 6/21/16.
 */
public class Roster extends ArrayList<Student> {
    final static String rostersFolderName = "Rosters";

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

    public static void save(Context c, Roster r) {
        try {
            File rostersFolder = c.getDir(rostersFolderName, Context.MODE_PRIVATE);
            File outputFile = new File(rostersFolder,r.toString());
            FileOutputStream fos = new FileOutputStream(outputFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(r);
            oos.close();
            fos.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static Roster load(Context c, String rName) {
        try {
            File rostersFolder = c.getDir(rostersFolderName, Context.MODE_PRIVATE);
            File tempFile = new File(rostersFolder,rName);
            FileInputStream fis = new FileInputStream(tempFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                Roster r = (Roster) ois.readObject();
                ois.close();
                fis.close();
                return r;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null; // did not load
    }
    public static void deleteFromStorage(Context c, String rName) {
        File rostersFolder = c.getDir(rostersFolderName, Context.MODE_PRIVATE);
        File tempFile = new File(rostersFolder,rName);
        tempFile.delete();
    }
    public void setName(String n) {
        name = n;
    }
}
