package View;import ViewModel.MyViewModel;import javafx.event.ActionEvent;import javafx.fxml.Initializable;import javafx.scene.control.Button;import javafx.scene.image.Image;import javafx.scene.image.ImageView;import java.io.InputStream;import java.net.URL;import java.util.ResourceBundle;/** * ExitController - controller of Exit stage */public class ExitController implements Initializable {    public Button cancel_btn; //cancel the exit button    public Button yes_Btn; //yes = exit the game    public ImageView ursula;    public void continueExit(ActionEvent actionEvent) { //if user decide to exit the game        MyViewModel viewModel = MyViewModel.getInstance(); //hold viewModel instance        viewModel.exitGame(); //exit game = close servers and finish the game        actionEvent.consume();        System.exit(0);    }    @Override    public void initialize(URL url, ResourceBundle resourceBundle) {        ursula.setImage(new Image(getImageResources("images/Ursula2.png")));    }    //return images    private InputStream getImageResources(String path){        return (getClass().getClassLoader().getResourceAsStream(path));    }}