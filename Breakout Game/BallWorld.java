import java.util.Iterator;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class BallWorld extends World {
	private Score score;
	private boolean isPlaying;
	private boolean isGameOver;
	private final int GAME_OVER_FONT_SIZE = 30;
	private final int INSTRUCTION_FONT_SIZE = 20;

	
	
	public BallWorld() {
		super();
		score = new Score();
		score.setLayoutX(0);
		score.setLayoutY(470);
		getChildren().add(score);
		isPlaying = false;
		isGameOver = false;
	}

	@Override
	public void act(long now) {
		if (isPressed(KeyCode.SPACE)) {
			Iterator<Node> itr = getChildren().iterator();
			while(itr.hasNext()) {
				Node node = itr.next();
				if (node instanceof Label)
					itr.remove();
			}
			
			setPlaying(true);
		}
	}

	public void onGameOver() {
		
		Scene scene = getParent().getScene();
		stop();
		isPlaying = false;
		isGameOver = true;

		
		// input field for entering name to be stored in leaderboard
		Label label1 = new Label("Enter your name:");
		TextField name = new TextField();
		HBox hb = new HBox();
		hb.getChildren().addAll(label1, name);
		hb.setSpacing(10);
		
		// label for Game over message
		Label gameOverTxt = new Label("Game Over.");
		gameOverTxt.setFont(new Font(GAME_OVER_FONT_SIZE));
		gameOverTxt.setLayoutX(scene.getWidth() / 2 - gameOverTxt.getWidth() - 100);
		gameOverTxt.setLayoutY(scene.getHeight() / 2 - gameOverTxt.getHeight());
		// label for instructions to tell user to press [space] to return to main menu
		Label instructionTxt = new Label("Enter your name to return to the main menu. ");
		instructionTxt.setFont(new Font(INSTRUCTION_FONT_SIZE));
		instructionTxt.setLayoutX(scene.getWidth() / 2 - instructionTxt.getWidth() - 150);
		instructionTxt.setLayoutY(scene.getHeight() / 1.5);

		getChildren().addAll(gameOverTxt, instructionTxt, hb);
		BallWorld bw = this;
		
		System.out.println(getScore()); // replace with writing score to a file
		
		// keyboard input, go back to main menu when user presses space
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (isPressed(KeyCode.ENTER)) {
					Game.updateHighScore(score.getScore(), name.getText());
					Game.resetLevels();
					stop();
					bw.stop();
					removeKeyCode(KeyCode.ENTER);
				} 
			}
		};
		timer.start();
	}

	// resets status of BallWorld to initial status(not playing yet, but not game
	// over either)
	// score is reset to '0', removes all the bricks
	public void reset() {
		isPlaying = false;
		isGameOver = false;
		setScore(0);

		//remove all the Bricks and the Labels(Score is subclass of Text, so won't be removed
		Iterator<Node> itr = getChildren().iterator();
		while(itr.hasNext()) {
			Node node = itr.next();
			if (node != score && (node instanceof Brick || node instanceof Label || node instanceof TemporaryBall)) {
				itr.remove();
			}
		}

	}

	public int getScore() {
		return score.getScore();
	}

	public void setScore(int val) {
		score.setScore(val);
		score.updateDisplay();
	}

	public void setScoreObj(Score score) {
		this.score = score;
	}

	public void copyScore(BallWorld bw) {
		setScore(bw.getScore());
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public Paddle getPaddle() {
		for (Node node : this.getChildren()) {
			if (node instanceof Paddle) {
				return (Paddle) node;
			}
		}
		return null;
	}

	public boolean hasBricks() {
		for (Node node : this.getChildren()) {
			if (node instanceof Brick)
				return true;
		}
		return false;
	}

	
}
