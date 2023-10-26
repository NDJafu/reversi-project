package com.reversi.game;

import android.util.Log;
import com.reversi.ReversiView;
import com.reversi.helpers.Player;
import com.reversi.helpers.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    public static final int rows = 8;
    public static final int cols = 8;

    static Player[][] board = ReversiView.board;

    public static boolean isInsideBoard(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private static List<Position> outflankedInDirection(Position pos, Player player, int rDelta, int cDelta) {
        List<Position> outflanked = new ArrayList<>();
        int r = pos.row + rDelta;
        int c = pos.col + cDelta;
        while (isInsideBoard(r, c) && board[r][c] != Player.None) {
            if (board[r][c] == Player.Opponent(player)) {
                outflanked.add(new Position(r, c));
                r += rDelta;
                c += cDelta;
            } else if (board[r][c] == player) {
                return outflanked;
            }
        }

        return new ArrayList<>();
    }

    private static List<Position> outflanked(Position pos, Player player) {
        List<Position> outflanked = new ArrayList<>();

        for (int rDelta = -1; rDelta <= 1; rDelta++) {
            for (int cDelta = -1; cDelta <= 1; cDelta++) {
                if (rDelta == 0 && cDelta == 0) {
                    continue;
                }
                outflanked.addAll(outflankedInDirection(pos, player, rDelta, cDelta));
            }
        }

        return outflanked;
    }

    private static boolean isMoveLegal(Player player, Position pos) {
        List<Position> outflanked;
        if (board[pos.row][pos.col] != Player.None) {
            return false;
        }

        outflanked = outflanked(pos, player);
        return !outflanked.isEmpty();
    }

    public static Map<Position, List<Position>> findLegalMoves(Player player) {
        Map<Position, List<Position>> legalMoves = new HashMap<>();

        for (int r = 3; r < rows; r++) {
            for (int c = 3; c < cols; c++) {
                Position position = new Position(r, c);
                if (GameLogic.isMoveLegal(player, position)) {
                    Log.d("isMoveLegal", "yes");
                    legalMoves.put(position, outflanked(position, player));
                }
            }
        }

        return legalMoves;
    }

}
