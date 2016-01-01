package checkers.model;

import org.junit.Assert;
import org.junit.Test;



public class CheckerTest {

    @Test
    public void testSide() {
        // actual bug in the "isSide()" implementation:
        Checker checker;

        checker = new Checker(CheckerType.REGULAR, CheckerSide.BLACK);
        Assert.assertTrue(checker.isSide(CheckerSide.BLACK));
        Assert.assertTrue(! checker.isSide(CheckerSide.RED));

        checker = new Checker(CheckerType.REGULAR, CheckerSide.RED);
        Assert.assertTrue(! checker.isSide(CheckerSide.BLACK));
        Assert.assertTrue(checker.isSide(CheckerSide.RED));

    }

    @Test
    public void testKing() {
        Checker checker;

        checker = new Checker(CheckerType.REGULAR, CheckerSide.BLACK);
        Assert.assertTrue(! checker.isKing());

        checker = new Checker(CheckerType.KING, CheckerSide.RED);
        Assert.assertTrue(checker.isKing());
    }

}
