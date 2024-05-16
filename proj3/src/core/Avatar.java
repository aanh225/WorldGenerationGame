package core;
import tileengine.TETile;
import tileengine.Tileset;


public class Avatar {
    TETile[][] world;
    int x;
    int y;

    public Avatar(TETile[][] world, int startX, int startY) {
        // instance vars
        this.world = world;
        this.x = startX;
        this.y = startY;
        world[x][y] = Tileset.AVATAR; //should the initial position be randomly selected?
    }

    public boolean move(char key) {
        return switch (key) {
            case 'w' -> moveIfPossible(x, y + 1);
            case 's' -> moveIfPossible(x, y - 1);
            case 'a' -> moveIfPossible(x - 1, y);
            case 'd' -> moveIfPossible(x + 1, y);
            default -> false;
        };
    }

    public boolean moveIfPossible(int newX, int newY) {
        // checks that new location is valid (not a wall) and in bounds
        if (world[newX][newY] == Tileset.FLOOR) {
            world[x][y] = Tileset.FLOOR; //clears old position
            x = newX;
            y = newY;
            world[x][y] = Tileset.AVATAR; // makes new position an avatar til
            return true;
        }
        return false;
    }
}
