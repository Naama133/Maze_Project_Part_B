package Model;import Client.Client;import IO.MyDecompressorInputStream;import Server.Server;import algorithms.mazeGenerators.IMazeGenerator;import algorithms.mazeGenerators.Maze;import algorithms.search.*;import Server.ServerStrategyGenerateMaze;import Server.ServerStrategySolveSearchProblem;import Client.IClientStrategy;import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;import Server.Configurations;import java.io.*;import java.net.InetAddress;import java.util.Observable;import java.util.Observer;/** * MyModel - implements IModel interface * Response for the logic of the application - create mazes & solve them by using servers * Fields: *     ModelInstance (single tone - hold one instance of this class) *     modelMaze (holds it's current maze - one at a time) *     PlayerRow (player row in the maze) *     PlayerCol (player col in the maze) *     modelMazeSolution (solution of the current maze) *     GenerateMazeServer (server that generates mazes) *     SolveMazeServer (server that solves mazes) *     LOG (logger - log4j2 - single tone) */public class MyModel extends Observable implements IModel {    private static MyModel ModelInstance;    private Maze modelMaze;    private int PlayerRow;    private int PlayerCol;    private Solution modelMazeSolution;    private Server GenerateMazeServer;    private Server SolveMazeServer;    private static final Logger LOG = LogManager.getLogger();    //constructor    private MyModel() {        modelMaze = null;        PlayerRow = 0;        PlayerCol = 0;        modelMazeSolution = null;        initServers(); //init the two servers    }    public static MyModel getInstance(){        if(ModelInstance == null)            ModelInstance = new MyModel();        return ModelInstance;    }    //getters    @Override    public Solution getSolution() {return modelMazeSolution;}    public Maze getMaze(){        return modelMaze;    }    public int getPlayerRow() {        return PlayerRow;    }    public int getPlayerCol() {        return PlayerCol;    }    //delete the solution field (set null)    public void deleteSolution() {        modelMazeSolution = null;    }    @Override    public void solveMaze() { //solve the current maze, using the server        ISearchingAlgorithm searcher = Configurations.getSearchingAlgorithm(); //find the searcher configuration, and set searcher        LOG.info("Start solving maze operation");        LOG.info("Current Solver = " + catString(searcher.getClass().toString()));        try{            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {                @Override                public void clientStrategy(InputStream inputStream, OutputStream outputStream) {                    try{                        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);                        ObjectInputStream fromServer = new ObjectInputStream(inputStream);                        toServer.flush();                        toServer.writeObject(modelMaze); //send maze to server                        toServer.flush();                        modelMazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server                        LOG.info("Solution path length = " + modelMazeSolution.getSolutionPath().size()); //write solution size to the log file                    } catch (Exception e) {                        LOG.error("Connection failed - server couldn't solve the given maze");}}});            client.communicateWithServer();            LOG.info("Client " + InetAddress.getLocalHost() + " connected to server with port 5400, for solving the maze");        }        catch(IOException e){            LOG.error("Connection to server with port 5401 has failed");        }        LOG.info("Finish solving maze..");        setChanged();        notifyObservers("ModelSolvedMaze");    }    public void generateMaze(int rows, int cols){ //generate new maze, using the server        IMazeGenerator generator = Configurations.getGeneratingAlgorithm(); //find the generator configuration, and set generator        LOG.info("Start generating maze operation, with Rows = " + rows + " and Columns = " + cols); //write maze dimensions to log file        LOG.info("Generating algorithm = " + catString(generator.getClass().toString())); //write generator type to log file        String action = "ModelGenerateMaze";        try{            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {                public void clientStrategy(InputStream inputStream, OutputStream outputStream){                    try{                        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);                        ObjectInputStream fromServer = new ObjectInputStream(inputStream);                        toServer.flush();                        int[] mazeDimensions = new int[]{rows, cols};                        toServer.writeObject(mazeDimensions);                        toServer.flush();                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));                        byte[] decompressedMaze = new byte[mazeDimensions[0]*mazeDimensions[1]+12]; //allocating byte[] for the decompressed maze -                        is.read(decompressedMaze); //Fill decompressedMaze with bytes                        modelMaze = new Maze(decompressedMaze);                        PlayerRow =modelMaze.getStartPosition().getRowIndex();                        PlayerCol = modelMaze.getStartPosition().getColumnIndex();                        LOG.debug("Player start position = {" + PlayerRow +"," + PlayerCol + "}"); //write maze start & goal positions to log file)                        LOG.debug("Player goal position = {" + modelMaze.getGoalPosition().getRowIndex() +"," + modelMaze.getGoalPosition().getColumnIndex() + "}");                    }                    catch (Exception e) {                        LOG.error("Connection failed - server couldn't create the new maze");                    }                }            });            client.communicateWithServer();            LOG.info("Client " + InetAddress.getLocalHost() + " connected to server with port 5400, for creating the maze");        }        catch (Exception e) {            LOG.error("Connection to server with port 5400 has failed");        }        LOG.info("Finish generating maze operation");        deleteSolution();        setChanged();        notifyObservers(action);    }    //set the given maze (which loaded by user) to the model instance    public void setLoadedMaze(Maze loadedMaze){        String action = "ModelLoadedMaze";        LOG.debug("Set Loaded maze to MyModel instance..");        //set maze & player position        modelMaze = loadedMaze;        PlayerRow =modelMaze.getStartPosition().getRowIndex();        PlayerCol = modelMaze.getStartPosition().getColumnIndex();        deleteSolution(); //delete the solution of the old maze        setChanged();        notifyObservers(action);    }    //Update Location of the player is the Maze    public void UpdatePlayerPosition(int direction){        LOG.debug("Request to update player position has been made");        String ActionMessage = "ModelUpdatePlayerPosition";        switch (direction) {            case 0: //restart maze -> move player to start position                PlayerRow =modelMaze.getStartPosition().getRowIndex();                PlayerCol = modelMaze.getStartPosition().getColumnIndex();                ActionMessage = "restartPlayerPosition";                LOG.debug("Player moved to the start position of the maze");                break;            case 8:                if(PlayerRow>0)                    if(modelMaze.getMazeContent()[PlayerRow-1][PlayerCol]==0)                        PlayerRow--; //UP                    else{ActionMessage = "Wall";}                else{ActionMessage = "BoundariesProblem";}                break;            case 2:                if(PlayerRow<modelMaze.getRows()-1)                    if(modelMaze.getMazeContent()[PlayerRow+1][PlayerCol]==0)                        PlayerRow++; //DOWN                    else{ActionMessage = "Wall";}                else{ActionMessage = "BoundariesProblem";}                break;            case 4:                if(PlayerCol>0)                    if(modelMaze.getMazeContent()[PlayerRow][PlayerCol-1]==0)                        PlayerCol--; //LEFT                    else{ActionMessage = "Wall";}                else{ActionMessage = "BoundariesProblem";}                break;            case 6:                if(PlayerCol<modelMaze.getColumns()-1)                    if(modelMaze.getMazeContent()[PlayerRow][PlayerCol+1]==0)                        PlayerCol++; //RIGHT                    else{ActionMessage = "Wall";}                else{ActionMessage = "BoundariesProblem";}                break;            case 1:                if((PlayerRow<modelMaze.getRows()-1)&&(PlayerCol>0)){                    if(modelMaze.getMazeContent()[PlayerRow+1][PlayerCol-1]==0){                        PlayerRow++; //DOWN                        PlayerCol--; //LEFT                    }                    else{ActionMessage = "Wall";}                }                else{ActionMessage = "BoundariesProblem";}                break;            case 3:                if((PlayerRow<modelMaze.getRows()-1)&&(PlayerCol<modelMaze.getColumns()-1)){                    if(modelMaze.getMazeContent()[PlayerRow+1][PlayerCol+1]==0){                        PlayerRow++; //DOWN                        PlayerCol++; //RIGHT                    }                    else{ActionMessage = "Wall";}                }                else{ActionMessage = "BoundariesProblem";}                break;            case 7:                if((PlayerRow>0)&&(PlayerCol>0)){                    if(modelMaze.getMazeContent()[PlayerRow-1][PlayerCol-1]==0){                        PlayerRow--; //UP                        PlayerCol--; //LEFT                    }                    else{ActionMessage = "Wall";}                }                else{ActionMessage = "BoundariesProblem";}                break;            case 9:                if((PlayerRow>0)&&(PlayerCol<modelMaze.getColumns()-1)){                    if(modelMaze.getMazeContent()[PlayerRow-1][PlayerCol+1]==0){                        PlayerRow--; //UP                        PlayerCol++; //RIGHT                    }                    else{ActionMessage = "Wall";}                }                else{ActionMessage = "BoundariesProblem";}                break;        }        String currPlayerPosition = "{" + PlayerRow + "," + PlayerCol + "}";        if(currPlayerPosition.equals(modelMaze.getGoalPosition().toString())){ //check if the user found the solution            ActionMessage = "UserSolvedTheMaze";            LOG.debug("The user solved the maze");}        setChanged();        notifyObservers(ActionMessage);}    @Override    public void assignObserver(Observer O) {        this.addObserver(O);    }    //stop the two servers    public void shutDownServers(){        GenerateMazeServer.stop();        SolveMazeServer.stop();        LOG.info("stop GenerateMazeServer (port 5400)");        LOG.info("stop SolveMazeServer (port 5401)");        //wait for closer to finish        GenerateMazeServer.JoinTermination();        SolveMazeServer.JoinTermination();    }    //init the two servers    public void initServers(){        GenerateMazeServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());        SolveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());        GenerateMazeServer.start();        SolveMazeServer.start();        LOG.info("Starting GenerateMazeServer at port = 5400");        LOG.info("Starting SolveMazeServer at port = 5401");    }    //clean string from unnecessary parts    private String catString(String str){        int i = str.indexOf(".");        str = str.substring(i+1);        i = str.indexOf(".");        return str.substring(i+1);    }}