package com.example.khanhvo.mdp.util;

import android.util.Log;

import com.example.khanhvo.mdp.cBaseApplication;
import com.example.khanhvo.mdp.enumType.CellStatus;
import com.example.khanhvo.mdp.enumType.Direction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.example.khanhvo.mdp.enumType.Direction.NORTH;


public class ReceiveCommand {

    public static final ReceiveCommand DEFAULT_COMMAND = new ReceiveCommand(Constant.DEFAULT_STATUS);
    public static final CellStatus[][] DEFAULT_GRID = computeGrid(DEFAULT_COMMAND);
    private static ReceiveCommand cache;

    private String str = Constant.EMPTY_STRING;
    String gridValue;
    private Scanner scanner = null;
//    private HashSet obstacles = new HashSet();
    private String mdf1 = "0000000000000000000000000000000000000000000000000000000000000000000000000000";
    private String mdf2 = "000041000000000200008000000000000000000000000000000000000000000000000000000";
    private CellStatus[][] grid;
    private int xCoor = 0;
    private int yCoor = 0;
    private int xA = 0;
    private int yA = 0;
    private String face = "D";
    private Direction dir = NORTH;
    private String status = Constant.EMPTY_STRING;

    private static final String TAG = "ReceiveCommand: ";
    
    public ReceiveCommand(String str) {
        setStr(str);
    }

    private void tokenize() {
        try {
            Log.d(TAG, String.valueOf(str));
            str = str.replace("{", "").replace("}","");//str.substring(1, str.length()-2);           //remove curly brackets
            String[] keyValuePairs = str.split(",");              //split the string to creat key-value pairs
            Map<String,String> map = new HashMap<>();
            Log.d(TAG, "-----------------------------");
            Log.d(TAG, String.valueOf(str));
            Log.d(TAG, "----------------------------------");
            /*for(String pair : keyValuePairs)                        //iterate over the pairs
            {
                String[] entry = pair.split(":");                   //split the pairs to get key and value
                map.put(entry[0], entry[1]);          //add them to the hashmap and trim whitespaces
            }*/
            Log.d(TAG, "#############################");
            for (int i=0;i<keyValuePairs.length;i++) {
                String pair = keyValuePairs[i];
                String[] keyValue = pair.split(":");
                if (keyValue.length>1){
                    Log.d(TAG, keyValue[0] + "=" +keyValue[1]);
                    map.put(keyValue[0].substring(1,keyValue[0].length()-1 ), keyValue[1].substring(1,keyValue[1].length()-1 ));
                }
                //map.put(keyValue[0].toString(), keyValue[1].toString());
            }
            Log.d(TAG, "############"+map.get("grid")+"##############");
            //scanner = new Scanner(this.str);
//            int n = scanner.nextInt();
//            for (int i = 0; i < n; i++) {
//                int x = scanner.nextInt();
//                int y = scanner.nextInt();
//                obstacles.add(new Obstacle(x, y));
//            }
            if (map.get("grid")!=null){
                mdf1 = map.get("grid");//scanner.next();
            } else {

            }
            if (map.get("mdf")!= null){
                cBaseApplication.mdf1 = map.get("mdf");
                Log.d(TAG,cBaseApplication.mdf1);
                //mdf2 = map.get("mdf");//scanner.next();
            }
            if (map.get("x") != null){
                xCoor = Integer.valueOf(map.get("x"));//scanner.nextInt();
            }
            if (map.get("y")!=null){
                yCoor = 17 - Integer.valueOf(map.get("y"));//scanner.nextInt();
            }
            if (map.get("xA") != null){
                xA = Integer.valueOf(map.get("xA"));//scanner.nextInt();
            }
            if (map.get("yA")!=null){
                yA = 17 - Integer.valueOf(map.get("yA"));//scanner.nextInt();
            }
            if (map.get("dirA")!=null){
                face = map.get("dirA");//scanner.nextInt();
            }
            if (map.get("dir")!= null){
                //dir = Direction.getEnum(Integer.valueOf(map.get("dir")));//Direction.getEnum(scanner.nextInt());
                //dir = Direction.getEnum(Integer.valueOf(map.get("dir").charAt(0)));//.substring(0,0)));//Direction.getEnum(scanner.nextInt());
                Log.d(TAG, String.valueOf(dir));
                switch(map.get("dir").charAt(0)){
                    case '0': dir = Direction.getEnum(0); break;
                    case '1': dir = Direction.getEnum(1); break;
                    case '2': dir = Direction.getEnum(2); break;
                    case '3': dir = Direction.getEnum(3); break;
                }
            }
            if (map.get("status")!=null){
                status = map.get("status");//scanner.nextLine().trim();
            }
            if (map.get("grid")!=null){
                cBaseApplication.gridValue = map.get("grid");

            }
            grid = computeGrid(this);
            Log.d(TAG, "New grid "+ map.get("grid"));
            Log.d(TAG, "New grid "+ cBaseApplication.gridValue);
            Log.d(TAG, "New grid "+ Arrays.deepToString(grid));
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
        /*String bin1 = hexToBin(rc.mdf1);
        bin1 = bin1.substring(2, bin1.length() - 2);
        String bin2 = hexToBin(rc.mdf2);
        if (Constant.LOG) {
            Log.d(TAG, "mdf1= " + bin1);
            Log.d(TAG, "mdf2= " + bin2);
        }
        char[] bin1Arr = bin1.toCharArray();
        char[] bin2Arr = bin2.toCharArray();*/

        CellStatus[][] grid = new CellStatus[Constant.HEIGHT][Constant.WIDTH];
        for (int i = 0; i < Constant.HEIGHT; i++) {
            grid[i] = new CellStatus[Constant.WIDTH];
        }
        char[] tempGrid = hexToBin(cBaseApplication.gridValue ).toCharArray();
        char[] tempExploreGrid = hexToBin(cBaseApplication.mdf1 ).toCharArray();
        //char[] tempGrid = hexToBin("800100020007000000000000000000000000000000000000000000000000000000000000000").toCharArray();
        Log.d(TAG, String.valueOf(tempGrid));
        Log.d(TAG, String.valueOf(tempGrid.length));
        //grid[4][4]=CellStatus.OBSTACLE;
        char[][] realGrid = new char[Constant.HEIGHT][Constant.WIDTH];
        char[][] realExploreGrid = new char[Constant.HEIGHT][Constant.WIDTH];
        int index = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j <15 ; j++) {
                realGrid[i][j] = tempGrid[index];
                //Log.d(TAG, String.valueOf(index));
                index++;
            }
        }
        index = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j <15 ; j++) {
                realExploreGrid[i][j] = tempExploreGrid[index];
                //Log.d(TAG, String.valueOf(index));
                index++;
            }
        }
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 15; j++) {
                //Log.d("grid", String.valueOf(realGrid[i][j]));
                if (realExploreGrid[i][j] == '0'){
                    grid[19-i][j]=CellStatus.UNEXPLORED;
                } else {
                    if (realGrid[i][j] == '1') {
                        grid[19 - i][j] = CellStatus.OBSTACLE;
                    } else {
                        grid[19 - i][j] = CellStatus.FREE;
                    }
                }
            }
        }
        /*
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
        }*/
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

    public int getXA() {
        return this.xA;
    }

    public int getYA() {
        return this.yA;
    }

    public Direction getDir() {
        return this.dir;
    }

    public String getStatus() {
        return this.status;
    }

    public String getFace() {
        return this.face;
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
