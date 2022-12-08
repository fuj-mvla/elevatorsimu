package building;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myfileio.MyFileIO;
import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc
/**
 * The Class Building.
 * @author tohar
 */
// TODO: Auto-generated Javadoc
public class Building {
	
	/**  Constants for direction. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	
	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;
	
	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;
	
	/**  File that will receive the information for data analysis. */
	private File passDataFile;

	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;
	
	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;
	
	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;
	
	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;	
	
	/** passQ holds the time-ordered queue of Passengers, initialized at the start 
	 *  of the simulation. At the end of the simulation, the queue will be empty.
	 */
	private GenericQueue<Passengers> passQ;

	/**  The size of the queue to store Passengers at the start of the simulation. */
	private final int PASSENGERS_QSIZE = 1000;	

	/**  The number of elevators - must be initialized in constructor. */
	private final int NUM_ELEVATORS;
	
	/** The floors. */
	public Floor[] floors;
	
	/** The elevators. */
	private Elevator[] elevators;
	
	/**  The Call Manager - it tracks calls for the elevator, analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;
	
	/** The offload delay. */
	// Add any fields that you think you might need here...
	private int offloadDelay;
	
	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param numElevators the num elevators
	 * @param logfile the logfile
	 */
	public Building(int numFloors, int numElevators, String logfile) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		passQ = new GenericQueue<Passengers>(PASSENGERS_QSIZE);
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		Passengers.resetStaticID();		
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));
		
		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i] = new Floor(FLOOR_QSIZE); 
		}
		callMgr = new CallManager(floors,NUM_FLOORS);
		elevators = new Elevator[NUM_ELEVATORS];
		//TODO: if you defined new fields, make sure to initialize them here
		offloadDelay = 0;
	}
	
	// TODO: Place all of your code HERE - state methods and helpers...
	
	/**
	 * Elevator state changed.
	 *
	 * @param lift the lift
	 * @return true, if successful
	 */
	public boolean elevatorStateChanged(Elevator lift) {
		if (lift.getPrevState() != lift.getCurrState()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Adds the passengers to queue.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param fromFloor the from floor
	 * @param toFloor the to floor
	 * @param polite the polite
	 * @param wait the wait
	 * @return true, if successful
	 */
	public boolean addPassengersToQueue(int time, int numPass, int fromFloor, int toFloor, boolean polite, int wait) {
		return passQ.add(new Passengers(time, numPass, fromFloor, toFloor, polite, wait)) ? true : false;
	}
	
	/**
	 * Config elevators.
	 *
	 * @param numFloors the num floors
	 * @param capacity the capacity
	 * @param floorTicks the floor ticks
	 * @param doorTicks the door ticks
	 * @param passPerTick the pass per tick
	 */
	public void configElevators(int numFloors, int capacity, int floorTicks, int doorTicks, int passPerTick) {
		for (int i = 0; i < NUM_ELEVATORS; i++) {
			elevators[i] = new Elevator(numFloors, capacity, floorTicks, doorTicks, passPerTick);
		}
	}
	
	
	/**
	 * Checks if passengers were processed.
	 *
	 * @return true, if successful
	 */
	public boolean passengersProcessed() {
		return passQ.isEmpty();
	}
	
	/**
	 * Check passenger arrival.
	 *
	 * @return true, if successful
	 */
	public boolean checkPassengerArrival(int time) {
		Passengers[] p = getPassengersInQueue();
		return time == p[p.length-1].getTimeArrived();
	}
	
	/**
	 * Stop state.
	 * If there are no calls in any direction - @STOP
	 * If there is a call down or up - @OPENDR
	 * If there are no calls on this floor, but there are calls on other floors - @MVTOFLOOR
	 *
	 * @param time thxe time
	 * @param lift the lift
	 * @return the int
	 */
	public int currStateStop(int time, Elevator lift) {
		lift.setTimeInState(lift.getTimeInState()+1);
		if (!callMgr.isUpCallPending() && !callMgr.isDownCallPending()) {
			return Elevator.STOP;
		}
		else {
			int floor = lift.getCurrFloor();
			Passengers p = callMgr.prioritizePassengerCalls(floor);
			if (p == null) {
				return Elevator.STOP;
			}
			logCalls(time, p.getNumPass(), lift.getCurrFloor(), lift.getDirection(), p.getId());
			if (!floors[floor].goingUpEmpty() || !floors[floor].goingDownEmpty()) {
				lift.setDirection(p.getDirection());
				return Elevator.OPENDR;
			}
			else {
				lift.setDirection(p.getOnFloor() > lift.getCurrFloor() ? UP : DOWN);
				lift.setMoveToFloor(p.getOnFloor());
				lift.setPostMoveToFloorDir(p.getDestFloor());
				return Elevator.MVTOFLR;
			}
		}
	}
	
	/**
	 * Close dr state.
	 * Closes the elevator doors, decrements door state variable
	 * Passengers arrive on current floor in same direction and are NOT polite - @OPENDR
	 * Doors are not closed yet - @CLOSEDR
	 * Elevator is empty and no calls in any direction - @STOP
	 * There are passengers in the elevator to get off on other floors in the current direction or there
	 * are calls on floors moving in the current direction waiting to be serviced. Elevator direction could change
	 * if no calls on floors moving in curr direction, but there are calls on floors moving in opp direction - @MV1FLR
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	protected int currStateCloseDr(int time, Elevator lift) {
		lift.setTimeInState(lift.getTimeInState()+1);
		lift.setDoorState(lift.getDoorState()-1);
		int direction = lift.getDirection();
		Passengers pInCurrDir = direction == UP ? floors[lift.getCurrFloor()].peekFromUp() 
				: floors[lift.getCurrFloor()].peekFromDown();
		if (pInCurrDir != null && !pInCurrDir.isPolite()) {
			return Elevator.OPENDR;
		}
		if (lift.getCurrState() == Elevator.CLOSEDR) {
			if (lift.getPassengers() == 0) {
				return elevatorEmpty(direction, lift);
			}
			else {
				return Elevator.MV1FLR;
			}
		}
		return Elevator.CLOSEDR;
	}
	
	
	/**
	 * Elevator empty.
	 * Helper method
	 * @param direction the direction
	 * @param lift the lift
	 * @return the int
	 */
	private int elevatorEmpty(int direction, Elevator lift) {
		if (!callMgr.isDownCallPending() && !callMgr.isUpCallPending()) {
			return Elevator.STOP;
		}
		if (direction == UP && !floors[lift.getCurrFloor()].goingUpEmpty()
				|| direction == DOWN && !floors[lift.getCurrFloor()].goingDownEmpty()) {
			return Elevator.OPENDR;
		}
		if (direction == UP && callMgr.isUpCallPending() 
				|| direction == DOWN && callMgr.isDownCallPending()) {
			return Elevator.MV1FLR;
		}
		else {
			lift.setDirection(direction == UP ? DOWN : UP);
			if (direction == UP && !floors[lift.getCurrFloor()].goingUpEmpty()
					|| direction == DOWN && !floors[lift.getCurrFloor()].goingDownEmpty()) {
				return Elevator.OPENDR;
			}
			else {
				return Elevator.MV1FLR;
			}
		}
	}
	
	/**
	 * Board state.
	 * Boards all waiting passengers in current direction
	 * Based upon number of boarders, wait time needs to continually
	 * be re-evaluated, since new boarders can arrive
	 * Floor queue needs to be examined for new arrivals every tick in this state
	 * There is capacity in the elevator and not enough time has passed
	 * to board all waiting passengers - @BOARD
	 * All passengers have boarded or no room for more - @CLOSEDR
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	protected int currStateBoard(int time, Elevator lift) {
		lift.setTimeInState(lift.getTimeInState()+1);
		Passengers p = passQ.peek();
		boolean atCapacity = false;
		if (time > p.getTimeWillGiveUp()) {
			logGiveUp(time, p.getNumPass(), lift.getCurrFloor(), lift.getDirection(), p.getId());
			passQ.remove();
		}
		if (lift.getCapacity() < (lift.getPassengers() + p.getNumPass())) {
			logSkip(time, p.getNumPass(), lift.getCurrFloor(), lift.getDirection(), p.getId());
			atCapacity = true;
			if (!p.isPolite()) {
				if (lift.getCapacity() >= (lift.getPassengers() + p.getNumPass())) {
					logBoard(time, p.getNumPass(), lift.getCurrFloor(), lift.getDirection(), p.getId());
					return Elevator.BOARD;
				}
				p.setPolite(true);
			}
		}
		if (atCapacity) {
			return Elevator.CLOSEDR;
		}
		return Elevator.BOARD;
	}
	
	/**
	 * Open dr state.
	 * Opens the door for off-loading or boarding
	 * Opens the door, increments door state variable
	 * If the doors are not fully open - @OPENDR
	 * If the doors are open and there are passengers waiting to get off - @OFFLD
	 * If the doors are open, no passengers getting off, and passengers want to get on in the
	 * current direction - @BOARD
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	protected int currStateOpenDr(int time, Elevator lift) {
		lift.setDoorState(Elevator.OPENDR);
		lift.setTimeInState(lift.getTimeInState()+1);
		if (lift.getTimeInState() != lift.getTicksDoorOpenClose()) {
			return Elevator.OPENDR;
		}
		else {
			List<Passengers>[] pToExit = lift.getPassByFloor();
			if (pToExit.length == 0) {
				return Elevator.OFFLD;
			}
			int floor = lift.getCurrFloor();
			int dir = lift.getDirection();
			if (pToExit.length == 0 && !floors[floor].goingDownEmpty() && dir == DOWN) {
				return Elevator.BOARD;
			}
			if (pToExit.length == 0 && !floors[floor].goingUpEmpty() && dir == UP) {
				return Elevator.BOARD;
			}
			return Elevator.CLOSEDR;
		}	
	}
	
	/**
	 * Mvto flr state.
	 * Move the elevator
	 * Once it reaches the target floor, change the elevator direction
	 * If the elevator has not reached the target floor - @MVTOFLR
	 * If the elevator has reached the target floor - @OPENDR
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	protected int currStateMvToFlr(int time, Elevator lift) {
		lift.setTimeInState(lift.getTimeInState()+1);
		int currFloor = lift.getCurrFloor();
		Passengers p = callMgr.prioritizePassengerCalls(currFloor);
		if (currFloor == p.getDestFloor()) {
			return Elevator.OPENDR;
		}
		else {
			lift.moveElevator();
			return Elevator.MVTOFLR;
		}
	}
	
	/**
	 * Off ld state.
	 * Models the time for passengers to leave the elevator
	 * Rate is specified by passPerTick
	 * Passengers leave the elevator, all passengers are assumed to leave in the first cycle in this state,
	 * May change directions in this state after passengers leave
	 * Not enought ime has passed to allow all passengers to exit - @OFFLD
	 * All passengers have exited, passengers want to board - @BOARD
	 * All passengers have exited, no passengers to board - @CLOSEDR
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 * @todo Keep working on this. Might not be fully correct. getTimeArrived() time to offload?
	 */
	protected int currStateOffLd(int time, Elevator lift) {
		int timeInState = lift.getTimeInState();
		lift.setTimeInState(timeInState+1);
		if (lift.getPrevState() != Elevator.OFFLD) {
			List<Passengers>[] passengers = lift.getPassByFloor();
			for (List<Passengers> p : passengers) {
				for (Passengers i : p) {
					offloadDelay += i.getTimeArrived();
					i.setTimeArrived(time);
				}
				passSuccess.addAll(p);
			}
		}
		if (timeInState == offloadDelay) {
			timeInStateEqualsOffldDelay(lift);
		}
		return Elevator.OFFLD;
	}
	
	/**
	 * Time in state equals offld delay.
	 * Helper method
	 * @param lift the lift
	 * @return the int
	 */
	private int timeInStateEqualsOffldDelay(Elevator lift) {
		if (lift.getDirection() == DOWN && callMgr.isDownCallPending()) {
			return Elevator.BOARD;
		}
		if (lift.getDirection() == UP && callMgr.isUpCallPending()) {
			return Elevator.BOARD;
		}
		if (lift.getPassengers() == 0) {
			if (lift.getDirection() == UP && !callMgr.isUpCallPending()) {
				if (callMgr.isDownCallPending()) {
					lift.setDirection(DOWN);
					return Elevator.BOARD;
				}
			}
			if (lift.getDirection() == DOWN && !callMgr.isDownCallPending()) {
				if (callMgr.isUpCallPending()) {
					lift.setDirection(UP);
					return Elevator.BOARD;
				}
			}
		}
		return Elevator.CLOSEDR;
	}
	
	/**
	 * Mv 1 flr state.
	 * Move the elevator to the next floor in the current direction
	 * In between floors or no passengers to exit or board at the new floor - @MV1FLR
	 * Reached the new floor and there are either passengers to exit or passengers
	 * to board in the same direction. Also possible that direction could change - @OPENDR
	 *
	 * @param time the time
	 * @param lift the lift
	 * @return the int
	 */
	protected int currStateMv1Flr(int time, Elevator lift) {
		lift.setTimeInState(lift.getTimeInState()+1);
		lift.moveElevator();
		Passengers p = lift.getDirection() == UP ? callMgr.getLowestUpCall() : callMgr.getHighestDownCall();
		if (lift.getCurrFloor() == p.getDestFloor()) {
			logArrival(time, p.getNumPass(), lift.getCurrFloor(), p.getId());
		}
		if (lift.getPrevFloor() != lift.getCurrFloor()) {
			if (!(lift.getPassByFloor().length == 0)) {
				return Elevator.OPENDR;
			}
			if (lift.getDirection() == DOWN && 
					!floors[lift.getCurrFloor()].goingDownEmpty()) {
				return Elevator.OPENDR;
			}
			if (lift.getDirection() == UP && 
					!floors[lift.getCurrFloor()].goingUpEmpty()) {
				return Elevator.OPENDR;
			}
			if (lift.getPassengers() == 0) {
				return elevatorEmpty(lift);
			}
		}
		return Elevator.MV1FLR;
	}
	
	/**
	 * Elevator empty.
	 * Helper method
	 * @param lift the lift
	 * @return the int
	 */
	private int elevatorEmpty(Elevator lift) {
		if (lift.getDirection() == UP && !callMgr.isUpCallPending()) {
			if (!floors[lift.getCurrFloor()].goingDownEmpty()) {
				lift.setDirection(DOWN);
				return Elevator.OPENDR;
			}
		}
		if (lift.getDirection() == DOWN && !callMgr.isDownCallPending()) {
			if (!floors[lift.getCurrFloor()].goingUpEmpty()) {
				lift.setDirection(UP);
				return Elevator.OPENDR;
			}
		}
		return Elevator.MV1FLR;
	}
	
	
	/**
	 * Gets the passengers in passQ.
	 * Necessary for controller to give to the GUI
	 * @return the passengers
	 */
	public Passengers[] getPassengersInQueue() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		List<Passengers> p = new ArrayList<>();
		if (passengers != null) {
			while (passengers.hasNext()) {
				p.add(passengers.next());
			}
		}
		return p.toArray(new Passengers[p.size()]);
	}
	
	/**
	 * Gets the current state.
	 * This is a hacky solution. If more elevators were added, this would
	 * not work.
	 * @return the current state
	 */
	public int getCurrentState() {
		return elevators[0].getCurrState();
	}
	
	/**
	 * Gets the current floor.
	 * This is a hacky solution. If more elevators were added, this would
	 * not work.
	 * @return the current floor
	 */
	public int getCurrentFloor() {
		return elevators[0].getCurrFloor();
	}
	
	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	// YOU WILL NEED TO CODE ANY MISSING METHODS IN THE APPROPRIATE CLASSES...
	public void updateElevator(int time) {
		for (Elevator lift: elevators) {
			if (elevatorStateChanged(lift))
				logElevatorStateChanged(time,lift.getPrevState(),lift.getCurrState(),lift.getPrevFloor(),lift.getCurrFloor());

			switch (lift.getCurrState()) {
				case Elevator.STOP: lift.updateCurrState(currStateStop(time,lift)); break;
				case Elevator.MVTOFLR: lift.updateCurrState(currStateMvToFlr(time,lift)); break;
				case Elevator.OPENDR: lift.updateCurrState(currStateOpenDr(time,lift)); break;
				case Elevator.OFFLD: lift.updateCurrState(currStateOffLd(time,lift)); break;
				case Elevator.BOARD: lift.updateCurrState(currStateBoard(time,lift)); break;
				case Elevator.CLOSEDR: lift.updateCurrState(currStateCloseDr(time,lift)); break;
				case Elevator.MV1FLR: lift.updateCurrState(currStateMv1Flr(time,lift)); break;
			}
		}
	}

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
		for (Elevator el:elevators)
			logElevatorConfig(el.getCapacity(),el.getTicksPerFloor(), el.getTicksDoorOpenClose(), el.getPassPerTick(), el.getCurrState(), el.getCurrFloor());
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Dump passQ contents. Debug hook to view the contents of the passenger queue...
	 */
	public void dumpPassQ() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		if (passengers != null) {
			System.out.println("Passengers Queue:");
			while (passengers.hasNext()) {
				Passengers p = passengers.next();
				System.out.println(p);
			}
		}
	}

	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		+"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		+" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}
