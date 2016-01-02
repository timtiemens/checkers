package checkers.model;

import java.awt.Point;
import java.util.List;

public interface GameStrategy {

    void setBoard(Board ret);

    List<String> splitBoardStateString(String s);

    Checker createPieceFromSingleString(String s);

    // for debugging
    String convertPointToDumpString(Point point);

    // given the current board, is the piece allowed to move?
    boolean canMovePieceAtPoint(Point point);

    void movePiece(Point from, Point to);

    boolean isValidToMove(Point from, Point to);


}
