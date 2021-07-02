package controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.AnchorPane;
import algorithms.*;

public class GridDrawer {
    private int width = 615;
    private int height = 615;
    private double squareSize; //dla 5 jeszcze mi ciagnie, pozniej jest outofmemory
    private int x_square;
    private int y_square;
    public Square[][] grid;
    //ustawienia poczatkowe startu i enda
    private int sx;
    private int sy;
    private int ex;
    private int ey;
    private boolean isClicked = false;
    private boolean taskStop;
    private ArrayList<Square>  Wall;
    private Button btnAalg = new Button("A*algorytm");
    private Button btnDalg = new Button("Djikstra");
    private Button btnClr = new Button("Clear");
    private Button btnMaze = new Button("Maze");
    private Button btnRand = new Button("Random");
    private Button btnStop = new Button("Stop");
    private Button btnData = new Button("Generate Data");
    private ToggleButton btnRT = new ToggleButton("RTX ON/OFF");
    private Label alg = new Label("Select an algorithm: ");
    private RadioButton algA = new RadioButton("A*");
    private RadioButton algDj = new RadioButton("Djikstra");
    private Slider czasSl = new Slider(0,50,5); //min,max,start value
    private Slider rozm = new Slider(10,120,24);
    private Slider dens = new Slider(0,100,40);
    private Label czasSlValue = new Label("5 ms");
    private Label odwiedzeni = new Label("visited: 0");
    private Label czasSlName = new Label("Speed");
    private Label rozmValue = new Label("41x41");
    private Label rozmName = new Label("Grid size");
    private Label densName = new Label("Random density");
    private Label densVal = new Label("40%");
    private Label path = new Label("path: 0");
    private ToggleGroup group = new ToggleGroup();

