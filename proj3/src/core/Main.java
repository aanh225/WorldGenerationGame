package core;
import tileengine.TERenderer;
import edu.princeton.cs.algs4.StdDraw;
import utils.FileUtils;
import tileengine.TETile;

public class Main {
    
    private static final int WIDTH = 70;
    private static final int HEIGHT = 30;

    private static final int HALFWIDTH = 35;

    private static void renderMainMenu() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(HALFWIDTH, 17, "New Game (N)");
        StdDraw.text(HALFWIDTH, 15, "Load Game (L)");
        StdDraw.text(HALFWIDTH, 13, "Quit (Q)");
        StdDraw.show();
    }

    private static void setHoverText(TETile hoverTile) {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(1, HEIGHT + 1, WIDTH, 0.5);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(1, HEIGHT + 1, "Tile: " + hoverTile.description());
    }

    private static void setSeedText(long seed) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(HALFWIDTH, 18, "Enter seed: ");
        StdDraw.text(HALFWIDTH, 15, String.valueOf(seed));
        StdDraw.show();
    }

    private static boolean mousePressedText(double mouseX, double mouseY, int textY) {
        return mouseX > HALFWIDTH - 10 && mouseX < HALFWIDTH + 10
                && mouseY > textY - 1 && mouseY < textY + 1;
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 2);
        renderMainMenu();

        World generatedWorld;
        while (true) { // get seed loop
            if (StdDraw.hasNextKeyTyped() || StdDraw.isMousePressed()) {
                char c = ' ';
                if (StdDraw.hasNextKeyTyped()) {
                    c = Character.toLowerCase(StdDraw.nextKeyTyped());
                }

                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                boolean mouseClicked = StdDraw.isMousePressed();

                if (c == 'n' || (mouseClicked && mousePressedText(mouseX, mouseY, 17))) {
                    StdDraw.clear(StdDraw.BLACK);
                    StdDraw.text(HALFWIDTH, 18, "Enter seed: ");
                    StdDraw.text(HALFWIDTH, 15, "_");
                    StdDraw.show();

                    long seed = 0;
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char d = Character.toLowerCase(StdDraw.nextKeyTyped());
                            if (d == 'q') {
                                System.exit(0); // maybe not necessary
                            } else if (d == 's') {
                                break;
                            } else if (Character.isDigit(d)) {
                                seed = seed * 10 + Integer.parseInt(String.valueOf(d));
                                setSeedText(seed);
                            }
                        }
                    }
                    generatedWorld = new World(seed, HEIGHT, WIDTH);
                    break;
                } else if (c == 'l' || (mouseClicked && mousePressedText(mouseX, mouseY, 15))) {
                    // load game from save
                    String save = FileUtils.readFile("save.txt"); // errors if there is no file
                    generatedWorld = World.loadWorld(save, HEIGHT, WIDTH);
                    break;
                } else if (c == 'q' || (mouseClicked && mousePressedText(mouseX, mouseY, 13))) {
                    System.exit(0);
                }
            }
        }
        ter.renderFrame(generatedWorld.getTileSet());

        char prevKey = ' ';
        while (true) { // movement loop
            // handles mouse hovering display
            int mouseX = (int) Math.min(StdDraw.mouseX(), WIDTH - 1);
            int mouseY = (int) Math.min(StdDraw.mouseY(), HEIGHT - 1);
            if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
                TETile hoverTile = generatedWorld.getTileSet()[mouseX][mouseY];
                setHoverText(hoverTile);
            }
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (key == 'q' && prevKey == ':') {
                    // save and quit
                    FileUtils.writeFile("save.txt", generatedWorld.getSave());
                    System.exit(0);
                } else if ("wasd".indexOf(key) != -1) {
                    generatedWorld.moveAvatar(key);
                } else if (key == 't') {
                    generatedWorld.toggleSightLimit();
                } else if (key == 'u') {
                    generatedWorld.undoMove();
                }
                prevKey = key;
                ter.renderFrame(generatedWorld.getTileSet());
            }
        }
    }
}
