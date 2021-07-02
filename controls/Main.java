package controls;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    GridDrawer grid = new GridDrawer();
    Scene gridScene = new Scene(grid.drawScene());

    @Override
    public void start(Stage primaryStage) {
        gridScene.getRoot().requestFocus();
        primaryStage.setTitle("Pathfinding Visualizer");
        primaryStage.setScene(gridScene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
