import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

public class Paddle extends Actor{
    private int isMoving;
    private double mouseX;
    private boolean mouseMoved;
    
    public Paddle(){
        super();
		String path = getClass().getClassLoader().getResource("paddle.png").toString();
		Image paddleImg = new Image(path); //maybe path?
		setImage(paddleImg);
        isMoving = 0;
    }

    @Override
    public void act(long now){

    	BallWorld bw = (BallWorld) getWorld();
    	
    	if(!bw.isGameOver() && mouseMoved) {
    		double oldX = getX();
			setX(mouseX - getWidth() / 2);
			if(getX() > oldX)
				isMoving = 1;
			else if(getX() < oldX)
				isMoving = -1;
			else {
				isMoving = 0;
				mouseMoved = false;
			}
			
			if (getX() <= 0) {
				setX(0);
			}
			if (getX() + getWidth() >= bw.getWidth()) {
				setX(bw.getWidth() - getWidth());
			}
		}
    	
    	
    	bw.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseX = event.getX();
				mouseMoved = true;
			}
		});
//    	
    	if(!bw.isGameOver() && !mouseMoved) {
			if (getWorld().isPressed(KeyCode.LEFT)) {
				this.setX(this.getX() - 5);
				isMoving = -1;
			} else if (getWorld().isPressed(KeyCode.RIGHT)) {
				this.setX(this.getX() + 5);
				isMoving = 1;
			}
			if (this.getX() <= 0) {
				this.setX(0);
			}
			if (this.getX() + this.getWidth() >= getWorld().getWidth()) {
				this.setX(getWorld().getWidth() - this.getWidth());
			}
    	}
    }

    public int isMoving(){
        return isMoving;
    }
    public void setMoving(int i){
        isMoving = i;
    }
}
