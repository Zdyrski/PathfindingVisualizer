package algorithms;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import controls.GridDrawer;
import controls.MyLog;
import java.util.ArrayList;
import java.util.logging.Level;

public class Dijkstra {
    // Define color constants
    /*public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_WHITE = "\u001B[37m";*/

    private DNode[][] mapa;
    private DNode start, end;
    private int rows, cols, cost;
    private ArrayList<DNode> path, avaliable, checked, notchecked;
    private controls.GridDrawer.Square[][] grid;
    private HBox hbox;
    private Label odw;
    private Label pathVal;
    private Button btnStop;
    private boolean taskStop = false;

    public Dijkstra(int r, int c, GridDrawer.Square[][] grid1, HBox hbox1, Label odw1, Label pathVal1, Button btnStop1) {
        rows = r;
        cols = c;
        cost = 1;
        grid = grid1;
        hbox = hbox1;
        odw = odw1;
        btnStop = btnStop1;
        pathVal = pathVal1;
        avaliable = new ArrayList<DNode>();
        checked = new ArrayList<DNode>();
        notchecked = new ArrayList<DNode>();
        path = new ArrayList<DNode>();
        mapa = new DNode[rows][cols];
        for(int i=0; i < rows; i++){
            for(int j=0; j < cols; j++) {
                mapa[i][j] = new DNode();
                mapa[i][j].setX(i);
                mapa[i][j].setY(j);
                mapa[i][j].setWall(false);
            }
        }
        start = mapa[1][1];
        end = mapa[r-2][c-2];
    }
    public Dijkstra(int r, int c, GridDrawer.Square[][] grid1, Label odw1, Label pathVal1) {
        rows = r;
        cols = c;
        cost = 1;
        grid = grid1;
        odw = odw1;
        pathVal = pathVal1;
        avaliable = new ArrayList<>();
        checked = new ArrayList<>();
        notchecked = new ArrayList<>();
        path = new ArrayList<>();
        mapa = new DNode[rows][cols];
        for(int i=0; i < rows; i++){
            for(int j=0; j < cols; j++) {
                mapa[i][j] = new DNode();
                mapa[i][j].setX(i);
                mapa[i][j].setY(j);
                mapa[i][j].setWall(false);
            }
        }
        start = mapa[1][1];
        end = mapa[r-2][c-2];
    }

