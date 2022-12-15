
import java.util.ArrayList;

import building.Elevator;
import building.Passengers;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimulation.
 */
public class ElevatorSimulation extends Application {

	/** Instantiate the GUI fields. */
	private ElevatorSimController controller;

	/** The num floors. */
	private final int NUM_FLOORS;

	/** The num elevators. */
	private final int NUM_ELEVATORS;

	/** The curr floor. */
	private int currFloor;

	/** The passengers. */
	private int passengers;

	/** The time. */
	private int time;

	/** The t. */
	private Timeline t;

	/** The elevator. */
	private Rectangle elevator;

	/** The bp. */
	private BorderPane bp;

	/** The gp. */
	private GridPane gp;

	/** The logging. */
	private Button logging = new Button("Log");

	/** The Step. */
	private Button Step = new Button("Step");

	/** The run. */
	private Button run = new Button("Run");

	/** The enter. */
	private Button enter = new Button("Enter");

	/** The duration. */
	private int duration = 1000;

	/** The cycle count. */
	private int cycleCount = 1;

	/** The door 1. */
	private Rectangle door1 = new Rectangle(50, 80);

	/** The door 2. */
	private Rectangle door2 = new Rectangle(50, 80);

	/** The time label. */
	private Label timeLabel = new Label("Time = " + time);

	/** The gp 2. */
	private GridPane gp2;

	/** The Constant MAXCELLY. */
	private final static int MAXCELLY = 13;

	/** The Constant MAXFLOORY. */
	private final static int MAXFLOORY = 13;

	/** The cell Y. */
	private int cellY = 13;

	/** The p label. */
	private Label pLabel;

	/** The sp. */
	private StackPane sp;

	/** The num pass. */
	private int numPass;

	/** The stepticks. */
	private TextField stepticks = new TextField();

	/** The background. */
	private Pane background;

	/** The floor array. */
	private int[] floorArray = { 5, 5, 5, 5, 5, 5 };

	/** The up. */
	private final int UP = 1;

	/** The down. */
	private final int DOWN = -1;

	/** The circ array. */
	private ArrayList<StackPane> circArray = new ArrayList<>();

	/** The pass array. */
	private ArrayList<Passengers> passArray = new ArrayList<>();

	/** Local copies of the states for tracking purposes. */
	private final int STOP = Elevator.STOP;

	/** The mvtoflr. */
	private final int MVTOFLR = Elevator.MVTOFLR;

	/** The opendr. */
	private final int OPENDR = Elevator.OPENDR;

	/** The offld. */
	private final int OFFLD = Elevator.OFFLD;

	/** The board. */
	private final int BOARD = Elevator.BOARD;

	/** The closedr. */
	private final int CLOSEDR = Elevator.CLOSEDR;

	/** The mv1flr. */
	private final int MV1FLR = Elevator.MV1FLR;

	/** The mm. */
	private Passengers mm = new Passengers(1, 3, 2, 4, true, 1000);

	/** The test. */
	private Passengers test = new Passengers(1, 3, 2, 4, true, 1000);

	/** The m. */
	private Passengers[] m = { mm, test };
	