    private String setStyle() {
        return ("-fx-background-color: \n" +
                "        linear-gradient(#f2f2f2, #d6d6d6),\n" +
                "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),\n" +
                "        linear-gradient(#dddddd 0%, #f6f6f6 50%);\n" +
                "    -fx-background-radius: 8,7,6;\n" +
                "    -fx-background-insets: 0,1,2;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
    }

    public Pane drawScene() {
        Logger logger = MyLog.getLogger();
        logger.info("DRAWING A SCENE!");
        czasSl.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                czasSlValue.textProperty().setValue(newValue.intValue() +" ms");
            }
        });
        rozm.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                rozmValue.textProperty().setValue(newValue.intValue()+"x"+newValue.intValue());
            }
        });
        dens.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                densVal.textProperty().setValue(newValue.intValue() +"%");
            }
        });

        czasSl.setShowTickLabels(true);
        czasSl.setMaxWidth(65);
        rozm.setShowTickLabels(true);
        rozm.setMaxWidth(65);
        dens.setShowTickLabels(true);
        dens.setMaxWidth(65);
        BorderPane root = new BorderPane();
        HBox czasSlider = new HBox(4,czasSl,czasSlValue);
        HBox rozmSlider = new HBox(4,rozm,rozmValue);
        HBox densSlider = new HBox(4,dens,densVal);
        VBox czasVBox = new VBox(1,czasSlName,czasSlider);
        czasVBox.setAlignment(Pos.CENTER);
        VBox rozmVBox = new VBox(1,rozmName,rozmSlider);
        rozmVBox.setAlignment(Pos.CENTER);
        VBox densVBox = new VBox(1,densName,densSlider);
        densVBox.setAlignment(Pos.CENTER);

        Pane red = new Pane(new Rectangle(15,15,Color.RED));
        Label redTX = new Label("start(alt)");
        Pane green = new Pane(new Rectangle(15,15,Color.GREEN));
        Label greenTX = new Label("end(shift)");
        Pane orange = new Pane(new Rectangle(15,15,Color.ORANGE));
        Label orangeTX = new Label("actual");
        Pane yellow = new Pane(new Rectangle(15,15,Color.YELLOW));
        Label yellowTX = new Label("path");
        Pane black = new Pane(new Rectangle(15,15,Color.BLACK));
        Label blackTX = new Label("wall(ctrl)");
        Pane blue = new Pane(new Rectangle(15,15,Color.DEEPSKYBLUE));
        Label blueTX = new Label("visited       ");
        Pane wheat = new Pane(new Rectangle(15,15,Color.LIGHTSKYBLUE));
        Label wheatTX = new Label("avaliable");

        btnRT.setTooltip(new Tooltip("Real Time Experience, toggle at your own risk"));
        algA.setToggleGroup(group);
        algDj.setToggleGroup(group);
        group.selectToggle(algA);
        algA.setDisable(true);
        algDj.setDisable(true);

        HBox menu = new HBox(4,btnAalg,btnDalg,btnClr,btnMaze,btnRand);
        menu.setAlignment(Pos.CENTER);
        HBox menu1 = new HBox(5,menu,densVBox,czasVBox,rozmVBox); //horizontalbox na menu
        HBox menu2 = new HBox(5,btnStop,btnRT,alg,algA,algDj,btnData);
        HBox menu3 = new HBox(3,red,redTX,green,greenTX,black,blackTX,orange,orangeTX,yellow,yellowTX,wheat,wheatTX,blue,blueTX,odwiedzeni,path);
        VBox wholeMenu = new VBox(1,menu1,menu2,menu3);
        AnchorPane anchor = new AnchorPane();

        root.setTop(wholeMenu);
        root.setPrefSize(width, height+100);
        root.setCenter(anchor);

        btnAalg.setStyle(setStyle());
        btnDalg.setStyle(setStyle());
        btnClr.setStyle(setStyle());
        btnMaze.setStyle(setStyle());
        btnRand.setStyle(setStyle());
        btnStop.setStyle(setStyle());
        btnRT.setStyle(setStyle() + "-fx-border-color: red");
        btnData.setStyle(setStyle());

        rozm.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            anchor.getChildren().clear();

            squareSize =(width / rozm.getValue()); //dla 3 jeszcze mi ciagnie, ale laguje, pozniej jest outofmemory
            x_square = (int) rozm.getValue();
            y_square = (int) rozm.getValue();
            sx = 1;
            sy = 1;
            ex = x_square - 2;
            ey = y_square - 2;
            grid = new Square[x_square][y_square];
            for (int i = 0; i < y_square; i++) {
                for (int j = 0; j < x_square; j++) {
                    Square square = new Square(i, j);
                    square.wall = false;
                    grid[i][j] = square;
                    anchor.getChildren().add(square);
                }
            }
            grid[sx][sy].setWall(false);
            grid[sx][sy].setStart(true);
            grid[sx][sy].setStartNode(grid[sx][sy].getBorder2());
            grid[ex][ey].setWall(false);
            grid[ex][ey].setEnd(true);
            grid[ex][ey].setEndNode(grid[ex][ey].getBorder2());
            anchor.resize(width,width);
        });
        rozm.setValue(41);

        btnAalg.setOnAction(actionEvent -> {
            for (int i=0; i<menu1.getChildren().size(); i++){
                menu1.getChildren().get(i).setDisable(true);
            }
            odwiedzeni.setText("visited: 0");
            path.setText("path: 0");
            algorithms.Aalgorythm alg = new algorithms.Aalgorythm(x_square, y_square, grid, menu1, odwiedzeni, path, btnStop);
            alg.mapCreate();    //grid na mapke w algorytmie
            alg.findPath((long)czasSl.getValue());
        });
        btnDalg.setOnAction(actionEvent -> {
            for (int i=0; i<menu1.getChildren().size(); i++){
                menu1.getChildren().get(i).setDisable(true);
            }
            odwiedzeni.setText("visited: 0");
            path.setText("path: 0");
            Dijkstra dj = new Dijkstra(x_square, y_square, grid, menu1, odwiedzeni, path, btnStop);
            dj.mapCreate();     //grid na mapke w algorytmie
            dj.findPath((long)czasSl.getValue());
            //dj.findPath();
        });
        btnClr.setOnAction(actionEvent -> gridClear());
        btnMaze.setOnAction(actionEvent -> {
            for (int i=0; i<menu1.getChildren().size(); i++){
                menu1.getChildren().get(i).setDisable(true);
            }
            primsMaze(menu1);
        });
        btnRand.setOnAction(actionEvent -> {
            gridClear();
            Random g = new Random();
            for (int i = 0; i < x_square*x_square*(dens.getValue()/100); i++) {
                int x = g.nextInt(x_square);
                int y = g.nextInt(y_square);
                grid[x][y].setWall(true);
                grid[x][y].changeColorWall(grid[x][y].getBorder2());
            }
            grid[sx][sy].setWall(false);
            grid[sx][sy].setStartNode(grid[sx][sy].getBorder2());
            grid[ex][ey].setWall(false);
            grid[ex][ey].setEndNode(grid[ex][ey].getBorder2());
        });
        btnRT.setOnAction(actionEvent -> {
            if(isClicked) {
                btnRT.setStyle("-fx-border-color: red");
                isClicked = false;
            } else {
                btnRT.setStyle("-fx-border-color: green");
                isClicked = true;
            }
            if(!btnRT.isSelected()){
                algA.setDisable(true);
                algDj.setDisable(true);
            }else {
                algA.setDisable(false);
                algDj.setDisable(false);
            }
        });
        btnData.setOnAction(actionEvent -> {
            ArrayList<String> data = new ArrayList<>();
            ArrayList<String> empty = null;
            for(int i = 0; i < y_square; i++){
                for(int j = 0; j < x_square; j++){
                    Square w = grid[i][j];
                    data.add("X:"+w.getX()+" Y:"+w.getY()+" W:"+String.valueOf(w.isWall())+" E:"+String.valueOf(w.isEnd())+" S:"+String.valueOf(w.isStart())+" C:"+w.getBorder2().getFill());
                    System.out.println("X:"+w.getX()+" Y:"+w.getY()+" W:"+String.valueOf(w.isWall())+" E:"+String.valueOf(w.isEnd())+" S:"+String.valueOf(w.isStart())+" C:"+w.getBorder2().getFill());
                }
            }
            MyLog.log(Level.INFO, MyLog.class.getName(), "we did it everybody, data:", "DataLog.log");
            MyLog.writeToFile(data, empty, "DataLog.log", true);
        });
        return root;
    }

    private void gridClear(){        //czysci grid
        for(int i = 0; i < y_square; i++){
            for(int j = 0; j < x_square; j++) {
                if(!grid[i][j].isStart() && !grid[i][j].isEnd()){
                    grid[i][j].wall=false;
                    grid[i][j].end=false;
                    grid[i][j].start=false;
                    grid[i][j].changeColorNotWall(grid[i][j].getBorder2());
                }
            }
        }
    }

    public class Square extends StackPane {
        private int x;
        private int y;
        private boolean wall;
        private boolean start;
        private boolean end;
        public int getX() { return x; }
        public int getY() { return y; }
        public void setWall(boolean wall) {
            this.wall = wall;
        }
        public void setStart(boolean start) { this.start = start; }
        public void setEnd(boolean end) { this.end = end; }
        public boolean isWall() {
            return wall;
        }
        public boolean isStart() {
            return start;
        }
        public boolean isEnd() {
            return end;
        }
        public Rectangle getBorder2() {
            return border;
        }
        private Rectangle border = new Rectangle(squareSize, squareSize);

        public Square(int x, int y) {
            this.x = x;
            this.y = y;

            border.setFill(Color.WHITE);
            border.setStroke(Color.BLUEVIOLET);
            getChildren().add(border);
            setTranslateX(x * squareSize);
            setTranslateY(y * squareSize);
            //ustawienie scian

            setOnMouseEntered(e -> {
                if(!btnAalg.isDisabled()) {
                    if (e.isControlDown()) {
                        if (!this.isEnd() && !this.isStart() ) {
                            if (this.isWall()) {              // sciana
                                changeColorNotWall(border);
                                this.wall = false;
                            }else {                           //sciana
                                changeColorWall(border);
                                this.wall = true;
                            }
                        }
                        //System.out.println(x + ", " + y);
                    }
                    if (e.isAltDown()) {         //start
                        if(!this.isEnd()){
                            this.wall = false;
                            this.start = true;
                            grid[sx][sy].start = false;
                            grid[sx][sy].changeColorNotWall(grid[sx][sy].getBorder2());
                            setStartNode(border);
                            sy = this.y;
                            sx = this.x;
                            if(btnRT.isSelected()){
                                if(group.getSelectedToggle()==algA) {
                                    algorithms.Aalgorythm alg = new algorithms.Aalgorythm(x_square, y_square, grid,odwiedzeni,path);
                                    alg.mapCreate();    //przekazuje gotowy grid do algorytmu
                                    alg.findPathRTX();
                                }else {
                                    Dijkstra dj = new Dijkstra(x_square, y_square, grid,odwiedzeni,path);
                                    dj.mapCreate();     //grid na mapke w algorytmie
                                    dj.findPathRTX();
                                }
                            }
                        }
                    }
                    if (e.isShiftDown()) {        //end
                        if(!this.isStart()) {
                            this.wall = false;
                            this.end = true;
                            grid[ex][ey].end = false;
                            grid[ex][ey].changeColorNotWall(grid[ex][ey].getBorder2());
                            setEndNode(border);
                            ey = this.y;
                            ex = this.x;
                            if(btnRT.isSelected()){
                                if(group.getSelectedToggle()==algA) {
                                    algorithms.Aalgorythm alg = new algorithms.Aalgorythm(x_square, y_square, grid,odwiedzeni,path);
                                    alg.mapCreate();    //przekazuje gotowy grid do algorytmu
                                    alg.findPathRTX();
                                }else {
                                    Dijkstra dj = new Dijkstra(x_square, y_square, grid,odwiedzeni,path);
                                    dj.mapCreate();     //grid na mapke w algorytmie
                                    dj.findPathRTX();
                                }
                            }
                        }
                    }
                }
            });
        }

        //kolorowanie kwadratow
        public void changeColorWall(Rectangle border) {
            border.setStroke(Color.BLACK);
            border.setFill(Color.BLACK);
        }
        public void changeColorNotWall(Rectangle border) {
            border.setStroke(Color.BLUEVIOLET);
            border.setFill(Color.WHITE);
        }
        public void setStartNode(Rectangle border) {
            border.setStroke(Color.RED);
            border.setFill(Color.RED);
        }
        public void setEndNode(Rectangle border) {
            border.setStroke(Color.GREEN);
            border.setFill(Color.GREEN);
        }
        public void setChecked(Rectangle border) {
            border.setStroke(Color.BLUEVIOLET);
            border.setFill(Color.DEEPSKYBLUE);
        }
        public void setActual(Rectangle border) {
            border.setStroke(Color.BLUEVIOLET);
            border.setFill(Color.ORANGE);
        }
        public void setPath(Rectangle border) {
            border.setStroke(Color.YELLOW);
            border.setFill(Color.YELLOW);
        }
        public void setNeigh(Rectangle border) {
            border.setStroke(Color.BLUEVIOLET);
            border.setFill(Color.LIGHTSKYBLUE);
        }
    }
    private void primsMaze(HBox menu){
        Wall = new ArrayList<>();
        taskStop = false;
        btnStop.setOnAction(actionEvent -> {
            if(!taskStop) {
                taskStop = true;
            }
        });
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < x_square; i++) {
                    for (int j = 0; j < y_square; j++) {
                        grid[i][j].setWall(true);
                        grid[i][j].changeColorWall(grid[i][j].getBorder2());
                    }
                }
                Square current = grid[sx][sy];
                current.setWall(false);
                current.setStartNode(current.getBorder2());
                pMne(current);
                Random gen = new Random();
                while (!Wall.isEmpty()) {
                    if (taskStop) {
                        Thread.currentThread().interrupt();
                        System.out.println("Maze interrupted");
                        break;
                    }
                    current = Wall.get(gen.nextInt(Wall.size()));
                    ArrayList<Square> sasiedzi = neigh(current);
                    Square rs = sasiedzi.get(gen.nextInt(sasiedzi.size()));
                    int w;
                    int z;
                    if (rs.getX() == current.getX()) {
                        w = rs.getX();
                        z = current.getY() + ((rs.getY() - current.getY()) / 2);
                    } else {
                        z = rs.getY();
                        w = current.getX() + ((rs.getX() - current.getX()) / 2);
                    }
                    grid[w][z].setWall(false);
                    grid[current.getX()][current.getY()].changeColorNotWall(grid[current.getX()][current.getY()].getBorder2());
                    grid[w][z].changeColorNotWall(grid[w][z].getBorder2());
                    current.setWall(false);
                    try {
                        Thread.sleep((long) czasSl.getValue());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    for (int k = 0; k < Wall.size(); k++) {
                        if (!Wall.get(k).isWall()) {
                            Wall.remove(Wall.get(k));
                        }
                    }
                    pMne(current);
                }
                grid[ex][ey].setWall(false);
                grid[ex][ey].setEndNode(grid[ex][ey].getBorder2());
                for (int i = 0; i < menu.getChildren().size(); i++) {
                    menu.getChildren().get(i).setDisable(false);
                }
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);     //zatrzymuje jak sie zamknie program
        th.start();
    }
    private void pMne(Square n){
        int x=n.getX();
        int y=n.getY();

        if(x>1 && grid[x-2][y].isWall()){
            Wall.add(grid[x-2][y]);
        }
        if(x<x_square-2 && grid[x+2][y].isWall()){
            Wall.add(grid[x+2][y]);
        }
        if(y>1 && grid[x][y-2].isWall()){
            Wall.add(grid[x][y-2]);
        }
        if(y<y_square-2 && grid[x][y+2].isWall()){
            Wall.add(grid[x][y+2]);
        }
    }
    private ArrayList<Square> neigh(Square n){
        ArrayList<Square> sasiedzi = new ArrayList<>();
        int x=n.getX();
        int y=n.getY();
        if(x>1 && !grid[x-2][y].isWall()){
            sasiedzi.add(grid[x-2][y]);
        }
        if(x<x_square-2 && !grid[x+2][y].isWall()){
            sasiedzi.add(grid[x+2][y]);
        }
        if(y>1 && !grid[x][y-2].isWall()){
            sasiedzi.add(grid[x][y-2]);
        }
        if(y<y_square-2 && !grid[x][y+2].isWall()){
            sasiedzi.add(grid[x][y+2]);
        }
        return sasiedzi;
    }
}