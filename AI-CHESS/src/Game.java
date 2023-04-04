import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Game {
	public static int maxTreeH = 9; //higher it goes, harder it will be(max search tree height)
	
	public static boolean GameCanRun = false;
	public static int turn = 1; //changes from 1 to -1 or -1 to 1 and shows which player's turn it is
	
	//for reading the file
	public static String fileName;
	public static BufferedReader br;
	public static String line = "";
	
	public static Soldier[] soldiers = new Soldier[8]; //pawns
	public static int soldiersCount = 0; //count of pawns in the Input
	public static Movement nextMove; //next move of your rival
	
	public static int[] preX = new int[8];
	public static int[] preY = new int[8];
	
	private static int yp = 0; //your point for mini-max state
	private static int rp = 0; //your rival's point for mini-max state
	
	public static String output = "";
	public static String outputMove = "";
	
	
	public static void main(String[] args) {
		ReadFile();
		int indx = -1;
		String s = "";
		nextMove = new Movement();
		
		for(int i=0 ; i<8 ; i++) {
			preX[i] = -1;
			preY[i] = -1;
		}
		
		int cr = 0; //secret var
		for(int i=0 ; i<soldiersCount ; i++) {
			
			if(soldiers[i].GetID() == turn && (soldiers[i].CanMove() || soldiers[i].CanKill())) {
				cr++;
			}
		}
		if(cr != 0) {
			GameCanRun = true;
		}
		while(GameCanRun) {
				
			for(int i=0 ; i<Game.soldiersCount ; i++) {
				preX[i] = soldiers[i].GetX();
				preY[i] = soldiers[i].GetY();
			}
			for(int i=0 ; i<Game.soldiersCount ; i++)
				soldiers[i].updateName();
			String input;
			if(turn == 1) {
				int index = -1;
				boolean canMove = false,canKill = false;
				
				String[] inputs;
				boolean selectedPosValid = false;
				
				//gets soldier position
				while (!selectedPosValid) {
					input = JOptionPane.showInputDialog(TheInputText() + "\n\n Enter your soldier position (with this format --> x,y ):");
					inputs = input.split(",");
					if(Integer.parseInt(inputs[0]) < 0 || Integer.parseInt(inputs[0]) > 3) {
						JOptionPane.showMessageDialog(null, "Inserted x is not valid ");
						continue;
					}
					if(Integer.parseInt(inputs[1]) < 0 || Integer.parseInt(inputs[1]) > 3) {
						JOptionPane.showMessageDialog(null, "Inserted y is not valid ");
						continue;
					}
					if(Soldier.isEmpty[Integer.parseInt(inputs[1])][Integer.parseInt(inputs[0])] == 0) {
						JOptionPane.showMessageDialog(null, "What? \nThis is not even a soldier! \nTry again.");
						continue;
					}
					for(int c=0 ; c<soldiersCount ; c++) {
						if(soldiers[c].GetX() == Integer.parseInt(inputs[0]) && soldiers[c].GetY() == Integer.parseInt(inputs[1])) {
							index = c;
							if(soldiers[c].GetID() != turn) {
								JOptionPane.showMessageDialog(null, "This soldier is not yours . Try again.");
								break;
							}
							if(!(soldiers[c].CanMove() || soldiers[c].CanKill())) {
								JOptionPane.showMessageDialog(null, "This soldier is not allowed to move . Try again.");
								break;
							}
							if(soldiers[c].CanMove()) {
								canMove = true;
							}
							if(soldiers[c].CanKill()) {
								canKill = true;
							}
							selectedPosValid = true;
							break;
						}
					}
				}//now we have our numbers
				if(soldiers[index].CanMove() && !soldiers[index].CanKill()) {
					soldiers[index].Move();
				}
				else if(!soldiers[index].CanMove() && soldiers[index].CanKill()) {
					int[] victims = new int[2];
					int victimCount = 0;
					if(soldiers[index].GetX() == 0) {
						for(int i=0 ; i<soldiersCount ; i++) {
							if(soldiers[i].GetX() == 1 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
								victims[victimCount] = i;
								victimCount++;
								break;
							}
						}
					}else if(soldiers[index].GetX() == 3) {
						for(int i=0 ; i<soldiersCount ; i++) {
							if(soldiers[i].GetX() == 2 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
								victims[victimCount] = i;
								victimCount++;
								break;
							}
						}
					}else {
						for(int i=0 ; i<soldiersCount ; i++) {
							if(soldiers[i].GetID() == 1)
								continue;
							if(soldiers[i].GetX() == soldiers[index].GetX()-1 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
								victims[victimCount] = i;
								victimCount++;
							}else if(soldiers[i].GetX() == soldiers[index].GetX()+1 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
								victims[victimCount] = i;
								victimCount++;
							}
						}
					}
					if(victimCount == 1) {
						soldiers[index].Kill(soldiers[victims[0]]);
					}else {
						int x = Integer.parseInt(JOptionPane.showInputDialog(TheInputText() + "\nSelect one of the following soldiers to kill :\n" + "1." + soldiers[victims[0]].GetX() + "," + soldiers[victims[0]].GetY() + "\n2." + soldiers[victims[1]].GetX() + "," + soldiers[victims[1]].GetY()));
						if(x == 1) {
							soldiers[index].Kill(soldiers[victims[0]]);
						}else {
							soldiers[index].Kill(soldiers[victims[1]]);
						}
					}
				}
				////////done until this part...
				else if(soldiers[index].CanMove() && soldiers[index].CanKill()) {
					input = JOptionPane.showInputDialog("You have two options for your soldier.\nEnter 1 to move or 2 to attack.");
					while(!(input.equals("1") || input.equals("2"))) {
						JOptionPane.showMessageDialog(null, "Entered command is not valid .\nTry Again !");
						input = JOptionPane.showInputDialog("Again you have two options for your soldier.\nEnter 1 to move or 2 to attack.");
					}
					
					if(input.equals("1")) {
						soldiers[index].Move();
					}else {
						int[] victims = new int[2];
						int victimCount = 0;
						if(soldiers[index].GetX() == 0) {
							for(int i=0 ; i<soldiersCount ; i++) {
								if(soldiers[i].GetX() == 1 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
									victims[victimCount] = i;
									victimCount++;
									break;
								}
							}
						}else if(soldiers[index].GetX() == 3) {
							for(int i=0 ; i<soldiersCount ; i++) {
								if(soldiers[i].GetX() == 2 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
									victims[victimCount] = i;
									victimCount++;
									break;
								}
							}
						}else {
							for(int i=0 ; i<soldiersCount ; i++) {
								if(soldiers[i].GetID() == 1)
									continue;
								if(soldiers[i].GetX() == soldiers[index].GetX()-1 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
									victims[victimCount] = i;
									victimCount++;
								}else if(soldiers[i].GetX() == soldiers[index].GetX()+1 && soldiers[i].GetY() == soldiers[index].GetY()+1) {
									victims[victimCount] = i;
									victimCount++;
								}
							}
						}
						if(victimCount == 1) {
							soldiers[index].Kill(soldiers[victims[0]]);
						}else {
							int x = Integer.parseInt(JOptionPane.showInputDialog(TheInputText() + "\nSelect one of the following soldiers to kill :\n" + "1." + soldiers[victims[0]].GetX() + "," + soldiers[victims[0]].GetY() + "\n2." + soldiers[victims[1]].GetX() + "," + soldiers[victims[1]].GetY()));
							if(x == 1) {
								soldiers[index].Kill(soldiers[victims[0]]);
							}else {
								soldiers[index].Kill(soldiers[victims[1]]);
							}
						}
					}
				}
				/////////////////////////-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/
			}else {///////////////////////////////////////////AIAIAIAIAIAI
				ArrayList<State> moves = new ArrayList<State>();
				for(int i=0 ; i<Game.soldiersCount ; i++) {//setting up moves
					if(soldiers[i].GetID() == -1) {
						if(soldiers[i].CanMove()) {
							moves.add(new State(1,soldiers,new Movement(i,"move",-1)));
						}
						if(soldiers[i].CanKill()) {
							if(soldiers[i].GetX() == 0) {
								int x = -1;
								for(int k=0 ; k<Game.soldiersCount ; k++) {
									if(soldiers[k].GetY() == soldiers[i].GetY()-1 && soldiers[k].GetX() == 1) {
										x = k;
										break;
									}
								}
								if(x != -1)
								moves.add(new State(1 , soldiers , new Movement(i,"kill",x)));
							}else if(soldiers[i].GetX() == 3) {
								int x = -1;
								for(int k=0 ; k<Game.soldiersCount ; k++) {
									if(soldiers[k].GetY() == soldiers[i].GetY()-1 && soldiers[k].GetX() == 2) {
										x = k;
										break;
									}
								}
								if(x != -1)
								moves.add(new State(1 , soldiers , new Movement(i,"kill",x)));
							}else {//has an enemy in the middle
								for(int x=0 ; x< Game.soldiersCount ; x++) {
									if(soldiers[x].GetY() == soldiers[i].GetY()-1) {
										if(soldiers[x].GetX() == soldiers[i].GetX()+1) {
											moves.add(new State(1 , soldiers , new Movement(i,"kill",x)));
										}
										else if(soldiers[x].GetX() == soldiers[i].GetX()-1) {
											moves.add(new State(1 , soldiers , new Movement(i,"kill",x)));
										}
									}
								}
							}
						}
					}
				}
				if(moves.size() == 1) {//if there is only one move
					nextMove.action = moves.get(0).move.action;
					nextMove.soldierIndex = moves.get(0).move.soldierIndex;
					nextMove.targetIndex = moves.get(0).move.targetIndex;
					
					if(nextMove.action.equals("move")) {
						soldiers[nextMove.soldierIndex].Move();
					}else {
						soldiers[nextMove.soldierIndex].Kill(soldiers[nextMove.targetIndex]);
					}
				}
				else {
					int min = 100;
					for(int i=0 ; i<moves.size() ; i++) {
						if(moves.get(i).value < min) {
							min = moves.get(i).value;
						}
					}
					for(int i=0 ; i<moves.size() ; i++) {
						if(moves.get(i).value == min) {
							nextMove.action = moves.get(i).move.action;
							nextMove.soldierIndex = moves.get(i).move.soldierIndex;
							nextMove.targetIndex = moves.get(i).move.targetIndex;
							break;
						}
					}
					if(nextMove.action.equals("move")) {
						s = nextMove.action;
						indx = nextMove.soldierIndex;
						soldiers[nextMove.soldierIndex].Move();

					}else {
						s = nextMove.action;
						indx = nextMove.soldierIndex;
						soldiers[nextMove.soldierIndex].Kill(soldiers[nextMove.targetIndex]);

					}
					
				}
				outputMove = ("(" + preX[nextMove.soldierIndex] + "," + preY[nextMove.soldierIndex] + ")-->(" + soldiers[nextMove.soldierIndex].GetX() + "," + soldiers[nextMove.soldierIndex].GetY() + ")");
				JOptionPane.showMessageDialog(null, outputMove);
				
			}
			/////////////////////////-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/-/

			//////////////////////////////////////////
			//reset isEmpty
			ResetIsEmpty(soldiers);
			SetupOutput(indx , s);
			turn *= -1;
			cr = 0;
			for(int i=0 ; i<soldiersCount ; i++) {
				if(soldiers[i].GetID() == turn && (soldiers[i].CanMove() || soldiers[i].CanKill())) {
					cr++;
				}
			}
			if(cr != 0) {
				GameCanRun = true;
			}else {
				GameCanRun = false;
			}
			///////////////////////////////////////////
		}
		calculatePoints();
		JOptionPane.showMessageDialog(null, "The game is over :\n\n" + TheInputText() + "\nAnd according to these points:\n" + "You : " + yp + "\nRival : " + rp + "\nThe winner is " + ((yp > rp)? "you!" : ((rp > yp)? "your rival!" : "nobody!")));
		WriteFile();
		System.out.println("END");
	}

	//reads our file
	public static void ReadFile() {
		fileName = JOptionPane.showInputDialog("Enter file name with address and without format(.csv) :") + ".csv";
		try {
			br = new BufferedReader(new FileReader(fileName));
			
			int j = 0;
			
			while((line = br.readLine()) != null) {
				String[] values = line.split(";");
				for(int i=0 ; i<4 ; i++) {
					if(!values[i].contains("0")) {
						soldiers[soldiersCount] = new Soldier(Integer.parseInt(values[i]) , i , j);
						soldiersCount ++;
					}
				}
				j++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void SetupOutput(int indx , String state) {
		for(int i=0 ; i<4 ; i++) {
			for(int j=0 ; j<4 ; j++) {
				output += (Soldier.isEmpty[i][j] + ";");
			}
			output += "\n";
		}
		output += (outputMove + "\n");
	}
	//not written
	public static void WriteFile() {
		
		try {
			FileWriter csWriter = new FileWriter("res.csv");
			csWriter.append(output);
			csWriter.flush();
			csWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//just returns a string that shows the current state of the game
	public static String TheInputText() {
		int[][] b = {{0,0,0,0},
					 {0,0,0,0},
					 {0,0,0,0},
					 {0,0,0,0}};
		for(int i=0 ; i<Game.soldiersCount ; i++) {
			if(soldiers[i].getState().equals("alive")) {
				b[soldiers[i].GetY()][soldiers[i].GetX()] = soldiers[i].GetID();
			}
		}
		
		String State = "";
		
		for(int i = 0 ; i<4 ; i++) {
			for (int k = 0 ; k<4 ; k++) {
				if(b[i][k] == 0) {
					State += "  0  ";
				}else if(b[i][k] == -1)
					State += "(-1) ";
				else {
					for(int z=0 ; z<Game.soldiersCount ; z++) {
						if(soldiers[z].GetX() == k && soldiers[z].GetY() == i) {
							State += soldiers[z].getName();
							break;
						}
					}
				}
			}
			State += "\n";
		}
		
		return State;
	}
	
	//resets isEmpty array in 'Soldier' class
	public static void ResetIsEmpty(Soldier[] s) {
		for(int i=0 ; i<4 ; i++) {
			for(int j=0 ; j<4 ; j++) {
				Soldier.isEmpty[i][j] = 0;
			}
		}
		for(int i=0 ; i<Game.soldiersCount ; i++) {
			if(s[i].getState().equals("alive")) {
				Soldier.isEmpty[s[i].GetY()][s[i].GetX()] = s[i].GetID();
			}
		}
	}

	//calculates points of each team
	private static void calculatePoints() {
		for(int i=0 ; i<4 ; i++) {
			if(Soldier.isEmpty[0][i] == -1)
				rp++;
			if(Soldier.isEmpty[3][i] == 1)
				yp++;
		}
	}
}





