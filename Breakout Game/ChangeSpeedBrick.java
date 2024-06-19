import javafx.scene.image.Image;

public class ChangeSpeedBrick extends Brick {
	private double xChange;
	private double yChange;
	
	public ChangeSpeedBrick() {
		super();
		//make this brick a different image
		String path = getClass().getClassLoader().getResource("brick2.png").toString();
		Image brickImg = new Image(path);
		setImage(brickImg);
	}
	
	public ChangeSpeedBrick(int points) {
		super(points);
		String path = getClass().getClassLoader().getResource("brick2.png").toString();
		Image brickImg = new Image(path);
		setImage(brickImg);
		xChange = 2;
		yChange = 2;
	}
	
	public ChangeSpeedBrick(double x, double y) {
		this();
		this.xChange = x;
		this.yChange = y;
	}
	
	
	public ChangeSpeedBrick(int points, double x, double y) {
		this(points);
		this.xChange = x;
		this.yChange = y;
	}
	
	@Override
	public void act(long now) {
		super.act(now);
		Ball b = getOneIntersectingObject(Ball.class);
		
		if(b != null) {
			b.setDy(b.getDy() + yChange);
			b.setDx(b.getDx() + xChange);
		}
	}
	
}
