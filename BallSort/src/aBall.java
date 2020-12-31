import acm.graphics.GOval;
import java.awt.Color;

public class aBall extends Thread {
    /**
     * aBall class generates an instance of ball in motion and creates a thread
     */


    /* Convert simulation coordinates to screen coordinates*/
    public double SCALEX(double x)
    {
        return x*SCALE;
    }

    public double SCALEY(double y)
    {
        return HEIGHT - y*SCALE;
    }

    public double LtoScreen(double length)
    {
        return length*SCALE;
    }

    /* Make the resulting GOval accessible outside of aBall class*/
    public GOval getBall()
    {
        return myball;
    }


    public aBall(double Xi, double Yi, double Vo, double theta, double bSize, Color bColor, double bLoss)
    {
        this.Xi = Xi;			this.Yi = Yi; 			this.Vo = Vo;		this.theta = theta;
        this.bSize = bSize;		this.bColor = bColor;	this.bLoss = bLoss;

        myball = new GOval(SCALEX(Xi),SCALEY(Yi),LtoScreen(2*bSize),LtoScreen(2*bSize)); //create myball
        myball.setFilled(true);
        myball.setFillColor(bColor);
    }// close aBall()


    public void run() {
        /* Simulation Strategy:
         * 1) With given velocity and launch angle, calculate X,Y positions and update corresponding velocity
         * 2) Once a ball hit the ground, energy loss is applied
         * 3) Simulation keep runs until the ball runs out of steam
         * Most of the codes used are the same as the one in assignment 1,2
         */

        double Xo,Xlast,Y,Ylast,Elast; // initialize parameters

        double t = 0; 							// simulated time relative to the current starting point
        double Vt = m*g /(4*Math.PI*bSize*bSize*k);	// terminal velocity; the point at which the force due to air resistance balances gravity

        double Vox = Vo*Math.cos(theta*Math.PI/180);	// X component of initial velocity
        double Voy = Vo*Math.sin(theta*Math.PI/180);	// Y component of initial velocity

        double KEx=ETHR,KEy=ETHR; // Kinetic energy in X and Y directions
        Elast=0.5*Vo*Vo; 		  // total energy

        double signVox = 1; 	  //sign of Vox.
        if (Vox < 0) signVox = -1;

        Xo=Xi; 		// Initial X position
        Y=Yi; 		// Initial Y position
        Ylast=Y; 	// record Y position of the ball at the end of previous iteration
        Xlast=Xo;   // record X position of the ball at the end of previous iteration

        boolean totalEnergy = true; // the entire simulation stops when there is no enough total energy
        boolean collisionDetected = true; // each parabola stops when collision is detected

        while(totalEnergy) {
            double X, Vx, Vy;
            X = 0;	// initial x position relative to the current starting point
            while(collisionDetected) {
                /* Expressions for X and Y taking into account loss due to air resistance through the Vt term */
                X = Vox*Vt/g*(1-Math.exp(-g*t/Vt)); // displacement of X from its starting point of each loop
                Y = bSize + Vt/g*(Voy+Vt)*(1-Math.exp(-g*t/Vt))-Vt*t;// Y position

                Vx = (X-Xlast)/TICK;
                Vy = (Y-Ylast)/TICK;

                Xlast = X; // record the previous X position
                Ylast = Y; // record the previous Y position


                if ((Vy<0)&&(Y<=bSize)) { //collision detected
                    KEx = 0.5*Vx*Vx*(1-bLoss); // Kinetic energy in X direction after collision
                    KEy = 0.5*Vy*Vy*(1-bLoss); // Kinetic energy in Y direction after collision
                    break;
                }

                /* Update ball position on the screen */
                double ScrX = SCALEX(Xo+X-bSize);
                double ScrY = SCALEY(Y+bSize);
                myball.setLocation(ScrX,ScrY);

                try { //pause
                    Thread.sleep(50);	// the sleep duration for the aBall thread should be half as long as the time step.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t += TICK;

            } // close while(collisionDetected)

            Vox = Math.sqrt(2*KEx)*signVox; // Resulting horizontal velocity
            Voy = Math.sqrt(2*KEy); 		// Resulting vertical velocity


            if (KEx+KEy < ETHR || KEx+KEy >= Elast) // check if ther is enough energy for the simulation
            {
                ballRun = false;
                totalEnergy = false;	// terminate the simulation if there is not enough total energy
            }
            else
            {
                Elast=KEx+KEy; //update the value of total energy
            }

            t=0;     // Reset current time interval t
            Xo+=X;	 // record the total displacement of X
            /* reset the values of X and Y at the end of each trajectory*/
            X=0;	 // reset X since it only describes displacement relative to the current starting point
            Y=bSize; // reset Y: the lowest point on the trajectory =  radius of the ball

            Xlast=X;
            Ylast=Y;
        }
    }

    /* initialization of parameters */

    double Xi, Yi, Vo, theta, bSize, bLoss;
    Color bColor;
    GOval myball;

    /* Value of each parameters is given by Prof.Ferrie */
    private static final int HEIGHT = 600;
    private static final double SCALE = HEIGHT/100; // pixels per meter
    public boolean ballRun = true; //track the status of the ball
    private static final double m = 1.0; 		// mass of each ball
    private static final double k = 0.0001; 	// parameter to be used for terminal velocity
    private static final double g = 9.8;		// MKS gravitational constant: 9.8m/s^2
    private static final double TICK = 0.1;		// clock tick duration (sec): the delay between adjacent samples
    private static final double ETHR = 0.01;	// threshold of Kinetic energy
} // close class aBall