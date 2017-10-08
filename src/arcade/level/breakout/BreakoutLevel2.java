package arcade.level.breakout;

import arcade.Arcade;
import arcade.handler.GameLoop;
import arcade.sprite.Ball;
import arcade.sprite.Block;
import arcade.sprite.Paddle;
import arcade.handler.PaddleAimer;
import arcade.handler.KeyPressedActions;
import arcade.handler.KeyReleasedActions;
import arcade.level.ScoringLevel;
import java.util.ArrayList;
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
 * Level 2 of the Breakout game mode.
 * @author Kevin
 */
public class BreakoutLevel2 implements BreakoutLevel
{
    private static final int LEVEL_NUM = 2;
    private final Stage stage;
    private final Scene scene;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle p;
    private Ball b;
    private ArrayList<Block> blocks;
    private final int totalBlocks;
    private final int initialScore;
    private final int initialLives;
    private final AtomicBoolean playNextLevel;
    private int blocksBroken;
    private int score;
    private int drops;
    private int lives;
    
    public BreakoutLevel2(Stage stage, Canvas canvas, int score, int lives)
    {
        stage.setTitle(GAME_MODE + " Level " + LEVEL_NUM);
        this.stage = stage;
        scene = stage.getScene();
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        p = new Paddle(canvas);
        scene.setOnKeyPressed(new KeyPressedActions(p, this));
        scene.setOnKeyReleased(new KeyReleasedActions(p));
        scene.setCursor(Cursor.CROSSHAIR);
        scene.setOnMouseMoved(new PaddleAimer(p));
        b = new Ball(canvas, Ball.DEFAULT_RADIUS, canvas.getWidth()/4, 7*canvas.getHeight()/9, Arcade.STAGE_HEIGHT/180.0, 300);
        blocks = new ArrayList<>();
        initializeBlocks();
        totalBlocks = blocks.size();
        blocksBroken = 0;
        playNextLevel = new AtomicBoolean();
        this.score = score;
        initialScore = score;
        if (lives < 0)
            lives = 0;
        this.lives = lives;
        initialLives = lives;
        drops = 0;
    }
    private void initializeBlocks()
    {
        double blockWidth = (canvas.getWidth() - Block.DEFAULT_GAP*12)/11;
        double blockHeight = canvas.getHeight()/9 - 2*Block.DEFAULT_GAP;
        for (int i = 0; i < 3; ++i) // first three bottom blocks
            blocks.add(new Block(canvas, (canvas.getWidth()-blockWidth)/2,
                                 7*canvas.getHeight()/9 - blockHeight - i*(blockHeight + Block.DEFAULT_GAP),
                                 blockWidth, blockHeight));
        for (int i = 0; i < 3; ++i) // row of three blocks in center
            blocks.add(new Block(canvas, (canvas.getWidth()-3*blockWidth)/2
                                          - Block.DEFAULT_GAP + i*(blockWidth + Block.DEFAULT_GAP),
                                 7*canvas.getHeight()/9 - 4*blockHeight - 3*Block.DEFAULT_GAP,
                                 blockWidth, blockHeight));
        for (int i = 0; i < 5; ++i) // row of five blocks in center
            blocks.add(new Block(canvas, (canvas.getWidth()-5*blockWidth)/2
                                          - 2*Block.DEFAULT_GAP + i*(blockWidth + Block.DEFAULT_GAP),
                                 7*canvas.getHeight()/9 - 5*blockHeight - 4*Block.DEFAULT_GAP,
                                 blockWidth, blockHeight));
        for (int i = 0; i < 11; ++i) // full row across screen
            blocks.add(new Block(canvas, Block.DEFAULT_GAP + i*(blockWidth + Block.DEFAULT_GAP),
                                 7*canvas.getHeight()/9 - 6*blockHeight - 5*Block.DEFAULT_GAP,
                                 blockWidth, blockHeight));
        for (int i = 0; i < 11; ++i)
            if (i != 4 && i != 5 && i != 6) // skip middle three
                blocks.add(new Block(canvas, Block.DEFAULT_GAP + i*(blockWidth + Block.DEFAULT_GAP),
                                     7*canvas.getHeight()/9 - 7*blockHeight - 6*Block.DEFAULT_GAP,
                                     blockWidth, blockHeight));
    }
    
