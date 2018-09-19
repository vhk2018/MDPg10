package com.example.khanhvo.mdp.util;

import android.util.Log;

import com.example.khanhvo.mdp.enumType.CellStatus;
import com.example.khanhvo.mdp.enumType.Direction;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.example.khanhvo.mdp.enumType.Direction.NORTH;


public class ReceiveCommand {

    public static final ReceiveCommand DEFAULT_COMMAND = new ReceiveCommand(Constant.DEFAULT_STATUS);
    public static final CellStatus[][] DEFAULT_GRID = computeGrid(DEFAULT_COMMAND);
    private static ReceiveCommand cache;

    private String str = Constant.EMPTY_STRING;
    private Scanner scanner = null;
//    private HashSet obstacles = new HashSet();
    private String mdf1;
    private String mdf2;
    private CellStatus[][] grid;
    private int xCoor = 0;
    private int yCoor = 0;
    private Direction dir = NORTH;
    private String status = Constant.EMPTY_STRING;

    private static final String TAG = "ReceiveCommand: ";
    
    public ReceiveCommand(String str) {
        setStr(str);
    }

    private void tokenize() {
        try {
            scanner = new Scanner(this.str);
//            int n = scanner.nextInt();
//            for (int i = 0; i < n; i++) {
//                int x = scanner.nextInt();
//                int y = scanner.nextInt();
//                obstacles.add(new Obstacle(x, y));
//            }
            mdf1 = scanner.next();
            mdf2 = scanner.next();
            xCoor = scanner.nextInt();
            yCoor = scanner.nextInt();
            dir = Direction.getEnum(scanner.nextInt());
            status = scanner.nextLine().trim();
            grid = computeGrid(this);
            cache = this;
        } catch (InputMismatchException e) {
            if (Constant.LOG) {
                Log.d(TAG, "Exception: Format Mismatch!!");
            }
        } catch (NoSuchElementException e) {
            if (Constant.LOG) {
                Log.d(TAG, "Input Missing!!");
            }
        }
    }
    
    private void setStr(String str) {
        this.str = str;
        tokenize();
    }
    
//    private void appendObstacles(HashSet obstacles) {
//        this.obstacles.addAll(obstacles);
//    }

//    private void setX(int x) {
//        this.xCoor = x;
//    }
//
//    private void setY(int y) {
//        this.yCoor = y;
//    }
//
//    private void setDir(Direction dir) {
//        this.dir = dir;
//    }
//
//    private void setStatus(String status) {
//        this.status = status;
//    }

    private static String hexToBin(String s) {
        StringBuilder str = new StringBuilder();
        for (char c : s.toCharArray()) {
            String bin;
            switch (c) {
                case 'f':
                    bin = "1111";
                    break;
                case 'e':
                    bin = "1110";
                    break;
                case 'd':
                    bin = "1101";
                    break;
                case 'c':
                    bin = "1100";
                    break;
                case 'b':
                    bin = "1011";
                    break;
                case 'a':
                    bin = "1010";
                    break;
                case '9':
                    bin = "1001";
                    break;
                case '8':
                    bin = "1000";
                    break;
                case '7':
                    bin = "0111";
                    break;
                case '6':
                    bin = "0110";
                    break;
                case '5':
                    bin = "0101";
                    break;
                case '4':
                    bin = "0100";
                    break;
                case '3':
                    bin = "0011";
                    break;
                case '2':
                    bin = "0010";
                    break;
                case '1':
                    bin = "0001";
                    break;
                default:
                    bin = "0000";
            }
            str.append(bin);
        }
        return str.toString();
    }

    private static CellStatus[][] computeGrid(ReceiveCommand rc) {
        String bin1 = hexToBin(rc.mdf1);
        bin1 = bin1.substring(2, bin1.length() - 2);
        String bin2 = hexToBin(rc.mdf2);
        if (Constant.LOG) {
            Log.d(TAG, "mdf1= " + bin1);
            Log.d(TAG, "mdf2= " + bin2);
        }
        char[] bin1Arr = bin1.toCharArray();
        char[] bin2Arr = bin2.toCharArray();
        CellStatus[][] grid = new CellStatus[Constant.HEIGHT][Constant.WIDTH];
        for (int i = 0; i < Constant.HEIGHT; i++) {
            grid[i] = new CellStatus[Constant.WIDTH];
        }
        int index = 0;
        for (int i = 0; i < bin1Arr.length; i++) {
            int c = i % Constant.WIDTH;
            int r = i / Constant.WIDTH;
            if (bin1Arr[i] == '0') {
                grid[r][c] = CellStatus.UNEXPLORED;
            } else {
                if (bin2Arr[index] == '0') {
                    grid[r][c] = CellStatus.FREE;
                } else {
                    grid[r][c] = CellStatus.OBSTACLE;
                }
                index++;
            }
        }
        return grid;
    }

    public CellStatus[][] getGrid() {
        return this.grid;
    }
    
    public String getStr() {
        return this.str;
    }

//    public HashSet getObstacles() {
//        return this.obstacles;
//    }

    public String getMdf1() {
        return this.mdf1;
    }

    public String getMdf2() {
        return this.mdf2;
    }

    public int getX() {
        return this.xCoor;
    }

    public int getY() {
        return this.yCoor;
    }

    public Direction getDir() {
        return this.dir;
    }

    public String getStatus() {
        return this.status;
    }

//    public void merge(ReceiveCommand rc) {
//        if (Constant.LOG) {
//            Log.d(TAG, "Queued: " + rc.getStr());
//        }
//        setStr(rc.getStr());
////        appendObstacles(rc.getObstacles());
//        setX(rc.getX());
//        setY(rc.getY());
//        setDir(rc.getDir());
//        setStatus(rc.getStatus());
//        if (Constant.LOG) {
//            Log.d(TAG, "Status: " + this.getStatus());
//        }
//    }

}
