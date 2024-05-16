import core.Room;
import core.Hallway;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import java.util.Random;

import static com.google.common.truth.Truth.*;

public class HallwayGenTest {
    @Test
    public void testHallwayTurns() {
        Room r1 = new Room(4, 4, new Point(0, 0));
        Room r2 = new Room(4, 4, new Point(6, 0));
        Hallway h = Hallway.generateHallway(r1, r2, new Random(0));
        assertThat(h.hasTurn()).isFalse();

        r1 = new Room(4, 4, new Point(0, 0));
        r2 = new Room(4, 4, new Point(6, 6));
        h = Hallway.generateHallway(r1, r2, new Random(0));
        // assertThat(h.hasTurn()).isTrue();
    }
}
