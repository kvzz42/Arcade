package arcade;

import arcade.handler.BallAdderLoop;
import arcade.handler.GameLoop;
import arcade.level.ChaosLevel;
import arcade.level.SurvivalLevel;
import arcade.level.breakout.BreakoutLevel;
import arcade.level.breakout.BreakoutLevel1;
import arcade.sprite.Ball;
import arcade.sprite.Block;
import arcade.sprite.Paddle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * The main class of the program.
 * This class contains the main menu, with buttons allowing access to game modes
 * and informative text, such as How to Play instructions and High Scores.
 * @author Kevin
 */
public class Arcade extends Application
{
    // all game modes for which high scores are supported
    public static final String[] HIGH_SCORE_GAME_MODES =
    {
        BreakoutLevel.GAME_MODE,
        SurvivalLevel.GAME_MODE
    };
    public static final File[] HIGH_SCORE_FILES = new File[HIGH_SCORE_GAME_MODES.length];
    
    // text files; private because only accessed by main class
    private String HowToPlay;
    private final ArrayList<String> HighScoresTemplate = new ArrayList<>(); // array to print lines to text files properly
    
    // default colors and dimensions
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final int DEFAULT_STAGE_WIDTH = 1200;
    public static final int DEFAULT_STAGE_HEIGHT = 900;
    
    // non-final variables so they can be changed, i.e. in Chaos mode
    // static variables so they can be accessed from within levels when drawing
    public static Color BACKGROUND_COLOR = DEFAULT_BACKGROUND_COLOR;
    public static Color TEXT_COLOR = DEFAULT_TEXT_COLOR;
    public static int STAGE_WIDTH = DEFAULT_STAGE_WIDTH;
    public static int STAGE_HEIGHT = DEFAULT_STAGE_HEIGHT;
    
    // the approximate amount of space text in the top
    // left corner of the game screen takes up on either side
    public static final int VERT_TEXT_SPACE = 60;
    public static final int HORIZ_TEXT_SPACE = 200;
    
    // gameplay related variables
    public static boolean SAVE_STATES = false;
    
    @Override
    public void start(Stage primaryStage) throws IOException
    {
        // set up stage
        primaryStage.setTitle("Main Menu");
        StackPane root = new StackPane();
        Group group = new Group();
        VBox box = new VBox();
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);
        
        // create text/buttons
        Text welcome = new Text("Welcome to Kevin's arcade! Choose your game!");
        box.getChildren().add(welcome);
        addButtons(primaryStage, box);
        
        // read in all files once
        initializeFiles();
        
