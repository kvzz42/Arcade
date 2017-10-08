package arcade.sprite;

import arcade.Arcade;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Blocks are placed within Breakout levels.
 * They can be breakable or unbreakable depending on level design.
 * Collision detection with a Ball and removing Blocks is handled in the Ball and BreakoutLevel classes.
 * @author Kevin
 */
public class Block
{
    // all fields
    private final Canvas canvas;
    private final GraphicsContext gc;
    private double x;
    private double y; // x and y left non-final to allow for possible implementation of moving blocks
    private final double width;
    private final double thickness;
    private Color color;
    private Color unbreakableColor;
    private final boolean breakable;
    
    // class constants
    public static double DEFAULT_GAP = Arcade.STAGE_HEIGHT/90.0;
    public static final Color DEFAULT_COLOR = Color.STEELBLUE;
    public static final Color DEFAULT_UNBREAKABLE_COLOR = Color.GREY;
    
    public Block(Canvas canvas, double x, double y, double width, double thickness)
    {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        this.x = x;
        this.y = y;
        this.width = width;
        this.thickness = thickness;
        color = DEFAULT_COLOR;
        unbreakableColor = DEFAULT_UNBREAKABLE_COLOR;
        breakable = true;
    }
    public Block(Canvas canvas, double x, double y, double width, double thickness, boolean breakable)
    {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        this.x = x;
        this.y = y;
        this.width = width;
        this.thickness = thickness;
        color = DEFAULT_COLOR;
        unbreakableColor = DEFAULT_UNBREAKABLE_COLOR;
        this.breakable = breakable;
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
    public void setUnbreakableColor(Color color)
    {
        unbreakableColor = color;
    }
    public double getX()
    {
        return x;
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
    public boolean isBreakable()
    {
        return breakable;
    }
    
    public void draw()
    {
        Color temp = (Color) gc.getFill();
        gc.setFill(breakable ? color : unbreakableColor);
        gc.fillRect(x, y, width, thickness);
        gc.setFill(temp);
    }
}