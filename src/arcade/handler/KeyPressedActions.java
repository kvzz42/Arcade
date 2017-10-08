package arcade.handler;

import arcade.Arcade;
import arcade.level.Level;
import arcade.level.breakout.BreakoutLevel;
import arcade.sprite.Block;
import arcade.sprite.Paddle;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Contains all actions for key presses, such as Paddle moving and saving/loading states.
 * @author Kevin
 */
public class KeyPressedActions implements EventHandler<KeyEvent>
{
    private final Paddle p;
    private BreakoutLevel bLvl;
    private double savedTimeElapsed;
    private int savedScore;
    private int savedDrops;
    private ArrayList<Block> savedBlocks;
    private double savedBallX;
    private double savedBallY;
    private double savedBallSpeed;
    private double savedBallAngle;

    public KeyPressedActions(Paddle p, Level lvl)
    {
        this.p = p;
        if (lvl instanceof BreakoutLevel)
            bLvl = (BreakoutLevel) lvl;
    }

    @Override
    public void handle(KeyEvent event)
    {
        switch (event.getCode())
        {
            case LEFT: case A:  p.setMoveLeft(true); break;
            case RIGHT: case D: p.setMoveRight(true); break;
            case Q: if (Arcade.SAVE_STATES)
            {
                savedTimeElapsed = GameLoop.timeElapsed;
                savedScore = bLvl.getScore();
                savedDrops = bLvl.getDrops();
                savedBlocks = new ArrayList<>();
                for (Block bl : bLvl.getBlocks())
                    savedBlocks.add(bl);
                savedBallX = bLvl.getBall().getX();
                savedBallY = bLvl.getBall().getY();
                savedBallSpeed = bLvl.getBall().getSpeed();
                savedBallAngle = bLvl.getBall().getAngle();
            }
            break;
            case E: if (Arcade.SAVE_STATES)
                bLvl.loadState(savedTimeElapsed, savedScore, savedDrops, savedBlocks,
                               savedBallX, savedBallY, savedBallSpeed, savedBallAngle);
            break;
        }
    }
}