package arcade.sprite;

import arcade.Arcade;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Paddles are used to hit Ball objects and can be moved with the mouse or keyboard,
 * as defined by the PaddleAimer, PaddleMover, and PaddleStopper objects.
 * @author Kevin
 */
public class Paddle
{
    // all fields
    private final Canvas canvas;
    private final GraphicsContext gc;
    private Color color;
    private double x;
    private final double y;
    private final double width;
    private final double thickness;
    private double speed;
    private boolean moveLeft;
    private boolean moveRight;
    
    // class constants
    public static double DEFAULT_WIDTH = Arcade.STAGE_HEIGHT/3.0;
    public static double DEFAULT_THICKNESS = Arcade.STAGE_HEIGHT/90.0;
    public static double DEFAULT_SPEED = Arcade.STAGE_HEIGHT/90.0;
    public static final Color DEFAULT_COLOR = Color.STEELBLUE;
    
    public Paddle(Canvas canvas)
    {
        this(canvas, DEFAULT_WIDTH, DEFAULT_THICKNESS, DEFAULT_SPEED);
    }
    public Paddle(Canvas canvas, double width, double thickness, double speed)
    {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        this.x = (canvas.getWidth() - width) / 2; // center of canvas
        this.y = 11*canvas.getHeight()/12; // default position, defined by canvas size
        this.width = width;
        this.thickness = thickness;
        this.speed = speed;
        color = DEFAULT_COLOR;
    }
    
    public void draw()
    {
        Color temp = (Color) gc.getFill();
        gc.setFill(color);
        gc.fillRect(x, y, width, thickness);
        gc.setFill(temp);
    }
    
    public void updatePos()
    {
        if (moveLeft)
            x -= speed;
        if (moveRight)
            x += speed;
        if (x < 0)
            x = 0;
        if (x > canvas.getWidth() - width)
            x = canvas.getWidth() - width;
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
    public double getX()
    {
        return x;
    }
    public void setX(double x)
    {
        this.x = x;
        if (x < 0)
            this.x = 0;
        if (x > canvas.getWidth() - width)
            this.x = canvas.getWidth() - width;
    }
    public double getY()
    {
        return y;
    }
    public double getWidth()
    {
        return width;
    }
    public double getThickness()
    {
        return thickness;
    }
    public double getSpeed()
    {
        return speed;
    }
    public void setSpeed(double speed)
    {
        this.speed = speed;
    }
    public boolean getMoveLeft()
    {
        return moveLeft;
    }
    public void setMoveLeft(boolean moveLeft)
    {
        this.moveLeft = moveLeft;
    }
    public boolean getMoveRight()
    {
        return moveRight;
    }
    public void setMoveRight(boolean moveRight)
    {
        this.moveRight = moveRight;
    }
}