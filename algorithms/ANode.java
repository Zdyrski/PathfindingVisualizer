package algorithms;

public class ANode extends Node{
    private int f,g,h;
    private ANode parent;

    public int getF() {
        return f;
    }
    public void setF(int f) { this.f = f; }
    public int getG() {
        return g;
    }
    public void setG(int g) {
        this.g = g;
    }
    public int getH() {
        return h;
    }
    public void setH(int h) {
        this.h = h;
    }
    public ANode getParent() {
        return parent;
    }
    public void setParent(ANode parent) {
        this.parent = parent;
    }
}
