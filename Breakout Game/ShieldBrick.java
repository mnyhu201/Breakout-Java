import javafx.scene.image.Image;


/** This class is a Brick that gives the Ball a shield when it collides with it.
 * 	Shields are not stackable, and they last indefinitely until the Ball hits the floor(shield 
 * 	will be removed and no "1000 pt score reduction" or game over. 
 * */
public class ShieldBrick extends Brick {
	public ShieldBrick() {
		super();
		// make this brick a different image
		String path = getClass().getClassLoader().getResource("brick3.png").toString();
		Image brickImg = new Image(path);
		setImage(brickImg);
		setFitWidth(32);
		setFitHeight(16);

	}

	public ShieldBrick(int points) {
		this();
		setPointValue(points);
	}
	
	
	@Override
	public void act(long now) {}
	
	@Override 
	public void onHit(Ball b) {
		b.setShield(true);
		System.out.println("shield activated");
	}

}
