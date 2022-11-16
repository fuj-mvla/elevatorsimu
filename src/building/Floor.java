package building;
// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator.
 * @author tohar
 */
public class Floor {
	/**  Constant for representing direction. */
	private static final int UP = 1;
	
	/** The Constant DOWN. */
	private static final int DOWN = -1;

	/**  The queues to represent Passengers going UP or DOWN. */	
	private GenericQueue<Passengers> down;
	
	/** The up. */
	private GenericQueue<Passengers> up;

	/**
	 * Instantiates a new floor.
	 *
	 * @param qSize the q size
	 */
	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	// TODO: Write the helper methods needed for this class. 
	// You probably will only be accessing one queue at any
	// given time based upon direction - you could choose to 
	
	/**
	 * Going up empty.
	 *
	 * @return true, if successful
	 */
	// account for this in your methods.
	public boolean goingUpEmpty() {
		return up.isEmpty();
	}
	
	/**
	 * Adds the to up.
	 *
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean addToUp(Passengers p) {
		return up.add(p);
	}
	
	/**
	 * Peek from up.
	 *
	 * @return the passengers
	 */
	public Passengers peekFromUp() {
		return up.peek();
	}
	
	/**
	 * Poll from up.
	 *
	 * @return the passengers
	 */
	public Passengers pollFromUp() {
		return up.poll();
	}
	
	/**
	 * Going down empty.
	 *
	 * @return true, if successful
	 */
	public boolean goingDownEmpty() {
		return down.isEmpty();
	}
	
	/**
	 * Adds the to down.
	 *
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean addToDown(Passengers p) {
		return down.add(p);
	}
	
	/**
	 * Peek from down.
	 *
	 * @return the passengers
	 */
	public Passengers peekFromDown() {
		return down.peek();
	}
	
	/**
	 * Poll from down.
	 *
	 * @return the passengers
	 */
	public Passengers pollFromDown() {
		return down.poll();
	}
	
	/**
	 * Gets the up.
	 *
	 * @return the up
	 */
	public static int getUp() {
		return UP;
	}

	/**
	 * Gets the down.
	 *
	 * @return the down
	 */
	public static int getDown() {
		return DOWN;
	}

	/**
	 * Sets the down.
	 *
	 * @param down the new down
	 */
	public void setDown(GenericQueue<Passengers> down) {
		this.down = down;
	}

	/**
	 * Sets the up.
	 *
	 * @param up the new up
	 */
	public void setUp(GenericQueue<Passengers> up) {
		this.up = up;
	}

	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 */
	String queueString(int dir) {
		String str = "";
		ListIterator<Passengers> list;
		list = (dir == UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				// Example: str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
	
}
