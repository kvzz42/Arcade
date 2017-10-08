package arcade.level;

import arcade.Arcade;
import arcade.sprite.Ball;
import arcade.sprite.Paddle;
import arcade.handler.PaddleAimer;
import arcade.handler.KeyPressedActions;
import arcade.handler.KeyReleasedActions;
import arcade.sprite.SurvivalBall;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Creates a Level of the Survival game mode.
 * @author Kevin
 */
public class SurvivalLevel implements ScoringLevel
{
    public static final String GAME_MODE = "Survival";
    public static final int HIGH_SCORE_GAME_NUM = 1;
    private static final int LEVEL_NUM = 1;
    
    private final Stage stage;
    private final Scene scene;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle p;
    private final ArrayList<SurvivalBall> balls;
    private final AtomicBoolean playNextLevel;
    private int score;
    private int drops;
    
    public SurvivalLevel(Stage stage, Canvas canvas)
    {
        stage.setTitle(GAME_MODE);
        this.stage = stage;
        scene = stage.getScene();
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        p = new Paddle(canvas);
        scene.setOnKeyPressed(new KeyPressedActions(p, this));
        scene.setOnKeyReleased(new KeyReleasedActions(p));
        scene.setCursor(Cursor.CROSSHAIR);
        scene.setOnMouseMoved(new PaddleAimer(p));
        balls = new ArrayList<>();
        Random r = new Random();
        balls.add(new SurvivalBall(canvas, Ball.DEFAULT_RADIUS,
                                   r.nextDouble() * canvas.getWidth(),
                                   r.nextDouble() * p.getY(), // can't spawn below paddle
                                   Arcade.STAGE_HEIGHT/600.0, r.nextDouble() * (180 - 2*Ball.MIN_ANGLE) + (180 + Ball.MIN_ANGLE))); // slower speed than usual,
                                                                    // angle = same as possible angles from paddle
        playNextLevel = new AtomicBoolean();
        score = 0;
        drops = 0;
    }
    
    @Override
    public void draw(double timeElapsed)
    {
        gc.setFill(Arcade.BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        p.draw();
        for (Ball b : balls)
            b.draw();
        gc.setFill(Arcade.TEXT_COLOR);
        gc.fillText("Total Balls: " + balls.size()
                  + "\nSeconds Elapsed: " + timeElapsed
                  + "\nScore: " + score, 0, 10);
    }
    
    @Override
    public void update(double timeElapsed)
    {
        for (Ball b : balls)
            if (!(b.hitsPaddle(p) || b.hitsWall()))
                if (b.updatePos())
                    ++drops;
        p.updatePos();
        score = 100*(int)timeElapsed;
    }
    
    @Override
    public boolean isOver()
    {
        return drops > 0;
    }
    
    @Override
    public void printEndText()
    {
        gc.setFill(Arcade.TEXT_COLOR);
        Font temp = gc.getFont();
        TextAlignment temp2 = gc.getTextAlign();
        VPos temp3 = gc.getTextBaseline();
        gc.setFont(new Font(42));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("GAME OVER"
                  + "\nYour final score is: " + score
                  + "\nClick anywhere to play again."
                                        ,
                    canvas.getWidth()/2, canvas.getHeight()/2);
        ScoringLevel.recordScore(this);
        gc.setFont(temp);
        gc.setTextAlign(temp2);
        gc.setTextBaseline(temp3);
    }
    
    @Override
    public boolean isStandalone()
    {
        return true;
    }
    @Override
    public boolean hasNextLevel()
    {
        return true;
    }
    @Override
    public void continueCheck()
    {
        scene.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                playNextLevel.set(true);
                stage.close();
            }
        });
    }
    @Override
    public AtomicBoolean playNextLevel()
    {
        return playNextLevel;
    }
    @Override
    public SurvivalLevel loadNextLevel()
    {
        Group root = new Group();
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        return new SurvivalLevel(stage, canvas);
    }
    
    @Override
    public int getHighScoreGameNum()
    {
        return HIGH_SCORE_GAME_NUM;
    }
    @Override
    public int getLevelNum()
    {
        return LEVEL_NUM;
    }
    @Override
    public int getScore()
    {
        return score;
    }
    public Paddle getPaddle()
    {
        return p;
    }
    public ArrayList<SurvivalBall> getBalls()
    {
        return balls;
    }
    public int getDrops()
    {
        return drops;
    }
    public Stage getStage()
    {
        return stage;
    }
    public Canvas getCanvas()
    {
        return canvas;
    }
    public Scene getScene()
    {
        return scene;
    }
}