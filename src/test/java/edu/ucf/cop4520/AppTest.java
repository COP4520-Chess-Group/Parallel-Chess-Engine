package edu.ucf.cop4520;

import static org.junit.Assert.assertTrue;

import edu.ucf.cop4520.game.Board;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(new Board("r6r/1b2k1bq/8/8/7B/8/8/R3K2R b KQ - 3 2").toString(), "r6r/1b2k1bq/8/8/7B/8/8/R3K2R b KQ - 3 2");
    }
}