        // set up and show scene
        group.getChildren().add(box);
        root.getChildren().add(group);
        Scene scene = new Scene(root, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    // adds all necessary buttons to main menu, keeping start method cleaner
    private void addButtons(Stage primaryStage, VBox box)
    {
        Button breakout = new Button("Play " + BreakoutLevel.GAME_MODE);
        breakout.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.close();
                playBreakout();
                primaryStage.show();
            }
        });
        box.getChildren().add(breakout);
        Button survival = new Button("Play " + SurvivalLevel.GAME_MODE);
        survival.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.close();
                playSurvival();
                primaryStage.show();
            }
        });
        box.getChildren().add(survival);
        Button chaos = new Button("Play " + ChaosLevel.GAME_MODE);
        chaos.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.close();
                playChaos();
                primaryStage.show();
            }
        });
        box.getChildren().add(chaos);
        Button highScores = new Button("High Scores");
        highScores.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.close();
                showHighScores();
                primaryStage.show();
            }
        });
        box.getChildren().add(highScores);
        Button howToPlay = new Button("How to Play");
        howToPlay.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.close();
                showHowToPlay();
                primaryStage.show();
            }
        });
        box.getChildren().add(howToPlay);
        Button options = new Button("Options");
        options.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                primaryStage.close();
                showOptions();
                primaryStage.show();
            }
        });
        box.getChildren().add(options);
    }
    // reads in all files and stores them or their information into class fields
    private void initializeFiles() throws IOException
    {
        // how to play text
        Scanner rd = new Scanner(Arcade.class.getResourceAsStream("files/HowToPlay.txt"));
        StringBuilder txt = new StringBuilder();
        while (rd.hasNextLine())
            txt.append(rd.nextLine()).append('\n');
        HowToPlay = txt.toString();
        
        // high scores template
        rd = new Scanner(Arcade.class.getResourceAsStream("files/HighScoresTemplate.txt"));
        while (rd.hasNextLine())
            HighScoresTemplate.add(rd.nextLine());
        
        // high score files
        for (int i = 0; i < HIGH_SCORE_FILES.length; ++i)
            HIGH_SCORE_FILES[i] = getHighScores(HIGH_SCORE_GAME_MODES[i]);
    }
    // retrieves high score file for given game mode
    // called in main class to initialize all files at once
    private File getHighScores(String gameMode) throws IOException
    {
        File f = new File(gameMode + " High Scores.txt");
        
        // creates new high score file for given game mode using template if it does not currently exist
        if (f.createNewFile())
        {
            PrintStream ps = new PrintStream(f);
            for (String line : HighScoresTemplate)
                ps.println(line);
        }
        
        // uneditable except when recording scores
        f.setReadOnly();
        
        return f;
    }
    
    // using given stage, initializes drawing canvas, appropriate Level object,
    // and game loop to start playing Breakout game mode
    public void playBreakout()
    {
        Stage stage = new Stage();
        stage.setWidth(STAGE_WIDTH);
        stage.setHeight(STAGE_HEIGHT);
        stage.setResizable(false);
        Group root = new Group();
        Canvas canvas = new Canvas(stage.getWidth(), stage.getHeight());
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        BreakoutLevel lvl = new BreakoutLevel1(stage, canvas, 0, BreakoutLevel.DEFAULT_LIVES);
                            // first level, 0 start score, 3 default start lives
        do // level loop
        {
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            final double timeStart = System.currentTimeMillis(); // to keep track of time spent in game
            GameLoop loop = new GameLoop(lvl, timeStart, timeline);
            KeyFrame frames = new KeyFrame(Duration.seconds(1/120.0), loop);
            
            timeline.getKeyFrames().add(frames);
            timeline.play();
            stage.showAndWait(); // showAndWait() keeps code execution from passing this line until window is closed
            timeline.stop(); // end timeline if user closes window without finishing Level
            
            // if player chooses to keep playing, load next level before restarting level loop
            if (GameLoop.continueGame())
                lvl = lvl.loadNextLevel();
        }
        while (GameLoop.continueGame());
    }
    
    // Survival game mode
    public void playSurvival()
    {
        Stage stage = new Stage();
        stage.setWidth(STAGE_WIDTH);
        stage.setHeight(STAGE_HEIGHT);
        stage.setResizable(false);
        Group root = new Group();
        Canvas canvas = new Canvas(stage.getWidth(), stage.getHeight());
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        SurvivalLevel lvl = new SurvivalLevel(stage, canvas);
        
        do
        {
            // music
            Clip clip;
            try
            {
                clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(Arcade.class.getResource("files/Survival.wav")));
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            catch (LineUnavailableException | UnsupportedAudioFileException | IOException e)
            {
                clip = null;
            }
            
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            final double timeStart = System.currentTimeMillis();
            GameLoop loop = new GameLoop(lvl, timeStart, timeline);
            KeyFrame frames = new KeyFrame(Duration.seconds(1/120.0), loop);
            
            // this timeline adds balls to the game every ten seconds
            // needs a new class because inner classes cannot interact with non-final variables
            Timeline ballAdder = new Timeline();
            ballAdder.setCycleCount(Timeline.INDEFINITE);
            BallAdderLoop ballLoop = new BallAdderLoop(lvl, ballAdder);
            KeyFrame addBallFrames = new KeyFrame(Duration.seconds(10), ballLoop);
            
            // plays both timelines side by side
            timeline.getKeyFrames().add(frames);
            ballAdder.getKeyFrames().add(addBallFrames);
            timeline.play();
            ballAdder.play();
            stage.showAndWait();
            if (clip != null)
                clip.stop();
            timeline.stop();
            ballAdder.stop();
            
            if (GameLoop.continueGame())
                lvl = lvl.loadNextLevel();
        }
        while (GameLoop.continueGame());
    }
    
    // Chaos game mode
    public void playChaos()
    {
        // stores background colors
        Color bcTemp = BACKGROUND_COLOR;
        Color tcTemp = TEXT_COLOR;
        
        Stage stage = new Stage();
        stage.setWidth(STAGE_WIDTH);
        stage.setHeight(STAGE_HEIGHT);
        stage.setResizable(false);
        Group root = new Group();
        Canvas canvas = new Canvas(stage.getWidth(), stage.getHeight());
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        ChaosLevel lvl = new ChaosLevel(stage, canvas);
        
        do
        {
            Timeline timeline = new Timeline();
            timeline.setCycleCount(3600); // for 30 seconds at 120 fps
            final double timeStart = System.currentTimeMillis();
            GameLoop loop = new GameLoop(lvl, timeStart, timeline);
            KeyFrame frames = new KeyFrame(Duration.seconds(1/120.0), loop);
            
            // this timeline changes the background color every third of a second
            // does not need a new class like BallAdderLoop because it only interacts with static variables
            Timeline colorChanger = new Timeline();
            colorChanger.setCycleCount(90); // for 30 seconds at 3 fps
            KeyFrame changeColorFrames = new KeyFrame(
                Duration.seconds(1/3.0),
                new EventHandler<ActionEvent>()
                {
                    final Random r = new Random();
                    
                    @Override
                    public void handle(ActionEvent event)
                    {
                        BACKGROUND_COLOR = Color.color(r.nextDouble(), r.nextDouble(), r.nextDouble());
                        TEXT_COLOR = BACKGROUND_COLOR.invert();
                    }
                });
            
            // plays both timelines side by side
            timeline.getKeyFrames().add(frames);
            colorChanger.getKeyFrames().add(changeColorFrames);
            timeline.play();
            colorChanger.play();
            stage.showAndWait();
            timeline.stop();
            colorChanger.stop();
            
            if (GameLoop.continueGame())
                lvl = lvl.loadNextLevel();
        }
        while (GameLoop.continueGame());
        
        // restores background colors
        BACKGROUND_COLOR = bcTemp;
        TEXT_COLOR = tcTemp;
    }
    
    // prints scores for every supported game mode
    public void showHighScores()
    {
        Stage stage = new Stage();
        stage.setTitle("High Scores");
        stage.setResizable(false);
        HBox box = new HBox();
        StringBuilder txt = new StringBuilder();
        try
        {
            for (int i = 0; i < HIGH_SCORE_FILES.length; ++i)
            {
                txt.append(HIGH_SCORE_GAME_MODES[i]).append('\n');
                File f = HIGH_SCORE_FILES[i];
                Scanner rd = new Scanner(f);
                while (rd.hasNextLine())
                    txt.append(rd.nextLine()).append('\n');
                txt.append('\n'); // line separator between game modes
            }
        }
        catch (FileNotFoundException e)
        {
        }
        TextArea info = new TextArea(txt.toString());
        info.setEditable(false);
        info.setWrapText(true);
        box.getChildren().add(info);
        Scene scene = new Scene(box, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    // prints how to play text using stored file
    public void showHowToPlay()
    {
        Stage stage = new Stage();
        stage.setTitle("How to Play");
        stage.setResizable(false);
        HBox box = new HBox();
        TextArea info = new TextArea(HowToPlay);
        info.setEditable(false);
        info.setWrapText(true);
        box.getChildren().add(info);
        Scene scene = new Scene(box, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    // displays options menu
    public void showOptions()
    {
        Stage stage = new Stage();
        stage.setTitle("Options");
        stage.setResizable(false);
        TabPane tp = new TabPane();
        
        Tab res = new Tab("Resolutions");
        VBox resBox = new VBox();
        //resBox.setSpacing(10);
        resBox.setAlignment(Pos.BASELINE_CENTER);
        resBox.getChildren().add(new Text("Choose a resolution:"));
        ComboBox chooseRes = new ComboBox(FXCollections.observableArrayList("1200 x 900", "800 x 600"));
        chooseRes.getSelectionModel().select(STAGE_WIDTH + " x " + STAGE_HEIGHT);
        chooseRes.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Scanner rd = new Scanner(chooseRes.getValue().toString());
                STAGE_WIDTH = rd.nextInt();
                rd.next(); // skip " x "
                STAGE_HEIGHT = rd.nextInt();
                
                // updating everything affected by resolution change
                Ball.MAX_SPEED = STAGE_HEIGHT/60.0;
                Ball.DEFAULT_SPEED_INC = STAGE_HEIGHT/1800.0;
                Ball.DEFAULT_RADIUS = STAGE_HEIGHT/180.0;
                Paddle.DEFAULT_WIDTH = STAGE_HEIGHT/3.0;
                Paddle.DEFAULT_THICKNESS = STAGE_HEIGHT/90.0;
                Paddle.DEFAULT_SPEED = STAGE_HEIGHT/90.0;
                Block.DEFAULT_GAP = STAGE_HEIGHT/90.0;
            }
        });
        resBox.getChildren().add(chooseRes);
        res.setContent(resBox);
        res.setClosable(false);
        tp.getTabs().add(res);
        
        //Tab colors = new Tab("Colors");
        //colors.setClosable(false);
        //tp.getTabs().add(colors);
        
        Tab saveStates = new Tab("Save States");
        VBox ssBox = new VBox();
        ssBox.setAlignment(Pos.BASELINE_CENTER);
        CheckBox checkSS = new CheckBox("Toggle save states (No high scores when on!)");
        checkSS.setSelected(SAVE_STATES);
        checkSS.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                SAVE_STATES = !SAVE_STATES;
            }
        });
        ssBox.getChildren().add(checkSS);
        saveStates.setContent(ssBox);
        saveStates.setClosable(false);
        tp.getTabs().add(saveStates);
        
        Scene scene = new Scene(tp, 300, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}