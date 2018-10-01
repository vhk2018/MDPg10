package com.example.khanhvo.mdp.mazeDrawer;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.khanhvo.mdp.R;
import com.example.khanhvo.mdp.enumType.Command;
import com.example.khanhvo.mdp.enumType.Direction;
import com.example.khanhvo.mdp.util.RemoteController;
import com.example.khanhvo.mdp.util.Constant;

public class WayPoint {
    private RemoteController rc;
    private final String TAG = "Waypoint: ";
    private int x;// = 7;
    private int y;// = 10;

    public WayPoint(RemoteController rc) {
        this.rc = rc;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
