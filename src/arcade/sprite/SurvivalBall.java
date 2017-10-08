package arcade.sprite;

import javafx.scene.canvas.Canvas;

/**
 * The variant of Ball used in the Survival game mode.
 * @author Kevin
 */
public class SurvivalBall extends Ball
{
    public SurvivalBall(Canvas canvas, double radius, double x, double y, double speed, double angle)
    {
        super(canvas, radius, x, y, speed, angle);
    }
    
    @Override
    // does not try to respawn Ball if dropped, which super class does
    public boolean updatePos()
    {
        setX(getX() + getDeltaX());
        setY(getY() + getDeltaY());
        return getY() > getCanvas().getHeight();
    }
    
    @Override
    // does not change speed of Ball, which super class does
    public void reflectPaddle(double distFromLeft)
    {
        setAngle(distFromLeft*(180 - 2*MIN_ANGLE) + (180 + MIN_ANGLE));
    }
}