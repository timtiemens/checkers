package checkers.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Board is a set of Squares. The Board numbers its Squares starting in
 * upper-left: (1,1) (2,1) ... (x,y) (1,2) ... ... (x,y)
 *
 * You can interrogate a Board about (x, y) to find information on a square:
 * 1) NOT_VALID_COORDINATES --outside the bounding rectangle
 * 2) NOT_IN_PLAY -- inside the bounding rectangle, but is a "hole"
 * 3) IN_PLAY -- inside and valid
 *
 */
public class Board {
    private final int sizeX;
    private final int sizeY;

    private Map<Point, Square> point2Square = new HashMap<>();
    private Map<Point, Piece> point2Piece = new HashMap<>();

    // implements "rules" for the game - i.e. knows about Pieces
    private final GameStrategy gameStrategy;

    public Board(GameStrategy strategy, int x, int y) {
        gameStrategy = strategy;
        this.sizeX = x;
        this.sizeY = y;
    }

    /**
     * @return points, top row first,
     *         e.g. (1,1), (2,1), (3,1) ... (sizeX, 1) (1,2) (2,2) (3,2) (sizeX, 2) .... (sizeX,sizeY)
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


    public Square getSquare(Point point) {
        if (point2Square.containsKey(point)) {
            return point2Square.get(point);
        } else {
            return Square.NOT_VALID_COORDINATES;
        }
    }

    public Piece getPiece(Point point) {
        checkPoint(point);
        return getPoint2Piece(point);
    }

    public void checkPoint(Point point) {
        Square square = getSquare(point);
        if (square.equalsType(Square.NOT_VALID_COORDINATES)) {
            throw new RuntimeException("Invalid coordinates " + point);
        }
    }

    public void place(Piece piece, Point point) {
        checkPoint(point);

        // DESIGN: allow a placement to potentially "remove" a piece, because
        //         we are not going to check.
        // If we were going to check, it would be:
        // if (point2Checker.containsKey(point)) {
        //   throw new RuntimeException("Point already contains a piece");
        // }

        putPoint2Piece(point, piece);
    }

    //
    // It can get messy having internal maps.
    // So - gather all operations into one area.
    // This also allows us to open permissions "a bit" for friendly outsiders,
    //    without having to make the maps completely public.
    //

    /* default */ Piece getPoint2Piece(Point point) {
        return point2Piece.get(point);
    }
    /* default */ void putPoint2Piece(Point point, Piece piece) {
        point2Piece.put(point,  piece);
    }
    /* default*/ void removePoint2Piece(Point point) {
        point2Piece.remove(point);
    }
    /* default */ void putPoint2Square(Point point, Square square) {
        point2Square.put(point, square);
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();

        for (Point point : generatePointsTopDownLeftRight()) {
            final String cell = gameStrategy.convertPointToDumpString(point);

            sb.append(cell);
            if (point.getX() == getSizeX()) {
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



    public void movePiece(Point from, Point to) {
        gameStrategy.movePiece(from, to);
    }

    public boolean isValidToMove(Point from, Point to) {
        return gameStrategy.isValidToMove(from, to);

    }

    public boolean canMovePieceAtPoint(Point point) {
        return gameStrategy.canMovePieceAtPoint(point);

    }

    public void unmodifiablePoint2Square() {
        // TODO: prevent multiple calls
        point2Square = Collections.unmodifiableMap(point2Square);
    }

   /**
    * Load all IN_PLAY squares with (newly created) pieces.
    *
    * @param s like "bbbbbbbbbbbb--------wwwwwwwwwwww"
    */
   public final void loadPiecesFromString(String s) {
       List<String> piecesString = fromString(s);
       List<Piece> pieces = fromList(piecesString);
       loadPieces(pieces);
   }

   private List<String> fromString(String s) {
       return gameStrategy.splitBoardStateString(s);
   }

   private List<Piece> fromList(List<String> list) {
       List<Piece> ret = new ArrayList<>();
       if (list != null) {
           for (String s : list) {
               Piece piece = createFromSingleString(s);
               ret.add(piece);
           }
       }
       return ret;
   }

   private Piece createFromSingleString(String s) {
       return gameStrategy.createPieceFromSingleString(s);
   }




   public final void loadPieces(List<Piece> pieces) {
       List<Piece> copy = new ArrayList<>(pieces);
       for (Point point : generatePointsTopDownLeftRight()) {
           Square val = getSquare(point);
           if (Square.IN_PLAY.equalsType(val)) {
               Piece piece = copy.remove(0);

               // even if piece is null, "place" it (to clear that Point)
               place(piece, point);
           }
       }
       if (copy.size() != 0) {
           throw new RuntimeException("Programmer error- extra pieces, size=" + copy.size());
       }
   }


}
