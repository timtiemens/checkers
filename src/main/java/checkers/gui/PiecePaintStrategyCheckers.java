package checkers.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import checkers.model.Board;
import checkers.model.Board.Square;
import checkers.model.Checker;
import checkers.model.CheckerSide;
import checkers.model.Piece;

public class PiecePaintStrategyCheckers implements PiecePaintStrategy {
    private final Board board;

    // The rules say that pieces go on the dark squares.
    // But then the black pieces do not show up as well.
    private final boolean piecesOnDark = true;

    public PiecePaintStrategyCheckers(Board b) {
        board = b;
    }

    @Override
    public Color getColorForPoint(Point point) {
        Square square = board.getSquare(point);

        // The official rules say pieces go on dark.
        if (square.equalsType(Square.NOT_IN_PLAY)) {
            if (piecesOnDark) {
                return Color.WHITE;
            } else {
                return Color.BLACK;
            }
        } else if (square.equalsType(Square.IN_PLAY)) {
            if (piecesOnDark) {
                return Color.BLACK;
            } else {
                return Color.WHITE;
            }
        } else {
            throw new RuntimeException("cannot get color for point=" + point + " square=" + square);
        }
    }
    public Checker getChecker(Piece piece) {
        return (Checker) piece;
    }

    public Color getColorForPiece(Piece piece) {
        return getColorForChecker(getChecker(piece));
    }

    public Color getColorForChecker(Checker piece) {
        if (piece.isSide(CheckerSide.BLACK)) {
            return Color.BLACK;
        } else if (piece.isSide(CheckerSide.RED)) {
            return Color.RED;
        } else {
            throw new RuntimeException("cannot get color for piece=" + piece + " side=" + piece.getSide());
        }
    }

    public Color getColorForPieceBorder() {
        return Color.WHITE;
    }

    public Color getColorForKingMarker() {
        return Color.WHITE;
    }

    @Override
    public void draw(int cx, int cy, Piece piece, Graphics g, int squareSize, int pieceSize) {

        final int x = cx - pieceSize / 2;
        final int y = cy - pieceSize / 2;

        g.setColor(getColorForPiece(piece));
        g.fillOval(x,  y, pieceSize, pieceSize);
        g.setColor(getColorForPieceBorder());
        g.drawOval(x,  y, pieceSize, pieceSize);

        if (getChecker(piece).isKing()) {
            Color c = getColorForKingMarker();
            g.setColor(c);
            final String marker = "K";

            // adjust cx, cy for size of the "K"
            java.awt.FontMetrics fm = g.getFontMetrics(g.getFont());
            Rectangle2D bounds = fm.getStringBounds(marker, g);
            int width = (int) bounds.getWidth();
            int height = (int) bounds.getHeight();
            int atx = cx - (width / 2);
            int aty = cy + (height / 3); // TODO: /2 seems "too low"
            g.drawString(marker, atx, aty);
        }
    }

}
