package edu.ucf.cop4520;

import static org.junit.Assert.assertTrue;

import edu.ucf.cop4520.game.Board;
import org.junit.Assert;
import org.junit.Test;
import java.util.Set;
import edu.ucf.cop4520.game.Move;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void fenParser()
    {
        String tests[] = new String[8];
        tests[0] = "r6r/1b2k1bq/8/8/7B/8/8/R3K2R b KQ - 3 2";
        tests[1] = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        tests[2] = "bbrkqnr1/pppp1ppp/4p1n1/8/4P3/6N1/PPPP1PPP/BBRKQNR1 w - - 0 1";
        tests[3] = "bbrkqnr1/pppp1ppp/8/4nP2/8/4N3/PPPP1PPP/BBRKQ1R1 b - - 0 1";
        tests[4] = "bbrk1nr1/pppp1ppp/8/5P2/5P2/3Q4/PPPPq1PP/BBR2KR1 w - - 0 1";
        tests[5] = "bbrk1nr1/pppp1ppp/8/5P2/4QP2/8/PPPPK1PP/BBR3R1 w - - 0 1";
        tests[6] = "8/4b3/4P3/1k4P1/8/ppK5/8/4R3 b - - 1 45";
        tests[7] = "r6r/1b2k1bq/8/8/7B/8/8/R3K2R b KQ - 3 2";

        for (int i = 0; i < tests.length; i++)
        {
            Assert.assertEquals(new Board(tests[i]).toString(), tests[i]);
        }
    }
    @Test
    public void genMoves()
    {
        // Easily generate test cases using following website:
        // http://bernd.bplaced.net/fengenerator/fengenerator.html
        String tests[] = new String[2];
        int answers[] = new int[2];
        tests[0] = "8/8/K4k2/8/8/8/8/8 w - - 0 1";
        answers[0] = 5;
        tests[1] = "8/6k1/8/8/8/3r4/2K5/8 w - - 0 1";
        answers[1] = 4;

        for (int i = 0; i < tests.length; i++)
        {
            Set<Move> moves = (new Board(tests[i])).generateMoves();
            Assert.assertEquals(moves.size(), answers[i]);
        }
    }
}
