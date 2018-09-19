package com.example.khanhvo.mdp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.khanhvo.mdp.behaviour.ToastWrapper;
//import com.example.khanhvo.mdp.menu.CustomListAdapter;
//import com.mdp17.group12.labmoverscontroller.menu.CustomMenuItem;
import com.example.khanhvo.mdp.util.Constant;
import com.example.khanhvo.mdp.util.RemoteController;

public class PreInteractiveActivity extends MainActivity implements ToastWrapper {

    private final String TAG = "PreInteractive: ";

    private static RemoteController rc;

    private EditText xInput;
    private EditText yInput;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_interactive);
/*
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);*/

        rc = new RemoteController(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        /*// Set the adapter for the list view
        mDrawerList.setAdapter(new CustomListAdapter(this, Constant.drawerItemTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });*/


        mTitle = mDrawerTitle = getTitle();
        if (Constant.LOG) {
            Log.d(TAG, "Initial Title: " + mTitle);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        EditText x = (EditText) findViewById(R.id.coordinate_X);
        EditText y = (EditText) findViewById(R.id.coordinate_Y);
        x.setText("0");
        y.setText("0");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        switch (item.getItemId()) {

        }

        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call supportInvalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }
/*
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        if (Constant.LOG) {
            Log.d(TAG, "Current Position: " + position);
        }
        Intent i;
        CustomMenuItem item = Constant.drawerItemTitles.get(position);
        switch (item) {
            case INTERACTIVE:
                i = new Intent(this, CustomMenuItem.INTERACTIVE.getIntent());
                startActivity(i);
                break;
            case LEADERBOARD:
                i = new Intent(this, CustomMenuItem.LEADERBOARD.getIntent());
                startActivity(i);
                break;
            case SCAN_DEVICE:
                i = new Intent(this, CustomMenuItem.SCAN_DEVICE.getIntent());
                startActivity(i);
                break;
            case CONFIGURABLES:
                i = new Intent(this, CustomMenuItem.CONFIGURABLES.getIntent());
                startActivity(i);
                break;
        }
    }
*/
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void runInteractive(View view) {
        if (Constant.LOG) {
            Log.d(TAG, "To Interactive Map...");
        }
        Intent i = new Intent(this, InteractiveControlActivity.class);
        xInput = (EditText) this.findViewById(R.id.coordinate_X);
        yInput = (EditText) this.findViewById(R.id.coordinate_Y);
        try {
            int x = Integer.parseInt(xInput.getText().toString());
            int y = Integer.parseInt(yInput.getText().toString());
            if (x > 20 - Constant.ROBOT_SIZE || x < 0) {
                Toast.makeText(this, Constant.X_OUT_OF_BOUND, Toast.LENGTH_SHORT).show();
            } else if (y > 15 - Constant.ROBOT_SIZE || y < 0) {
                Toast.makeText(this, Constant.Y_OUT_OF_BOUND, Toast.LENGTH_SHORT).show();
            } else {
                if (!rc.setCoordinate(x, y)) {
                    return;
                }
                i.putExtra(Constant.START_X, x);
                i.putExtra(Constant.START_Y, y);
                startActivity(i);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_coordinate, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void sendToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
