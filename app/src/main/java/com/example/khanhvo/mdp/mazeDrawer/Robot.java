package com.example.khanhvo.mdp.mazeDrawer;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.khanhvo.mdp.R;
import com.example.khanhvo.mdp.enumType.Command;
import com.example.khanhvo.mdp.util.RemoteController;
import com.example.khanhvo.mdp.util.Constant;

import com.example.khanhvo.mdp.enumType.Direction;


public class Robot {

    private RemoteController rc;
    private final String TAG = "Robot: ";
    private int x = 1;
    private int y = 1;
    private Direction dir = Direction.NORTH;

    public Robot(RemoteController rc) {
        this.rc = rc;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Direction getDir() {
        return this.dir;
    }

    public void setStartCoordinate(int x, int y, Direction dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public boolean move(View view, Command command) {
        switch (command) {
            case MOVE_FORWARD:
                switch (this.dir) {
                    case NORTH:
                        if (y < 21 - Constant.ROBOT_SIZE) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    y += 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_north_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case EAST:
                        if (x < 16 - Constant.ROBOT_SIZE) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    x += 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_east_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case SOUTH:
                        if (y > 1) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    y -= 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_south_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case WEST:
                        if (x > 1) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    x -= 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_west_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            case TURN_RIGHT:
                if (rc.turnRight()) {
                    if (Constant.BUILTIN_MOVEMENT_ON) {
                        dir = dir.getRightDirection();
                    }
                    break;
                } else {
                    return false;
                }
            case MOVE_BACKWARD:
                switch (this.dir) {
                    case SOUTH:
                        if (y < 21 - Constant.ROBOT_SIZE) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    y += 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_north_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case WEST:
                        if (x < 16 - Constant.ROBOT_SIZE) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    x += 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_east_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case NORTH:
                        if (y > 1) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    y -= 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_south_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case EAST:
                        if (x > 1) {
                            if (rc.moveForward()) {
                                if (Constant.BUILTIN_MOVEMENT_ON) {
                                    x -= 1;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            Toast.makeText(view.getContext(), R.string.hit_west_wall, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
            case TURN_LEFT:
                if (rc.turnLeft()) {
                    if (Constant.BUILTIN_MOVEMENT_ON) {
                        dir = dir.getLeftDirection();
                    }
                    break;
                } else {
                    return false;
                }
        }
        if (Constant.LOG) {
            Log.d(TAG, x + "/" + y);
        }
        if (Constant.BUILTIN_MOVEMENT_ON) {
            view.invalidate();
        }
        return true;
    }

}