    public void mapCreate() { //z grida do mapki
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mapa[i][j].setX(i);
                mapa[i][j].setY(j);
                //jesli byla sciezka lub sasiedzi pokazani i od nowa chcemy algorithms to ustawia ich z powrotem na "nie sciane"
                if(grid[i][j].getBorder2().getFill()== Color.YELLOW ||
                        grid[i][j].getBorder2().getFill()== Color.DEEPSKYBLUE||
                        grid[i][j].getBorder2().getFill()== Color.ORANGE ||
                        grid[i][j].getBorder2().getFill()== Color.LIGHTSKYBLUE){
                    grid[i][j].changeColorNotWall(grid[i][j].getBorder2());
                }
                if(grid[i][j].isWall()) {
                    mapa[i][j].setWall(true);
                }else {
                    notchecked.add(mapa[i][j]);
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
        for(int i=0; i < rows; i++){
            for(int j=0; j < cols; j++) {
                if(i == start.getX() && j == start.getY()){
                    mapa[i][j].getL().add(0); // tu bedzie dla startu
                }else {
                    mapa[i][j].getL().add(9999); //its over 9000 !!!!
                }
                mapa[i][j].getP().add(mapa[0][0]);
            }
        }
    }

    public void findPathRTX(){ // do szukania sciezki gdy RTX ON
        //start inf = 99999
        DNode current = start;
        avaliable.add(current);
        //ogolnie to powinno sie liczyc dopoki S.size()>0, ale jak np start jest zamkniety to jest bez sensu,
        //wiec na 2 listy avaliable i checked to dziala
        while(avaliable.size()!=checked.size()) {
            if (current==end) {
                System.out.println("D dziala");
                grid[start.getX()][start.getY()].setStartNode(grid[start.getX()][start.getY()].getBorder2());
                grid[end.getX()][end.getY()].setEndNode(grid[end.getX()][end.getY()].getBorder2());
                returnPath();
                pathVal.setText("path: " + (path.size()+1));
                break;
            } else {
                obliczPiL(current);
                checked.add(current);
                notchecked.remove(current);
                if(notchecked.size()!=0){
                    current = minS();
                }
                odw.setText("visited: " + checked.size());
                if(avaliable.size()==checked.size()){
                    pathVal.setText("path: 0");
                    System.out.println("D nia dziaa");
                    break;
                }
                //current = minS();
                grid[current.getX()][current.getY()].setChecked(grid[current.getX()][current.getY()].getBorder2());
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
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                DNode current = start;
                int pcx = 1;
                int pcy = 1;
                avaliable.add(current);
                while (avaliable.size() != checked.size()) {
                    if(taskStop){
                        Thread.currentThread().interrupt();
                        System.out.println("Algorytm djikstry interrupted");
                        break;
                    }
                    if (current==end) {
                        System.out.println("\nDjikstra znalazl droge");
                        grid[start.getX()][start.getY()].setStartNode(grid[start.getX()][start.getY()].getBorder2());
                        grid[end.getX()][end.getY()].setEndNode(grid[end.getX()][end.getY()].getBorder2());
                        DNode crt = end.getP().get(end.getP().size() - 1);
                        while (true) {
                            if (crt.getX() == start.getX() && crt.getY() == start.getY()) {
                                Platform.runLater(() -> pathVal.setText("path: " + path.size()));
                                break;
                            } else {
                                grid[crt.getX()][crt.getY()].setPath(grid[crt.getX()][crt.getY()].getBorder2());
                                path.add(crt);
                                crtX.add(crt.getX());
                                crtY.add(crt.getY());
                                crt = crt.getP().get(crt.getP().size() - 1);
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                        System.out.println("Droga = "+path.size()+"\n");
                        break;
                    } else {
                        obliczPiL(current);
                        checked.add(current);
                        notchecked.remove(current);
                        Platform.runLater(() -> odw.setText("visited: " + checked.size()));
                        if (avaliable.size() == checked.size()) {
                            System.out.println("\nDjikstra nie znalazl drogi");
                            break;
                        }
                        current = minS();
                        if (checked.size() > 1) {
                            //grid[pcx][pcy].getBorder2().setFill(Color.PINK);;
                            grid[pcx][pcy].setChecked(grid[pcx][pcy].getBorder2());
                        }
                        grid[current.getX()][current.getY()].setActual(grid[current.getX()][current.getY()].getBorder2());
                        pcx = current.getX();
                        pcy = current.getY();
                        try {
                            Thread.sleep(ms);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                for (int i=0; i<hbox.getChildren().size(); i++){
                    hbox.getChildren().get(i).setDisable(false);
                }
                MyLog.log(Level.INFO, MyLog.class.getName(), "Dijkstra algorithm terminated", "DijkstraLog.log");
                MyLog.writeToFile(crtX, crtY, "DijkstraLog.log", false);
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);     //zatrzymuje jak sie zamknie program
        th.start();
    }
    private void obliczPiL(DNode n){ // sprawdza czy miesci sie w granicach, czy nie sciana, P i L - skladniki potrzebne do algorytmu
        int x=n.getX();
        int y=n.getY();

        if(x>0 && !mapa[x-1][y].isWall()){
            DNode U = mapa[x-1][y]; //up
            if(!avaliable.contains(U)) {
                avaliable.add(U);
            }
            if(U.getL().get(U.getL().size()-1) > n.getL().get(n.getL().size()-1)+cost) {
                U.getL().add(n.getL().get(n.getL().size() - 1) + cost);
                U.getP().add(n);
            }
        }
        if(x<rows-1 && !mapa[x+1][y].isWall()){
            DNode D = mapa[x+1][y]; //down
            if(!avaliable.contains(D)) {
                avaliable.add(D);
            }
            if(D.getL().get(D.getL().size()-1) > n.getL().get(n.getL().size()-1)+cost) {
                D.getL().add(n.getL().get(n.getL().size() - 1) + cost);
                D.getP().add(n);
            }
        }
        if(y>0 && !mapa[x][y-1].isWall()){
            DNode L = mapa[x][y-1]; //left
            if(!avaliable.contains(L)) {
                avaliable.add(L);
            }
            if(L.getL().get(L.getL().size()-1) > n.getL().get(n.getL().size()-1)+cost) {
                L.getL().add(n.getL().get(n.getL().size() - 1) + cost);
                L.getP().add(n);
            }
        }
        if(y<cols-1 && !mapa[x][y+1].isWall()){
            DNode R = mapa[x][y+1]; //right
            if(!avaliable.contains(R)) {
                avaliable.add(R);
            }
            if(R.getL().get(R.getL().size()-1) > n.getL().get(n.getL().size()-1)+cost) {
                R.getL().add(n.getL().get(n.getL().size() - 1) + cost);
                R.getP().add(n);
            }
        }
    }

    private DNode minS(){
        int min = 0;
        for(int i=0; i<notchecked.size(); i++){
            if(notchecked.get(min).getL().get(notchecked.get(min).getL().size()-1) > notchecked.get(i).getL().get(notchecked.get(i).getL().size()-1)){
                min = i;
            }
        }
        return notchecked.get(min);
    }

    private void returnPath(){
        DNode crt = end.getP().get(end.getP().size()-1);
        while (true) {
            if (crt.getX() == start.getX() && crt.getY() == start.getY()) {
                break;
            } else {
                grid[crt.getX()][crt.getY()].setPath(grid[crt.getX()][crt.getY()].getBorder2());
                path.add(crt);
                crt = crt.getP().get(crt.getP().size()-1);
            }
        }
    }

    /*//do printowania w konsoli
    public void malujmapeprzed() {
        System.out.println("--------------------");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == start.getX() && j == start.getY()) {
                    System.out.print(" ");
                    System.out.print(TEXT_RED + "S" + TEXT_RESET);
                } else if (i == end.getX() && j == end.getY()) {
                    System.out.print(" ");
                    System.out.print(TEXT_RED + "E" + TEXT_RESET);
                } else if (mapa[i][j].isWall()) {
                    System.out.print(" ");
                    System.out.print(TEXT_CYAN + "#" + TEXT_RESET);
                } else {
                    System.out.print(" ");
                    System.out.print(TEXT_GREEN + "_" + TEXT_RESET);
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
                    System.out.print(TEXT_YELLOW + "o" + TEXT_RESET);
                } else if (i == start.getX() && j == start.getY()) {
                    System.out.print(" ");
                    System.out.print(TEXT_RED +"S" + TEXT_RESET);
                } else if (i == end.getX() && j == end.getY()) {
                    System.out.print(" ");
                    System.out.print(TEXT_RED +"E" + TEXT_RESET);
                } else if (mapa[i][j].isWall()) {
                    System.out.print(" ");
                    System.out.print(TEXT_CYAN + "#" + TEXT_RESET);
                } else {
                    System.out.print(" ");
                    System.out.print(TEXT_GREEN + "_" + TEXT_RESET);
                }
            }
            System.out.println();
        }
    }*/
}