package arcade.handler;

import arcade.level.Level;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Defines what is done every frame of the game loop.
 * Used to initialize a javafx.animation.KeyFrame object.
 * @author Kevin
 */
public class GameLoop implements EventHandler<ActionEvent>
{
    public static double timeStart;
    private final Timeline timeline;
    private final Level lvl;
    public static double timeElapsed;
    private static AtomicBoolean continueGame;
    
    public GameLoop(Level lvl, double timeStart, Timeline timeline)
    {
        GameLoop.timeStart = timeStart;
        this.lvl = lvl;
        this.timeline = timeline;
        timeline.setOnFinished(new EventHandler<ActionEvent>() // if timeline ends on its own, i.e. Chaos game mode
        {
            @Override
            public void handle(ActionEvent event)
            {
                lvl.draw(timeElapsed);
                lvl.printEndText();
                if (lvl.hasNextLevel())
                {
                    lvl.continueCheck();
                    continueGame = lvl.playNextLevel();
                }
            }
        });
        timeElapsed = 0.0;
        continueGame = new AtomicBoolean(); // defaults to false
    }
    
    @Override
    public void handle(ActionEvent event)
    {
        // draw and update Level every frame
        timeElapsed = (System.currentTimeMillis() - timeStart) / 1000;
        lvl.draw(timeElapsed);
        lvl.update(timeElapsed);
        
        // check if Level is over after updating
        if (lvl.isOver())
        {
            timeline.stop(); // stop running
            lvl.draw(timeElapsed); // draw ending frame after final update
            lvl.printEndText();
            lvl.continueCheck(); // check if player wants to keep playing
            if (lvl.hasNextLevel())
                continueGame = lvl.playNextLevel(); // binds this variable to variable in Level by reference (AtomicBoolean)
                                                    // because they share references, the check can be performed inside the Level,
                                                    // and therefore only one assignment is necessary to ensure check is done correctly
        }
    }
    
    // called after game window is closed due to showAndWait() in main class
    public static boolean continueGame()
    {
        return continueGame.get();
    }
}