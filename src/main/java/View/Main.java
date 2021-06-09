package View;import Model.*;import ViewModel.MyViewModel;import javafx.application.Application;import javafx.fxml.FXMLLoader;import javafx.scene.Parent;import javafx.scene.Scene;import javafx.stage.Stage;//todo - check all exceptions of last projects - handle them here//todo: music correction (change when needed) + let user stop and start//todo: "eat" the solution?//todo: add music button (play & stop) & add volume scale//todo: image zoom/** * Main - init the Application (main stage) */public class Main extends Application {    @Override    public void start(Stage primaryStage) throws Exception{        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("View/MyView.fxml"));        Parent root = fxmlLoader.load();        primaryStage.setTitle("Maze Game");        Scene scene = new Scene(root, 800, 600);        primaryStage.setScene(scene);        IModel model = MyModel.getInstance();        MyViewModel viewModel = MyViewModel.getInstance();        MyViewController view =fxmlLoader.getController();        viewModel.addObserver(view);        MyViewController MvController = fxmlLoader.getController();        MvController.setStageAndScene(primaryStage);        primaryStage.setOnCloseRequest(e->{ //listen to close event (user press on "x" key)            view.checkExitWanted();        });        primaryStage.show();    }    public static void main(String[] args) {        launch(args);    }}