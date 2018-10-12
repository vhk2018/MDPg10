package com.example.khanhvo.mdp.mazeDrawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.example.khanhvo.mdp.R;
import com.example.khanhvo.mdp.cBaseApplication;
import com.example.khanhvo.mdp.enumType.CellStatus;
import com.example.khanhvo.mdp.enumType.Command;
import com.example.khanhvo.mdp.util.Constant;
import com.example.khanhvo.mdp.enumType.Direction;
import com.example.khanhvo.mdp.util.ReceiveCommand;
import com.example.khanhvo.mdp.util.RemoteController;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MazeView extends View {

    private static final int UNEXPLORED_ZONE = Color.GRAY;
    private static final int FREE_ZONE = Color.WHITE;
    private static final int START_ZONE = Color.YELLOW;
    private static final int GOAL_ZONE = Color.RED;
    private static final int OBSTACLE_ZONE = Color.BLACK;
    private static final int NULL_POINTER = Color.GRAY;

    private static final MazeCell UNEXPLORED = new MazeCell(UNEXPLORED_ZONE);
    private static final MazeCell FREE = new MazeCell(FREE_ZONE);
    private static final MazeCell START = new MazeCell(START_ZONE);
    private static final MazeCell GOAL = new MazeCell(GOAL_ZONE);
    private static final MazeCell OBSTACLE = new MazeCell(OBSTACLE_ZONE);
    private static final MazeCell NULL = new MazeCell(NULL_POINTER);

//    private static final HashSet<Obstacle> obstacles = new HashSet<>();
    private final String TAG = "MazeView: ";

    private CellStatus[][] grid = ReceiveCommand.DEFAULT_GRID;
    public Robot robot;
    public WayPoint waypoint;
    public List<ArrowBlock> arrowBlock;
    private int padding = Constant.MAZE_NO_PADDING;

    public void setCoordinate(int x, int y, Direction dir) {
        robot.setStartCoordinate(x + 1, y + 1, dir);
        if (Constant.LOG) {
            Log.d(TAG, robot.getX() + "-" + robot.getY() + "-" + robot.getDir());
        }
        invalidate();
    }
    public void setWaypoint(int x, int y) {
        waypoint.setCoordinate(x, y);
        if (Constant.LOG) {
            Log.d(TAG, waypoint.getX() + "-" + waypoint.getY());
        }
        invalidate();
    }

    public void setArrow(ArrowBlock a, int x, int y, String face) {
        a.set(x, y, face);
        if (Constant.LOG) {
            Log.d(TAG, a.getX() + "-" + a.getY() + "-" + a.getFace());
        }
        invalidate();
    }

    public MazeView(Context context, int x, int y, int padding, RemoteController rc) {
        super(context);
        this.robot = new Robot(rc);
        this.waypoint = new WayPoint(rc);
        this.arrowBlock = new ArrayList<ArrowBlock>();
        cBaseApplication.robot=this.robot;
        for(int i = 0; i <= 30; i++)
        {
            ArrowBlock a = new ArrowBlock();
            a.set(-2, -2, "D");
            arrowBlock.add(i, a);
            //drawArrowBlock(canvas,CELLSIZE,i);
        }

        robot.setStartCoordinate(x + 1, y + 1, Direction.NORTH);
        waypoint.setCoordinate(-1,-1);
        MazeCell.setPadding(padding);
        this.padding = padding;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int CELLSIZE = getWidth() / (Constant.WIDTH + 2 + padding * 2);

//        Draw Maze
        MazeCell.SIZE = CELLSIZE;

        for (int i = 1; i <= Constant.WIDTH; i++) {
            for (int j = 1; j <= Constant.HEIGHT; j++) {
                try {
                    CellStatus cell = grid[Constant.HEIGHT - j][i - 1];
                    switch (cell) {
                        case FREE:
                            FREE.drawCell(canvas, i, j);
                            break;
                        case OBSTACLE:
                            OBSTACLE.drawCell(canvas, i, j);
                            break;
                        default:
                            UNEXPLORED.drawCell(canvas, i, j);
                    }
                } catch (NullPointerException e) {
                    NULL.drawCell(canvas, i, j);
                }
            }
            OBSTACLE.drawCell(canvas, i, 0);
            OBSTACLE.drawCell(canvas, i, Constant.HEIGHT + 1);
        }

        for (int i = 0; i <= Constant.HEIGHT + 1; i++) {
            OBSTACLE.drawCell(canvas, 0, i);
            OBSTACLE.drawCell(canvas, Constant.WIDTH + 1, i);
        }

        for (int i = 1; i <= Constant.GOAL_SIZE; i++) {
            for (int j = 1; j <= Constant.GOAL_SIZE; j++) {
                START.drawCell(canvas, i, Constant.HEIGHT + 1 - j);
                GOAL.drawCell(canvas, i + (Constant.WIDTH - Constant.GOAL_SIZE), Constant.GOAL_SIZE + 1 - j);
            }
        }
//      Draw Waypoint
        drawWaypoint(canvas, CELLSIZE);
        //Draw ArrowBlocks
        for(int i = 0; i <= 30; i++)
        {
            drawArrowBlock(canvas,CELLSIZE,i);
        }
        //for(ArrowBlock a : arrowBlock){
            //drawArrowBlock(canvas,CELLSIZE);
        //}

//        Draw Robot
        drawRobot(canvas, CELLSIZE);


//        Draw Obstacles
//        for (Obstacle o : obstacles) {
//            int oX = o.getX();
//            int oY = o.getY();
//            OBSTACLE.drawCell(canvas, oX + 1, getReverseY(oY) + 1);
//        }
    }

//    public void addObstacles(HashSet obstacles) {
//        this.obstacles.addAll(obstacles);
//        invalidate();
//
//    }

    public void setGrid(CellStatus[][] grid) {
        this.grid = grid;
    }

    private int getReverseY(int y) {
        return Constant.HEIGHT - y - 1;
    }

    private void drawWaypoint(Canvas canvas, int CELLSIZE) {
        int x = waypoint.getX();
        int y = getReverseY(waypoint.getY());
        Matrix matrix = new Matrix();

        matrix.postRotate(180);

        int WAYPOINT_PADDING = CELLSIZE;

        int WAYPOINT_DIAMETER = CELLSIZE - (WAYPOINT_PADDING * 2);

        int WAYPOINT_X = CELLSIZE * x+ WAYPOINT_PADDING;
        int WAYPOINT_Y = CELLSIZE * y+ WAYPOINT_PADDING;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.baseline_location_on_black_48);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, WAYPOINT_DIAMETER, WAYPOINT_DIAMETER, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(rotatedBitmap, WAYPOINT_X + padding * CELLSIZE, WAYPOINT_Y, null);
    }

    private void drawRobot(Canvas canvas, int CELLSIZE) {
        int x = robot.getX();
        int yR = robot.getY();
        int y = getReverseY(yR);
        Direction direction = robot.getDir();
        int ROBOT_PADDING = CELLSIZE / Constant.ROBOT_SIZE;

        int ROBOT_DIAMETER = CELLSIZE * Constant.ROBOT_SIZE - ROBOT_PADDING * 2;
        int ROBOT_X = CELLSIZE * x + ROBOT_PADDING;
        int ROBOT_Y = CELLSIZE * y + ROBOT_PADDING;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.baseline_bug_report_black_48);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, ROBOT_DIAMETER, ROBOT_DIAMETER, true);
        canvas.drawBitmap(scaledBitmap, ROBOT_X + padding * CELLSIZE, ROBOT_Y, null);

