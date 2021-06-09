package View;import javafx.fxml.Initializable;import javafx.scene.control.Label;import java.net.URL;import java.util.ResourceBundle;/** * HelpController - controller of help stage */public class HelpController implements Initializable {    public Label helpText;    //todo: update at the end (if something has been changed)    //todo: add explanation about start and goal (with the images)    @Override    public void initialize(URL location, ResourceBundle resources) {        String help =                "\nIn this game, to create a maze you must click on the button \"Generate Maze\" On the left side of the window,\n" +                "or press File -> New in the top Menu Bar.\n"+                "Note that you can choose the dimensions of the maze (the minimum size is 2 * 2, and the maximum is 1000 * 1000)\n\n"+                "Now, you have got a new maze, with the cartoon character with whom you move in the maze located at the starting point of the maze.\n " +                "Your goal (of course) is to reach the end point of the maze.\n\n" +                "As you move through the maze, you should avoid getting stuck in the maze walls, and move solely on the tracks.\n " +                "Also, do not go beyond the boundaries of the maze.\n\n" +                "You can move in any direction (up, down, right and left), and also diagonally,\n"+                "Using the NUMPAD keys, Or by using the keyboard arrows (only for up, down, right and left).\n"+                "But first, you must click once with your mouse on the game board, and than you will be able to press the keys.\n\n"+                "NUMPAD options:\n"+                "Press \"4\" to go Left\n"+                "Press \"6\" to go Right\n"+                "Press \"8\" to go Up\n"+                "Press \"2\" to go Down\n"+                "Press \"7\" to go Up-Left\n"+                "Press \"9\" to go Up-Right\n"+                "Press \"1\" to go Down-Left\n"+                "Press \"3\" to go Down-Right\n\n"+                "If you want to see the solution of the maze, click the \"Show/Hide Maze Solution\" button,\n"+                "and the solution path will be displayed on the game board.\nIf you want to hide the solution, press that button again.\n\n"+                "If you want to restart the game, click the \"Restart the game\" button.\n\n\n"+                "Good Luck,\n" +                "Dar Abu & Naama Baruch\n\n";        helpText.setText(help); //set the "help" text    }}