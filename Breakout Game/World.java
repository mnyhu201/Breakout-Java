package src;

import java.util.HashSet;
import java.util.Iterator;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public abstract class World extends Pane{
    
	private AnimationTimer timer;
	private HashSet<KeyCode> keysDown;
    public World(){
    	World world = this;
    	keysDown = new HashSet<KeyCode>();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now){
            	world.act(now);
            	
            	//calling act() on the Actors in the World
                for(int i = 0; i < world.getChildren().size(); i++){
                	if(world.getChildren().get(i) instanceof Actor) {
                		((Actor) world.getChildren().get(i)).act(now);
                	}
                    
                }
            }
        };
    }
	
	public void add(Actor a){
		this.getChildren().add(a);
	}
	public void remove(Actor a){
		this.getChildren().remove(a);
	}
	public void start(){
		timer.start();
	}
	public void stop(){
		timer.stop();
	}
	public <A extends Actor> java.util.List<A>getObjects(java.lang.Class<A> cls){
		java.util.List<A> arr = new java.util.ArrayList<A>();
		for(int i = 0; i < this.getChildren().size(); i++){
			if(cls.isInstance(this.getChildren().get(i))){
        		arr.add((A) this.getChildren().get(i));
			}
        }
		return arr;
	}
	public void addKeyCode(KeyCode kc){
		keysDown.add(kc);
	}
	public void removeKeyCode(KeyCode kc){
		keysDown.remove(kc);
	}
	public boolean isPressed(KeyCode kc){
		Iterator i = keysDown.iterator();
		while(i.hasNext()){
			if(i.next().equals(kc)){
				return true;
			}
		}
		return false;
	}
    public void act(long now){};
}
