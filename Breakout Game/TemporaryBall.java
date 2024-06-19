import javafx.scene.image.Image;

public class TemporaryBall extends Ball {

	public TemporaryBall() {
		super();
		
		//change the image file
		String path = getClass().getClassLoader().getResource("ball3.png").toString();
		Image ballImg = new Image(path); 
		setImage(ballImg);
	}


	public TemporaryBall(double dx, double dy) {
		this();
		setDx(dx);
		setDy(dy);
	}

	
	@Override
	public void onHitFloor() { 
		getWorld().remove(this);
	}
	
	
}
