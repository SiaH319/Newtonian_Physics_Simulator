import acm.graphics.*;
import acm.program.*;
import com.sun.deploy.nativesandbox.NativeSandboxOutputStream;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This class shows the Newtonian Physics Simulation for a bouncing ball of a mass of 1kg.
 * Users can choose initial velocity, launch angle, energy loss, and the radius of the ball.
 * The change of the position and the velocity of the ball are displayed in the console.
 * */

public class SingleBounce extends GraphicsProgram {
	/* Parameters related to the display screen */
	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
	private static final int OFFSET = 200;

	/* Parameters related to the simulation (expressed in simulation coordinates) */
	private static final double mass = 1.0;
	private static final double k = 0.0016; 	// parameter to be used for terminal velocity
	private static final double g = 9.8;		// MKS gravitational constant: 9.8m/s^2

	private static final double TICK = 0.1;		// clock tick duration (sec): the delay between adjacent samples (given assumption in the assignment 1 instruction p.3)
	private static final double ETHR = 0.01;	// threshold of Kinetic energy related to the projectile motion simulated in the program: if the kinetic energy is less than ETHR, terminate the simulation
	private static final double XMAX = 100.0;
	private static final double YMAX = 100.0;
	private static final double PD = 1; 		// trace point diameter
	private static final double SCALEX = WIDTH/XMAX;  // pixel/meter:
	private static final double SCALEY = HEIGHT/YMAX;
	private Exception IOException;

	/* Trace program variables at each t step */

	public void run() {
		/* Read simulation parameters from user (console input) */
		double Vo = readDouble ("Enter the initial velocity of the ball in meters/second [0,100]: ");
		double theta = readDouble ("Enter the launch angle in degrees [0,90]: ");
		double loss = readDouble ("Enter energy loss parameter [0,1]: ");
		double bSize = readDouble ("Enter the radius of the ball in meters [0.1, 5.0]: ");

		/* Initialize variables */
		double t = 0; 		// simulated t relative to the current starting point
		double Vt = mass * g / (4 * Math.PI * bSize * bSize * k);	// terminal velocity; the point at which the force due to air resistance balances gravity
		double Vox = Vo*Math.cos(theta * Math.PI / 180);
		double Voy = Vo*Math.sin(theta * Math.PI / 180);

		int i = 1;				// trace the number of parabolic trajectory
		double Xinit = 5.0;		// initial ball location in meters (X axis)
		double Yinit = bSize; 	// initial ball location in meters (Y axis)
		// since the ball position is determined at the center, the lowest value of Y is the radius of the ball (bSize)


		/* Draw a ground plane on the screen and place ball at the initial position */
		this.resize (WIDTH, HEIGHT+OFFSET);			 // create a window of a specific size (X,Y)

		//create ground
		GRect ground = new GRect (0,HEIGHT,WIDTH,3); // create a rectangle for the ground place With a height of 3 pixels
		ground.setFilled(true);
		ground.setColor(Color.BLACK);
		add(ground);

		// screen coords
		double coordX = (Xinit-bSize)*SCALEX; // Convert simulation to screen coordinates
		double coordY = HEIGHT-(2*bSize)*SCALEY;

		//create a ball
		GOval ball = new GOval(coordX,coordY, SCALEX*bSize*2,SCALEY*bSize*2); // set the position and radius of ball in pixels
		ball.setFilled(true);
		ball.setColor(Color.RED);
		add(ball);							// create the ball
		pause(1000);						// pause for 1 second

		double Xo = Xinit;
		double Y = bSize;
		double Yprev = Y;
		double Xprev = Xo;
		double X,Vx,Vy;
		double KEx = ETHR, KEy = ETHR;

		/*simulation loop begins here */
		while (true) {
			//update position
			X = Vox * Vt / g * (1 - Math.exp(-g * t / Vt));
			Y = bSize + Vt / g * (Voy + Vt) * (1 - Math.exp(-g * t / Vt)) -Vt * t;

			//update velocity
			Vx = (X-Xprev) / TICK;
			Vy = (Y-Yprev) / TICK;

			System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy: %.2f\n",t,Xo+X,Y,Vx,Vy);
			// when the ball hits the ground, its losses its kinetice energy
			if ((Vy < 0) && (Y <= bSize)) {
				KEx = 0.5 * Vx * Vx * (1 - loss);
				KEy = 0.5 * Vy * Vy * (1 - loss);

				Vox = Math.sqrt(2 * KEx);
				Voy = Math.sqrt(2 * KEy);

				Y = bSize;
				Xo += X;
				X = 0;
				t = 0;
			}

			Xprev = X;
			Yprev = Y;

			println("KEx = "+KEx+" KEy = "+KEy);
			if (KEx < ETHR || KEy < ETHR) { //projectile motion stops since kinetic energy < threshold
				println("Projectile motion ends.");
				break;
			}

			coordX = (Xo + X - bSize) * SCALEX;
			coordY = HEIGHT - (Y + bSize) * SCALEY;
			ball.setLocation(coordX,coordY); //update display

			trace(Xo + X,Y); // plot trace point

			pause(TICK * 1000);
			t += TICK; // Update t
		}
	}

	// add trace point plot
	private void trace(double x, double y) {
		GOval tracePt = new GOval(x * SCALEX, HEIGHT - y * SCALEY, PD, PD);
		tracePt.setColor(Color.BLACK);
		tracePt.setFilled(true);
		add(tracePt);
	}
}