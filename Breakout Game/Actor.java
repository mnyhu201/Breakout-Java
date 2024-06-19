
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
public abstract class Actor extends ImageView{
	
	public void act(long now){};
	
	public void move(double dx, double dy){
		this.setX(getX() + dx);
		this.setY(getY() + dy);
	}
	public World getWorld(){
		return (World)this.getParent();
	}
	public double getHeight(){
		return this.getBoundsInParent().getHeight();
	}
	public double getWidth(){
		return this.getBoundsInParent().getWidth();
	}
	public <A extends Actor> List<A> getIntersectingObjects(java.lang.Class<A> cls){
		List arr = new ArrayList();
		List objects = getWorld().getObjects(cls);
		for(int i = 0; i < objects.size(); i++){
			if(objects.get(i) != this && ((Node) objects.get(i)).intersects(this.getBoundsInParent())){
				arr.add(objects.get(i));
			}
		}
		return arr;
	}
	public <A extends Actor> A getOneIntersectingObject(java.lang.Class<A> cls){
		List a = this.getIntersectingObjects(cls);
		if(a.size() > 0){
			return (A) a.get(0);
		}
		return null;
	}
 }