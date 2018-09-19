package com.example.khanhvo.mdp.util;

import com.example.khanhvo.mdp.BluetoothService;
import com.example.khanhvo.mdp.R;
import com.example.khanhvo.mdp.behaviour.ToastWrapper;

import static com.example.khanhvo.mdp.enumType.BluetoothState.CONNECTED;


public class RemoteController {

    private static final int ANDROID = 0;
    private static final int ARDUINO = 1;
    private static final int PC = 2;

    private BluetoothService mBluetoothService;
    private ToastWrapper context;

    private static final String FORWARD = "w";
    private static final String REVERSE = "s";
    private static final String TURN_LEFT = "a";
    private static final String TURN_RIGHT = "d";
    private static final String EXPLORATION_START = "e";
    private static final String FASTEST_RUN_START = "y";
    private static final String SET_COORDINATE = "sc";
    private static final String BACK_TO_START = "se";
    private static final String WHITESPACE = " ";

    public RemoteController(ToastWrapper context)
    {
        mBluetoothService = BluetoothService.getInstance(null);
        this.context = context;
    }

    public boolean moveForward() {
        return sendTo(ARDUINO, FORWARD);
    }

    public boolean moveReverse() {
        return sendTo(ARDUINO, REVERSE);
    }

    public boolean turnLeft() {
        return sendTo(ARDUINO, TURN_LEFT);
    }

    public boolean turnRight() {
        return sendTo(ARDUINO, TURN_RIGHT);
    }

    public boolean startExploration() {
        boolean b = sendTo(PC, EXPLORATION_START);
        if (b) {
            context.sendToast(R.string.exploration_started);
        }
        return b;
    }

    public boolean startFastestRun() {

        boolean b = sendTo(PC, FASTEST_RUN_START);
        if (b) {
            context.sendToast(R.string.fastest_started);
        }
        return b;
    }

    public boolean setCoordinate(int x, int y) {
        boolean b = sendTo(PC, new StringBuilder().append(SET_COORDINATE).append(WHITESPACE).append(x).append(WHITESPACE).append(y).toString());
        if (b) {
            context.sendToast(R.string.coordinate_reset);
        }
        return b;
    }

    public boolean backToStart() {
        boolean b = sendTo(PC, BACK_TO_START);
        if (b) {
            context.sendToast(R.string.exploration_stop);
        }
        return b;
    }

    private boolean sendTo(int device, String s) {
        if (!Constant.OVERRIDE_BLUETOOTH_DISCONNECTED && mBluetoothService.getState() != CONNECTED) {
            context.sendToast(R.string.not_connected);
            return false;
        }
        String command = new StringBuilder().append(ANDROID).append(device).append(s).append('\n').toString();
        mBluetoothService.write(command.getBytes());
        return true;
    }
}
