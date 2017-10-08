package arcade.handler;

import arcade.Arcade;
import arcade.sprite.Ball;
import arcade.sprite.SurvivalBall;
import arcade.level.SurvivalLevel;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Adds Balls to a Survival game.
 * Used to initialize a javafx.animation.KeyFrame object.
 * @author Kevin
 */
public class BallAdderLoop implements EventHandler<ActionEvent>
{
    private final SurvivalLevel lvl;
    private final Timeline ballAdder;
    private final Random r;
    
    public BallAdderLoop(SurvivalLevel lvl, Timeline ballAdder)
    {
        this.lvl = lvl;
        this.ballAdder = ballAdder;
        r = new Random();
    }
    
    @Override
    public void handle(ActionEvent event)
    {
        if (lvl.isOver())
            ballAdder.stop(); // stop running if game is over
        
        // add a Ball in a random place on the canvas with same speed/angles as original SurvivalBall
        lvl.getBalls().add(new SurvivalBall(lvl.getCanvas(), Ball.DEFAULT_RADIUS,
                                            r.nextDouble() * lvl.getCanvas().getWidth(),
                                            r.nextDouble() * lvl.getPaddle().getY(),
                                            Arcade.STAGE_HEIGHT/600.0, r.nextDouble() * (180 - 2*Ball.MIN_ANGLE) + (180 + Ball.MIN_ANGLE)));
    }
}