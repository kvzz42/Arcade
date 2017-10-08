package arcade.level;

import arcade.Arcade;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Defines how to create a Level with scoring.
 * @author Kevin
 */
public interface ScoringLevel extends Level
{
    // used to record high scores
    public static void recordScore(ScoringLevel lvl)
    {
        if (Arcade.SAVE_STATES)
            return;
        
        try
        {
            // gets necessary file for given game mode and makes it editable
            File f = Arcade.HIGH_SCORE_FILES[lvl.getHighScoreGameNum()];
            f.setWritable(true);
            
            // creates temporary file for reading from while writing to original
            File tempScores = File.createTempFile("tempScores", ".txt");
            PrintStream temp = new PrintStream(tempScores);
            Scanner in = new Scanner(f);
            while (in.hasNextLine())
                temp.println(in.nextLine());
            
            Scanner rd = new Scanner(tempScores);
            PrintStream scores = new PrintStream(f);
            boolean recordedScore = false; // tracks whether a score has been recorded
            String movedScore = null; // used to move down scores if a new score is recorded above them
                                      // temp value that theoretically should never be used
            
            // check every rank/position
            while (rd.hasNextLine())
            {
                scores.print(rd.next() + " "); // rank: e.g. "1. "
                int currScore = rd.nextInt(); // score comes directly after rank
                
                // record new high score
                if (lvl.getScore() > currScore && !recordedScore)
                {
                    scores.print(lvl.getScore());
                    if (lvl.isStandalone())
                        scores.println();
                    else // if game mode has multiple levels
                        scores.println(" : Level " + lvl.getLevelNum());
                    movedScore = currScore + rd.nextLine();
                    recordedScore = true;
                }
                
                // moved score needs to be printed
                else if (recordedScore)
                {
                    scores.println(movedScore);
                    movedScore = currScore + rd.nextLine();
                }
                
                // no high score recorded
                else
                    scores.println(currScore + rd.nextLine());
            }
            
            // delete temporary file and make original read only again
            tempScores.delete();
            f.setReadOnly();
        }
        catch (IOException e)
        { // stops code from crashing if error reading from or writing to a file
        }
    }
    
    // position in high score File[] in main class
    public int getHighScoreGameNum();
    // returns level number to be used if !isStandalone()
    public int getLevelNum();
    public int getScore();
    
    @Override
    public ScoringLevel loadNextLevel();
}