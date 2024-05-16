package core;

import tileengine.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Point;
import tileengine.TETile;

public class World {

    private final TETile[][] tileSet;
    private final List<Room> rooms;
    private final List<Hallway> hallways;
    private final int width;
    private final int height;
    private final Random rnd;
    private final Avatar avatar;
    private boolean sightLimit = false;
    private final StringBuilder storeMoves;
    private final long seed;

    public World(long seed, int height, int width) {
        // instance vars
        this.rnd = new Random(seed);
        this.width = width;
        this.height = height;
        rooms = new ArrayList<>();
        hallways = new ArrayList<>();
        storeMoves = new StringBuilder();
        this.seed = seed;

        // initialize with blank tiles
        tileSet = new TETile[width][height];

        fillWorldWithNothing();
        generateRooms(0.4);
        sortRooms();
        generateHallways();
        generateTileSet();

        // place the avatar in a random room
        Room startRoom = rooms.get(rnd.nextInt(rooms.size()));
        int startX = rnd.nextInt(startRoom.origin.x + 1, startRoom.getRightX() - 1);
        int startY = rnd.nextInt(startRoom.origin.y + 1, startRoom.getTopY() - 1);
        avatar = new Avatar(tileSet, startX, startY);
    }

    private void fillWorldWithNothing() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tileSet[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void generateHallways() {
        if (rooms.isEmpty()) {
            return;
        }

        Room currentRoom = rooms.get(0);
        List<Room> connectedRooms = new ArrayList<>();
        connectedRooms.add(currentRoom);

        // could also extract this into a room list sorting method
        while (connectedRooms.size() < rooms.size()) {
            Room nearestRoom = null;
            int minDistance = Integer.MAX_VALUE;

            //find nearest unconnected room
            for (Room room : rooms) {
                if (!connectedRooms.contains(room)) {
                    int distance = Room.distBetweenRooms(currentRoom, room);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestRoom = room;
                    }
                }
            }

            // connect curr room to the closest room
            if (nearestRoom != null) {
                Hallway hallway = Hallway.generateHallway(currentRoom, nearestRoom, rnd);
                if (hallway != null) {
                    hallways.add(hallway);
                    connectedRooms.add(nearestRoom);
                    currentRoom = nearestRoom;
                } else {
                    break;
                }
            } else {
                //no valid nearest room
                break;
            }

        }
    }

    private void generateTileSet() {

        // fill in the rooms
        for (Room r : rooms) {
            for (Point p : r) {
                tileSet[p.x][p.y] = Tileset.FLOOR;
            }
        }

        // fill in the hallways
        for (Hallway h : hallways) {
            for (Point p : h) {
                tileSet[p.x][p.y] = Tileset.FLOOR;
            }
        }

        // fill in the walls
        surroundWithWalls();
    }

    /**
     * Generate the list of rooms
     *
     * @param threshold the min percentage of the world that should be filled with rooms
     */
    private void generateRooms(double threshold) {
        double filled = 0;

        roomGen:
        while (filled < threshold * area()) {
            Room currRoom = Room.generateRoom(5, 10, width, height, rnd);

            // check and make sure the room is not clipping or overlapping another one
            for (Room r : rooms) {
                if (r.collidesWith(currRoom)) {
                    continue roomGen;
                }
            }

            rooms.add(currRoom);

            filled += currRoom.height * currRoom.width;
        }
    }

    /**
     * surround all non-nothing tiles with walls
     * this is a helper method for generateTileSet
     */
    private void surroundWithWalls() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tileSet[x][y] != Tileset.FLOOR && nextToFloor(x, y)) {
                    tileSet[x][y] = Tileset.WALL;
                }
            }
        }
    }

    /**
     * check if a tile is next to a floor tile
     * this is a helper method for surroundWithWalls
     *
     * @param x the x coordinate of the tile
     * @param y the y coordinate of the tile
     * @return true if the tile is next to a floor tile, false otherwise
     */
    private boolean nextToFloor(int x, int y) {
        if (x > 0 && tileSet[x - 1][y] == Tileset.FLOOR) {
            return true;
        }

        if (x < width - 1 && tileSet[x + 1][y] == Tileset.FLOOR) {
            return true;
        }

        if (y > 0 && tileSet[x][y - 1] == Tileset.FLOOR) {
            return true;
        }

        if (y < height - 1 && tileSet[x][y + 1] == Tileset.FLOOR) {
            return true;
        }

        return false;
    }

    private void sortRooms() {
        rooms.sort(Room::distBetweenRooms);
    }

    public static World loadWorld(String input, int height, int width) {
        // input should be all lowercase
        String seedString = input.substring(1, input.indexOf('s'));
        long seed = Long.parseLong(seedString);
        World world = new World(seed, height, width);

        for (int i = input.indexOf('s') + 1; i < input.length(); i++) {
            char key = input.charAt(i); // assumes lowercase
            world.moveAvatar(key);
        }

        return world;
    }

    public TETile[][] getTileSet() {
        if (!sightLimit) {
            return tileSet;
        }
        TETile[][] limitedSet = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                limitedSet[x][y] = distanceFromAvatar(x, y) < 8 ? tileSet[x][y] : Tileset.NOTHING;
            }
        }
        return limitedSet;
    }

    private int distanceFromAvatar(int x, int y) {
        return Math.abs(avatar.x - x) + Math.abs(avatar.y - y);
    }

    public void moveAvatar(char key) {
        if (avatar.move(key)) {
            storeMoves.append(key);
        }
    }
    
    public void undoMove() {
        if (!storeMoves.isEmpty()) {
            char lastKey = storeMoves.charAt(storeMoves.length() - 1);
            storeMoves.deleteCharAt(storeMoves.length() - 1);
            switch (lastKey) {
                case 'w' -> avatar.move('s');
                case 's' -> avatar.move('w');
                case 'a' -> avatar.move('d');
                case 'd' -> avatar.move('a');
                default -> System.out.println("Invalid key");
            }
        }
    }
    
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int area() {
        return width * height;
    }

    public void toggleSightLimit() {
        storeMoves.append('t');
        sightLimit = !sightLimit;
    }

    public String getSave() {
        StringBuilder out = new StringBuilder(storeMoves);
        out.insert(0, 's').insert(0, seed).insert(0, 'n');
        return out.toString();
    }
}
