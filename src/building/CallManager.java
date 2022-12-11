package building;

// TODO: Auto-generated Javadoc
/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 * @author tohar
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/**  The up call pending - true if any up calls exist. */
	private boolean upCallPending;
	
	/**  The down call pending - true if any down calls exit. */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
		
		//TODO: Initialize any added fields here
	}
	
	/**
	 * Update call status. This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 */
	void updateCallStatus() {
		//TODO: Write this method if you choose to implement it...
	}

	/**
	 * Prioritize passenger calls from STOP STATE.
	 *
	 * @param lift the lift
	 * @param floor the floor
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(Elevator lift, int floor) {
		//TODO: Write this method based upon prioritization from STOP...
		// compare numCalls up and down
		if (!floors[floor].goingUpEmpty() && !floors[floor].goingDownEmpty()) {
			if (numUpCallsPending(floor) >= numDownCallsPending(floor)) {
				lift.setDirection(UP);
				lift.setCurrState(Elevator.OPENDR);
			}
			else {
				lift.setDirection(DOWN);
				lift.setCurrState(Elevator.OPENDR);
			}
			return null;
			// return prioritizePassengerCalls(lift, floor);
		}
		else {
			lift.setCurrState(Elevator.MVTOFLR);
			if (numUpCallsPending() > numDownCallsPending()) {
				return getLowestUpCall();
			}
			if (numDownCallsPending() > numUpCallsPending()) {
				return getHighestDownCall();
			}
			int distanceToLowestUp = Math.abs(floor - getLowestUpCallFloor());
			int distanceToHighestDown = Math.abs(floor - getHighestDownCallFloor());
			return distanceToHighestDown < distanceToLowestUp ? getHighestDownCall() : getLowestUpCall();
		}
	}

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
	/**
	 * Num up calls pending.
	 *
	 * @return the int
	 */
	int numUpCallsPending() {
		int numCalls = 0;
		for (int i = 0; i < upCalls.length; i++) {
			if (upCalls[i] == true) {
				numCalls++;
			}
		}
		return numCalls;
	}
	
	/**
	 * Num up calls pending.
	 *
	 * @param floor the floor
	 * @return the int
	 */
	int numUpCallsPending(int floor) {
		int numCalls = 0;
		for (int i = floor; i < upCalls.length; i++) {
			if (upCalls[i] == true) {
				numCalls++;
			}
		}
		return numCalls;
	}
	
	/**
	 * Num down calls pending.
	 *
	 * @return the int
	 */
	int numDownCallsPending() {
		int numCalls = 0;
		for (int i = 0; i < downCalls.length; i++) {
			if (downCalls[i] == true) {
				numCalls++;
			}
		}
		return numCalls;
	}
	
	/**
	 * Num down calls pending.
	 *
	 * @param floor the floor
	 * @return the int
	 */
	int numDownCallsPending(int floor) {
		int numCalls = 0;
		for (int i = 0; i < floor; i++) {
			if (downCalls[i] == true) {
				numCalls++;
			}
		}
		return numCalls;
	}
	/**
	 * Gets the lowest up call.
	 *
	 * @return the lowest up call
	 */
	Passengers getLowestUpCall() {
		int floor = 0;
		for (int i = 0; i < upCalls.length; i++) {
			if (upCalls[i]) {
				floor = i;
				break;
			}
		}
		return floors[floor].peekFromUp();
	}
	
	/**
	 * Gets the highest down call.
	 *
	 * @return the highest down call
	 */
	Passengers getHighestDownCall() {
		int saveHighest = 0;
		for (int i = 0; i < downCalls.length; i++) {
			if (downCalls[i]) {
				saveHighest = i;
			}
		}
		return floors[saveHighest].peekFromDown();
	}
	
	/**
	 * Gets the lowest up call floor.
	 *
	 * @return the lowest up call floor
	 */
	int getLowestUpCallFloor() {
		int floor = 0;
		for (int i = 0; i < upCalls.length; i++) {
			if (upCalls[i]) {
				floor = i;
				break;
			}
		}
		return floor;
	}
	
	/**
	 * Gets the highest down call floor.
	 *
	 * @return the highest down call floor
	 */
	int getHighestDownCallFloor() {
		int saveHighest = 0;
		for (int i = 0; i < downCalls.length; i++) {
			if (downCalls[i]) {
				saveHighest = i;
			}
		}
		return saveHighest;
	}
	
	/**
	 * Checks if is up call pending.
	 *
	 * @return true, if is up call pending
	 */
	public boolean isUpCallPending() {
		int numCalls = 0;
		for (int i = 0; i < upCalls.length; i++) {
			if (upCalls[i] == true) {
				numCalls++;
			}
		}
		upCallPending = numCalls > 0 ? true : false;
		return upCallPending;
	}
	
	/**
	 * Checks if is down call pending.
	 *
	 * @return true, if is down call pending
	 */
	public boolean isDownCallPending() {
		int numCalls = 0;
		for (int i = 0; i < downCalls.length; i++) {
			if (downCalls[i] == true) {
				numCalls++;
			}
		}
		downCallPending = numCalls > 0 ? true : false;
		return downCallPending;
	}

}