	private VBox vb;

	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getCurrentFloor();
		

	}

	/**
	 * Inits the timeline.
	 */
	private void initTimeline() {
		t = new Timeline(new KeyFrame(Duration.millis(duration), ae -> controller.stepSim()));

	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		gp = new GridPane();
		bp = new BorderPane();
		sp = new StackPane();
		vb = new VBox();
		initTimeline();
		makeHotelDoors();
		initializeFloors();
		initializeElevatorPosition();
		makeLegend();
		stepticks.setText("Step n ticks");
		HBox x = new HBox(25);
		pLabel = new Label("" + numPass);
		sp.getChildren().addAll(elevator, pLabel);
		sp.setAlignment(pLabel, Pos.TOP_CENTER);
		bp.setRight(vb);
		makeElevatorDoors();
		
		x.getChildren().addAll(logging, Step, run, timeLabel, stepticks, enter);
		Step.setOnAction(e -> Closedr());
		logging.setOnAction(e -> enableLogging());

		run.setOnAction(e -> {t.setCycleCount(Animation.INDEFINITE); t.play();});


		enter.setOnAction(e -> {
			setTicks(stepticks.getText());	t.play();});
	
		setGridPaneConstraints();
		
	
		bp.setCenter(gp);
		bp.setTop(x);

		Scene scene = new Scene(bp, 800, 800, Color.BLUE);
		// scene.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new
		// Stop(0, Color.web("#81c483")),new Stop(1, Color.web("#fcc200"))));

		primaryStage.setScene(scene);
		primaryStage.setTitle("Elevator Simulation - " + controller.getTestName());
		primaryStage.show();
	}

	public void stepSim() {
		controller.stepSim();
	}

	/**
	 * Make legend.
	 */
	private void makeLegend() {
		gp.add(new Circle(15, Color.GREEN), 0, 0);
		gp.add(new Circle(15, Color.RED), 0, 1);
		gp.add(new Label(" Up"), 1, 0);
		gp.add(new Label(" Down"), 1, 1);

	}

	/**
	 * Initialize elevator position.
	 */
	private void initializeElevatorPosition() {
		// TODO Auto-generated method stub
		elevator = new Rectangle(100, 100);
		elevator.setFill(Color.TRANSPARENT);
		elevator.setStroke(Color.LIGHTSTEELBLUE);
		for (int i = currFloor; i > 1; i--) {
			cellY -= 2;
		}
		gp.add(sp, 2, cellY);
	}

	/**
	 * Make elevator doors.
	 */
	private void makeHotelDoors() {
		Rectangle ElevatorBackground = new Rectangle(180, 625);
		ElevatorBackground.setFill(Color.GRAY);
		gp.add(ElevatorBackground, 1, 8);
		Rectangle wall1 = new Rectangle(600, 120);
		wall1.setFill(Color.BROWN);
		gp.add(wall1, 5, 3);
		Rectangle wall2 = new Rectangle(600, 120);
		wall2.setFill(Color.BROWN);
		gp.add(wall2, 5, 5);
		Rectangle wall3 = new Rectangle(600, 120);
		wall3.setFill(Color.BROWN);
		gp.add(wall3, 5, 7);
		Rectangle wall4 = new Rectangle(600, 120);
		wall4.setFill(Color.BROWN);
		gp.add(wall4, 5, 9);
		Rectangle wall5 = new Rectangle(600, 120);
		wall5.setFill(Color.BROWN);
		gp.add(wall5, 5, 11);
		Rectangle wall6 = new Rectangle(600, 120);
		wall6.setFill(Color.BROWN);
		gp.add(wall6, 5, 13);
		for(int x = 0; x < 6; x++) {
			gp.add(new Rectangle(50, 80), 8, 2 * x + 3);
		}
		for(int x = 0; x < 6; x++) {
			gp.add(new Rectangle(50, 80), 11, 2 * x + 3);
		}
		for(int x = 0; x < 6; x++) {
			gp.add(new Rectangle(50, 80), 14, 2 * x + 3);
		}
	}
	private void makeElevatorDoors() {

		door1.setFill(Color.LIGHTGRAY);
		door2.setFill(Color.LIGHTGRAY);
		door1.setStroke(Color.BLACK);
		door2.setStroke(Color.BLACK);
		sp.getChildren().add(door1);
		sp.getChildren().add(door2);

		sp.setAlignment(door1, Pos.BOTTOM_LEFT);
		sp.setAlignment(door2, Pos.BOTTOM_RIGHT);

	}

	/**
	 * Opendr.
	 */
	private void Opendr() {
		door1.setWidth(25);
		door2.setWidth(25);
	}

	/**
	 * Closedr.
	 */
	private void Closedr() {
		door1.setWidth(50);
		door2.setWidth(50);
	}

	/**
	 * Sets the ticks.
	 *
	 * @param ticks the new ticks
	 */
	private void setTicks(String ticks) {
		int tick = Integer.parseInt(ticks);
		t.setCycleCount(tick);
		t.play();
	}

	/**
	 * Sets the grid pane constraints.
	 */
	private void setGridPaneConstraints() {
		for (int i = 0; i < 16; i++)
			gp.getColumnConstraints().add(new ColumnConstraints(50));

		for (int i = 0; i < 16; i++)
			gp.getRowConstraints().add(new RowConstraints(50));
	}

	/**
	 * Initialize floors.
	 */
	private void initializeFloors() {

		int startingFloor = 14;
		int floor = 1;
		for (int i = 0; i < NUM_FLOORS; i++) {

		Rectangle floorR = new Rectangle(700, 2);
			floorR.setFill(Color.TAN);
			;
			gp.add(floorR, 5, startingFloor);
			Label numFloor = new Label("" + floor);
			numFloor.setFont(Font.font("Cambria", 32));
			gp.add(numFloor, 0, startingFloor - 1);
			startingFloor -= 2;
			floor++;

		}
	}

	/**
	 * Enable logging.
	 */
	private void enableLogging() {
		controller.enableLogging();
	}

	/**
	 * Move.
	 *
	 * @param up the up
	 */
	private void move(boolean up) {
		if (up) {
			gp.getChildren().remove(sp);
			cellY -= 2;
			gp.add(sp, 1, cellY);
			this.currFloor++;
		} else {
			gp.getChildren().remove(sp);
			cellY += 2;
			gp.add(sp, 1, cellY);
			this.currFloor--;
		}
		
	}

	/**
	 * Sets the time.
	 *
	 * @param time the new time
	 */
	public void setTime(int time) {

		timeLabel.setText("Time = " + time);
	}

	/**
	 * Update state.
	 *
	 * @param currstate the currstate
	 * @param currFloor the curr floor
	 */
	public void updateState(int currstate, int currFloor) {
		System.out.println(currstate);
		System.out.println(currFloor);
		if (currstate == MV1FLR) {

			if (this.currFloor < currFloor || this.currFloor > currFloor) {
				move(this.currFloor < currFloor);
			}
			
		} else if (currstate == MVTOFLR) {
			if (this.currFloor < currFloor || this.currFloor > currFloor) {
				move(this.currFloor < currFloor);
			}

		} else if (currstate == OPENDR) {
			Opendr();
		} else if (currstate == CLOSEDR) {
			Closedr();
		} else if (currstate == STOP) {

		}

	}

	/**
	 * Off load.
	 *
	 * @param passengers the passengers
	 * @param currFloor  the curr floor
	 */
	public void offLoad(int passengers, int currFloor) {
		this.passengers -= passengers;
		int place = 1;

		pLabel.setText("" + this.passengers);

	}

	/**
	 * Board.
	 *
	 * @param passenger the passenger
	 */
	public void board(Passengers passenger) {
		int yCord = 0;
		int xCord = 0;
		boolean boarded = false;
		for (int j = 0; j < passArray.size(); j++) {
			if (passenger.equals(passArray.get(j))) {
				StackPane x = circArray.remove(j);
				passArray.remove(j);
				yCord = gp.getColumnIndex(x);
				xCord = gp.getRowIndex(x);
				gp.getChildren().remove(x);
				boarded = true;
				floorArray[currFloor]--;
				pLabel.setText("" + passenger.getNumPass());
			}
		}
		if (boarded) {
			for (int i = 0; i < circArray.size(); i++) {
				StackPane y = circArray.get(i);
				int sCordy = gp.getRowIndex(y);
				int sCordX = gp.getColumnIndex(y);
				if (sCordy == xCord && sCordX > yCord) {
					System.out.println("gothere");
					gp.getChildren().remove(y);
					gp.add(y, --sCordX, sCordy);

				}
			}
		}
	}

	public void giveUp(Passengers passengers) {

	}

	/**
	 * Called by the controller, passes in an array of passengers that have arrived,
	 * reflects change on gui.
	 *
	 * @param passengers the passengers
	 */
	public void arrivalPassengers(Passengers[] passengers) {

		for (int i = 0; i < passengers.length; i++) {
			int floor = MAXFLOORY - ((passengers[i].getOnFloor()) * 2);
			if (passengers[i].getDirection() == UP) {
				Label numP = new Label("" + passengers[i].getNumPass());
				Circle x = new Circle(25);
				StackPane y = new StackPane(x, numP);
				x.setFill(Color.GREEN);

				gp.add(y, floorArray[passengers[i].getOnFloor()], floor);
				circArray.add(y);
				passArray.add(passengers[i]);
				floorArray[passengers[i].getOnFloor()]++;
			} else {
				Label numP = new Label("" + passengers[i].getNumPass());
				Circle x = new Circle(25);
				StackPane y = new StackPane(x, numP);
				x.setFill(Color.RED);

				gp.add(y, floorArray[passengers[i].getOnFloor()], floor);
				circArray.add(y);
				passArray.add(passengers[i]);
				floorArray[passengers[i].getOnFloor()]++;
			}
		}

	}

	/**
	 * End sim.
	 */
	public void endSim() {

	}

	/**
	 * Test.
	 */
	private void test() {

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

}
