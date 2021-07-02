package algorithms;

public class Node {
    private int x,y;
    private boolean Wall;
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public boolean isWall() {
        return Wall;
    }
    public void setWall(boolean wall) {
        Wall = wall;
    }
}
