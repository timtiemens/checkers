package checkers;

import java.awt.Color;
import java.awt.Graphics;

public final class Checker
{
   private final static int DIMENSION = 50;

   private CheckerType checkerType;

   public Checker(CheckerType checkerType)
   {
      this.checkerType = checkerType;
   }

   public void draw(Graphics g, int cx, int cy)
   {
      int x = cx - DIMENSION / 2;
      int y = cy - DIMENSION / 2;

      // Set checker color.

      g.setColor(checkerType == CheckerType.BLACK_REGULAR ||
                 checkerType == CheckerType.BLACK_KING ? Color.BLACK :
                 Color.RED);

      // Paint checker.

      g.fillOval(x, y, DIMENSION, DIMENSION);
      g.setColor(Color.WHITE);
      g.drawOval(x, y, DIMENSION, DIMENSION);

      if (checkerType == CheckerType.RED_KING ||
          checkerType == CheckerType.BLACK_KING)
         g.drawString("K", cx, cy);
   }

   public static boolean contains(int x, int y, int cx, int cy)
   {
      return (cx - x) * (cx - x) + (cy - y) * (cy - y) < DIMENSION / 2 *
             DIMENSION / 2;
   }

   // The dimension is returned via a method rather than by accessing the
   // DIMENSION constant directly to avoid brittle code. If the constant was
   // accessed directly and I changed its value in Checker and recompiled only
   // this class, the old DIMENSION value would be accessed from external
   // classes whereas the new DIMENSION value would be used in Checker. The
   // result would be erratic code.

   public static int getDimension()
   {
      return DIMENSION;
   }
}