package com.example.khanhvo.mdp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khanhvo.mdp.behaviour.ResetDialogWrapper;
import com.example.khanhvo.mdp.behaviour.ToastWrapper;
import com.example.khanhvo.mdp.enumType.CellStatus;
import com.example.khanhvo.mdp.enumType.Command;
import com.example.khanhvo.mdp.enumType.Direction;
import com.example.khanhvo.mdp.enumType.ResetCode;
import com.example.khanhvo.mdp.mazeDrawer.MazeView;
import com.example.khanhvo.mdp.util.Constant;
import com.example.khanhvo.mdp.util.ReceiveCommand;
import com.example.khanhvo.mdp.util.RemoteController;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import me.aflak.bluetooth.Bluetooth;

//import com.example.khanhvo.mdp.dialog.ResetDialogFragment;

public class InteractiveControlActivity extends AppCompatActivity implements ToastWrapper, ResetDialogWrapper, SensorEventListener, Bluetooth.CommunicationCallback {

    private static RemoteController rc;
    private final String TAG = "InteractiveActivity: ";
    private DrawerLayout nDrawerLayout;
    private ActionBarDrawerToggle nToggle;
    TextView robotStatusView;
    Switch nSwitch;
    ImageView refresh;
    Boolean updateManual = false;
    public int index = 0;
    private MazeView mazeView;
    private Button explore;
    private Button run;
    Button calibration;
    Button btn_L;
    Button btn_R;
    private Bluetooth b;
    private BluetoothService mBluetoothService;
    private String name;
    Canvas canvas = new Canvas();
    private SensorManager sensorManager;
    private Sensor sensor;
    long lastUpdate;

    //NEW
    Button btn_StopChronometer;
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

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
        calibration = (Button) findViewById(R.id.calibration_button);
        btn_L = (Button) findViewById(R.id.l_button);
        btn_R = (Button) findViewById(R.id.r_button);
        robotStatusView = (TextView) findViewById(R.id.robot_current_status);
        nToggle = new ActionBarDrawerToggle(this, nDrawerLayout, R.string.open, R.string.close);
        refresh =(ImageView) findViewById(R.id.refresh_button);
        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        nSwitch = (Switch) findViewById(R.id.switch1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //declaring Sensor Manager and sensor type
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        lastUpdate = System.currentTimeMillis();

        IntentFilter mFilter = new IntentFilter(Constant.MESSAGE_READ);
        LocalBroadcastManager.getInstance(this).registerReceiver(localBluetoothReceiver, mFilter);

        Intent i = getIntent();
        int x = i.getIntExtra(Constant.START_X, 1);
        int y = i.getIntExtra(Constant.START_Y, 1);
        RelativeLayout mazeLayout = (RelativeLayout) findViewById(R.id.maze_layout);
        rc = new RemoteController(this);
        mazeView = new MazeView(this, y, x, Constant.MAZE_PADDED, rc);
        cBaseApplication.mazeView = mazeView;


        //NEW
        btn_StopChronometer = (Button)findViewById(R.id.btn_StopChronometer);
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                /*if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(MainActivity.this, "Bing!", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });

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

        //mazeView.drawArrowBlock(canvas,1,8,10);

        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cBaseApplication.arrowString = "";
                    cBaseApplication.arrowArray = new ArrayList<String>();
                    //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("Av".toString().getBytes(Charset.defaultCharset()));
                    cBaseApplication.mBluetoothChat.write(("Pexs{"+(mazeView.robot.getX()+1)+"},{"+(mazeView.robot.getY()+1)+"}").getBytes(Charset.defaultCharset()));

                    //NEW
                    resetChronometer(chronometer);
                    startChronometer(chronometer);
                } catch (NullPointerException e){
                    sendToast("NO CONNECTION");
                }

            }
        });

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("Aw".toString().getBytes(Charset.defaultCharset()));
                    cBaseApplication.mBluetoothChat.write(("Pfps").getBytes(Charset.defaultCharset()));

                    //NEW
                    resetChronometer(chronometer);
                    startChronometer(chronometer);
                } catch (NullPointerException e){
                    sendToast("NO CONNECTION");
                }
            }
        });

        calibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("Au".toString().getBytes(Charset.defaultCharset()));

                } catch (NullPointerException e){
                    sendToast("NO CONNECTION");
                }

            }
        });

        btn_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("Av".toString().getBytes(Charset.defaultCharset()));

                } catch (NullPointerException e){
                    sendToast("NO CONNECTION");
                }

            }
        });

        btn_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("Aw".toString().getBytes(Charset.defaultCharset()));

                } catch (NullPointerException e){
                    sendToast("NO CONNECTION");
                }
            }
        });

        nSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    refresh.setVisibility(View.GONE);
                    try {
                        ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("sendArena".toString().getBytes(Charset.defaultCharset()));
                    } catch (NullPointerException e){
                        sendToast("NO CONNECTION");
                    }
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

        //NEW
        btn_StopChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running){
                    pauseChronometer(chronometer);
                    //btn_StopChronometer.setText("Reset");
                }
                else {
                    //resetChronometer(chronometer);
                    //btn_StopChronometer.setText("Stop");
                }
            }
        });

        /*b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        //Display("Connecting...");
        b.connectToDevice(b.getPairedDevices().get(pos));*/

    }

