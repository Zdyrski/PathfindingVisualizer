package algorithms;

import java.util.ArrayList;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import controls.*;

public class Aalgorythm {
    private ArrayList<ANode> open, closed, path;
    private ANode[][] mapa;
    private ANode start, end;
    private int rows, cols;
    private GridDrawer.Square[][] grid;
    private HBox hbox;
    private Label odw;
    private Label pathVal;
    private Button btnStop;
    private boolean taskStop = false;
    public Aalgorythm(int r, int c, GridDrawer.Square[][] grid1, HBox hbox1, Label odw1, Label pathVal1, Button btnStop1) {
        rows = r;
        cols = c;
        grid = grid1;
        hbox = hbox1;
        odw = odw1;
        btnStop = btnStop1;
        pathVal = pathVal1;
        path = new ArrayList<>();
        open = new ArrayList<>();
        closed = new ArrayList<>();
        mapa = new ANode[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mapa[i][j] = new ANode();
            }
        }
        start = mapa[1][1];
        start.setG(0);
        end = mapa[c-2][r-2];
        end.setH(0);
    }
    public Aalgorythm(int r, int c, GridDrawer.Square[][] grid1, Label odw1, Label pathVal1) {
        rows = r;
        cols = c;
        grid = grid1;
        odw = odw1;
        pathVal = pathVal1;
        path = new ArrayList<>();
        open = new ArrayList<>();
        closed = new ArrayList<>();
        mapa = new ANode[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mapa[i][j] = new ANode();
            }
        }
        start = mapa[1][1];
        start.setG(0);
        end = mapa[c-2][r-2];
        end.setH(0);
    }
    //Tablica
    public void mapCreate() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mapa[i][j].setX(i);
                mapa[i][j].setY(j);
                //jesli byla sciezka lub sasiedzi pokazani i od nowa chcemy algorithms to ustawia ich z powrotem na "nie sciane"
                if(grid[i][j].getBorder2().getFill()== Color.YELLOW ||
                        grid[i][j].getBorder2().getFill()== Color.DEEPSKYBLUE ||
                        grid[i][j].getBorder2().getFill()== Color.ORANGE ||
                        grid[i][j].getBorder2().getFill()== Color.LIGHTSKYBLUE){
                    grid[i][j].changeColorNotWall(grid[i][j].getBorder2());
                }
                if(grid[i][j].isWall()) {
                    mapa[i][j].setWall(true);
                }else {
                    mapa[i][j].setWall(false);
                }
                if(grid[i][j].isStart()) {
                    start=mapa[i][j];
                }
                if(grid[i][j].isEnd()) {
                    end=mapa[i][j];
                }
            }
        }
    }

    //dodaj sasiadow do listy
    private ArrayList<ANode> Neighbours(ANode n) {
        ArrayList<ANode> sasiedzi = new ArrayList<>();
        int x = n.getX();
        int y = n.getY();
        if (x > 0 && !closed.contains(mapa[x - 1][y]) && !open.contains(mapa[x - 1][y]) && !mapa[x - 1][y].isWall()) {
            mapa[x - 1][y].setParent(n);
            sasiedzi.add(mapa[x - 1][y]);
            open.add(mapa[x - 1][y]);
        }
        if (x < rows - 1 && !closed.contains(mapa[x + 1][y]) && !open.contains(mapa[x + 1][y]) && !mapa[x + 1][y].isWall()) {
            mapa[x + 1][y].setParent(n);
            sasiedzi.add(mapa[x + 1][y]);
            open.add(mapa[x + 1][y]);
        }
        if (y > 0 && !closed.contains(mapa[x][y - 1]) && !open.contains(mapa[x][y - 1]) && !mapa[x][y - 1].isWall()) {
            mapa[x][y - 1].setParent(n);
            sasiedzi.add(mapa[x][y - 1]);
            open.add(mapa[x][y - 1]);
        }
        if (y < cols - 1 && !closed.contains(mapa[x][y + 1]) && !open.contains(mapa[x][y + 1]) && !mapa[x][y + 1].isWall()) {
            mapa[x][y + 1].setParent(n);
            sasiedzi.add(mapa[x][y + 1]);
            open.add(mapa[x][y + 1]);
        }
        //kolorowanie na avaliable
        for(int i=0; i < sasiedzi.size(); i++){
            int x1 = sasiedzi.get(i).getX();
            int y1 = sasiedzi.get(i).getY();
            grid[x1][y1].setNeigh(grid[x1][y1].getBorder2());
        }
        return sasiedzi;
    }

    //ustaw dla algorithms.ANode n G,H i F , ze wzgledu na parent
    private void fghNeighboursSet(ANode n) {
        ArrayList<ANode> lista = Neighbours(n);
        for (int i = 0; i < lista.size(); i++) {
            ANode cr = lista.get(i);
            cr.setG(cr.getParent().getG() + 1);    //odleglosc tego ANode'a od Start
            cr.setH(Math.abs(cr.getX() - end.getX()) + Math.abs(cr.getY() - end.getY()));    //odleglosc tego ANode'a od End
            cr.setF(cr.getG() + cr.getH());         //F = suma G i H
        }
    }

    public void findPathRTX() { // do szukania sciezki gdy RTX ON
        open.add(start);
        while (true) {
            if (open.size() > 0) {
                int min = lowestF();
                ANode current = open.get(min);
                if (current.getX() == end.getX() && current.getY() == end.getY()) {                           //czy aktualny to koniec
                    System.out.println("A dziala");
                    returnPath();
                    pathVal.setText("path: " + (path.size()+1));
                    break;
                } else {
                    closed.add(current);
                    open.remove(min);
                    fghNeighboursSet(current);
                    //System.out.println(current.getX()+" "+current.getY()+" open size: "+open.size());
                    grid[current.getX()][current.getY()].setChecked(grid[current.getX()][current.getY()].getBorder2());
                    odw.setText("visited: " + (closed.size()));
                }
            } else {
                System.out.println("A nie dziala");               //brak sciezki
                pathVal.setText("path: 0");
                break;
            }
        }
        //malujmapeprzed();
        //malujdroge();
    }

    public void findPath(long ms){            //dziala wolno, pokazyje krok po kroku, szukanie sciezki pod przycisk
        btnStop.setOnAction(actionEvent -> {
            if(!taskStop) {
                taskStop = true;
            }
        });
        ArrayList crtX = new ArrayList();
        ArrayList crtY = new ArrayList();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                open.add(start);
                ANode previous = new ANode();
                while (true) {
                    if (taskStop) {
                        Thread.currentThread().interrupt();
                        System.out.println("Algorytm A* interrupted");
                        break;
                    }
                    if (open.size() > 0) {
                        int min = lowestF();
                        ANode current = open.get(min);
                        if (current.getX() == end.getX() && current.getY() == end.getY()) {                           //czy aktualny to koniec
                            System.out.println("\nAalgorytm znalazl droge");
                            //returnPath();  //wyswietlenie sciezki bez opoznienia, jesli chcemy z to zakomentowac nizej
                            ANode crt = end.getParent();

                            while (true) {
                                if (crt.getX() == start.getX() && crt.getY() == start.getY()) {
                                    grid[start.getX()][start.getY()].setStartNode(grid[start.getX()][start.getY()].getBorder2());
                                    grid[end.getX()][end.getY()].setEndNode(grid[end.getX()][end.getY()].getBorder2());
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            pathVal.setText("path: " + path.size());
                                        }
                                    });
                                    break;
                                } else {
                                    path.add(crt);
                                    grid[crt.getX()][crt.getY()].setPath(grid[crt.getX()][crt.getY()].getBorder2());
                                    crt = crt.getParent();
                                    crtX.add(crt.getX());
                                    crtY.add(crt.getY());
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }
                            System.out.println("Droga = " + path.size()+"\n");
                            break;
                        } else {
                            closed.add(current);
                            open.remove(min);
                            if (closed.size() > 2) {
                                grid[previous.getX()][previous.getY()].setChecked(grid[previous.getX()][previous.getY()].getBorder2());
                            }
                            fghNeighboursSet(current);
                            Platform.runLater(() -> odw.setText("visited: " + (closed.size())));
                            if (current != start) {
                                grid[current.getX()][current.getY()].setActual(grid[current.getX()][current.getY()].getBorder2());
                            }
                            previous = current;
                            try {
                                Thread.sleep(ms);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    } else {
                        System.out.println("\nAalgorytm nie znalazl drogi");               //brak sciezki
                        break;
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                for (int i = 0; i < hbox.getChildren().size(); i++) {
                    hbox.getChildren().get(i).setDisable(false);
                }
                MyLog.log(Level.INFO, MyLog.class.getName(), "A* algoritim terminated", "AalgorytmLog.log");
                MyLog.writeToFile(crtX, crtY, "AalgorytmLog.log", false);
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);     //zatrzymuje jak sie zamknie program
        th.start();
    }

    //zwraca najmniejsze ANode z najmniejszym F patrzac od ostatnich dodanych
    private int lowestF() {
        int min = open.size() - 1;
        if(open.size()>1) {
            for (int i = open.size() - 2; i >= 0; i--) {
                if (open.get(i).getF() < open.get(min).getF()) {
                    min = i;
                }
            }
        }
        return min;
    }

    //zwraca sciezke
    private void returnPath() {
        ANode crt = end.getParent();
        while (true) {
            if (crt.getX() == start.getX() && crt.getY() == start.getY()) {
                grid[start.getX()][start.getY()].setStartNode(grid[start.getX()][start.getY()].getBorder2());
                grid[end.getX()][end.getY()].setEndNode(grid[end.getX()][end.getY()].getBorder2());
                break;
            } else {
                path.add(crt);
                grid[crt.getX()][crt.getY()].setPath(grid[crt.getX()][crt.getY()].getBorder2());
                crt = crt.getParent();
            }
        }
        System.out.println("Droga = "+(path.size()+1));
    }

    /*//do sprawdzenia w konsoli
    public void malujmapeprzed() {
        System.out.println("--------------------");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == start.getX() && j == start.getY()) {
                    System.out.print(" ");
                    System.out.print("S");
                } else if (i == end.getX() && j == end.getY()) {
                    System.out.print(" ");
                    System.out.print("E");
                } else if (mapa[i][j].isWall()) {
                    System.out.print(" ");
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                    System.out.print("_");
                }
            }
            System.out.println();
        }
    }

    public void malujdroge() {
        System.out.println("--------------------");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (path.contains(mapa[i][j])) {
                    System.out.print(" ");
                    System.out.print("o");
                } else if (i == start.getX() && j == start.getY()) {
                    System.out.print(" ");
                    System.out.print("S");
                } else if (i == end.getX() && j == end.getY()) {
                    System.out.print(" ");
                    System.out.print("E");
                } else if (mapa[i][j].isWall()) {
                    System.out.print(" ");
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                    System.out.print("_");
                }
            }
            System.out.println();
        }
    }*/
}


