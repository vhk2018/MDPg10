package com.example.khanhvo.mdp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


public class Constant {

    private Constant() {

    }

    public static final boolean LOG = true;

    public static final String RPI1 = "00:02:72:33:8C:C1";
    public static final String RPI2 = "00:02:72:DB:F9:2B";
    public static boolean RPI1_IN_USE = false;
    public static String RPI_MAC_ADDR = RPI1_IN_USE ? RPI1 : RPI2;

    //    Robot configuration
    public static final int ROBOT_SIZE = 3;

    //    Map configuration
    public static final int MAZE_PADDED = 2;
    public static final int MAZE_NO_PADDING = 0;
    public static final int GOAL_SIZE = 3;
    public static final int HEIGHT = 20;
    public static final int WIDTH = 15;

    public static boolean AUTO_REFRESH_ENABLED = false;
    public static boolean BUILTIN_MOVEMENT_ON = true;
    public static boolean OVERRIDE_BLUETOOTH_DISCONNECTED = true;

    private static final char[] DEFAULT_MDF = new char[76];
    static {
        Arrays.fill(DEFAULT_MDF, '0');
    }
    public static final String DEFAULT_STATUS = new StringBuilder().append(new String(DEFAULT_MDF)).append(" 0 0 0 1 Initialized").toString();

    //    Message constant
    public static final String START_X = "STARTING_X";
    public static final String START_Y = "STARTING_Y";
    public static final String X_OUT_OF_BOUND = "x-coordinate must be within 0-" + (20 - ROBOT_SIZE);
    public static final String Y_OUT_OF_BOUND = "y-coordinate must be within 0-" + (15 - ROBOT_SIZE);
    public static final String EMPTY_STRING = "";

//    Navigation drawer items
    /*public static final ArrayList<CustomMenuItem> drawerItemTitles = new ArrayList<>();
    static {
        for (CustomMenuItem c : CustomMenuItem.values()) {
            drawerItemTitles.add(c);
        }
    }*/

    public static final String MESSAGE_READ = "message_read";
    public static final String MESSAGE_WRITE = "message_write";
    public static final String MESSAGE_TOAST = "message_toast";
    public static final String MESSAGE_STATE_CHANGE = "message_state_change";
    public static final String MESSAGE_DEVICE_NAME = "message_device_name";
    public static final String MESSAGE_STATUS = "message_status";

    public static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String NAME_SECURE = "BluetoothSecure";
    public static final String NAME_INSECURE = "BluetoothInsecure";


    public static final String READ_MESSAGE = "read_message";
    public static final String EXTRA_BUFFER = "extra_buffer";
    public static final String EXTRA_NUMBYTES = "extra_numbytes";
    public static final String EXTRA_STRING = "extra_string";

    // ... (Add other message types here as needed.)
}