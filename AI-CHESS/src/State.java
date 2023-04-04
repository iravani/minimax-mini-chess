import java.util.ArrayList;

//AI uses this class to move
public class State {
	
	public ArrayList<State> subStates = new ArrayList<State>();//children of this node
	public int value = -100;//value of this node
	public int d = 0;//depth of this node
	public boolean containsChildren = true;//checks if this node contains children
	public Soldier[] soldiers = new Soldier[8];//this array is used to simulate game state in every node and calculate the node's value
	public Movement move ;//the movement of this node
	
	//initialize our states
	public State(int d , Soldier[] army , Movement move) {
		this.move = new Movement(move.soldierIndex , move.action, move.targetIndex);
		this.d = d;
		//setting up soldiers
		for(int i=0 ; i<Game.soldiersCount ; i++) {
			this.soldiers[i] = new Soldier(army[i].GetID() ,army[i].GetX() ,army[i].GetY());
			this.soldiers[i].setState(army[i].getState());
		}
		//makes a move
		Move(move);
		
		//setting up children
		setUpChildren();
		
		//setting up the value
		setValue();
	}
	
	//move made for this node
	public void Move(Movement m) {
		if(m.action.equals("move")) {
			this.soldiers[m.soldierIndex].Move();
			Game.ResetIsEmpty(this.soldiers);
		}else {
			if(m.targetIndex > -1)
			this.soldiers[m.soldierIndex].Kill(this.soldiers[m.targetIndex]);
			Game.ResetIsEmpty(this.soldiers);
		}
	}
	
	//setting up children
	public void setUpChildren() {
		if(d == Game.maxTreeH) {//if it's a leaf of our state tree
			this.containsChildren = false ; 
			return;
		}
		for(int i=0 ; i<Game.soldiersCount ; i++) {//setting up moves
			if(d%2 == 0) {
				if(soldiers[i].GetID() == -1) {
					if(soldiers[i].CanMove()) {
						subStates.add(new State(d+1,soldiers,new Movement(i,"move",-1)));
					}
					if(soldiers[i].CanKill()) {
						if(soldiers[i].GetX() == 0) {
							int x = -1;
							for(int k=0 ; k<Game.soldiersCount ; k++) {
								if(soldiers[k].GetID() == 1 && soldiers[k].GetY() == soldiers[i].GetY()-1 && soldiers[k].GetX() == 1) {
									x = k;
									break;
								}
							}
							if(x != -1)
							subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
						}else if(soldiers[i].GetX() == 3) {
							int x = -1;
							for(int k=0 ; k<Game.soldiersCount ; k++) {
								if(soldiers[k].GetID() == 1 && soldiers[k].GetY() == soldiers[i].GetY()-1 && soldiers[k].GetX() == 2) {
									x = k;
									break;
								}
							}
							subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
						}else {//has an enemy in the middle
							for(int x=0 ; x< Game.soldiersCount ; x++) {
								if(soldiers[x].GetY() == soldiers[i].GetY()-1 && soldiers[x].GetID() == 1) {
									if(soldiers[x].GetX() == soldiers[i].GetX()+1) {
										subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
									}
									else if(soldiers[x].GetX() == soldiers[i].GetX()-1) {
										subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
									}
								}
							}
						}
					}
				}
			}else {//for your soldiers
				if(soldiers[i].GetID() == 1) {
					if(soldiers[i].CanMove()) {
						subStates.add(new State(d+1,soldiers,new Movement(i,"move",-1)));
					}
					if(soldiers[i].CanKill()) {
						if(soldiers[i].GetX() == 0) {
							int x = -1;
							for(int k=0 ; k<Game.soldiersCount ; k++) {
								if(soldiers[k].GetID() == -1 && soldiers[k].GetY() == soldiers[i].GetY()+1 && soldiers[k].GetX() == 1) {
									x = k;
									break;
								}
							}
							subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
						}else if(soldiers[i].GetX() == 3) {
							int x = -1;
							for(int k=0 ; k<Game.soldiersCount ; k++) {
								if(soldiers[k].GetID() == -1 && soldiers[k].GetY() == soldiers[i].GetY()+1 && soldiers[k].GetX() == 2) {
									x = k;
									break;
								}
							}
							if(x != -1)
							subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
						}else {//has an enemy in the middle
							for(int x=0 ; x< Game.soldiersCount ; x++) {
								if(soldiers[x].GetY() == soldiers[i].GetY()+1 && soldiers[x].GetID() == -1) {
									if(soldiers[x].GetX() == soldiers[i].GetX()+1) {
										subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
									}
									else if(soldiers[x].GetX() == soldiers[i].GetX()-1) {
										subStates.add(new State(d+1 , soldiers , new Movement(i,"kill",x)));
									}
								}
							}
						}
					}
				}
			}
		}
		if(this.subStates.size() == 0)
			this.containsChildren = false;
		else
			this.containsChildren = true;
	}
	
	//calculates the value of each node in states tree
	public void setValue() {
		if(!this.containsChildren) {
			CalculateLeafValue(); 
			return;
		}
		
		if(this.d%2 == 0) {//max
			if(this.subStates.get(0).value != -100) {
				int max = -100;
				for(int i=0 ; i<this.subStates.size() ; i++) {
					if(this.subStates.get(i).value > max)
						max = this.subStates.get(i).value;
				}
				this.value = max;
				return;
			}else {
				for(int i=0 ; i<this.subStates.size() ; i++) {
					this.subStates.get(0).setValue();
				}
				setValue();
				return;
			}
		}else {			   //min
			if(this.subStates.get(0).value != -100) {
				int min = 100;
				for(int i=0 ; i<this.subStates.size() ; i++) {
					if(this.subStates.get(i).value < min)
						min = this.subStates.get(i).value;
				}
				this.value = min;
				return;
			}else {
				for(int i=0 ; i<this.subStates.size() ; i++) {
					this.subStates.get(0).setValue();
				}
				setValue();
				return;
			}
		}
	}
	
	//calculates leaf nodes of our states tree
	public void CalculateLeafValue() {
		int s = 0;//count of your soldiers
		for(int i=0; i<Game.soldiersCount ; i++) {
			if(soldiers[i].getState().equals("alive") && soldiers[i].GetID() == 1)
				s++;
		}
		
		int rs = 0;//count of your rival's soldiers
		for(int i=0; i<Game.soldiersCount ; i++) {
			if(soldiers[i].getState().equals("alive") && soldiers[i].GetID() == -1)
				rs++;
		}
		
		int w = 0;//count of your winner soldiers
		for(int i=0; i<Game.soldiersCount ; i++) {
			if(soldiers[i].getState().equals("alive") && soldiers[i].GetID() == 1 && soldiers[i].GetY() == 3)
				w++;
		}
		
		int rw = 0;//count of your rival's winner soldiers
		for(int i=0; i<Game.soldiersCount ; i++) {
			if(soldiers[i].getState().equals("alive") && soldiers[i].GetID() == -1 && soldiers[i].GetY() == 0)
				rw++;
		}
		
		if(d != Game.maxTreeH) {
			this.value = (w - rw);
			return;
		}
		
		//calculating the value
		this.value = (10*s+5*w-rs-2*rw);
	}
	
}

