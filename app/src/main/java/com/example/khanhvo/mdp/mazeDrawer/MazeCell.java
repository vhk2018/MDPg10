package com.example.khanhvo.mdp.mazeDrawer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.khanhvo.mdp.util.Constant;


public class MazeCell {

    protected static int SIZE = 20;
    private static int padding = Constant.MAZE_NO_PADDING;

    private Paint p = new Paint();
    private Paint b = new Paint();

    public MazeCell(int color) {
        p.setColor(color);
        p.setDither(true);
        p.setAntiAlias(true);

        b.setColor(Color.BLACK);
        b.setStyle(Paint.Style.STROKE);
        b.setDither(true);
        b.setAntiAlias(true);
    }

    public static void setPadding(int customPadding) {
        padding = customPadding;
    }

    public void drawCell(Canvas canvas, int x, int y) {
        int CellRadius = 1;
        int xCoordinate = SIZE * (x + padding) + SIZE/2;
        int yCoordinate = SIZE * y + SIZE/2;
        RectF r = new RectF(xCoordinate - SIZE/2, yCoordinate - SIZE/2, xCoordinate + SIZE/2, yCoordinate + SIZE/2);
        canvas.drawRoundRect(r, CellRadius, CellRadius, p);
        canvas.drawRoundRect(r, CellRadius, CellRadius, b);
    }
}
