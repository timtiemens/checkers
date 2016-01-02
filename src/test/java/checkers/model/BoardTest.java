package checkers.model;

import org.junit.Test;

public class BoardTest {

    @Test
    public void test() {
        Board board = BoardFactoryCheckers.createCheckerBoardStandardStarting();
        System.out.println(board.dump());
    }

}
