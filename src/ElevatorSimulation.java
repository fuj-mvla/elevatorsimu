
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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
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
	private Rectangle door1 = new Rectangle(50,80);
	private Rectangle door2 = new Rectangle(50,80);
	private Label timeLabel = new Label("Time = " + time);
	private GridPane gp2;
	private final static int MAXCELLY = 13;
	private final static int MAXFLOORY = 13;
	private int cellY = 13;
	private Label pLabel;
	private StackPane sp;
	private int numPass;
	private TextField stepticks = new TextField();
	private Pane legendPane;
	private int[] floorArray = {4,4,4,4,4,4};
	private final int UP = 1;
	private final int DOWN = -1;
	private ArrayList<Circle> circArray = new ArrayList<>();
	private ArrayList<Circle> passArray = new ArrayList<>();
	/** Local copies of the states for tracking purposes */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;

	private Passengers testP = new Passengers(1,2,1,3,false,2);
	private Passengers testP2 = new Passengers(1,2,3,2,false,2);
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
		t = new Timeline(new KeyFrame(Duration.millis(duration),ae -> test()));
	
		
	
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
		makeLegend();
		stepticks.setText("Step n ticks");
		HBox x = new HBox(25);
		pLabel = new Label("" + numPass);
		sp.getChildren().addAll(elevator,pLabel);
		sp.setAlignment(pLabel, Pos.TOP_CENTER);
		makeElevatorDoors();
		x.getChildren().addAll(logging,Step,run,timeLabel,stepticks,enter);
		Step.setOnAction(e -> Closedr());
		logging.setOnAction(e -> enableLogging());
		run.setOnAction(e -> Opendr());
		enter.setOnAction(e -> {setTicks(stepticks.getText()); t.play();});
		setGridPaneConstraints();
		bp.setLeft(gp);
		bp.setTop(x);
		
		Scene scene = new Scene(bp,800,800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();
	}
	private void makeLegend() {
		gp.add(new Circle(15,Color.GREEN),  0,0);
		gp.add(new Circle(15,Color.RED),  0,1);
		gp.add(new Label(" Up"), 1, 0);
		gp.add(new Label(" Down"), 1, 1);
		
	}
	private void initializeElevatorPosition() {
		// TODO Auto-generated method stub
		elevator = new Rectangle(100,100);
		elevator.setFill(Color.TRANSPARENT);
		elevator.setStroke(Color.LIGHTSTEELBLUE);
		for (int i = currFloor;i > 1;i-- ) {
			cellY -=2;
		}
		gp.add(sp,1,cellY);
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
	private void Opendr() {
		door1.setWidth(25);
		door2.setWidth(25);
	}
	private void Closedr() {
		door1.setWidth(50);
		door2.setWidth(50);
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
		int floor = 1;
		for (int i = 0;i < NUM_FLOORS;i++) {

			Rectangle floorR = new Rectangle(700,2);
			floorR.setFill(Color.TAN);;
			gp.add(floorR, 4, startingFloor);
			Label numFloor = new Label("" + floor);
			numFloor.setFont(Font.font("Cambria",32));
			gp.add(numFloor, 0, startingFloor-1);
			startingFloor -=2;
			floor++;

		}
	}
	private void enableLogging() {
		controller.enableLogging();
	}
	
	
	private void move(boolean up) {
		if (up) {
			gp.getChildren().remove(sp);
			cellY -=2;
			gp.add(sp, 1, cellY);
			if (cellY < 4) {
				t.stop();
			}
		}
		else {
			gp.getChildren().remove(sp);
			cellY +=2;
			gp.add(sp, 1, cellY);
		}
			
	}
	public void setTime(int time) {
		
		timeLabel.setText("Time = " + time);
	}
	public void updateState(int currstate,int currFloor) {
		if (currstate == MV1FLR) {
			if (this.currFloor < currFloor) {
				move(true);
				
			}
			else if (this.currFloor > currFloor) {
				move(false);
			}
			this.currFloor = currFloor;
		}
		else if (currstate == OPENDR) {
			Opendr();
		}
		else if (currstate == CLOSEDR) {
			Closedr();
		}
		
	}
	public void offLoad(Passengers[] passengers) {
		
	}
	public void board(Passengers[] passengers) {
		
	}
	public void arrivalPassengers(Passengers[] passengers) {
		
		
		for(int i = 0; i < passengers.length;i++) {
			int floor = MAXFLOORY - ((passengers[i].getOnFloor())*2);
			if (passengers[i].getDirection() ==UP) {
				Label numP = new Label("" + passengers[i].getNumPass());
				Circle x = new Circle(25);
				StackPane y = new StackPane(x,numP);
				x.setFill(Color.GREEN);
				
				gp.add(y, floorArray[passengers[i].getOnFloor()], floor);
				floorArray[passengers[i].getOnFloor()]++;
			}
			else {
				Label numP = new Label("" + passengers[i].getNumPass());
				Circle x = new Circle(25);
				StackPane y = new StackPane(x,numP);
				x.setFill(Color.RED);
				
				gp.add(y, floorArray[passengers[i].getOnFloor()], floor);
				floorArray[passengers[i].getOnFloor()]++;
			}
		}
		
	}
	public void endSim() {
		
	}
	private void test() {
		Opendr();
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
