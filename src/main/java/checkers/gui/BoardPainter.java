package checkers.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import checkers.model.Board;
import checkers.model.Board.Square;
import checkers.model.Checker;
import checkers.model.CheckerSide;

public class BoardPainter extends JComponent {
    private static final long serialVersionUID = 1L;

    private Board board;

    // Helper: decide on square color
    private ColorStrategy squarePainter;

    private int currentPieceSize;

    // encapsulate all of the "drag" information:
    private DragHelper dragHelper;

    public BoardPainter(Board board) {
        super();
        this.board = board;
        this.currentPieceSize = 50;

        // helper aka "strategies"
        squarePainter = new ColorStrategy(board);

        // track dragging, set up listeners, etc.:
        dragHelper = new DragHelper();
    }

    public int getCurrentPieceSize() {
        return currentPieceSize;
    }

    public int getSquareSize() {
        // 25% bigger than piece
        return (int) (getCurrentPieceSize() * 1.25);
    }

    @Override
    public Dimension getPreferredSize() {
        int x = board.getSizeX() * getSquareSize();
        int y = board.getSizeY() * getSquareSize();
        return new Dimension(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {

        paintCheckerBoard(g);

        paintPieces(g);
    }

    private void paintCheckerBoard(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                          RenderingHints.VALUE_ANTIALIAS_ON);

        final int squareSize = getSquareSize();
        for (Point point : board.generatePointsTopDownLeftRight()) {
            int gx = (point.x - 1) * squareSize;
            int gy = (point.y - 1) * squareSize;
            g.setColor(squarePainter.getColorForPoint(point));
            g.fillRect(gx, gy, squareSize, squareSize);
        }
    }

    private void paintPieces(Graphics g) {

        paintPiecesNotBeingDragged(g);

        paintPieceBeingDragged(g);
    }

    private void paintPiecesNotBeingDragged(Graphics g) {
        for (Point point : board.generatePointsTopDownLeftRight()) {
            Checker piece = board.getPiece(point);
            if (piece != null) {
                if (! dragHelper.isPieceBeingDragged(piece)) {
                    drawPiece(point, piece, g);
                }
            }
        }
    }

    private void paintPieceBeingDragged(Graphics g) {
        dragHelper.paintPieceBeingDragged(g);
    }




    public Point getPointFromGuiXY(int guiX, int guiY) {
        int over = guiX / getSquareSize();
        int down = guiY / getSquareSize();
        Point ret = new Point(over + 1, down + 1);
        return ret;
    }


    public void drawPiece(Point point, Checker piece, Graphics g) {
        final int squareSize = getSquareSize();

        int cx = (point.x - 1) * squareSize + squareSize / 2;
        int cy = (point.y - 1) * squareSize + squareSize / 2;

        drawPiece(cx, cy, piece, g);
    }

    // "lower level" draw, used by "Dragging" code.
    // allows piece to be drawn not centered on a square.
    public void drawPiece(int cx, int cy, Checker piece, Graphics g) {
        final int pieceSize = getCurrentPieceSize();
        final int x = cx - pieceSize / 2;
        final int y = cy - pieceSize / 2;

        g.setColor(squarePainter.getColorForPiece(piece));
        g.fillOval(x,  y, pieceSize, pieceSize);
        g.setColor(squarePainter.getColorForPieceBorder());
        g.drawOval(x,  y, pieceSize, pieceSize);

        if (piece.isKing()) {
            g.setColor(squarePainter.getColorForKingMaker());
            final String marker = "K";

            // adjust cx, cy for size of the "K"
            FontMetrics fm = g.getFontMetrics(g.getFont());
            Rectangle2D bounds = fm.getStringBounds(marker, g);
            int width = (int) bounds.getWidth();
            int height = (int) bounds.getHeight();
            int atx = cx - (width / 2);
            int aty = cy + (height / 3); // TODO: /2 seems "too low"
            g.drawString(marker, atx, aty);
        }
    }


    public static class ColorStrategy {
        private final Board board;
        public ColorStrategy(Board b) {
            board = b;
        }

        public Color getColorForPoint(Point point) {
            Square square = board.getSquare(point);

            // The official rules say pieces go on dark.
            // But this implementation puts them on light.
            if (square.equalsType(Square.NOT_IN_PLAY)) {
                return Color.BLACK;
            } else if (square.equalsType(Square.IN_PLAY)) {
                return Color.WHITE;
            } else {
                throw new RuntimeException("cannot get color for point=" + point + " square=" + square);
            }
        }

        public Color getColorForPiece(Checker piece) {
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

        public Color getColorForKingMaker() {
            return Color.WHITE;
        }
    }

    // "instance" inner class, so it has access to outer
    private class DragHelper {
        private boolean inDrag = false;
        private Point dragging = null;
        private int draggingCx = 0;
        private int draggingCy = 0;

        public DragHelper() {
            addMouseListener(getMouseAdapter());
            addMouseMotionListener(getMouseMotionAdapter());
        }

        public void triggerRepaint() {
            repaint();
        }
        public void paintPieceBeingDragged(Graphics g) {
            if (inDrag) {
                if (dragging != null) {
                    Checker piece = board.getPiece(dragging);
                    drawPiece(draggingCx, draggingCy, piece, g);
                } else {
                    throw new RuntimeException("indrag is true, but dragging is null");
                }
            } else {
                // ok, not in drag mode
            }

        }
        public Checker getPieceBeingDragged() {
            if (dragging != null) {
                return board.getPiece(dragging);
            } else {
                return null;
            }
        }

        public boolean isPieceBeingDragged(Checker piece) {
            if (piece.equalsType(getPieceBeingDragged())) {
                return true;
            } else {
                return false;
            }
        }
        public Point findPointForXY(int guiX, int guiY) {
            return getPointFromGuiXY(guiX, guiY);
        }
        public MouseListener getMouseAdapter() {
            return new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {
                    final int x = me.getX();
                    final int y = me.getY();
                    // Locate the board Point under the mouse press
                    Point point = findPointForXY(x, y);
                    if (point != null) {
                        // Check for piece at that Point
                        if (board.canMovePieceAtPoint(point)) {
                            // start dragging:
                            dragging = point;
                            inDrag = true;
                        }
                    }
                    System.out.println("me x=" + x + " y=" + y + " inDrag=" + inDrag + " dragging=" + dragging);
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    if (! inDrag) {
                        return;
                    }


                    // When mouse is released, clear inDrag no matter what
                    // But for now, don't clear the 'dragging' variable

                    inDrag = false;
                    // dragging = null;

                    final int x = me.getX();
                    final int y = me.getY();
                    Point point = findPointForXY(x, y);

                    if (board.isValidToMove(dragging, point)) {
                        board.movePiece(dragging, point);
                    } else {
                        // nothing  TODO: better option?
                    }
                    dragging = null;

                    triggerRepaint();
                }
            };
        }
        public MouseMotionAdapter getMouseMotionAdapter() {
            return new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent me) {
                    if (inDrag) {
                        draggingCx = me.getX(); // - deltaX;
                        draggingCy = me.getY(); //  - deltaY;
                        triggerRepaint();
                    }
                }
            };
        }
    }
}