//        Draw Head
        Paint paint = new Paint();

        paint.setStrokeWidth(1);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        int xa;
        int ya;
        int xb;
        int yb;
        int xc;
        int yc;

        switch (direction) {
            case NORTH:
                int axis = CELLSIZE * (2 * x + Constant.ROBOT_SIZE) / 2;
                xa = axis;
                ya = ROBOT_Y - ROBOT_PADDING;
                xb = axis + CELLSIZE / 4;
                yb = ROBOT_Y;
                xc = axis - CELLSIZE / 4;
                yc = yb;
//                a = new Point(axis, ROBOT_Y - PADDING);
//                b = new Point(axis + CELLSIZE / 4, ROBOT_Y);
//                c = new Point(axis - CELLSIZE / 4, ROBOT_Y);
                break;
            case SOUTH:
                axis = CELLSIZE * (2 * x + Constant.ROBOT_SIZE) / 2;
                xa = axis;
                ya = (y + Constant.ROBOT_SIZE) * CELLSIZE;
                xb = axis + CELLSIZE / 4;
                yb = ROBOT_Y + ROBOT_DIAMETER;
                xc = axis - CELLSIZE / 4;
                yc = yb;
                break;
            case WEST:
                axis = CELLSIZE * (2 * y + Constant.ROBOT_SIZE) / 2;
                xa = ROBOT_X - ROBOT_PADDING;
                ya = axis;
                xb = ROBOT_X;
                yb = axis + CELLSIZE / 4;
                xc = xb;
                yc = axis - CELLSIZE / 4;
                break;
            default:
                axis = CELLSIZE * (2 * y + Constant.ROBOT_SIZE) / 2;
                xa = (x + Constant.ROBOT_SIZE) * CELLSIZE;
                ya = axis;
                xb = ROBOT_X + ROBOT_DIAMETER;
                yb = axis + CELLSIZE / 4;
                xc = xb;
                yc = axis - CELLSIZE / 4;
        }

        Point a = new Point(xa + padding * CELLSIZE, ya);
        Point b = new Point(xb + padding * CELLSIZE, yb);
        Point c = new Point(xc + padding * CELLSIZE, yc);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    //public void addArrowBlock(int x, int y){
    //    drawArrowBlock(canvas, CELLSIZE, x, y);
    //}
    public void drawArrowBlock(Canvas canvas, int CELLSIZE, int pos){//}, int xArrow, int yArrow) {
        Paint pA = new Paint();
        //ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(this, Color.WHITE), PorterDuff.Mode.SRC_IN);
        pA.setColor(Color.GREEN);

        int xA = (arrowBlock.get(pos)).getX();//xArrow;
        int yA = arrowBlock.get(pos).getY() + 2;
        String face = arrowBlock.get(pos).getFace();
        Matrix matrix = new Matrix();

        switch(face){
            case "L": matrix.postRotate(270);break;
            case "R": matrix.postRotate(90);break;
            case "U": break;
            case "D": matrix.postRotate(180);break;
        }


        int WAYPOINT_PADDING = CELLSIZE;

        int WAYPOINT_DIAMETER = CELLSIZE - (WAYPOINT_PADDING * 2);

        int WAYPOINT_X = CELLSIZE * xA+ WAYPOINT_PADDING;
        int WAYPOINT_Y = CELLSIZE * yA+ WAYPOINT_PADDING;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.baseline_arrow_upward_white_48);
