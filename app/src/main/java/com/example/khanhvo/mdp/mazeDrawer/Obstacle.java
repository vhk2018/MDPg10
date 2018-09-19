package com.example.khanhvo.mdp.mazeDrawer;

import com.example.khanhvo.mdp.util.Constant;


public class Obstacle {

    private int x;
    private int y;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Constant.HEIGHT * x + y;
    }
}
