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
 * Level 4 of the Breakout game mode.
 * @author Kevin
 */
public class BreakoutLevel4 implements BreakoutLevel
{
    private static final int LEVEL_NUM = 4;
    private final Stage stage;
    private final Scene scene;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Paddle p;
    private Ball b;
    private ArrayList<Block> blocks;
    private final int totalBlocks;
    private final int breakableBlocks;
    private final int initialScore;
    private final int initialLives;
    private final AtomicBoolean playNextLevel;
    private int blocksBroken;
    private int score;
    private int drops;
    private int lives;
    
    public BreakoutLevel4(Stage stage, Canvas canvas, int score, int lives)
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
        b = new Ball(canvas, Ball.DEFAULT_RADIUS, canvas.getWidth()/4, 7*canvas.getHeight()/9, Arcade.STAGE_HEIGHT/450.0, 300);
        blocks = new ArrayList<>();
        initializeBlocks();
        totalBlocks = blocks.size();
        int counter = 0;
        for (Block bl : blocks)
            if (bl.isBreakable())
                ++counter;
        breakableBlocks = counter;
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
        double columnWidth = canvas.getWidth()*11/36;
        double wallWidth = canvas.getWidth()/24;
        double wallGap = Arcade.VERT_TEXT_SPACE*Arcade.STAGE_HEIGHT/900.0;
        double wallHeight = p.getY() - Block.DEFAULT_GAP - wallGap;
        
        // "walls"
        blocks.add(new Block(canvas, columnWidth, wallGap,
                             wallWidth, wallHeight, false));
        blocks.add(new Block(canvas, 2*columnWidth + wallWidth, wallGap,
                             wallWidth, wallHeight, false));
        
        double blockWidth = (columnWidth - 5*Block.DEFAULT_GAP)/4; // 4 blocks wide
        double blockHeight = (wallHeight - 10*Block.DEFAULT_GAP)/10; // 5 rows tall (one empty by Paddle)
        
        // left side blocks
        for (int i = 0; i < 4; ++i) // rows
            for (int j = 0; j < 3; ++j) // columns
            {
                blocks.add(new Block(canvas, Block.DEFAULT_GAP + (columnWidth+wallWidth)*j,
                                     p.getY() - 2*(Block.DEFAULT_GAP + blockHeight)*(i+1),
                                     blockWidth, blockHeight));
                blocks.add(new Block(canvas, 2*Block.DEFAULT_GAP + blockWidth + (columnWidth+wallWidth)*j,
                                     p.getY() - 2*(Block.DEFAULT_GAP + blockHeight)*(i+1),
                                     blockWidth, blockHeight));
            }
        
        // right side blocks
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 3; ++j)
            {
                blocks.add(new Block(canvas, 3*Block.DEFAULT_GAP + 2*blockWidth + (columnWidth+wallWidth)*j,
                                     p.getY() - (Block.DEFAULT_GAP + blockHeight)*(3+2*i),
                                     blockWidth, blockHeight));
                blocks.add(new Block(canvas, 4*Block.DEFAULT_GAP + 3*blockWidth + (columnWidth+wallWidth)*j,
                                     p.getY() - (Block.DEFAULT_GAP + blockHeight)*(3+2*i),
                                     blockWidth, blockHeight));
            }
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
                  + "\nBlocks Broken: " + blocksBroken + "/" + breakableBlocks
                  + "\nSeconds Elapsed: " + timeElapsed
                  + "\nScore: " + score, 0, 10);
    }
    
    @Override
    public void update(double timeElapsed)
    {
        b.hitsBlock(blocks);
        blocksBroken = totalBlocks - blocks.size();
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
        return blocksBroken == breakableBlocks || drops > initialLives;
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
        if (blocksBroken == breakableBlocks)
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
                if (blocksBroken == breakableBlocks)
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
        return new BreakoutLevel5(stage, canvas, score, lives + (drops == 0 ? 1 : 0));
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