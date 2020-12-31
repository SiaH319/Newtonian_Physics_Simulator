

import acm.graphics.GOval;
import java.awt.Color;

public class aBall extends Thread {
    /**
     * aBall class generates an instance of ball in motion according to assignment 1
     * The constructor specifies the parameters for simulation:
     * @param Xi double: The initial X position of the center of the ball (in meters)
     * @param Yi double: The initial Y position of the center of the ball (in meters)
     * @param Vo double: The initial velocity of the ball at launch (in meters/second)
     * @param theta double: Launch angle (with the horizontal plane) (in degrees)
     * @param bSize double: The radius of the ball in simulation units (in meters)
     * @param bColor Color: The initial color of the ball
     * @param bLoss double: Energy loss coefficient (fraction [0,1] of the energy lost on each bounce)
     */

    /* initialization of parameters */
    double Xi, Yi, Vo, theta, bSize, bLoss;
    Color bColor;
    GOval myball;

    /* Value of each parameters is given by Prof.Ferrie */
    private static final int HEIGHT = 600;
    private static final double SCALE = HEIGHT/100; // pixels per meter

    private static final double m = 1.0; 		// mass of each ball
    private static final double k = 0.0001; 	// parameter to be used for terminal velocity
    private static final double g = 9.8;		// MKS gravitational constant: 9.8m/s^2
    private static final double Pi = 3.141592654;	// value of pi needed to convert degrees to radians
    private static final double TICK = 0.1;		// clock tick duration (sec): the delay between adjacent samples
    private static final double ETHR = 0.01;	// threshold of Kinetic energy
    private static final double YMAX = HEIGHT/SCALE;

    /* Convert simulation to screen coordinates*/
    public double SCALEX(double x) {
        return (int)(SCALE*(x-bSize*2));
    }

    public double SCALEY(double y) {
        return (int)((HEIGHT-SCALE*(y+bSize)));
    }

    public aBall(double Xi, double Yi, double Vo, double theta,double bSize, Color bColor, double bLoss)
    {
        this.Xi = Xi;			this.Yi = Yi; 			this.Vo = Vo;		this.theta = theta;
        this.bSize = bSize;		this.bColor = bColor;	this.bLoss = bLoss;

        myball = new GOval(SCALEX(Xi),SCALEY(Yi),bSize*2*SCALE,bSize*2*SCALE);
        myball.setFilled(true);
        myball.setFillColor(bColor);
    }// close aBall()

    public GOval getBall()  // make the resulting GOval accessable outside of aBall
    {
        return myball;
    } // close getBall()


    public void run()
    {
        /* Simulation Strategy:
         * 1) Determine the initial Y position of a ball
         * 2-1) If a ball drops from the top of the screen, it undergoes "horizontal projection"
         * 2-2) If a ball is launched from the ground, it undergoes "angular projection" which is very similar to assignment 1
         * 3) once a ball hit the ground, energy loss is applied and undergoes angular projection until the ball runs out of steam.
         * Most of the codes used are the same as the one in assignment 1
         */

        double t = 0; 							// simulated time relative to the current starting point
        double Vt = m*g /(4*Pi*bSize*bSize*k);	// terminal velocity; the point at which the force due to air resistance balances gravity
        double Vox = Vo*Math.cos(theta*Pi/180);	// X component of initial velocity
        double Voy = Vo*Math.sin(theta*Pi/180);	// Y component of initial velocity

        /* Simulation loop */
        while (true)
        {
            /* Initialize variables */
            double X = 0;			// displacement of X from its starting point of each loop
            double Y = Yi;			// Y position
            double Xlast = X;		// value of X at the end of each loop
            double Ylast = Yi;		// value of Y at the end of each loop
            double Xo = Xi + Xlast;	// sum of the displacements at the conclusion of each parabola
            double Vx = Vox;
            double Vy = Voy;

            if (Yi == YMAX-bSize) { // if a ball is falling from the top of the screen => horizontal projection
                while (true) {
                    Y = Yi-0.5*g*t*t; //Y component is only under the influence of gravity
                    X = Vox*Vt/g*(1-Math.exp(-g*t/Vt));
                    Vx = (X-Xlast)/TICK;
                    Vy = (Y-Ylast)/TICK;

                    myball.setLocation(SCALEX(Xi+X-bSize*2),Math.min(SCALEY(Y+bSize), HEIGHT)); // Math.min function guarantees that the current value of height never exceeds the ground plane
                    try { // pause
                        Thread.sleep(50);	// the sleep duration for the aBall thread should be half as long as the time step.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    t+=TICK;

                    if (Y <= bSize) { //if a collision is detected, now ball undergoes angular projection
                        Yi = 0; // reset Yi
                        Xi += X;
                        t = 0; // reset t = 0
                        break;
                    }
                }

            }


            else{ // if a ball is lunched on the ground =>  angular projection
                while (true)
                {
                    /* Expressions for X and Y taking into account loss due to air resistance through the Vt term */
                    X = Vox*Vt/g*(1-Math.exp(-g*t/Vt)); // X position; variable X only describes displacement relative to the current starting point
                    Y = bSize+ Vt/g*(Voy+Vt)*(1-Math.exp(-g*t/Vt))-Vt*t; // Y position

                    Vx = (X-Xlast)/TICK;
                    Vy = (Y-Ylast)/TICK;

                    if (Vy < 0 && Y<=bSize) // collision detected
                    {
                        break;
                    }


                    myball.setLocation (SCALEX(Xo+X-bSize*2), Math.min(SCALEY(Y), HEIGHT)); // Math.min function guarantees that the current value of height never exceeds the ground plane
                    try { //pause
                        Thread.sleep(50);	// the sleep duration for the aBall thread should be half as long as the time step.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    t += TICK;


                    /* Record the values of X and Y at the end of each trajectory*/
                    Xlast = X ;
                    Ylast = Y ;

                } // close while loop for angular projection

                Xo += Xlast; // record the total displacement of X

                /* After each collision */
                X= 0; // reset X since it only describes displacement relative to the current starting point
                Y = bSize; // the lowest point on the trajectory =  radius of the ball

                t = 0;
                Xi = Xo;
                Yi = Y;

                double KEx = 0.5*Vx*Vx*(1-bLoss); // Kinetic energy in X direction after collision
                double KEy = 0.5*Vy*Vy*(1-bLoss); // Kinetic energy in Y direction after collision
                Vox = Math.sqrt(2*KEx); // Resulting horizontal velocity
                Voy = Math.sqrt(2*KEy); // Resulting vertical velocity
                if (KEx < ETHR | KEy<ETHR)
                { //projectile motion stops since kinetic energy < threshold
                    break;
                }

            }	// close else -> angular projection
        }	// close while loop for the entire simulation
    }	// close run()
}	// close class aBall