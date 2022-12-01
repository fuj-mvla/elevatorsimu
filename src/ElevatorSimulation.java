
import building.Elevator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class ElevatorSimulation extends Application {
	/** Instantiate the GUI fields */
	private ElevatorSimController controller;
	private final int NUM_FLOORS;
	private final int NUM_ELEVATORS;
	private int currFloor;
	private int passengers;
	private int time;
	private Timeline t;
	private Rectangle elevator = new Rectangle(100,100);
	private BorderPane bp;
	private GridPane gp;
	private Button logging = new Button("Log");
	private Button Step = new Button ("Step");
	private Button run = new Button("Run");
	private int duration = 1000;
	private int cycleCount = 1;
	private Label timeLabel = new Label("Time = " + time);
	private int floor = 1;
	private int cellY = 13;
	
	/** Local copies of the states for tracking purposes */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;

	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
	NUM_FLOORS = controller.getNumFloors();
	NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getCurrentFloor();
		
	}
	public void initTimeline() {
		t = new Timeline(new KeyFrame(Duration.millis(duration),ae -> move(true)));
		t.setCycleCount(10);
		
	
	}
	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		initTimeline();
		bp = new BorderPane();
		HBox x = new HBox(25);
		x.getChildren().addAll(logging,Step,run,timeLabel);
		Step.setOnAction(e -> controller.stepSim());
		logging.setOnAction(e -> enableLogging());
		run.setOnAction(e -> {t.setCycleCount(Animation.INDEFINITE); t.play();});
		gp = new GridPane();
		setGridPaneConstraints();
		elevator.setFill(Color.TRANSPARENT);
		elevator.setStroke(Color.BLACK);
		gp.add(elevator,1,cellY);
		initializeFloors(gp);
		bp.setCenter(gp);
		bp.setTop(x);
		
		Scene scene = new Scene(bp,800,800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();

		//TODO: Complete your GUI, including adding any helper methods.
		//      Meet the 30 line limit...
		
	}
	private void setGridPaneConstraints() {
		for (int i = 0; i < 16; i ++) 
			gp.getColumnConstraints().add(new ColumnConstraints(50));

		for (int i = 0; i < 16; i ++) 
			gp.getRowConstraints().add(new RowConstraints(50));
	}
	public int getTime() {
		return time;
	}
	private void initializeFloors(GridPane gp) {
		int startingFloor = 14;
		for (int i = 0;i < NUM_FLOORS;i++) {
		gp.add(new Rectangle(700,2), 4, startingFloor);
		startingFloor -=2;
		}
	}
	private void enableLogging() {
		controller.enableLogging();
	}
	public void updateState(int currstate) {
		
	}
	
	public void move(boolean up) {
		if (up) {
			gp.getChildren().remove(elevator);
			cellY -=2;
			gp.add(elevator, 1, cellY);
			
		}
		
	}
	public void setTime(int time) {
		this.time = time;
	}
	public void updateGUI(int state) {
		
	}
	public void arrivalPassengers(int[] passengers) {
		
	}
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		Application.launch(args);
	}

}
