package View;

import Model.*;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    //todo - check all exceptions of last projects - handle them here
    //todo - not config - menu to choose generator, algorithm, number of threads
    //todo - press key before we have any maze  - exception
    //todo - move maze to the middle of the screen?
    //todo - add button to set size of game board
    //todo - add comments to all files
    //todo - no printStackTrace - all exceptions will be shown in alert window!
    //todo - where to put a main file?

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Maze Game");
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        IModel model = MyModel.getInstance();
        MyViewModel viewModel = MyViewModel.getInstance();
        MyViewController view =fxmlLoader.getController();
        viewModel.addObserver(view);
        primaryStage.setOnCloseRequest(e->{  //todo : clean closer (X or exit button)
            view.checkExitWanted();
        });
        primaryStage.show();
    }

    public void closeProgram(IModel model) {
        model.shutDownServers();
    }

    public static void main(String[] args) {
        launch(args);
    }
}