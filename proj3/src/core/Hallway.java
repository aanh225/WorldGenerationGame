package core;

import java.util.Iterator;
import java.util.Random;
import java.awt.Point;
import com.google.common.collect.Iterators;

public class Hallway implements Iterable<Point> {
    Point start;
    Point end;
    Point turn;

    /**
     * Generate a random hallway between two rooms
     *
     * @param r1 the first room
     * @param r2 the second room
     * @param rnd the random object to use
     * @return a new hallway object
     */
    public static Hallway generateHallway(Room r1, Room r2, Random rnd) {
        int y1 = r1.getBottomY();
        int y2 = r1.getTopY();
        int y3 = r2.getBottomY();
        int y4 = r2.getTopY();

        int x1 = r1.getLeftX();
        int x2 = r1.getRightX();
        int x3 = r2.getLeftX();
        int x4 = r2.getRightX();


        if (y2 - 1 > y3 && y4 - 1 > y1) {
            // then it is a horizontal hallway and starts on the right or left
            return generateHorizontalHallway(r1, r2, rnd);
        } else if (x2 - 1 > x3 && x4 - 1 > x1) {
            // then it is a vertical hallway and starts on the top or bottom
            return generateVerticalHallway(r1, r2, rnd);
        } else {
            // then it is a hallway with a turn (scary)
            return generateDiagonalHallway(r1, r2, rnd);
        }
    }

    // Sam approach:
    private static Hallway generateDiagonalHallway(Room r1, Room r2, Random rnd) {
        int startX, startY, endX, endY;
        boolean horizontalFirst = rnd.nextBoolean();

        if (!horizontalFirst) {
            // startY = rnd.nextInt(r1.getBottomY() + 1, r1.getTopY());
            if (r1.origin.y < r2.origin.y) {
                startY = rnd.nextInt(r1.getBottomY() + 1, Math.min(r1.getTopY(), r2.getBottomY()));
            } else {
                startY = rnd.nextInt(Math.max(r1.getBottomY() + 1, r2.getTopY() + 1), r1.getTopY());
            }
            if (r1.origin.x < r2.origin.x) {
                startX = r1.getRightX();
            } else {
                startX = r1.getLeftX();
            }

            // endX = rnd.nextInt(r2.getLeftX() + 1, r2.getRightX());
            if (r1.origin.x < r2.origin.x) {
                endX = rnd.nextInt(Math.max(r2.getLeftX() + 1, r1.getRightX() + 1), r2.getRightX());
            } else {
                endX = rnd.nextInt(r2.getLeftX() + 1, Math.min(r2.getRightX(), r1.getLeftX()));
            }
            if (r1.origin.y < r2.origin.y) {
                endY = r2.getBottomY();
            } else {
                endY = r2.getTopY();
            }
        } else {
            // startX = rnd.nextInt(r1.getLeftX() + 1, r1.getRightX());
            if (r1.origin.x < r2.origin.x) {
                startX = rnd.nextInt(r1.getLeftX() + 1, Math.min(r1.getRightX(), r2.getLeftX()));
            } else {
                startX = rnd.nextInt(Math.max(r1.getLeftX() + 1, r2.getRightX() + 1), r1.getRightX());
            }
            if (r1.origin.y < r2.origin.y) {
                startY = r1.getTopY();
            } else {
                startY = r1.getBottomY();
            }

            // endY = rnd.nextInt(r2.getBottomY() + 1, r2.getTopY());
            if (r1.origin.y < r2.origin.y) {
                endY = rnd.nextInt(Math.max(r2.getBottomY() + 1, r1.getTopY() + 1), r2.getTopY());
            } else {
                endY = rnd.nextInt(r2.getBottomY() + 1, Math.min(r2.getTopY(), r1.getBottomY()));
            }
            if (r1.origin.x < r2.origin.x) {
                endX = r2.getLeftX();
            } else {
                endX = r2.getRightX();
            }
        }
        Point turnPoint = horizontalFirst ? new Point(startX, endY) : new Point(endX, startY);
        return new Hallway(new Point(startX, startY), new Point(endX, endY), turnPoint);
    }



