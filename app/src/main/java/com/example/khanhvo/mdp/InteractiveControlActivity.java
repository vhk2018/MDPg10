package com.example.khanhvo.mdp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khanhvo.mdp.behaviour.ResetDialogWrapper;
import com.example.khanhvo.mdp.behaviour.ToastWrapper;
//import com.example.khanhvo.mdp.dialog.ResetDialogFragment;
import com.example.khanhvo.mdp.enumType.CellStatus;
import com.example.khanhvo.mdp.enumType.Command;
import com.example.khanhvo.mdp.enumType.Direction;
import com.example.khanhvo.mdp.enumType.ResetCode;
import com.example.khanhvo.mdp.mazeDrawer.MazeView;
import com.example.khanhvo.mdp.util.Constant;
import com.example.khanhvo.mdp.util.ReceiveCommand;
import com.example.khanhvo.mdp.util.RemoteController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import me.aflak.bluetooth.Bluetooth;

public class InteractiveControlActivity extends AppCompatActivity implements ToastWrapper, ResetDialogWrapper, Bluetooth.CommunicationCallback {

    private static RemoteController rc;
    private final String TAG = "InteractiveActivity: ";
    private DrawerLayout nDrawerLayout;
    private ActionBarDrawerToggle nToggle;
    TextView robotStatusView;
    Switch nSwitch;
    ImageView refresh;
    Boolean updateManual = false;
    private MazeView mazeView;
    private Button explore;
    private Button run;
    private Bluetooth b;
    private BluetoothService mBluetoothService;
    private String name;
    Canvas canvas = new Canvas();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_control);

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);*/
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        nDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        explore = (Button) findViewById(R.id.explore_button);
        run = (Button) findViewById(R.id.run_button);
        robotStatusView = (TextView) findViewById(R.id.robot_current_status);
        nToggle = new ActionBarDrawerToggle(this, nDrawerLayout, R.string.open, R.string.close);
        refresh =(ImageView) findViewById(R.id.refresh_button);
        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        nSwitch = (Switch) findViewById(R.id.switch1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IntentFilter mFilter = new IntentFilter(Constant.MESSAGE_READ);
        LocalBroadcastManager.getInstance(this).registerReceiver(localBluetoothReceiver, mFilter);

        Intent i = getIntent();
        int x = i.getIntExtra(Constant.START_X, 1);
        int y = i.getIntExtra(Constant.START_Y, 1);
        RelativeLayout mazeLayout = (RelativeLayout) findViewById(R.id.maze_layout);
        rc = new RemoteController(this);
        mazeView = new MazeView(this, y, x, Constant.MAZE_PADDED, rc);
        cBaseApplication.mazeView = mazeView;
        /*mazeView.setOnTouchListener(new OnSwipeListener(InteractiveControlActivity.this) {

            public void onSwipeUp() {
                if (Constant.LOG) {
                    Log.d(TAG, "Up");
                }
                mazeView.moveBySwipe(Direction.NORTH);
            }

            public void onSwipeRight() {
                if (Constant.LOG) {
                    Log.d(TAG, "Right");
                }
                mazeView.moveBySwipe(Direction.EAST);
            }

            public void onSwipeDown() {
                if (Constant.LOG) {
                    Log.d(TAG, "Down");
                }
                mazeView.moveBySwipe(Direction.SOUTH);
            }

            public void onSwipeLeft() {
                if (Constant.LOG) {
                    Log.d(TAG, "Left");
                }
                mazeView.moveBySwipe(Direction.WEST);
            }
        });*/
        mazeLayout.addView(mazeView);
        //mazeLayout.onTouchEvent()
        // Implement it's on touch listener.
        mazeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // Show an alert dialog.
                //AlertDialog alertDialog = new AlertDialog.Builder(InteractiveControlActivity.this).create();
                //alertDialog.setMessage("You touched the Linear Layout.");
                //alertDialog.show();



                SetCoordinates setCoordinates = new SetCoordinates();
                setCoordinates.show(getSupportFragmentManager(),"set_coordinates");

                // Return false, then android os will still process click event,
                // if return true, the on click listener will never be triggered.
                return false;
            }
        });

        mazeView.drawArrowBlock(canvas,1,8,10);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("beginExplore".toString().getBytes(Charset.defaultCharset()));

            }
        });

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("beginFastest".toString().getBytes(Charset.defaultCharset()));

            }
        });

        nSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refresh.setVisibility(View.GONE);
                    ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("sendArena".toString().getBytes(Charset.defaultCharset()));
                } else {
                    // The toggle is disabled
                    refresh.setVisibility(View.VISIBLE);
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("sendArena".toString().getBytes(Charset.defaultCharset()));
                ReceiveCommand receiveCommand = new ReceiveCommand(cBaseApplication.mazeGrid);
                update(receiveCommand);
            }
        });

        /*b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        //Display("Connecting...");
        b.connectToDevice(b.getPairedDevices().get(pos));*/

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.interactive_menu, menu);
        MenuItem resetObstacles = menu.findItem(R.id.obstacles_reset);
        resetObstacles.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DialogFragment obstaclesReset = ResetDialogFragment.newInstance(R.string.obstacles_reset_title, ResetCode.OBSTACLES_RESET_CODE);
                obstaclesReset.show(getSupportFragmentManager(), TAG);
                return true;
            }
        });
        MenuItem resetRobot = menu.findItem(R.id.robot_reset);
        resetRobot.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DialogFragment robotReset = ResetDialogFragment.newInstance(R.string.robot_reset_title, ResetCode.ROBOT_RESET_CODE);
                robotReset.show(getSupportFragmentManager(), TAG);
                return true;
            }
        });
        return true;
    }
