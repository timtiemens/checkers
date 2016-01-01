package checkers.model;

import org.junit.Test;

public class BoardTest {

    @Test
    public void test() {
        Board board = Board.createCheckerBoardStandardStarting();
        System.out.println(board.dump(8));
    }

}
