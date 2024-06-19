import javafx.scene.image.Image;

public class AddBallBrick extends Brick {

	public AddBallBrick() {
		super();
		//make this brick a different image
		String path = getClass().getClassLoader().getResource("brick4.png").toString();
		Image brickImg = new Image(path);
		setImage(brickImg);
		setFitWidth(32);
		setFitHeight(16);
	}
	
	public AddBallBrick(int points) {
		super(points);
		String path = getClass().getClassLoader().getResource("brick4.png").toString();
		Image brickImg = new Image(path);
		setImage(brickImg);
		setFitWidth(32);
		setFitHeight(16);
	}
	
	@Override
	public void act(long now) {}
	
	@Override
	public void onHit(Ball b) {
		BallWorld bw = (BallWorld) getWorld();
		
		TemporaryBall tb = new TemporaryBall(-b.getDx(), b.getDy());
		tb.setX(b.getX());
		tb.setY(b.getY());

		bw.add(tb);
	}
}