    //NEW
    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        } else {
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
    }

    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void resetChronometer(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
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
        int xA = receiveCommand.getXA();
        int yA = receiveCommand.getYA();
        String face = receiveCommand.getFace().replace("\"","");
        Log.d(TAG, "*************"+String.valueOf(xA));
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


        Log.d("TAG","INDEX="+index);
        if ((xA!=0 || yA!=0) && index <50){
            Log.d(TAG, "~~~~~~"+String.valueOf(yA)+"~~"+String.valueOf((yA*15+xA) + "~~~~~~"+String.valueOf(cBaseApplication.binaryGrid.charAt(yA*15+xA -1)) +cBaseApplication.binaryGrid.charAt(yA*15+xA) + cBaseApplication.binaryGrid.charAt(yA*15+xA+1)));
            if (cBaseApplication.binaryGrid.charAt(yA*15+xA) == '1'){
                Log.d(TAG,"&&&&&" + face);
                //mazeView.setArrow(mazeView.arrowBlock.get(index), 5, 3+2, "U");
                mazeView.setArrow(mazeView.arrowBlock.get(index), xA, 19-yA-2, face);
                //cBaseApplication.arrowString += "(" + xA + "," + yA + "," + face + ")     ";
                if (!cBaseApplication.arrowArray.contains("(" + xA + "," + yA + "," + face + ")")){
                    cBaseApplication.arrowArray.add("(" + xA + "," + yA + "," + face + ")");
                    cBaseApplication.arrowString += "(" + xA + "," + yA + "," + face + ")     ";
                }
                index++;
            }
        } else {
            mazeView.setCoordinate(x-1, y+1, dir);
//      mazeView.addObstacles(obstacles);
            mazeView.setGrid(grid);
        }

        robotStatusView = (TextView) findViewById(R.id.robot_current_status);
        robotStatusView.setText(status);
        Log.d(TAG,status);
        Log.d(TAG,"set grid "+ Arrays.deepToString(grid));
    }

    public void moveForward(View view) {
        //mazeView.moveByButton(Command.MOVE_FORWARD);
        //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("f".toString().getBytes(Charset.defaultCharset()));
        MDFString mdfString = new MDFString();
        mdfString.setMDF();
        mdfString.show(getSupportFragmentManager(),"show_mdf");
    }

    public void turnLeft(View view) {
        mazeView.moveByButton(Command.TURN_LEFT);
        try {
            ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tl".toString().getBytes(Charset.defaultCharset()));
        } catch (NullPointerException e){
            sendToast("NO CONNECTION");
        }

    }

    public void turnRight(View view) {
        mazeView.moveByButton(Command.TURN_RIGHT);
        try {
            ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tr".toString().getBytes(Charset.defaultCharset()));
        } catch (NullPointerException e){
            sendToast("NO CONNECTION");
        }

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        //mHandler = new Handler();
        /*

        float x = event.values[0];
        float y = event.values[1];

        Log.d(TAG, "onSensorChanged: X: " + x + ", onSensorChanged: Y: " + y);


        if (Math.abs(x) > Math.abs(y)) {
            if (x < -4) {
                //mazeView.moveByButton(Command.TURN_RIGHT);
                //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tr".toString().getBytes(Charset.defaultCharset()));
                //sendToast("RIGHT");

            }
            if (x > 4) {
                //mazeView.moveByButton(Command.TURN_LEFT);
                //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tl".toString().getBytes(Charset.defaultCharset()));
                //sendToast("LEFT");
            }
        } else {
            if (y < -2) {
                //mazeView.moveByButton(Command.MOVE_FORWARD);
                //((cBaseApplication)getApplicationContext()).mBluetoothChat.write("f".toString().getBytes(Charset.defaultCharset()));
                //sendToast("UP");
            }
            if (y > 4) {

            }
        }
        if (x > (-4) && x < (4) && y > (-2) && y < (4)) {
            Log.d(TAG, "Stable");
        }
    /*
        float x = event.values[0];
        float y = event.values[1];
        if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) {
                //image.setImageResource(R.drawable.right);
                //textView.setText("You tilt the device right");
                mazeView.moveByButton(Command.TURN_RIGHT);
                ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tr".toString().getBytes(Charset.defaultCharset()));
                sendToast("RIGHT");
            }
            if (x > 0) {
                //image.setImageResource(R.drawable.left);
                //textView.setText("You tilt the device left");
                mazeView.moveByButton(Command.TURN_LEFT);
                ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("tl".toString().getBytes(Charset.defaultCharset()));
                sendToast("LEFT");
            }
        } else {
            if (y < 0) {
                //image.setImageResource(R.drawable.up);
                //textView.setText("You tilt the device up");
                mazeView.moveByButton(Command.MOVE_FORWARD);
                ((cBaseApplication)getApplicationContext()).mBluetoothChat.write("f".toString().getBytes(Charset.defaultCharset()));
                sendToast("UP");
            }
            if (y > 0) {
                //image.setImageResource(R.drawable.down);
                //textView.setText("You tilt the device down");
            }
        }
        if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
            //image.setImageResource(R.drawable.center);
            //textView.setText("Not tilt device");
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
