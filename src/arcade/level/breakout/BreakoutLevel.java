package arcade.level.breakout;

import arcade.sprite.Ball;
import arcade.sprite.Block;
import arcade.sprite.Paddle;
import arcade.level.ScoringLevel;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

/**
 * Defines how to create a Level of the Breakout game mode.
 * @author Kevin
 */
public interface BreakoutLevel extends ScoringLevel
{
    // game mode used for recording scores and displaying name in titles
    public static final String GAME_MODE = "Breakout";
    public static final int HIGH_SCORE_GAME_NUM = 0;
    
    public static final int DEFAULT_LIVES = 3;
    
    // getters for all fields in a game of Breakout
    public Stage getStage();
    public Canvas getCanvas();
    public Scene getScene();
    public ArrayList<Block> getBlocks();
    public Paddle getPaddle();
    public Ball getBall();
    public int getTotalBlocks();
    public int getBlocksBroken();
    public int getDrops();
    public int getLives();
    
    
    // no setters because most fields are final and
    // those which are not are only to be changed internally
    
    @Override
    public BreakoutLevel loadNextLevel();
    public void loadState(double timeElapsed, int score, int drops, ArrayList<Block> blocks,
                          double ballX, double ballY, double ballSpeed, double ballAngle);
}