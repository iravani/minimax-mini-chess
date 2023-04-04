//this class defines a move for each state
public class Movement {
	public int soldierIndex = -1;//the soldier's index that is about to move
	public String action = "move"; //move or kill
	public int targetIndex;//takes a valid value only on kill action
	
	public Movement() {
		
	}
	public Movement(int i , String a , int t) {
		this.soldierIndex = i;
		this.action = a;
		this.targetIndex = t;
	}
}
