package com.cocoadrillosoftware.rosterquiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Roster> rosters = new ArrayList<Roster>();
    ArrayAdapter adapter;
    final String rostersFolderName = "Rosters";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView rosterTable = (ListView) findViewById(R.id.rosterTable);

        //final rosterTableArrayAdapter adapter = new rosterTableArrayAdapter(this,
        //        android.R.layout.simple_list_item_1, rosterNames);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,rosters);
        rosterTable.setAdapter(adapter);
        registerForContextMenu(rosterTable);

        rosterTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Roster roster = rosters.get(position);
                System.out.println("Clicked on "+roster.toString());
                // save the roster to transfer it

                // send
                Intent intent = new Intent(MainActivity.this, ShowRoster.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("rosterFilename", roster.toString()); //Optional parameters
                startActivity(intent);

            }
        });

        // set up new roster button
        final ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ChooseImportMethod.class);
                //myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        // read in rosters if they have been saved
        File rostersFolder = getDir(rostersFolderName, Context.MODE_PRIVATE);
        if (!rostersFolder.exists())
            rostersFolder.mkdirs();
        // get a list of rosters from the directory
        File files[] = rostersFolder.listFiles();
        // read in each file
        for (File f : files) {
            try {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    rosters.add((Roster) ois.readObject());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
// TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction() == Intent.ACTION_SEND) {
            String rosterIncoming = intent.getStringExtra("rosterIncoming"); // defaults to ""
            if (rosterIncoming != "") {
                // read roster from temp file
                try {
                    File rostersFolder = getDir(rostersFolderName, Context.MODE_PRIVATE);
                    File tempFile = new File(rostersFolder,rosterIncoming);
                    FileInputStream fis = new FileInputStream(tempFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    try {
                        Roster r = (Roster) ois.readObject();
                        rosters.add(r);
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
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.rosterTable) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(rosters.get(info.position).toString());
            String[] menuItems = {"Delete"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0) { // clicked "Delete"
            // get name from title
            rosters.remove(info.position);
            adapter.notifyDataSetChanged();
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}




