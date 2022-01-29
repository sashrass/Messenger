package com.messenger.minimessenger;

import Roles.User;
import com.messenger.minimessenger.Threads.MessagesTracker;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    double x, y;
    public static volatile Stage stg;
    private static Parent pane;
    public static User user;
    @FXML private AnchorPane Top0;

    @Override
    public void start(Stage stage) throws IOException {
        stg = stage;
        stage.setResizable(true);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        pane = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        stage.initStyle(StageStyle.DECORATED.UNDECORATED);
        Scene scene = new Scene(pane, 800, 600);



        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                x = mouseEvent.getSceneX();
                y = mouseEvent.getSceneY();
            }
        });


        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() - x);
                stage.setY(mouseEvent.getScreenY() - y);
            }
        });
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void stop() {
        MessagesTracker.isProgrammRun = false;
    }

    public void changeScene(String fxml) throws IOException {
        pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.getScene().setRoot(pane);
        //stg.setTitle("Logged in");
    }

    public static Parent getPane() {
        return pane;
    }

    public static Stage getStage() {
        return stg;
    }


    public static void main(String[] args) {
        launch();
    }


}
