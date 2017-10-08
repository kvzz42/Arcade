package arcade.sprite;

import arcade.Arcade;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The Ball object is used to play each game mode.
 * It can be drawn, update its position, and hit surfaces
 * such as walls, Paddles, and Blocks, reacting accordingly.
 * @author Kevin
 */
public class Ball
{
    // variable fields
    private Color color;
    private double radius;
    private double x;
    private double y;
    private double speed;
    private double angle;
    private double deltaX;
    private double deltaY;
    
    // final fields
    private final Canvas canvas;
    private final GraphicsContext gc;
    
    // used to respawn Ball when dropped
    private final double initialX;
    private final double initialY;
    private final double initialSpeed;
    private final double initialAngle;
    
    // class "constants," some of which are based on window size
    public static double MAX_SPEED = Arcade.STAGE_HEIGHT/60.0;
    public static final double MIN_ANGLE = 15.0;
    public static double DEFAULT_SPEED_INC = Arcade.STAGE_HEIGHT/1800.0;
    public static double DEFAULT_RADIUS = Arcade.STAGE_HEIGHT/180.0;
    public static final Color DEFAULT_COLOR = Color.ORANGE;
    
    public Ball(Canvas canvas, double radius, double x, double y, double speed, double angle)
    {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;
        initialX = x;
        initialY = y;
        initialSpeed = speed;
        initialAngle = angle;
        setDeltaXY();
        color = DEFAULT_COLOR;
    }
    
    public void draw()
    {
        if (y > Arcade.VERT_TEXT_SPACE || x > Arcade.HORIZ_TEXT_SPACE) // space allocated to Level text
        {
            Color temp = (Color) gc.getFill();
            gc.setFill(color);
            gc.fillOval(x, y, 2*radius, 2*radius);
            gc.setFill(temp);
        }
    }
    
    public boolean updatePos() // returns whether the ball was dropped
    {
        x += deltaX;
        y += deltaY;
        if (y > canvas.getHeight())
        {
            x = initialX;
            y = initialY;
            speed = initialSpeed;
            setAngle(initialAngle);
            return true;
        }
        return false;
    }
    
    // returns if a wall was hit
    public boolean hitsWall()
    {
        // flag keeps track of whether a wall was hit, just in case Ball hits two walls at the same time
        boolean flag = false;
        
        // distRatio allows for predicting collision in advance and manually adjusting position accordingly
        double horizDistRatio = (deltaX > 0 ? (canvas.getWidth() - (x + 2*radius)) : -x) / deltaX;
        if (horizDistRatio <= 1)
        {
            x += horizDistRatio*deltaX;
            y += horizDistRatio*deltaY;
            reflectHorizontal();
            x += (1-horizDistRatio)*deltaX;
            y += (1-horizDistRatio)*deltaY;
            flag = true;
        }
        // no vertical distance ratio because it sometimes causes the Ball to phase through the ceiling
        if (y + deltaY <= 0)
        {
            double vertDistRatio = -y / deltaY;
            x += vertDistRatio*deltaX;
            y += vertDistRatio*deltaY;
            reflectVertical();
            x += (1-vertDistRatio)*deltaX;
            y += (1-vertDistRatio)*deltaY;
            flag = true;
        }
        return flag;
    }
    // returns whether the Paddle was hit
    public boolean hitsPaddle(Paddle p)
    {
        double distRatio = (p.getY() - (y + 2*radius)) / deltaY;
        if (distRatio <= 1 && distRatio >= 0 &&
            x + distRatio*deltaX <= p.getX() + p.getWidth() &&
            x + distRatio*deltaX + 2*radius >= p.getX()) // for when ball comes straight at paddle
        {
            x += distRatio*deltaX;
            y += distRatio*deltaY;
            reflectPaddle((x + 2*radius - p.getX()) / (p.getWidth() + 2*radius)); // percent distance from left
            x += (1-distRatio)*deltaX;
            y += (1-distRatio)*deltaY;
            return true;
        }
        if (x <= p.getX() + p.getWidth() && x + 2*radius >= p.getX() &&
            y <= p.getY() + p.getThickness() && y + 2*radius >= p.getY()) // for when ball phases into paddle
        {
            reflectPaddle((x + 2*radius - p.getX()) / (p.getWidth() + 2*radius));
            x += deltaX;
            y += deltaY;
            return true;
        }
        return false;
    }
    // returns whether a Block was hit
    // does not use distance ratios because of magnetism glitches (phasing through blocks)
    public boolean hitsBlock(ArrayList<Block> blocks)
    {
        for (Block bl : blocks)
        {
            if (x <= bl.getX() + bl.getWidth() && x + 2*radius >= bl.getX() &&
            y + deltaY <= bl.getY() + bl.getThickness() && y + 2*radius + deltaY >= bl.getY()) // hits bottom or top
            {
                reflectVertical();
                if (bl.isBreakable())
                    blocks.remove(bl);
                return true;
            }
            if (y <= bl.getY() + bl.getThickness() && y + 2*radius >= bl.getY() &&
                x + deltaX <= bl.getX() + bl.getWidth() && x + 2*radius + deltaX >= bl.getX()) // hits either side
            {
                reflectHorizontal();
                if (bl.isBreakable())
                    blocks.remove(bl);
                return true;
            }
        }
        return false;
    }
    
    public void reflectHorizontal()
    {
        setAngle(180 - angle);
    }
    public void reflectVertical()
    {
        setAngle(-angle);
    }
    public void reflectPaddle(double distFromLeft)
    {
        // speed up Ball, keep Ball below max speed
        speed += DEFAULT_SPEED_INC;
        if (speed > MAX_SPEED)
            speed = MAX_SPEED;
        
        // change angle based on where the Ball hit the Paddle
        setAngle(distFromLeft*(180 - 2*MIN_ANGLE) + (180 + MIN_ANGLE));
    }
    
    public Canvas getCanvas()
    {
        return canvas;
    }
    public Color getColor()
    {
        return color;
    }
    public void setColor(Color color)
    {
        this.color = color;
    }
    public double getRadius()
    {
        return radius;
    }
    public void setRadius(double radius)
    {
        this.radius = radius;
    }
    public double getX()
    {
        return x;
    }
    public void setX(double x)
    {
        this.x = x;
    }
    public double getY()
    {
        return y;
    }
    public void setY(double y)
    {
        this.y = y;
    }
    public double getSpeed()
    {
        return speed;
    }
    public void setSpeed(double speed)
    {
        this.speed = speed;
        setDeltaXY();
    }
    public double getAngle()
    {
        return angle;
    }
    public void setAngle(double angle)
    {
        this.angle = angle;
        setDeltaXY();
    }
    public double getDeltaX()
    {
        return deltaX;
    }
    public double getDeltaY()
    {
        return deltaY;
    }
    private void setDeltaXY()
    {
        deltaX = (Math.cos(Math.toRadians(angle))) * speed;
        deltaY = (Math.sin(Math.toRadians(angle))) * speed;
    }
}