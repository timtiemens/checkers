package checkers.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import checkers.model.Board.Square;

public class GameStrategyCheckers implements GameStrategy {

    private Board board;

    @Override
    public void setBoard(Board ret) {
        board = ret;
    }

    @Override
    public List<String> splitBoardStateString(String s) {
        String[] split = s.split("");
        if (split.length != s.length()) {
            System.out.println("WTF splitBoardstra s.len=" + s.length() + " split.len=" + split.length);
            // TODO: WTF? split just stopped working!
            List<String> ret = new ArrayList<>();
            for (int i = 0, n = s.length(); i < n; i++) {
                ret.add(s.substring(i, i+1));
            }
            return ret;
            //throw new RuntimeException("length mismatch: s.len=" + s.length() + " but array.len=" + split.length);
        }
        return Arrays.asList(split);
    }

    @Override
    public Checker createPieceFromSingleString(String s) {
        return Checker.createFromSingleString(s);
    }

    @Override
    public String convertPointToDumpString(Point point) {
        String cell;

        final Square square = getSquare(point);
        if (square.equalsType(Square.NOT_VALID_COORDINATES)) {
            cell = "<ERROR at point=" + point;
        } else if (square.equalsType(Square.NOT_IN_PLAY)) {
            cell = " ";
        } else if (square.equalsType(Square.IN_PLAY)) {
            Checker checker = getPiece(point);
            if (checker == null) {
                cell = "_";
            } else {
                final String tmpcell;
                if (checker.isSide(CheckerSide.BLACK)) {
                    tmpcell = "b";
                } else if (checker.isSide(CheckerSide.RED)) {
                    tmpcell = "r";
                } else {
                    tmpcell = "error";
                }

                if (checker.isKing()) {
                    cell = tmpcell.toUpperCase();
                } else {
                    cell = tmpcell;
                }
            }
        } else {
            cell = "Case error point=" + point + " square=" + square;
        }

        return cell;
    }

    private Checker getPiece(Point point) {
        return (Checker) board.getPiece(point);
    }

    private Square getSquare(Point point) {
        return board.getSquare(point);
    }

    @Override
    public boolean canMovePieceAtPoint(Point point) {
        // TODO: better implementation - pay attention to which side has the turn,
        //       pay attention to "must move another piece" rules
        return (getPiece(point) != null);
    }


    /**
     * @param point to check
     * @return true IFF the point is IN_PLAY and is EMPTY
     */
    public boolean isAvailableTargetForMove(Point point) {
        final boolean ret;
        if (Square.IN_PLAY.equalsType(getSquare(point))) {
            if (null == getPiece(point)) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            ret = false;
        }
        System.out.println("isAvailable(" + point + ") ret=" + ret);
        return ret;
    }

    @Override
    public void movePiece(Point from, Point to) {

        final Piece piece = getPiece(from);
        if (piece != null) {
            if (isAvailableTargetForMove(to)) {
                board.removePoint2Piece(from);
                board.putPoint2Piece(to, piece);
            } else {
                throw new RuntimeException("Programmer error - point not available, point=" + to);
            }
        } else {
            throw new RuntimeException("Programmer error - no piece at original, point=" + from);
        }
    }

    @Override
    public boolean isValidToMove(Point from, Point to) {
        if (getPiece(from) != null) {
            if (isAvailableTargetForMove(to)) {
                //  TODO: rule check too
                return true;
            }
        }
        return false;
    }

}
