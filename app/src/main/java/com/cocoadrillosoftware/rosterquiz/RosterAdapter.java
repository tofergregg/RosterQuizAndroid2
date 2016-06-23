package com.cocoadrillosoftware.rosterquiz;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Tofer on 6/23/16.
 */
public class RosterAdapter extends ArrayAdapter<Student> {

    public RosterAdapter(Activity activity, List<Student> imageAndTexts) {
        super(activity, 0, imageAndTexts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate the views from XML
        View rowView = inflater.inflate(R.layout.image_and_text, null);
        Student student = getItem(position);

        // Load the image and set it on the ImageView
        if (student.picture != null) {
            Bitmap b = student.picture.getBitmap();

            ImageView imageView = (ImageView) rowView.findViewById(R.id.studentImage);
            imageView.setImageDrawable(new BitmapDrawable(parent.getResources(),b));
            //imageView.setImageDrawable(loadImageFromUrl(imageAndText.getImageUrl()));
        }

        // Set the text on the TextView
        TextView textView = (TextView) rowView.findViewById(R.id.studentText);
        textView.setText(student.commaName());

        return rowView;
    }
}