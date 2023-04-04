public class Soldier{
	
	private int id ;//1 or -1 stands for you and your rival's soldiers
	private int positionX;
	private int positionY;
	private String state;//dead or alive
	private String name;//soldier's name
	
	//the game board
	public static int[][] isEmpty =     {{0 , 0 , 0 , 0},
										 {0 , 0 , 0 , 0},
										 {0 , 0 , 0 , 0},
										 {0 , 0 , 0 , 0}};
	
	public Soldier(int ID , int x , int y) {
		this.id = ID;
		this.name = ID+"";
		this.positionX = x;
		this.positionY = y;
		this.state = "alive";
		if(x>=0 && y>=0)
			isEmpty[y][x] = ID;
		else
			this.state = "dead";
	}
	
	//moves the soldier
	public void Move() {
		if(this.state.equals("alive"))
		isEmpty[this.positionY][this.positionX] = 0;
		
		this.positionY += this.id;
		
		if(this.state.equals("alive"))
		isEmpty[this.positionY][this.positionX] = this.id;
		
	}
	
	//checks if this soldier can move
	public boolean CanMove() {
		if(this.state.equals("dead"))
			return false;
		if(this.id == 1 && this.positionY == 3)
			return false;
		
		if(this.id == -1 && this.positionY == 0)
			return false;
		
		if(this.id == 1) {
			return (isEmpty[this.positionY + ((this.positionY != 3)? id : 0)][this.positionX] == 0)? true : false;
		}
		
		//this line contains problems
		return (isEmpty[this.positionY + ((this.positionY != 0)? id : 0)][this.positionX] == 0)? true : false;
	}
	
	//checks if this soldier can kill
	public boolean CanKill() {
		if(this.state.equals("dead")) {
			return false;
		}
		if(this.id == 1 && this.positionY == 3)
			return false;
		
		if(this.id == -1 && this.positionY == 0)
			return false;
		
		if(this.id == 1) {
			if(this.positionY != 3) {
				switch(this.positionX) {
				case 0:
					if(isEmpty[this.positionY + this.id][1] == -1*this.id)
						return true;
					break;
				case 1:
					if(isEmpty[this.positionY + this.id][0] == -1*this.id)
						return true;
					if(isEmpty[this.positionY + this.id][2] == -1*this.id)
						return true;
					break;
				case 2:
					if(isEmpty[this.positionY + this.id][1] == -1*this.id)
						return true;
					if(isEmpty[this.positionY + this.id][3] == -1*this.id)
						return true;
					break;
				case 3:
					if(isEmpty[this.positionY + this.id][2] == -1*this.id)
						return true;
					break;
				}
			}
		}else {
			if(this.positionY != 0 && this.id == -1) {
				switch(this.positionX) {
				case 0:
					if(isEmpty[this.positionY + this.id][1] == -1*this.id)
						return true;
					break;
				case 1:
					if(isEmpty[this.positionY + this.id][0] == -1*this.id)
						return true;
					if(isEmpty[this.positionY + this.id][2] == -1*this.id)
						return true;
					break;
				case 2:
					if(isEmpty[this.positionY + this.id][1] == -1*this.id)
						return true;
					if(isEmpty[this.positionY + this.id][3] == -1*this.id)
						return true;
					break;
				case 3:
					if(isEmpty[this.positionY + this.id][2] == -1*this.id)
						return true;
					break;
				}
			}
		}		
		
		return false;
	}
	
	//kills another soldier
	public void Kill(Soldier Rival) {
		this.positionX = Rival.positionX;
		this.positionY = Rival.positionY;
		isEmpty[this.positionY][this.positionX] = this.id;
		Rival.Die();
	}
	
	//R.I.P
	public void Die() {
		this.state = "dead";
		this.positionX = -1;
		this.positionY = -1;
	}
	
	//returns 1 or -1
	public int GetID() {
		return this.id;
	}
	
	public int GetY() {
		return this.positionY;
	}
	
	public int GetX() {
		return this.positionX;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setState(String s) {
		this.state = s;
	}
	
	public void updateName() {
		if(this.id == -1) {
			this.name = "(-1) ";
			return;
		}
		if(this.CanKill() || this.CanMove())
			this.name = "  1  ";
		else
			this.name = "( 1)  ";
	}
	
	public String getName() {
		return this.name;
	}
}
