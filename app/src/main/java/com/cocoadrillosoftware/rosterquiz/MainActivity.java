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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final ArrayList<Roster> rosters = new ArrayList<Roster>();
    ArrayAdapter adapter;

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
                final String item = (String) parent.getItemAtPosition(position);

            }
        });

        // set up new roster button
        final ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ChooseImportMethod.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onRestart() {
// TODO Auto-generated method stub
        super.onRestart();
        Roster r = new Roster("New Row " + rosters.size());

        rosters.add(r);
        adapter.notifyDataSetChanged();
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
}




