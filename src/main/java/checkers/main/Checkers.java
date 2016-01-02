package checkers.main;

import java.awt.EventQueue;

import javax.swing.JFrame;

import checkers.gui.BoardPainter;
import checkers.gui.PiecePaintStrategy;
import checkers.gui.PiecePaintStrategyCheckers;
import checkers.model.Board;
import checkers.model.BoardFactoryCheckers;

/**
 * From http://www.javaworld.com/article/3014190/apis/checkers-anyone.html
 *
 *
 */
public class Checkers extends JFrame
{
   public Checkers(String title)
   {
      super(title);
      setDefaultCloseOperation(EXIT_ON_CLOSE);

      Board board = BoardFactoryCheckers.createCheckerBoardStandardStarting();
      PiecePaintStrategy piecePainter = new PiecePaintStrategyCheckers(board);

      BoardPainter boardPaint = new BoardPainter(board, piecePainter);
      setContentPane(boardPaint);

      pack();
      setVisible(true);
   }

   public static void main(String[] args)
   {
      Runnable r = new Runnable()
                   {
                      @Override
                      public void run()
                      {
                         new Checkers("Checkers");
                      }
                   };
      EventQueue.invokeLater(r);
   }
}