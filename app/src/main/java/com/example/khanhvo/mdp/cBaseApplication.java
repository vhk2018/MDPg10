package com.example.khanhvo.mdp;

import android.app.Activity;
import android.app.Application;

import com.example.khanhvo.mdp.mazeDrawer.MazeView;
import com.example.khanhvo.mdp.mazeDrawer.Robot;

import me.aflak.bluetooth.Bluetooth;

public class cBaseApplication extends Application {

    public Bluetooth myBlueTooth;
    private Activity activity;
    //public ConnectedThread mConnectedThread;
    public static BluetoothChatService mBluetoothChat;
    public static MazeView mazeView;
    public static Robot robot;
    public static String incomingMessage;
    public static String gridValue = "000000000000000000000000000000000000000000000000000000000000000000000000000";
    public static String mazeGrid;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //activity = this.activity;
        //myBlueTooth = new Bluetooth(activity);
    }

    public Bluetooth getBluetooth(){
        return myBlueTooth;
    }
    public void setBluetooth(Bluetooth b){
        myBlueTooth = b;
    }

}