*/

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            cBaseApplication.mazeGrid =text;
            Log.d(TAG, text);
            //robotStatusView.setText(text);

            if (text != null && text != "" && nSwitch.isChecked()){
                ReceiveCommand receiveCommand = new ReceiveCommand(text);
                update(receiveCommand);

            }
        }
    };

    private final BroadcastReceiver localBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            Log.d(TAG, text);
            Log.d(TAG,cBaseApplication.incomingMessage);
            if (Constant.LOG) {
                Log.d(TAG, "Map receive updates");
            }
            long start = System.nanoTime();
            String update = intent.getExtras().getString(Constant.EXTRA_STRING);
            if (Constant.LOG) {
                Log.d(TAG, update);
            }
            ReceiveCommand receiveCommand = new ReceiveCommand(update);
            update(receiveCommand);
            long end = System.nanoTime();
            if (Constant.LOG) {
                Log.d(TAG, "Refresh Time: " + (end - start));
            }
        }
    };

    /*BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");

            messages.append(text + "\n");

            tv_Receive.setText(messages);
        }
    };



    public void run(){
        byte[] buffer = new byte[1024]; //buffer store for the stream

        int bytes; //bytes returned from read()

        //keep listening to the InputStream until an exception occurs
        while (true){
            //Read from the InputStream
            try{
                bytes = mmInStream.read(buffer);
                String incomingMessage = new String(buffer, 0, bytes);
                Log.d(TAG, "InputStream: " + incomingMessage);

                Intent incomingMessageIntent = new Intent("incomingMessage");
                incomingMessageIntent.putExtra("theMessage", incomingMessage);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
            }
            catch (IOException e){
                Log.e(TAG, "write: Error reading input stream. " + e.getMessage());
                break;
            }
        }
    }*/

    private void update(ReceiveCommand receiveCommand) {
        int x = receiveCommand.getX();
        int y = receiveCommand.getY();
        Direction dir = receiveCommand.getDir();
//        HashSet obstacles = receiveCommand.getObstacles();
        CellStatus[][] grid = receiveCommand.getGrid();
        if (Constant.LOG) {
            Log.d(TAG, String.valueOf(receiveCommand.getGrid()));
        }
        String status = receiveCommand.getStatus();
        if (Constant.LOG) {
            Log.d(TAG, receiveCommand.getStr());
        }
        mazeView.setCoordinate(x, y, dir);
//      mazeView.addObstacles(obstacles);
        mazeView.setGrid(grid);

        robotStatusView = (TextView) findViewById(R.id.robot_current_status);
        robotStatusView.setText(status);
        Log.d(TAG,status);
        Log.d(TAG,"set grid "+ Arrays.deepToString(grid));
    }

    public void moveForward(View view) {
        mazeView.moveByButton(Command.MOVE_FORWARD);
        /*b = new Bluetooth(this);
        mBluetoothService = BluetoothService.getInstance(getApplicationContext());
        b.enableBluetooth();

        b.setCommunicationCallback(this);
        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        b.connectToDevice(b.getPairedDevices().get(pos));*/
        //b = new Bluetooth(this);
        //((cBaseApplication)this.getApplicationContext()).myBlueTooth.send("f");
        /*if (((cBaseApplication)this.getApplicationContext()).myBlueTooth.isConnected()) {
            if (Constant.LOG) {
                Log.d(TAG, "connected");
            }
        } else {
            if (Constant.LOG) {
                Log.d(TAG, "not connected");
            }
        }
        //b.getDevice();
        //b.getSocket();
        if(mBluetoothService.getReconnectionState())
            mBluetoothService.reconnect();
        if (Constant.LOG) {
            Log.d(TAG, String.valueOf(mBluetoothService.getState()));
            Log.d(TAG, String.valueOf(mBluetoothService.getConnectedDevice()));
            Log.d(TAG, String.valueOf(pos));
            Log.d(TAG, String.valueOf(b.getPairedDevices()));
            Log.d(TAG, String.valueOf(b.getSocket()));
            Log.d(TAG, String.valueOf(b.getDevice()));
        }

        b.connectToDevice(b.getDevice());
        if (b.isConnected()) {
            if (Constant.LOG) {
                Log.d(TAG, "connected to " + mBluetoothService.getConnectedDevice().getName());
            }
        } else {
            if (Constant.LOG) {
                Log.d(TAG, "not connected1");
            }
        }*/

        //mBluetoothChat.write(bytes);
        ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("f".toString().getBytes(Charset.defaultCharset()));

        //b.send("f");
    }

    public void turnLeft(View view) {
        mazeView.moveByButton(Command.TURN_LEFT);
        ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tl".toString().getBytes(Charset.defaultCharset()));

    }

    public void turnRight(View view) {
        mazeView.moveByButton(Command.TURN_RIGHT);
        ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tr".toString().getBytes(Charset.defaultCharset()));

    }

    public void moveBackward(View view) {
        mazeView.moveByButton(Command.MOVE_BACKWARD);
    }

    @Override
    public void doPositiveClick(ResetCode action) {
        switch (action) {
            case ROBOT_RESET_CODE:
                if (rc.setCoordinate(0, 0)) {
                    mazeView.resetRobot();
                    if (Constant.LOG) {
                        Log.d(TAG, "Resetting Robot...");
                    }
                }
                break;
            case OBSTACLES_RESET_CODE:
                mazeView.resetObstacles();
                if (Constant.LOG) {
                    Log.d(TAG, "Resetting Obstacles...");
                }
                break;
        }
    }

    @Override
    public void doNegativeClick() {
        if (Constant.LOG) {
            Log.d(TAG, "Reset Cancelled");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBluetoothReceiver);
    }

    @Override
    public void onConnect(BluetoothDevice device) {

    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {

    }
}
