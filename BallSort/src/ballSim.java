
import acm.graphics.GRect;
import acm.graphics.GLabel;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;
import java.awt.Color;
/***This class generates 100 balls with random size, color, launch angle, and initial velocity
 * Once all balls stop moving due to the lack of kinetic energy, when the use click the screen,
 * the balls are sorted in the order of its size.
 * */
public class ballSim extends GraphicsProgram
{
    /* Parameters related to the display screen (in screen coordinates)*/
    private static final int WIDTH = 1200;
    public static final int HEIGHT = 600;
    private static final int OFFSET = 200;

    /* Parameters related to the simulation (in simulation coordinates) */
    private static final int NUMBALLS = 60;			// # balls to simulate
    private static final double MINSIZE = 1.0;		// minimum ball radius (in meters)
    private static final double MAXSIZE = 7.0;		// maximum ball radius (in meters)
    private static final double EMIN = 0.2; 		// minimum loss coefficient
    private static final double EMAX = 0.6; 		// maximum loss coefficient
    private static final double VoMIN = 40.0; 		// minimum velocity (in meters/second)
    private static final double VoMAX = 50.0; 		// maximum velocity (in meters/second)
    private static final double ThetaMIN = 80.0; 	// minimum launch angle (in degrees)
    private static final double ThetaMAX = 100.0; 	// maximum launch angle (in degrees)

    public static final double SCALE = HEIGHT/100; // pixels per meter

    public void init()
    {
        addMouseListeners();
    }

    public void run()
    {
        // Set up display, create and start multiple instances of aBall
        this.resize (WIDTH, HEIGHT+OFFSET);			 // create a window of a specific size (X,Y)

        GRect ground = new GRect (0,HEIGHT,WIDTH,3); // create a rectangle for the ground place with a height of 3 pixels
        ground.setFilled(true);
        ground.setColor(Color.BLACK);
        add(ground);								// create the ground

        RandomGenerator rgen = RandomGenerator.getInstance(); // generates random numbers
        ballTree myTree = new ballTree();
        rgen.setSeed((long)424242);

        for (int i=0; i<NUMBALLS; i++)
        {
            // randomly generates the parameters for separate balls
            double iSize = rgen.nextDouble(MINSIZE,MAXSIZE);
            double iXi = WIDTH/(2*SCALE); 		//initial position of the ball in x: center of screen
            double iYi = iSize;					//initial position of the ball in y: current ball radius
            Color iColor = rgen.nextColor();
            double iLoss = rgen.nextDouble(EMIN,EMAX);
            double iVel = rgen.nextDouble(VoMIN,VoMAX);
            double iTheta = rgen.nextDouble(ThetaMIN,ThetaMAX);


            aBall newBall = new aBall(iXi, iYi, iVel,iTheta, iSize, iColor, iLoss);
            add(newBall.myball);
            myTree.addNode(newBall);
            newBall.start();

        }	// close for loop


        while(myTree.isRunning()); // wait until termination
        GLabel display = new GLabel ("Click mouse to continue",1050, 580); // prompt user for the further process
        display.setColor(Color.red);
        add(display);
        waitForClick();	// wait for users to click
        remove(display);

        myTree.moveTo(); // sort and stack balls in order
        GLabel displayaf = new GLabel ("All stacked!",1050, 580);
        displayaf.setColor(Color.red);
        add(displayaf);

    } // close run()
} // close class bSim