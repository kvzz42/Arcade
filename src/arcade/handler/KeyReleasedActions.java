package arcade.handler;

import arcade.sprite.Paddle;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Contains all actions for key releases, such as Paddle stopping.
 * @author Kevin
 */
public class KeyReleasedActions implements EventHandler<KeyEvent>
{
    private final Paddle p;
    
    public KeyReleasedActions(Paddle p)
    {
        this.p = p;
    }
    
    @Override
    public void handle(KeyEvent event)
    {
        switch (event.getCode())
        {
            case LEFT: case A:  p.setMoveLeft(false); break;
            case RIGHT: case D: p.setMoveRight(false); break;
        }
    }
}