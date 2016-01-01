package checkers.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Board is a set of Squares. The Board numbers its Squares starting in
 * upper-left: (1,1) (2,1) ... (x,y) (1,2) ... ... (x,y)
 *
 * You can interrogate a Board about (x, y) to find: NOT_VALID_COORDINATES --
 * outside the bounding rectangle NOT_IN_PLAY -- inside the bounding rectangle,
 * but is a "hole" IN_PLAY -- inside and valid
 *
 */
public class Board {
    private final int sizeX;
    private final int sizeY;

    private Map<Point, Square> point2Square = new HashMap<>();
    private Map<Point, Checker> point2Checker = new HashMap<>();

    public static Board createCheckerBoardStandardStarting() {
        final int size = 8;
        final int numberCheckersPerSide = 12;
        final int numberBlankInMiddle = 8;
        List<String> pieces = new ArrayList<>();

        for (int i = 0; i < numberCheckersPerSide; i++) {
            String piece = "b";
            pieces.add(piece);
        }
        for (int i = 0; i < numberBlankInMiddle; i++) {
            String piece = "-";
            pieces.add(piece);
        }
        for (int i = 0; i < numberCheckersPerSide; i++) {
            String piece = "r";
            pieces.add(piece);
        }

        return createCheckerBoard(size, pieces);
    }

    public static Board createCheckerBoard(int size, List<String> piecesAsStrings) {
        Board ret = new Board(size, size);

        // it seems everyone agrees that (1,1) is not in play:
        Square val = Square.NOT_IN_PLAY;
        for (Point point : ret.generatePointsTopDownLeftRight()) {
            ret.point2Square.put(point, val);

            // "alternate val" covers most cases,
            // but when we come to the end, we stay the same
            if (point.getX() == size) {
                // nothing
            } else {
                val = val.equalsType(Square.NOT_IN_PLAY) ? Square.IN_PLAY : Square.NOT_IN_PLAY;
            }
        }
        // no more modifications to the squares:
        ret.point2Square = Collections.unmodifiableMap(ret.point2Square);


        List<Checker> pieces = ret.fromList(piecesAsStrings);
        ret.loadPieces(pieces);

        return ret;
    }

    /**
     *
     * @param s like "bbbbbbbbbbbb--------wwwwwwwwwwww"
     */
    public void loadPiecesFromString(String s) {
        List<String> checkerString = fromString(s);
        List<Checker> pieces = fromList(checkerString);
        loadPieces(pieces);
    }

    private List<Checker> fromList(List<String> list) {
        List<Checker> ret = new ArrayList<>();
        if (list != null) {
            for (String s : list) {
                Checker piece = createFromSingleString(s);
                ret.add(piece);
            }
        }
        return ret;
    }

    private Checker createFromSingleString(String s) {
        return Checker.createFromSingleString(s);
    }

    private List<String> fromString(String s) {
        String[] split = s.split(".");
        return Arrays.asList(split);
    }


    public void loadPieces(List<Checker> checkers) {
        List<Checker> copy = new ArrayList<>(checkers);
        for (Point point : generatePointsTopDownLeftRight()) {
            Square val = getSquare(point);
            if (Square.IN_PLAY.equalsType(val)) {
                Checker piece = copy.remove(0);

                // even if piece is null, "place" it (to clear thet Point)
                place(piece, point);
            }
        }
        if (copy.size() != 0) {
            throw new RuntimeException("Programmer error- extra checkers, size=" + copy.size());
        }
    }


    /**
     * @return points, top row first,
     *         e.g. (1,1), (2,1), (3,1) ... (size, 1) (1,2) (2,2) (3,2) (size, 2) .... (size,size)
     */
    public List<Point> generatePointsTopDownLeftRight() {
        List<Point> ret = new ArrayList<>();

        for (int y = 1; y <= getSizeY(); y++) {
            for (int x = 1; x <= getSizeX(); x++) {
                Point point = new Point(x, y);
                ret.add(point);
            }
        }
        return ret;
    }

    public static enum Square {
        NOT_VALID_COORDINATES, NOT_IN_PLAY, IN_PLAY;

        public boolean equalsType(Square other) {
            return equals(other);
        }
    }

    public Board(int x, int y) {
        this.sizeX = x;
        this.sizeY = y;
    }

    public void place(Checker checker, int x, int y) {
        place(checker, new Point(x, y));
    }

    public Square getSquare(Point point) {
        if (point2Square.containsKey(point)) {
            return point2Square.get(point);
        } else {
            return Square.NOT_VALID_COORDINATES;
        }
    }

    public Checker getPiece(Point point) {
        checkPoint(point);
        return point2Checker.get(point);
    }

    public void checkPoint(Point point) {
        Square square = getSquare(point);
        if (square.equalsType(Square.NOT_VALID_COORDINATES)) {
            throw new RuntimeException("Invalid coordinates " + point);
        }
    }

    public void place(Checker checker, Point point) {
        checkPoint(point);

        // DESIGN: allow a placement to potentially "remove" a piece, because
        // we are not going to check
        // If we were going to check, it would be:
        // if (point2Checker.containsKey(point)) {
        //   throw new RuntimeException("Point already contains a piece");
        // }

        point2Checker.put(point, checker);
    }

    public String dump(int size) {
        StringBuilder sb = new StringBuilder();

        for (Point point : generatePointsTopDownLeftRight()) {
            final String cell;
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

            sb.append(cell);
            if (point.getX() == size) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
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

    public void movePiece(Point from, Point to) {
        final Checker piece = getPiece(from);
        if (piece != null) {
            if (isAvailableTargetForMove(to)) {
                point2Checker.remove(from);
                point2Checker.put(to, piece);
            } else {
                throw new RuntimeException("Programmer error - point not available, point=" + to);
            }
        } else {
            throw new RuntimeException("Programmer error - no piece at original, point=" + from);
        }
    }

    public boolean isValidToMove(Point from, Point to) {
        if (getPiece(from) != null) {
            if (isAvailableTargetForMove(to)) {
                //  TODO: rule check too
                return true;
            }
        }
        return false;
    }

    public boolean canMovePieceAtPoint(Point point) {
        // TODO: better implementation - pay attention to which side has the turn,
        //       pay attention to "must move another piece" rules
        return (getPiece(point) != null);
    }
}
