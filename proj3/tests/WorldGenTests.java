import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

import java.util.Random;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234567890123456789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        // TODO: write a test that uses an input like "n123swasdwasd"
    }

    @Test
    public void basicSaveTest() {
        // TODO: write a test that calls getWorldFromInput twice, with "n123swasd:q" and with "lwasd"
    }

    @Test
    public void noErrorsTest() {
        Random rnd = new Random();
        int errorCount = 0;
        for (int i = 0; i < 100; i++) {
            try {
                TETile[][] tiles = AutograderBuddy.getWorldFromInput("n" + rnd.nextInt() + "s");
            } catch (Exception e) {
                errorCount++;
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Number of errors: " + errorCount);
        assert errorCount == 0;
    }
}
