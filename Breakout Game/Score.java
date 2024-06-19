
import javafx.scene.text.Font;
import javafx.scene.text.Text; 
public class Score extends Text{
	private int scoreValue; 
	
	public Score() {
		scoreValue = 0; 
		this.setFont(new Font(30));
		this.updateDisplay(); 
		
	}
	
	public Score(int score) {
		this();
		scoreValue = score;
	}
	
	public void updateDisplay() {
		String val = String.valueOf(scoreValue); 
		this.setText(val);
	}
	
	public int getScore() {
		return scoreValue; 
	}
	
	public void setScore(int score) {
		scoreValue = score;
		String value = String.valueOf(scoreValue); 
		this.setText(value);
	}
}
