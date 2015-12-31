package checkers;

import java.awt.EventQueue;

import javax.swing.JFrame;

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

      Board board = new Board();
      board.add(new Checker(CheckerType.RED_REGULAR), 4, 1);
      board.add(new Checker(CheckerType.BLACK_REGULAR), 6, 3);
      board.add(new Checker(CheckerType.RED_KING), 5, 6);
      setContentPane(board);

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