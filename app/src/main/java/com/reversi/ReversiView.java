package com.reversi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

public class ReversiView extends SurfaceView implements SurfaceHolder.Callback {
    private final int BOARD_SIZE = 8;
    private final int BOARD_COLOR = Color.GREEN;
    private final int BLACK_COLOR = Color.BLACK;
    private final int WHITE_COLOR = Color.WHITE;

    private SurfaceHolder surfaceHolder;
    private int[][] board;

    public ReversiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        board = new int[BOARD_SIZE][BOARD_SIZE];

        board[3][3] = WHITE_COLOR;
        board[4][4] = WHITE_COLOR;
        board[3][4] = BLACK_COLOR;
        board[4][3] = BLACK_COLOR;
    }

    public ReversiView(Context context) {
        super(context);
    }

    public ReversiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReversiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(BOARD_COLOR);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int CELL_SIZE = canvasWidth / BOARD_SIZE;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);


        paint.setColor(Color.argb(120, 0, 0, 0));
        paint.setStrokeWidth(4);
        for (int i = CELL_SIZE; i < canvasWidth; i += CELL_SIZE) {
            canvas.drawLine(i, 0, i, canvasHeight, paint);
        }

        for (int i = CELL_SIZE; i < canvasHeight; i += CELL_SIZE) {
            canvas.drawLine(0, i, canvasWidth, i, paint);
        }
        paint.setStrokeWidth(0);

        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {

                int width = x * CELL_SIZE + CELL_SIZE / 2;
                int height = y * CELL_SIZE + CELL_SIZE / 2;
                int radius = CELL_SIZE / 2 - 4;

                //Placing first 4
                if (board[x][y] == BLACK_COLOR) {
                    paint.setColor(BLACK_COLOR);
                    canvas.drawCircle(width, height, radius, paint);
                } else if (board[x][y] == WHITE_COLOR) {
                    paint.setColor(WHITE_COLOR);
                    canvas.drawCircle(width, height, radius, paint);
                }
            }
        }

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float cordsX = (int) event.getX() / 100;
        float cordsY = (int) event.getY() / 100;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("Where: ", +cordsX + " " + cordsY);
        }
        return true;
    }
}
