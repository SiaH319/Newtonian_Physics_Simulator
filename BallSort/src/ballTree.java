/** This class sort the sie of the ball */
public class ballTree {
    private static double DELTASIZE = 0.1;	// value for sorting the size of the balls
    bNode root=null;			// initial root
    boolean BallRunning = true; // check if the entire ball is stop moving

    public void addNode(aBall newBall) {
        bNode current;
        if (root == null) 		// check if it is empty
        {
            root = makeNode(newBall);
        } // close if()

        else { // if not empty, descend to the leaf node according to the input newBall
            current = root;
            while (true)
            {
                if (newBall.bSize < current.newBall.bSize) {
                    // if the radius of new newBall < existing newBall at node, branch left
                    if (current.left == null)
                    {
                        current.left = makeNode(newBall);	// attach new node
                        break;
                    }
                    else
                    { // otherwise keep traversing
                        current = current.left;
                    }
                }

                else {
                    // if the radius of new newBall >= existing newBall at node, branch left
                    if (current.right == null) {			// leaf node
                        current.right = makeNode(newBall);	// attach new node
                        break;
                    }
                    else {									// otherwise
                        current = current.right;			// keep traversing
                    }
                } //close else
            } // close while
        } //close else
    } // close void addNode()


    bNode makeNode(aBall newBall) {
        bNode node = new bNode();							// create new object
        node.newBall = newBall;								// initialize newBall field
        node.left = null;									// set both successors
        node.right = null;
        return node;										// return to new object
    }



    /* Scan and check the status of each ball */
    private void ballRunning(bNode root)
    { // check the status of a single ball
        if (root.newBall.ballRun == true) BallRunning = true;
        if (root.left != null) ballRunning(root.left);   // if not empty, check the status
        if (root.right != null) ballRunning(root.right); // if not empty, check the status
    }
    public boolean isRunning()
    {
        //returns true if simulation is still running
        BallRunning = false;// check if the entire ball is still running or not
        ballRunning(root);
        return BallRunning; //returns false when there is at least a single ball moving
    }

    double xpos = 0;
    double ypos = 0;
    double lastSize = 0;   //track the previous size of the ball (radius)
    double currentSize = 0;//track the current size of the ball (radius)

    /*in-order traversal to move a ball to its sort order position*/
    public void stackBalls(bNode root)
    {
        if (root.left!= null)
        {
            stackBalls(root.left);
        }
        currentSize = root.newBall.bSize;	// track the current radius of a ball

        {
            if (currentSize-lastSize > DELTASIZE) // if the difference of the radius between current and previous ball is bigger than DELTASIZE
            {
                xpos += currentSize; // stack moves to the right
                ypos = 0; // reset the y position of stack
            }

            else { // if there is a very small difference of the radius
                ypos += currentSize; // stack on top of the previous ball
            }
        }

        lastSize = currentSize; // track the previous size of a ball
        double ScrX = xpos*2*ballSim.SCALE; // scale X position
        double ScrY = ballSim.HEIGHT - (ypos+currentSize)*ballSim.SCALE*2; // scale Y position
        root.newBall.myball.setLocation(ScrX, ScrY); // display

        if (root.right!= null) {
            stackBalls(root.right);
        }
    }

    /* Method to move a ball to in order as a stack*/
    public void moveTo()
    {
        stackBalls(root);
    }

} // close class bTree

class bNode // node class for binary tree
{
    aBall newBall;
    bNode left;
    bNode right;
}


