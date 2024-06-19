
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class Ball extends Actor {
	private double dx;
	private double dy;
	private boolean bounce;
	private boolean hasShield;
	private boolean DEBUG_TEST = false;
	
	public Ball() {
		super();
		String path = getClass().getClassLoader().getResource("ball.png").toString();
		Image ballImg = new Image(path); // maybe path?
		setImage(ballImg);
		bounce = false;
		dx = 4.3;
		dy = 4.3;
		hasShield = false;
	}

	public Ball(double dx, double dy) {
		this();
		this.dx = dx;
		this.dy = dy;
	}

	public Ball(double dx, double dy, boolean hasShield) {
		this(dx, dy);
		this.hasShield = hasShield;
	}

	@Override
	public void act(long now) {
		if(DEBUG_TEST) { // only for debugging to test ball interactions manually
			if (getWorld() != Game.ballWorldLv3) {
				dx = dx / 2;
				dy = dy / 2;
				Ball ball = this;
				getWorld().setOnMouseMoved(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						ball.setX(event.getX());
						ball.setY(event.getY());
					}
				});
			}
		}
		

		BallWorld bw = (BallWorld) getWorld();

		if (bw.isPlaying()) {
			double tempDx = dx; //used to adjust the speed in hard difficulty without changing the variables 
			double tempDy = dy;
			if (!Game.isEasyDifficulty) {
				tempDx *= (1 + Math.random() / 1.5);
				tempDy *= (1 + Math.random() / 1.5);
			}

			if (bw.isPressed(KeyCode.SHIFT))
				move(1.4 * tempDx, 1.4 * tempDy);
			else {
				move(tempDx, tempDy);
			}
			if (this.getY() + this.getHeight() / 2 >= getWorld().getHeight()) {
				onHitFloor();
			}
			if(this.getY() - this.getHeight()/2 < 0)
				this.setY(0.0);
			
			
		} else { // sets location of ball to where the Paddle of the BallWorld is
			Paddle paddleOfThis = bw.getPaddle();

			setX(paddleOfThis.getX() + paddleOfThis.getWidth() / 2 - this.getWidth() / 2);
			setY(paddleOfThis.getY() - paddleOfThis.getHeight() / 2 - this.getHeight() / 2);
			if (getWorld().isPressed(KeyCode.SPACE)) {
				for (Node node : bw.getChildren()) {
					if (node instanceof Label)
						bw.getChildren().remove(node);
				}
				bw.setPlaying(true);
			}
		}

		Paddle p = this.getOneIntersectingObject(Paddle.class);
		
		if (!bounce) {
			if (p != null) {
				bounce = true;

				double leftBound = p.getX();
				double rightBound = p.getX() + p.getWidth();
				double x = this.getX() + this.getWidth() / 2;
				if(p.isMoving() == 0 && x >= leftBound && x <= rightBound) {//paddle not moving, within bounds
					dy = -dy;
				} else if (x > (leftBound + (p.getWidth() / 3)) && x < (rightBound - (p.getWidth() / 3))) {//middle
					dy = -dy;
				} else if (x > leftBound && x <= (leftBound + p.getWidth() / 3)) { //left 1/3
					dx = -dx;
					if(p.isMoving() == -1)//moving left --> bounce left
						dx = -Math.abs(dx);
					dy = -dy;
				} else if (x >= (rightBound - p.getWidth() / 3) && x < rightBound) {//right 1/3
					dx = -dx;
					if(p.isMoving() == 1)//moving right --> bounce right
						dx = Math.abs(dx);
					dy = -dy;
				} else if (x <= leftBound) {//really left
					dy = -Math.abs(dy);
					dx = 2 * dy;
				} else if (x >= rightBound) {//really right
					dy = -Math.abs(dy);
					dx = -2 * dy;
				}
			} else {
				bounce = false;
			}
		}
		if (getY() + getHeight() / 2 >= getWorld().getHeight() || getY() - getHeight() / 2 <= 0) {
			dy = -dy;
		} else if (getX() + getWidth() / 2 >= getWorld().getWidth()) {
			dx = -Math.abs(dy);
		} else if (getX() - getWidth() / 2 <= 0) {
			dx = Math.abs(dy);
		}

		boolean doesHit = false;

		Brick br = this.getOneIntersectingObject(Brick.class);
		if (br != null) {
			if (getX() >= br.getX() && getX() <= br.getX() + br.getWidth()) { // Ball x-coord is between the Brick's
																				// left and right edges
				doesHit = true;
				dy = -dy;
			} else if (getY() >= br.getY() && getY() <= br.getY() + br.getHeight()) { // Ball y-coord is between the
																						// Brick's top and bottom edges
				doesHit = true;
				dx = -dx;
			} else { // reverse both cuz hit corner
				doesHit = true;
				dx = -dx;
				dy = -dy;
			}
			br.onHit(this);
			br.getWorld().remove(br);
		}

		if (doesHit) {
			BallWorld world = (BallWorld) getWorld();
			int points = (Game.isEasyDifficulty) ? (br.getPointValue()) : (2 * br.getPointValue());
			world.setScore(world.getScore() + points); ///////
			doesHit = false;
		}

		if (p != null) {
			bounce = true;
		} else {
			bounce = false;
		}
	}

	// if no shield and level 1, lose 1000 pts; if not level 1 and no shield,
	// game over
	public void onHitFloor() {
		if (!hasShield) {
			Scene level = getWorld().getParent().getScene();
			boolean whenToNotGameOver = (level == Game.level1Scene) && Game.isEasyDifficulty;

			if (whenToNotGameOver) {
				BallWorld world = (BallWorld) getWorld();
				world.setScore(0);
			} else {
				if (((BallWorld) getWorld()).isPlaying()) {
					((BallWorld) getWorld()).onGameOver();
				}
			}
		} else {
			hasShield = false;
		}
	}
	
	
	

	// getters and setters

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public boolean hasShield() {
		return hasShield;
	}

	public void setShield(boolean hasShield) {
		this.hasShield = hasShield;

		String path;
		if (hasShield)
			path = getClass().getClassLoader().getResource("ball2.png").toString();
		else
			path = getClass().getClassLoader().getResource("ball.png").toString();

		Image ballImg = new Image(path);
		setImage(ballImg);
	}

}
