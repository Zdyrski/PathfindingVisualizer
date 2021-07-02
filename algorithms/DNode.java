package algorithms;

import java.util.ArrayList;

public class DNode extends Node{
    private ArrayList<Integer> l;// l to wagi krawedzi- domyslnie 1(tylko polaczenia pionowe, poziome), p do wyszukania sciezki
    private ArrayList<DNode> p;

    public DNode(){
        l = new ArrayList<>();
        p = new ArrayList<>();
    }

    public ArrayList<Integer> getL() {
        return l;
    }
    public ArrayList<DNode> getP() {
        return p;
    }
}
