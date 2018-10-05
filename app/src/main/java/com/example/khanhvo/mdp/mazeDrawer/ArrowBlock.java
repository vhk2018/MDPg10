package com.example.khanhvo.mdp.mazeDrawer;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.khanhvo.mdp.R;
import com.example.khanhvo.mdp.enumType.Command;
import com.example.khanhvo.mdp.enumType.Direction;
import com.example.khanhvo.mdp.util.RemoteController;
import com.example.khanhvo.mdp.util.Constant;

public class ArrowBlock {
    private RemoteController rc;
    private final String TAG = "ArrowBlock: ";
    private int x;// = 7;
    private int y;// = 10;
    private String face;

    public ArrowBlock() {
        this.rc = rc;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String getFace() {
        return this.face;
    }

    public void set(int x, int y, String face) {
        this.x = x;
        this.y = y;
        this.face = face;
    }
}