//        bitmap.s
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, WAYPOINT_DIAMETER, WAYPOINT_DIAMETER, true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(rotatedBitmap, WAYPOINT_X + padding * CELLSIZE, WAYPOINT_Y, null);

    }

//    private boolean isCollide() {
//        Direction direction = robot.getDir();
//        int x = robot.getX();
//        int y = robot.getY();
//        if (Constant.LOG) {
//            Log.d(TAG, x + "-" + y);
//        }
//        Obstacle o;
//        boolean collide = false;
//        switch (direction) {
//            case NORTH:
//                int yObs = y + Constant.ROBOT_SIZE;
//                for (int i = x; i < x + Constant.ROBOT_SIZE; i++) {
//                    o = new Obstacle(i - 1, yObs - 1);
//                    if (obstacles.contains(o)) {
//                        collide = true;
//                    }
//                }
//                break;
//            case SOUTH:
//                yObs = y - 1;
//                for (int i = x; i < x + Constant.ROBOT_SIZE; i++) {
//                    o = new Obstacle(i - 1, yObs - 1);
//                    if (obstacles.contains(o)) {
//                        collide = true;
//                    }
//                }
//                break;
//            case WEST:
//                int xObs = x - 1;
//                for (int i = y; i < y + Constant.ROBOT_SIZE; i++) {
//                    o = new Obstacle(xObs - 1, i - 1);
//                    if (obstacles.contains(o)) {
//                        collide = true;
//                    }
//                }
//                break;
//            case EAST:
//                xObs = x + Constant.ROBOT_SIZE;
//                for (int i = y; i < y + Constant.ROBOT_SIZE; i++) {
//                    o = new Obstacle(xObs - 1, i - 1);
//                    if (obstacles.contains(o)) {
//                        collide = true;
//                    }
//                }
//                break;
//        }
//        if (Constant.LOG) {
//            Log.d(TAG, "isCollide=" + collide);
//        }
//        return collide;
//    }

    public void resetObstacles() {
        grid = ReceiveCommand.DEFAULT_GRID;
        invalidate();
    }

    public void resetRobot() {
        setCoordinate(0, 0, Direction.NORTH);
    }

    public void moveBySwipe(Direction swipeDirection) {
        Direction robotDirection = robot.getDir();
        Command command = robotDirection.getSwipeCommand(swipeDirection);
        moveByButton(command);
    }

    public void moveByButton(Command command) {
        robot.move(this, command);
    }

}
