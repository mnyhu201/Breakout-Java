import javafx.scene.image.Image;

public class Brick extends Actor{
	private int pointValue;
	
	public Brick() {
		String path = getClass().getClassLoader().getResource("brick.png").toString();
		Image brickImg = new Image(path);
		setImage(brickImg);
		pointValue = 10; 
	}
	
	public Brick(int points) {
		this();
		pointValue = points;
	}
	
	@Override
	public void act(long now) {
		
	}

	public int getPointValue() {
		return pointValue;
	}

	public void setPointValue(int pointValue) {
		this.pointValue = pointValue;
	}
	
	public void onHit(Ball b) {} //method to be overridden by subclasses if they have special feature
	
}
