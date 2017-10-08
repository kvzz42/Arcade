package arcade.handler;

import arcade.sprite.Paddle;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Binds middle of Paddle to the mouse's X position.
 * @author Kevin
 */
public class PaddleAimer implements EventHandler<MouseEvent>
{
    private final Paddle p;
    
    public PaddleAimer(Paddle p)
    {
        this.p = p;
    }
    
    @Override
    public void handle(MouseEvent event)
    {
        p.setX(event.getX() - p.getWidth() / 2);
    }
}