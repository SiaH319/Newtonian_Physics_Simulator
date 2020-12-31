import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

import java.awt.Color;

/***
 * This class creates multiple balls with a random size, energy loss, initial velocity, and launch angle.
 */

public class bSim extends GraphicsProgram {
    /* Parameters related to the display screen (in screen coordinates)*/
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 600;
    private static final int OFFSET = 200;

    /* Parameters related to the simulation (in simulation coordinates) */
    private static final double MINSIZE = 1.0;        // minimum ball radius (in meters)
    private static final double MAXSIZE = 10.0;        // maximum ball radius (in meters)
    private static final double EMIN = 0.1;        // minimum loss coefficient
    private static final double EMAX = 0.6;        // maximum loss coefficient
    private static final double VoMIN = 40.0;        // minimum velocity (in meters/second)
    private static final double VoMAX = 50.0;        // maximum velocity (in meters/second)
    private static final double ThetaMIN = 80.0;    // minimum launch angle (in degrees)
    private static final double ThetaMAX = 100.0;    // maximum launch angle (in degrees)

    private static final double SCALE = HEIGHT / 100; // pixels per meter
    private static final double XMAX = WIDTH / SCALE; // minimum X position = width in meters
    private static final double YMAX = HEIGHT / SCALE;// minimum Y position = height in meters

    private RandomGenerator rgen = RandomGenerator.getInstance(); // generates random numbers


    public void run() {
        // Set up display, create and start multiple instances of aBall
        int NUMBALLS = readInt("Enter the number of the balls: ");

        // rgen.setSeed((long)0.12345);
        this.resize(WIDTH, HEIGHT + OFFSET);             // create a window of a specific size (X,Y)

        GRect ground = new GRect(0, HEIGHT, WIDTH, 3); // create a rectangle for the ground place with a height of 3 pixels
        ground.setFilled(true);
        ground.setColor(Color.BLACK);
        add(ground);                                // create the ground

        for (int i = 0; i < NUMBALLS; i++) {
            // randomly generates the parameters for separate balls
            double iSize = rgen.nextDouble(MINSIZE, MAXSIZE);
            Color iColor = rgen.nextColor();
            double iLoss = rgen.nextDouble(EMIN, EMAX);
            double iVel = rgen.nextDouble(VoMIN, VoMAX);
            double iTheta = rgen.nextDouble(ThetaMIN, ThetaMAX);

            double iXi = XMAX / 2;    // all balls are launched from the same x coordinate (center of the field)
            double iYi = YMAX - iSize;// balls are dropping from the top of the screen

            aBall newBall = new aBall(iXi, iYi, iVel, iTheta, iSize, iColor, iLoss);
            add(newBall.getBall());
            newBall.start();        // the simulation embedded within the aBall instance runs in parallel with the main program
            // and all other instances of the aBall class.
        }    // close for loop
    } // close run()
} // close class bSim