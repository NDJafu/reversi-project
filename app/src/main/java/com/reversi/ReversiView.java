package com.reversi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import com.reversi.helpers.Player;
import com.reversi.helpers.Position;

import java.util.*;

public class ReversiView extends SurfaceView implements SurfaceHolder.Callback {
    public static Player[][] board;
    private final int BOARD_SIZE = 8;
    private final int BOARD_COLOR = Color.GREEN;
    private final int BLACK_COLOR = Color.BLACK;
    private final int WHITE_COLOR = Color.WHITE;
    public Map<Player, Integer> discCount;
    public Player currentPlayer;
    public Map<Position, List<Position>> legalMoves;
    private int CELL_SIZE;
    private int canvasWidth;
    private int canvasHeight;
    private SurfaceHolder surfaceHolder;
    private boolean gameOver;
    private Player winner;

    public ReversiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        board = new Player[BOARD_SIZE][BOARD_SIZE];

        for (Player[] row : board) {
            Arrays.fill(row, Player.None);
        }

        board[3][3] = Player.White;
        board[4][4] = Player.White;
        board[3][4] = Player.Black;
        board[4][3] = Player.Black;
        discCount = new HashMap<Player, Integer>() {{
            put(Player.Black, 2);
            put(Player.White, 2);
        }};

        currentPlayer = Player.Black;
        legalMoves = findLegalMoves(currentPlayer);


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

        canvasWidth = surfaceHolder.getSurfaceFrame().width();
        canvasHeight = surfaceHolder.getSurfaceFrame().height();

        CELL_SIZE = canvasWidth / BOARD_SIZE;
        render();
    }

    private void render() {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(BOARD_COLOR);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.argb(255, 25, 25, 25));
        paint.setStrokeWidth(4);
        for (int i = CELL_SIZE; i < canvasWidth; i += CELL_SIZE) {
            canvas.drawLine(i, 0, i, canvasHeight, paint);
        }

        for (int i = CELL_SIZE; i < canvasHeight; i += CELL_SIZE) {
            canvas.drawLine(0, i, canvasWidth, i, paint);
        }
        paint.setStrokeWidth(0);

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                int centreX = r * CELL_SIZE + CELL_SIZE / 2;
                int centreY = c * CELL_SIZE + CELL_SIZE / 2;
                int radius = CELL_SIZE / 2 - 4;

                if (board[r][c] == Player.Black) {
                    paint.setColor(BLACK_COLOR);
                    canvas.drawCircle(centreX, centreY, radius, paint);
                } else if (board[r][c] == Player.White) {
                    paint.setColor(WHITE_COLOR);
                    canvas.drawCircle(centreX, centreY, radius, paint);
                }
            }
        }

        for (Position pos : legalMoves.keySet()) {
            paint.setColor(Color.argb(100, 255, 255, 255));
            int rectLeft = pos.row * CELL_SIZE;
            int rectTop = pos.col * CELL_SIZE;
            canvas.drawRect(rectLeft + 2, rectTop + 2, rectLeft + CELL_SIZE - 2, rectTop + CELL_SIZE - 2, paint);
        }


        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private boolean isInsideBoard(int r, int c) {
        return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE;
    }

    private List<Position> getOutflankedInOneDirection(Position position, Player player, int rowOffset, int colOffset) {
        List<Position> outflankedDiscs = new ArrayList<>();
        //Starting row & col to check
        int r = position.row + rowOffset;
        int c = position.col + colOffset;

        while (isInsideBoard(r, c) && board[r][c] != Player.None) {
            if (board[r][c] == Player.Opponent(player)) {
                outflankedDiscs.add(new Position(r, c));
                //Go to in a line based on offset
                //E.g: r is 1 and c is -1, should go down, right diagonally
                r += rowOffset;
                c += colOffset;
            } else if (board[r][c] == player) {
                return outflankedDiscs;
            }
        }

        return new ArrayList<>();
    }

    private List<Position> getAllOutflanked(Position position, Player player) {
        List<Position> allOutflankedDiscs = new ArrayList<>();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) {
                    //Ignore cuz it's the position of the disc we're checking outflanks for
                    continue;
                }
                allOutflankedDiscs.addAll(getOutflankedInOneDirection(position, player, rowOffset, colOffset));
            }
        }
        return allOutflankedDiscs;
    }

    private boolean moveIsLegal(Player player, Position position) {
        if (board[position.row][position.col] != Player.None) {
            return false;
        }

        return !getAllOutflanked(position, player).isEmpty();
    }

    private Map<Position, List<Position>> findLegalMoves(Player currentPlayer) {
        Map<Position, List<Position>> legalMoves = new HashMap<>();

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Position position = new Position(r, c);

                if (moveIsLegal(currentPlayer, position)) {
                    List<Position> outflankedDiscs = getAllOutflanked(position, currentPlayer);
                    legalMoves.put(position, outflankedDiscs);
                }
            }
        }

        return legalMoves;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int row = (int) (event.getX() / (canvasWidth / BOARD_SIZE));
        int col = (int) (event.getY() / (canvasHeight / BOARD_SIZE));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                makeMove(new Position(row, col));
                render();
                break;
            default:
                break;
        }
        return true;
    }

    public void makeMove(Position position) {
        if (!legalMoves.containsKey(position)) {
            return;
        }
        List<Position> outflankedDiscs = legalMoves.get(position);

        board[position.row][position.col] = currentPlayer;

        if (outflankedDiscs == null) return;

        flipDiscs(outflankedDiscs);
        setDiscCount(currentPlayer, outflankedDiscs.size());
        passTurn();
    }

    public void setDiscCount(Player player, int outflankedCount) {
        discCount.put(player, discCount.get(player) + outflankedCount + 1);
        discCount.put(Player.Opponent(player), discCount.get(Player.Opponent(player)) - outflankedCount);
    }

    private void flipDiscs(List<Position> outflankedDiscs) {
        for (Position disc : outflankedDiscs) {
            board[disc.row][disc.col] = Player.Opponent(board[disc.row][disc.col]);
        }
    }

    private void changePlayer() {
        currentPlayer = Player.Opponent(currentPlayer);
        legalMoves = findLegalMoves(currentPlayer);
    }

    private void passTurn() {
        changePlayer();

        if (!legalMoves.isEmpty()) {
            return;
        }

        changePlayer();

        if (legalMoves.isEmpty()) {
            currentPlayer = Player.None;
            gameOver = true;
            winner = findWinner();
        }

    }

    private Player findWinner() {
        if (discCount.get(Player.Black) > discCount.get(Player.White)) {
            return Player.Black;
        }
        if (discCount.get(Player.White) > discCount.get(Player.Black)) {
            return Player.White;
        }
        return Player.None;
    }
}
