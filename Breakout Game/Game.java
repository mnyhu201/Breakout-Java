import java.util.ArrayList;
import java.util.Collections;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Game extends Application {
	public static Scene level1Scene;
	private static Scene mainMenuScene;
	public static Scene level2Scene;
	public static Scene level3Scene;
	public static BallWorld ballWorldLv1;
	public static BallWorld ballWorldLv2;
	public static BallWorld ballWorldLv3;
	public static Stage staticStage;
	static Scene highScoreScene;
	static Pane rootHighScrScene;
	static boolean isEasyDifficulty;
	static Label highScoreNumLabel;
	private static Background levelBackgrounds;
	private static ArrayList<String> nameList = new ArrayList<String>();
	private static ArrayList<Integer> scoreList = new ArrayList<Integer>();
	private static AnimationTimer timer; // timer for change levels
	private static final Color BACKGROUND_COLOR = Color.ALICEBLUE;

	public static int highScore;

	public static void main(String[] args) {
		System.out.println("hihi");
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		//background for game levels
		String path = getClass().getClassLoader().getResource("background_1.png").toString();
		Image imgForBg = new Image(path);
		levelBackgrounds = new Background(new BackgroundImage(imgForBg,
		BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));
		
		isEasyDifficulty = true;
		staticStage = stage;

		highScore = 0;

		createLevel1();
		createMainMenu();
		createLevel2();
		createLevel3();
		createHighScoreScene();

		// ----------------- timer for changing levels -------------------------------
		long lastTime = System.nanoTime();
		timer = new AnimationTimer() {
			long gapBetweenUpdates = (long) 1e9; //gap between each update of title's color
			long oldTime;
			
			@Override
			public void handle(long now) {
				Scene scene = staticStage.getScene();
				boolean goToLv3 = false;
				if (scene == level1Scene && !ballWorldLv1.hasBricks()) {
					ballWorldLv1.stop();
					staticStage.setScene(level2Scene);
					staticStage.setTitle("Breakout Level 2");

					ballWorldLv2.start();
					ballWorldLv2.requestFocus();
					ballWorldLv2.setScore(ballWorldLv1.getScore());

				} else if (scene == level2Scene && !ballWorldLv2.hasBricks()) {
					ballWorldLv2.stop();
					staticStage.setScene(level3Scene);
					staticStage.setTitle("Breakout Level 3");

					ballWorldLv3.start();
					ballWorldLv3.requestFocus();
					ballWorldLv3.setScore(ballWorldLv2.getScore());
				} else if(scene == level3Scene && !ballWorldLv3.hasBricks()) {
					onWinning();
					
					if(ballWorldLv3.isPressed(KeyCode.SPACE)) {
						staticStage.setScene(mainMenuScene);
					}
					if(ballWorldLv3.isPressed(KeyCode.ENTER)) {
						staticStage.setScene(highScoreScene);
					}
				}
				
				if(now - oldTime >= gapBetweenUpdates) {//every second change title to random color
					double transparency = 0.8 + ((1.0 - 0.8) * Math.random());
					Color randColor = new Color(Math.random(), Math.random(), Math.random(), transparency);
					
					for (Node node : mainMenuScene.getRoot().getChildrenUnmodifiable()) {
						if(node instanceof Label) {
							Label title = (Label) node;
							title.setTextFill(randColor);
						}		
					}
					oldTime = now;
				}
			}

		};
		
		timer.start();

		stage.setTitle("Breakout");
		stage.setWidth(512);
		stage.setHeight(500);
		stage.setResizable(false);
		stage.setScene(mainMenuScene);
		stage.show();

	}

	// loads file
	private void createHighScoreScene() {
		
		
		DropShadow dropShadow = new DropShadow(10, 3, 3, Color.GRAY);
		Lighting lighting = new Lighting();
		Distant light = new Distant();
		light.setAzimuth(-135.0f);
		lighting.setLight(light);
		lighting.setSurfaceScale(5.0f);
		dropShadow.setInput(lighting);
		
		rootHighScrScene = new Pane();
		highScoreScene = new Scene(rootHighScrScene);
		
		//nameList, scoreList
		Collections.addAll(nameList, "John Smth", "Jane Doe", "Robert Lee", "Atari123");
		Collections.addAll(scoreList, 5634070, 434080, 202490, 98230);
		
		Hyperlink goBack = new Hyperlink("<< main menu");
		goBack.setFont(new Font(18));
		goBack.setTextFill(Color.BLACK);
		goBack.setLayoutX(10);
		goBack.setLayoutY(445);
		goBack.setFocusTraversable(false);
		
		goBack.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {		
				staticStage.setScene(mainMenuScene);
			}	
		});
		
		Label highScoreTitle = new Label("High Scores");
		highScoreTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 35));
		highScoreTitle.setLayoutX(150);
		highScoreTitle.setAlignment(Pos.CENTER);
		highScoreTitle.setEffect(dropShadow);
		highScoreTitle.setTextAlignment(TextAlignment.CENTER);
		highScoreTitle.setLayoutY(20);
		
		
		//adding the score & names
		double xPosOfNum = -1;
		String gap = "\t\t\t\t\t\t\t";
		Font labelFont = new Font(25);
		VBox vboxOfHighScr = new VBox();
		vboxOfHighScr.setLayoutX(15);
		vboxOfHighScr.setLayoutY(100);
		vboxOfHighScr.setSpacing(10);
		for (int i = 0; i < nameList.size(); i++) {
			String nameAndScore = nameList.get(i) + gap + scoreList.get(i);
			Label label = new Label(nameAndScore);
			label.setFont(labelFont);
			vboxOfHighScr.getChildren().add(label);
			xPosOfNum = label.getLayoutX();
		}
		
		
		
		highScoreNumLabel = new Label("" + highScore);
		highScoreNumLabel.setLayoutX(384);
		highScoreNumLabel.setLayoutY(250);
		highScoreNumLabel.setPadding(new Insets(20, 0, 0, 0));
		highScoreNumLabel.setFont(Font.font("Arial", FontWeight.BOLD,labelFont.getSize()));
		Label youLabel = new Label("You");
		youLabel.setLayoutY(270);
		youLabel.setLayoutX(15);
		youLabel.setFont(Font.font("Arial", FontWeight.BOLD, labelFont.getSize()));
		
		Rectangle backgr = new Rectangle(0,0, 600, 600);
		backgr.setFill(BACKGROUND_COLOR);
		
		rootHighScrScene.getChildren().addAll(backgr, goBack, highScoreTitle, vboxOfHighScr, highScoreNumLabel, youLabel);
		rootHighScrScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
	        @Override
	        public void handle(KeyEvent t) {
	            KeyCode key = t.getCode();
	            if (key == KeyCode.ESCAPE){
	            	staticStage.setScene(mainMenuScene);
	            	
	            }
	        }
	    });
	}
	
	private void createMainMenu() {
		Rectangle backgr = new Rectangle(0,0, 600, 600);
		backgr.setFill(BACKGROUND_COLOR);
		DropShadow dropShadow = new DropShadow(10, 5, 5, Color.GRAY);
		
		BorderPane rootMainMenu = new BorderPane();
		mainMenuScene = new Scene(rootMainMenu);
		
		rootMainMenu.getChildren().add(backgr);
		
		// buttons in the main menu
		VBox menuBtsVBox = new VBox();
		Button playBt = new Button("Play");
		Button highScrBt = new Button("High Scores");
			//difficulty radio buttons
		RadioButton easyRadioBt = new RadioButton("Easy");
		RadioButton hardRadioBt = new RadioButton("Hard");
		easyRadioBt.setFont(new Font(17));
		hardRadioBt.setFont(new Font(17));
		easyRadioBt.setSelected(true);
		ToggleGroup radBtGroup = new ToggleGroup();
		radBtGroup.getToggles().addAll(easyRadioBt, hardRadioBt);
		HBox hboxRadioBts = new HBox();
		hboxRadioBts.getChildren().addAll(easyRadioBt, hardRadioBt);
		hboxRadioBts.setAlignment(Pos.CENTER);
		hboxRadioBts.setSpacing(10);
		Button creditsBt = new Button("Credits");
		creditsBt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				getHostServices().showDocument("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
			}
		});
			//adding everything
		menuBtsVBox.getChildren().addAll(playBt, highScrBt, hboxRadioBts, creditsBt);
		menuBtsVBox.setAlignment(Pos.CENTER);
		rootMainMenu.setCenter(menuBtsVBox);
		// sizing & formatting buttons
		menuBtsVBox.setPadding(new Insets(40, 0, 0, 0));
		menuBtsVBox.setSpacing(15);
		for (Node b : menuBtsVBox.getChildren()) {
			if(b instanceof Button) {
				Button bt = (Button) b;
				bt.setPrefWidth(200);
				bt.setFont(new Font("Arial", 25));
				bt.setEffect(dropShadow);
			}
		}

		// "Main Menu" Label
		Label mainMenuLabel = new Label("Breakout Game");
		mainMenuLabel.setAlignment(Pos.CENTER);
		mainMenuLabel.setTextAlignment(TextAlignment.CENTER);
		Font font = new Font(50);
		mainMenuLabel.setFont(new Font(50));
		rootMainMenu.setTop(mainMenuLabel);
		BorderPane.setAlignment(mainMenuLabel, Pos.CENTER);
		mainMenuLabel.setPadding(new Insets(30, 0, 0, 0));
		mainMenuLabel.setEffect(dropShadow);

		// gameBt functionality: change scene to level 1 when play button is pressed
		playBt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				staticStage.setScene(level1Scene);
				ballWorldLv1.start();
				ballWorldLv1.setScore(0);
				ballWorldLv1.requestFocus();
				staticStage.setTitle("Breakout Level 1");
			}
		});

		// high score button functionality
		highScrBt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				staticStage.setScene(highScoreScene);
			}
		});
		radBtGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				isEasyDifficulty = (newValue == easyRadioBt);
			}
		});
	}

	

	private void createLevel1() {
		BorderPane rootLv1 = new BorderPane(); // root node
		level1Scene = new Scene(rootLv1);

		ballWorldLv1 = new BallWorld();
		ballWorldLv1.setPrefWidth(500);
		ballWorldLv1.setPrefHeight(500);
		rootLv1.setCenter(ballWorldLv1);

		ballWorldLv1.setScore(400);

		// adding bricks to BallWorld level 1
		Brick brick = new Brick();
		addBricksLevel1();

		Paddle paddle = new Paddle();
		paddle.setX(250);
		paddle.setY(400);
		ballWorldLv1.add(paddle);

		Ball ball = new Ball();
		ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
		ball.setY(paddle.getY() - ball.getHeight() / 2 - paddle.getHeight() / 2);
		ballWorldLv1.add(ball);

		// adding Label telling player to press space to start game
		Label startLabel = new Label("Press the [spacebar] to begin the level!");
		startLabel.setFont(new Font(15));
		ballWorldLv1.getChildren().add(startLabel);
		startLabel.setLayoutX(100);
		startLabel.setLayoutY(440);

		// keyboard events on BallWorld
		ballWorldLv1.requestFocus();
		ballWorldLv1.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// center xcoor - imgWidth/2
				if (ballWorldLv1.isFocused()) {
					if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
						ballWorldLv1.addKeyCode(event.getCode());
					}
				}
			}
		});
		ballWorldLv1.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// center xcoor - imgWidth/2
				if (ballWorldLv1.isFocused()) {
					if (event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
						ballWorldLv1.removeKeyCode(event.getCode());
						paddle.setMoving(0);
					}
				}
			}
		});
		
		ballWorldLv1.setBackground(levelBackgrounds);
	}
	

	private void createLevel2() {
		BorderPane rootLv2 = new BorderPane();
		level2Scene = new Scene(rootLv2);
		ballWorldLv2 = new BallWorld();
		rootLv2.setCenter(ballWorldLv2);

		Paddle paddle2 = new Paddle();
		paddle2.setX(250);
		paddle2.setY(400);
		ballWorldLv2.add(paddle2);

		Ball ball2 = new Ball(5.3, 5.3);
		ballWorldLv2.add(ball2);
		ball2.setX(paddle2.getX() + paddle2.getWidth() / 2 - ball2.getWidth() / 2);
		ball2.setY(paddle2.getY() - paddle2.getHeight() / 2 - ball2.getHeight() / 2);

		// adding Bricks to level 2
		addBricksLevel2();

		// keyboard events on BallWorld
		ballWorldLv2.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// center xcoor - imgWidth/2
				if (ballWorldLv2.isFocused()) {
					if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
						ballWorldLv2.addKeyCode(event.getCode());
					}
				}
			}
		});
		ballWorldLv2.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// center xcoor - imgWidth/2
				if (ballWorldLv2.isFocused()) {
					if (event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
						ballWorldLv2.removeKeyCode(event.getCode());
						paddle2.setMoving(0);
					}
				}
			}
		});
		ballWorldLv2.setBackground(levelBackgrounds);
	}

	private void createLevel3() {
		BorderPane rootLv3 = new BorderPane();
		level3Scene = new Scene(rootLv3);
		ballWorldLv3 = new BallWorld();
		rootLv3.setCenter(ballWorldLv3);

		Paddle paddle3 = new Paddle();
		paddle3.setX(250);
		paddle3.setY(400);

		Ball ball3 = new Ball(5.5, 5.5);
		ball3.setX(paddle3.getX() + paddle3.getWidth() / 2 - ball3.getWidth() / 2);
		ball3.setY(paddle3.getY() - paddle3.getHeight() / 2 - ball3.getHeight() / 2);

		ballWorldLv3.add(ball3);
		ballWorldLv3.add(paddle3);

		// adding bricks to BallWorld level 3
		addBricksLevel3();

		// keyboard event
		ballWorldLv3.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// center xcoor - imgWidth/2
				if (ballWorldLv3.isFocused()) {
					if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
						ballWorldLv3.addKeyCode(event.getCode());
					}
				}
			}
		});
		ballWorldLv3.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// center xcoor - imgWidth/2
				if (ballWorldLv3.isFocused()) {
					if (event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
						ballWorldLv3.removeKeyCode(event.getCode());
						paddle3.setMoving(0);
					}
				}
			}
		});
		ballWorldLv3.setBackground(levelBackgrounds);
	}

	public static void updateHighScore(int score, String name) { // adds the name/score to the file
		if(score <= highScore)
			return;
			
		highScoreNumLabel.setText("" + score);
		highScore = score;
	}

	public static void resetLevels() {
		staticStage.setScene(mainMenuScene);

		// reset level 1
		ballWorldLv1.reset();
		addBricksLevel1();

		// reset level 2
		ballWorldLv2.reset();
		addBricksLevel2();

		// reset level 3
		ballWorldLv3.reset();
		addBricksLevel3();
		
		staticStage.setTitle("Breakout Game");
	}

	//what to do when the player beats the game
	private static void onWinning() {
		/* show congrats text - colorful?
		 * press [return] to go back to main menu
		 * animation???
		 * recommend try hard
		 * 
		 */
		Font congratsFont = new Font(20);
		Label congratsLine1 = new Label("Congratulations! You completed all the levels!");
		Label congratsLine2 = new Label("If you haven't, try the hard mode, which gives 2x points!");
		Label congratsLine3 = new Label("Press [space] to return to the main menu. ");
		Label congratsLine4 = new Label("Press [return] to check out the high scorers! ");
		VBox congratsVBox = new VBox(congratsLine1, congratsLine2, congratsLine3, congratsLine4);
		for (Node node : congratsVBox.getChildren()) {
			Label label = (Label) node;
			label.setTextFill(Color.BLACK);
			label.setFont(congratsFont);
		}
		
		congratsVBox.setLayoutX(30);
		congratsVBox.setLayoutY(170);
		congratsVBox.setSpacing(20);
		
		ballWorldLv3.getChildren().addAll(congratsVBox);
	}
	
	// helper methods for adding the appropriate Bricks to the levels

	private static void addBricksLevel1() {

		Brick brick = new Brick();
		
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 500 / brick.getWidth(); i++) {
				int points = 10 * (5 - j);
				int randNum = (int) (Math.random() * 100);
				
				if(randNum >= 98 && isEasyDifficulty) {
					AddBallBrick addBlBrk = new AddBallBrick(points);
					addBlBrk.setX(i * brick.getWidth());
					addBlBrk.setY(j * brick.getHeight());
					ballWorldLv1.add(addBlBrk);

				} else {
					brick.setX(i * brick.getWidth());
					brick.setY(j * brick.getHeight());
					ballWorldLv1.add(brick);
					brick = new Brick(points);
				}
				
			}
		}
	}

	private static void addBricksLevel2() {

		Brick brick = new Brick();

		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 500 / brick.getWidth(); i++) {
				int points = 10 * (6 - j);
				int randNum = (int) (Math.random() * 100);
				if (randNum < 90) {
					brick.setX(i * brick.getWidth());
					brick.setY(j * brick.getHeight());
					ballWorldLv2.add(brick);
					brick = new Brick(points);

				} else if(randNum >= 98) {
					AddBallBrick addBlBrk = new AddBallBrick(points);
					addBlBrk.setX(i * addBlBrk.getWidth());
					addBlBrk.setY(j * addBlBrk.getHeight());
					ballWorldLv2.add(addBlBrk);
				} else {
					ChangeSpeedBrick speedBrick = new ChangeSpeedBrick(points);
					double dx = (int) (Math.random() * 2) - 0.6;
					double dy = (int) (Math.random() * 2) - 0.6;
					speedBrick = new ChangeSpeedBrick(points, dx, dy);
					speedBrick.setX(i * speedBrick.getWidth());
					speedBrick.setY(j * speedBrick.getHeight());
					ballWorldLv2.add(speedBrick);
				}

			}
		}
	}

	private static void addBricksLevel3() {
	

		Brick brick = new Brick();

		for (int j = 0; j < 7; j++) {
			for (int i = 0; i < 500 / brick.getWidth(); i++) {
				int points = 10 * (7 - j);
				int randNum = (int) (Math.random() * 100);
				if (randNum < 90) {
					brick.setX(i * brick.getWidth());
					brick.setY(j * brick.getHeight());
					ballWorldLv3.add(brick);
					brick = new Brick(points);

				} else if (randNum >= 0 && randNum <= 5) {
					ShieldBrick shieldBrick = new ShieldBrick(points);
					shieldBrick.setX(i * brick.getWidth());
					shieldBrick.setY(j * brick.getHeight());
					ballWorldLv3.add(shieldBrick);
				}  else if(randNum >= 98) {
					AddBallBrick addBlBrk = new AddBallBrick(points);
					addBlBrk.setX(i * addBlBrk.getWidth());
					addBlBrk.setY(j * addBlBrk.getHeight());
					ballWorldLv3.add(addBlBrk);
				}	else {
					ChangeSpeedBrick speedBrick = new ChangeSpeedBrick(points);
					int dx = (int) (Math.random() * 3) - 1;
					int dy = (int) (Math.random() * 3) - 1;
					speedBrick = new ChangeSpeedBrick(points, dx, dy);
					speedBrick.setX(i * speedBrick.getWidth());
					speedBrick.setY(j * speedBrick.getHeight());
					ballWorldLv3.add(speedBrick);
				}

			}
		}

	}

}
