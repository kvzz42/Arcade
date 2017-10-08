package arcade.level;

import arcade.Arcade;
import arcade.sprite.Ball;
import arcade.sprite.Paddle;
import arcade.handler.PaddleAimer;
import arcade.handler.KeyPressedActions;
import arcade.handler.KeyReleasedActions;
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
 * Creates a Level of the Chaos game mode.
 * @author Kevin
 */
public class ChaosLevel implements Level
{
    public static final String GAME_MODE = "Chaos";
    
    private final Stage stage;
    private final Scene scene;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle p;
    private final Ball[] balls;
    private final AtomicBoolean playNextLevel;
    private int drops;
    
    public ChaosLevel(Stage stage, Canvas canvas)
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
        Random r = new Random();
        balls = new Ball[r.nextInt(100) + 1];
        for (int i = 0; i < balls.length; ++i)
            balls[i] = new Ball(canvas, Ball.DEFAULT_RADIUS,
                           r.nextDouble() * canvas.getWidth(),
                           r.nextDouble() * p.getY(),
                           Arcade.STAGE_HEIGHT/180.0, r.nextDouble() * 150 + 195);
        playNextLevel = new AtomicBoolean();
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
        gc.fillText("Total Balls: " + balls.length
                  + "\nTotal Drops: " + drops
                  + "\nSeconds left: " + (30 - (int)timeElapsed), 0, 10);
    }
    
    @Override
    public void update(double timeElapsed)
    {
        for (Ball b : balls)
            if (!(b.hitsPaddle(p) || b.hitsWall()))
                if (b.updatePos())
                    ++drops;
        p.updatePos();
    }
    
    @Override
    public boolean isOver()
    {
        return false; // ends when timeline ends
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
        gc.fillText("Finish!"
                  + "\nYou ended with " + drops + " drops."
                  + "\nClick anywhere to play again."
                                        ,
                    canvas.getWidth()/2, canvas.getHeight()/2);
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
    public ChaosLevel loadNextLevel()
    {
        Group root = new Group();
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        return new ChaosLevel(stage, canvas);
    }
    
    public Paddle getPaddle()
    {
        return p;
    }
    public Ball[] getBalls()
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