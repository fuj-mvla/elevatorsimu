
import building.Elevator;
import building.Passengers;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
	private Rectangle elevator;
	private BorderPane bp;
	private GridPane gp;
	private Button logging = new Button("Log");
	private Button Step = new Button ("Step");
	private Button run = new Button("Run");
	private Button enter = new Button("Enter");
	private int duration = 1000;
	private int cycleCount = 1;
	private Label timeLabel = new Label("Time = " + time);
	private GridPane gp2;
	private final static int MAXCELLY = 13;
	private int cellY = 13;
	private Label pLabel;
	private StackPane sp;
	private int numPass;
	private TextField stepticks = new TextField();
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
	private void initTimeline() {
		t = new Timeline(new KeyFrame(Duration.millis(duration),ae -> move(true)));
	
		
	
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
		initTimeline();
		initializeFloors();
		initializeElevatorPosition();
		stepticks.setText("Step n ticks");
		HBox x = new HBox(25);
		pLabel = new Label("" + numPass);
		sp.getChildren().addAll(elevator,pLabel);
		x.getChildren().addAll(logging,Step,run,timeLabel,stepticks,enter);
		Step.setOnAction(e -> controller.stepSim());
		logging.setOnAction(e -> enableLogging());
		run.setOnAction(e -> {t.setCycleCount(Animation.INDEFINITE); t.play();});
		enter.setOnAction(e -> {setTicks(stepticks.getText()); t.play();});
		setGridPaneConstraints();

		bp.setLeft(gp);
		bp.setCenter(gp2);
		bp.setTop(x);
		Scene scene = new Scene(bp,800,800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();
	}
	private void initializeElevatorPosition() {
		// TODO Auto-generated method stub
		elevator = new Rectangle(100,100);
		elevator.setFill(Color.TRANSPARENT);
		elevator.setStroke(Color.BLACK);
		for (int i = currFloor;i > 1;i-- ) {
			cellY -=2;
		}
		gp.add(sp,1,cellY);
	}
	private void setTicks(String ticks) {
		int tick = Integer.parseInt(ticks);
		t.setCycleCount(tick);
		t.play();
	}
	private void setGridPaneConstraints() {
		for (int i = 0; i < 16; i ++) 
			gp.getColumnConstraints().add(new ColumnConstraints(50));

		for (int i = 0; i < 16; i ++) 
			gp.getRowConstraints().add(new RowConstraints(50));
	}
	
	
	private void initializeFloors() {
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
			gp.getChildren().remove(sp);
			cellY -=2;
			gp.add(sp, 1, cellY);
			if (cellY < 4) {
				t.stop();
			}
		}
			
	}
	public void setTime(int time) {
		this.time = time;
		timeLabel.setText("Time = " + time);
	}
	public void changeState(int state) {
		
	}
	public void offLoad(int[] passengers) {
		
	}
	public void arrivalPassengers(int[] passengers, int floor) {
		
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
