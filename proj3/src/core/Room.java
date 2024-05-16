package core;

import java.util.Iterator;
import java.util.Random;
import java.awt.Point;

public class Room implements Iterable<Point> {
    int width;
    int height;

    Point origin;

    /**
     * Create a new room with the given dimensions and origin
     * @param width the width of the room
     * @param height the height of the room
     * @param origin the bottom left corner of the room
     */
    public Room(int width, int height, Point origin) {
        this.width = width;
        this.height = height;
        this.origin = origin; // bottom left corner
    }

    /**
     * Check if this room overlaps with another room
     * @param r the room to check for overlap
     * @return true if there is overlap, false otherwise
     */
    public boolean collidesWith(Room r) {
        return origin.x < r.origin.x + r.width && origin.x + width > r.origin.x
                && origin.y < r.origin.y + r.height && origin.y + height > r.origin.y;
    }

    /**
     * Calculate the distance between two rooms based on their centers.
     * Note that this is not the Euclidean distance, but the manhattan distance, which
     * means that diagonal rooms will be farther apart (by this metric) than adjacent rooms.
     *
     * @param r1 the first room
     * @param r2 the second room
     * @return the distance between the two rooms
     */
    public static int distBetweenRooms(Room r1, Room r2) {
        int center1X = r1.origin.x + r1.width / 2;
        int center1Y = r1.origin.y + r1.height / 2;
        int center2X = r2.origin.x + r2.width / 2;
        int center2Y = r2.origin.y + r2.height / 2;

        return Math.abs(center1X - center2X) + Math.abs(center1Y - center2Y);
    }

    /**
     * Generate a room with random dimensions and coordinates
     *
     * @param minDimension the minimum dimension of the room
     * @param maxDimension the maximum dimension of the room
     * @param gameWidth the width of the game world
     * @param gameHeight the height of the game world
     * @param rnd the random object to use
     * @return a new room object
     */
    public static Room generateRoom(int minDimension, int maxDimension, int gameWidth, int gameHeight, Random rnd) {
        int width = rnd.nextInt(minDimension, maxDimension);
        int height = rnd.nextInt(minDimension, maxDimension);
        int x = rnd.nextInt(gameWidth - width);
        int y = rnd.nextInt(gameHeight - height);
        return new Room(width, height, new Point(x, y));
    }

    /**
     * Get the y coordinate of the top of the room
     * @return the y coordinate of the top of the room
     */
    public int getTopY() {
        return origin.y + height - 1;
    }

    /**
     * Get the y coordinate of the bottom of the room
     * @return the y coordinate of the bottom of the room
     */
    public int getBottomY() {
        return origin.y;
    }

    /**
     * Get the x coordinate of the right side of the room
     * @return the x coordinate of the right side of the room
     */
    public int getRightX() {
        return origin.x + width - 1;
    }

    /**
     * Get the x coordinate of the left side of the room
     * @return the x coordinate of the left side of the room
     */
    public int getLeftX() {
        return origin.x;
    }


    /**
     * Returns an iterator over {@code Point} coordinates of floor tiles.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Point> iterator() {
        return new RoomIterator();
    }

    private class RoomIterator implements Iterator<Point> {
        private int x;
        private int y;

        public RoomIterator() {
            x = origin.x + 1;
            y = origin.y + 1;
        }

        @Override
        public boolean hasNext() {
            return x < origin.x + width && y < origin.y + height;
        }

        @Override
        public Point next() {
            Point p = new Point(x, y);
            x++;
            if (x == origin.x + width) {
                x = origin.x + 1;
                y++;
            }
            return p;
        }
    }
}
