/*
 ============================================================================
 Description : Translate the code written in Java to "C"
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

/* Parameters related to the simulation (expressed in simulation coordinates)*/

#define m 1.0 			// mass of the ball in kg
#define k 0.0016 		// parameter to be used for calculating the terminal velocity
#define g 9.8 			// MKS gravitational constant in meter/second^2
#define Pi 3.141592654  // value of pi needed to convert degrees to radians
#define TICK 0.1 		// clock tick duration in second: the delay between adjacent samples
#define ETHR 0.01		// threshold of Kinetic energy related to the projectile motion simulated in the program: if the kinetic energy is less than ETHR, terminate the simulation

#define false 0 		// define the value of false
#define true !false
#define TEST true

int main(void) {
	double Vo, theta, loss, bSize; // declare variables before use

	/* Read simulation parameters from user */
	printf("Enter the initial velocity of the ball in meters/second [0,100]: ");
	scanf("%lf",&Vo);
	printf("Enter the launch angle in degrees [0,90]: ");
	scanf("%lf",&theta);
	printf("Enter energy loss parameter [0,1]: ");
	scanf("%lf",&loss);
	printf("Enter the radius of the ball in meters [0.1,5.0]: ");
	scanf("%lf",&bSize);

	/* Initialize variables */
	double t = 0; 							// simulated time relative to the current starting point
	double Vt = m*g /(4*Pi*bSize*bSize*k);	// terminal velocity; the point at which the force due to air resistance balances gravity
	double Vox = Vo*cos(theta*Pi/180);		// X component of initial velocity
	double Voy = Vo*sin(theta*Pi/180);		// Y component of initial velocity
	int i = 1;								// trace the number of parabolic trajectory
	double Xint = 5.0;						// initial ball location in meters (X axis) (assumption given in the assignment #1 instruction)
	double Yint = bSize; 					// initial ball location in meters (Y axis)
											// since the ball position is determined at the center, the lowest value of Y is the radius of the ball (bSize)
	double tprev = t; 						// previous value of t
	double time = tprev;					// total simulated time

	/* Simulation loop */
	while (true)
	{
		/* Initialize variables */
		double X = 0;
		double Y = 0;
		double Xlast = X;					// previous value of X (one step right before)
		double Xo = Xint + Xlast;			// sum of the displacements at the conclusion of each parabola
		double Ylast = Yint;				// previous value of Y
		double Vx = Vox;
		double Vy = Voy;

		printf("\nFollowings are for the parabola #%d: \n",i);

		while (true)
		{
			/* Expressions for X and Y taking into account loss due to air resistance through the Vt term */
			X = Vox*Vt/g*(1-exp(-g*t/Vt)); 					// X position; variable X only describes displacement relative to the current starting point
			Y = bSize+ Vt/g*(Voy+Vt)*(1-exp(-g*t/Vt))-Vt*t; // Y position
			Vx = (X-Xlast)/TICK;
			Vy = (Y-Ylast)/TICK;
			if (Vy < 0 && Y<=bSize) // collision detected
			{break;}

			/* Trace program variables at each t step */
			if (TEST) // when TEST is true, program will output a table of values so that the program can be validated
				printf("t_total:%.2f t:%.2f X:%.2f Y:%.2f Vx:%.2f Vy:%.2f\n",t+time, t, X+Xo, Y, Vx, Vy);

			t += TICK;
			tprev = t; //record the last value of t of a single trajectory

			/* Record the values of X and Y after change for the next parabolic trajectory */
			Xlast = X ;
			Ylast = Y ;
		}

		Xo += Xlast;
		time += tprev;

		/* After each collision */
		X= 0; 		// reset X since it only describes displacement relative to the current starting point
		Y = bSize;  // the lowest point on the trajectory =  radius of the ball
		i+=1;		// the number of parabolic trajectory increase by 1
		t = 0;
		Xint = Xo;
		Yint = Y;

		double KEx = 0.5*m*Vx*Vx*(1-loss); // Kinetic energy in X direction after collision
		double KEy = 0.5*m*Vy*Vy*(1-loss); // Kinetic energy in Y direction after collision
		Vox = sqrt(2*KEx);	// resulting horizontal velocity after collision
		Voy = sqrt(2*KEy);  // resulting vertical velocity after collision
		Vx = Vox;
		Vy = Voy;

		printf("KEx = %.2f, KEy = %.2f \n ",KEx, KEy);

		if (KEx < ETHR || KEy < ETHR)
		{ //terminate the simulation if kinetic energy < threshold
			printf("\n=> Projectile motion ends.");
			break;}
	} // close while
} // close main
