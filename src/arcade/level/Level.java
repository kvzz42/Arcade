package arcade.level;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Defines how to create a game level.
 * @author Kevin
 */
public interface Level
{    
    // all methods needed to be a Level
    public void draw(double timeElapsed);
    public void update(double timeElapsed);
    public boolean isOver();
    public void printEndText();
    public boolean isStandalone();
    public boolean hasNextLevel();
    public void continueCheck();
    public AtomicBoolean playNextLevel(); // to pass the boolean by reference
    public Level loadNextLevel();
}