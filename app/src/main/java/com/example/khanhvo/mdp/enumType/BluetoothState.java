package com.example.khanhvo.mdp.enumType;

public enum BluetoothState {
    NONE(0), LISTENING(1), CONNECTING(2), CONNECTED(3);

    private int code;

    BluetoothState(int code) {
        this.code = code;
    }

    public static BluetoothState getEnum(int code) {
        switch (code) {
            case 1:
                return LISTENING;
            case 2:
                return CONNECTING;
            case 3:
                return CONNECTED;
            default:
                return NONE;
        }
    }

    public int getCode() {
        return this.code;
    }
}
