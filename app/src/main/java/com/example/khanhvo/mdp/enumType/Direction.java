package com.example.khanhvo.mdp.enumType;


public enum Direction {
    WEST(0), NORTH(1), EAST(2), SOUTH(3);

    private int code;

    Direction(int code) {
        this.code = code;
    }

    public static Direction getEnum(int code) {
        switch (code) {
            case 0:
                return WEST;
            case 2:
                return EAST;
            case 3:
                return SOUTH;
            default:
                return NORTH;
        }
    }

    public Direction getLeftDirection() {
        int leftCode = (code + 3) % 4;
        return Direction.getEnum(leftCode);
    }

    public Direction getRightDirection() {
        int rightCode = (code + 1) % 4;
        return Direction.getEnum(rightCode);
    }

    public Command getSwipeCommand(Direction swipeDir) {
        int instruction = (this.code - swipeDir.code + 4) % 4;
        Command command = Command.MOVE_FORWARD;
        switch (instruction) {
            case 0:
                command = Command.MOVE_FORWARD;
                break;
            case 1:
                command = Command.TURN_LEFT;
                break;
            case 2:
                command = Command.MOVE_BACKWARD;
                break;
            case 3:
                command = Command.TURN_RIGHT;
                break;
        }
        return command;
    }
}