    @Override
    public void draw(double timeElapsed)
    {
        gc.setFill(Arcade.BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        p.draw();
        b.draw();
        for (Block bl : blocks)
            bl.draw();
        gc.setFill(Arcade.TEXT_COLOR);
        gc.fillText("Lives: " + lives
                  + "\nBlocks Broken: " + blocksBroken + "/" + totalBlocks
                  + "\nSeconds Elapsed: " + timeElapsed
                  + "\nScore: " + score, 0, 10);
    }
    
    @Override
    public void update(double timeElapsed)
    {
        if (b.hitsBlock(blocks))
            ++blocksBroken;
        if (!(b.hitsPaddle(p) || b.hitsWall()))
            if (b.updatePos())
                ++drops;
        p.updatePos();
        score = initialScore + 100*blocksBroken - 20*(int)timeElapsed - 1000*drops;
        lives = initialLives - drops;
        if (lives < 0)
            lives = 0;
    }
    
    @Override
    public boolean isOver()
    {
        return blocks.isEmpty() || drops > initialLives;
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
        if (blocks.isEmpty())
            gc.fillText("Complete!"
                      + "\nYour total score is: " + score
                      + "\nClick anywhere to play the next level!",
                        canvas.getWidth()/2, canvas.getHeight()/2);
        else
        {
            gc.fillText("GAME OVER"
                      + "\nYour final score is: " + score
                      + "\nClick anywhere to end the game.",
                        canvas.getWidth()/2, canvas.getHeight()/2);
            ScoringLevel.recordScore(this);
        }
        gc.setFont(temp);
        gc.setTextAlign(temp2);
        gc.setTextBaseline(temp3);
    }
    
    @Override
    public boolean isStandalone()
    {
        return false;
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
                if (blocks.isEmpty())
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
    public BreakoutLevel loadNextLevel()
    {
        Group root = new Group();
        root.getChildren().add(canvas);
        stage.setScene(new Scene(root));
        return new BreakoutLevel3(stage, canvas, score, lives + (drops == 0 ? 1 : 0));
    }
    @Override
    public void loadState(double timeElapsed, int score, int drops, ArrayList<Block> blocks,
                          double ballX, double ballY, double ballSpeed, double ballAngle)
    {
        GameLoop.timeElapsed = timeElapsed;
        GameLoop.timeStart = System.currentTimeMillis() - timeElapsed*1000;
        this.score = score;
        this.drops = drops;
        this.blocks = new ArrayList<>();
        for (Block bl : blocks)
            this.blocks.add(bl);
        blocksBroken = totalBlocks - blocks.size();
        b.setX(ballX);
        b.setY(ballY);
        b.setSpeed(ballSpeed);
        b.setAngle(ballAngle);
    }
    
    @Override
    public int getHighScoreGameNum()
    {
        return BreakoutLevel.HIGH_SCORE_GAME_NUM;
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
    @Override
    public Stage getStage()
    {
        return stage;
    }
    @Override
    public Canvas getCanvas()
    {
        return canvas;
    }
    @Override
    public Scene getScene()
    {
        return scene;
    }
    @Override
    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }
    @Override
    public Paddle getPaddle()
    {
        return p;
    }
    @Override
    public Ball getBall()
    {
        return b;
    }
    @Override
    public int getTotalBlocks()
    {
        return totalBlocks;
    }
    @Override
    public int getBlocksBroken()
    {
        return blocksBroken;
    }
    @Override
    public int getDrops()
    {
        return drops;
    }
    @Override
    public int getLives()
    {
        return lives;
    }
}