    private static Hallway generateVerticalHallway(Room r1, Room r2, Random rnd) {
        int intersectFrom = Math.max(r1.origin.x, r2.origin.x) + 1;
        int intersectTo = Math.min(r1.origin.x + r1.width, r2.origin.x + r2.width) - 1;
        int randomX = rnd.nextInt(intersectFrom, intersectTo);
        if (r1.origin.y < r2.origin.y) {
            // r2 is above r1
            return new Hallway(new Point(randomX, r1.getTopY()), new Point(randomX, r2.getBottomY()));
        } else {
            // r1 is above r2
            return new Hallway(new Point(randomX, r2.getTopY()), new Point(randomX, r1.getBottomY()));
        }
    }


    private static Hallway generateHorizontalHallway(Room r1, Room r2, Random rnd) {
        int intersectFrom = Math.max(r1.origin.y, r2.origin.y) + 1;
        int intersectTo = Math.min(r1.origin.y + r1.height, r2.origin.y + r2.height) - 1;
        int randomY = rnd.nextInt(intersectFrom, intersectTo);
        if (r1.origin.x < r2.origin.x) {
            // r2 is to the right of r1
            return new Hallway(new Point(r1.getRightX(), randomY), new Point(r2.getLeftX(), randomY));
        } else {
            // r1 is to the right of r2
            return new Hallway(new Point(r2.getRightX(), randomY), new Point(r1.getLeftX(), randomY));
        }
    }

    public Hallway(Point start, Point end, Point turn) {
        if (!(start.x == turn.x || start.y == turn.y)) {
            throw new IllegalArgumentException("Start and turn points must align");
        }
        if (start.x == turn.x && start.y == turn.y) {
            throw new IllegalArgumentException("Start and turn points must be different");
        }
        if (!(turn.x == end.x || turn.y == end.y)) {
            throw new IllegalArgumentException("Turn and end points must align");
        }
        if (turn.x == end.x && turn.y == end.y) {
            throw new IllegalArgumentException("Turn and end points must be different");
        }
        this.start = start;
        this.end = end;
        this.turn = turn;
    }

    public Hallway(Point start, Point end) {
        if (!(start.x == end.x || start.y == end.y)) {
            throw new IllegalArgumentException("Start and end points must align");
        }
        if (start.x == end.x && start.y == end.y) {
            throw new IllegalArgumentException("Start and end points must be different");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Check if this hallway is not straight
     * @return true if there is a turn, false otherwise
     */
    public boolean hasTurn() {
        return turn != null;
    }

    /**
     * Returns an iterator over {@code Point} coordinates of floor tiles.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Point> iterator() {
        if (hasTurn()) {
            return Iterators.concat(new P2pIterator(start, turn), new P2pIterator(turn, end));
        } else {
            return new P2pIterator(start, end);
        }
    }

    private class P2pIterator implements Iterator<Point> {
        enum Direction {
            UP, DOWN, LEFT, RIGHT
        }
        Direction direction;
        int constValue;
        int pointer;
        int target;
        private P2pIterator(Point start, Point end) {
            if (start.x == end.x) {
                if (start.y < end.y) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }
                constValue = start.x;
                pointer = start.y;
                target = end.y;
            } else {
                if (start.x < end.x) {
                    direction = Direction.RIGHT;
                } else {
                    direction = Direction.LEFT;
                }
                constValue = start.y;
                pointer = start.x;
                target = end.x;
            }
        }

        @Override
        public boolean hasNext() {
            if (direction == Direction.UP || direction == Direction.RIGHT) {
                return pointer <= target;
            } else {
                return pointer >= target;
            }
        }

        @Override
        public Point next() {
            Point p;
            if (direction == Direction.UP) {
                p = new Point(constValue, pointer);
                pointer++;
            } else if (direction == Direction.DOWN) {
                p = new Point(constValue, pointer);
                pointer--;
            } else if (direction == Direction.RIGHT) {
                p = new Point(pointer, constValue);
                pointer++;
            } else {
                p = new Point(pointer, constValue);
                pointer--;
            }
            return p;
        }
    }
}
