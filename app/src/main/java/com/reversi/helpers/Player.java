package com.reversi.helpers;

public enum Player {
    Black, White, None;

    public static Player Opponent(Player player) {
        switch (player) {
            case Black:
                return White;
            case White:
                return Black;
            default:
                return None;
        }
    }
}


