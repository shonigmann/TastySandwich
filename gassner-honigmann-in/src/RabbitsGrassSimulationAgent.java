import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

	private int xPosition;
	private int yPosition;
	private int energy;
	private int vX;
	private int vY;

	private static int IDNumber = 0;
	private int ID;
	private RabbitsGrassSimulationSpace rgsSpace;

	public RabbitsGrassSimulationAgent(int startEnergy) {
		xPosition = -1;
		yPosition = -1;
		energy = startEnergy;
		setVxVy();
		IDNumber++;
		ID = IDNumber;
	}

	private int setVxVy() {
		int d = 0;
		vX = 0;
		vY = 0;
		d = (int) Math.floor(Math.random() * 4);
		switch (d) {
		case 0:
			vX = 1;
			vY = 0;
			break;
		case 1:
			vX = -1;
			vY = 0;
			break;
		case 2:
			vX = 0;
			vY = -1;
			break;
		case 3:
			vX = 0;
			vY = 1;
			break;
		default:
			vX = 0;
			vY = 0;
			break;
		}
		
		return d;
	}

	public int getX() {
		return xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public void setXY(int newXPosition, int newYPosition) {
		xPosition = newXPosition;
		yPosition = newYPosition;
	}

	public void setRabbitsGrassSimulationSpace(RabbitsGrassSimulationSpace rgs) {
		rgsSpace = rgs;
	}

	public String getID() {
		return "A-" + ID;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int newEnergy) {
		energy = newEnergy;
	}

	public void report() {
		System.out.println(getID() + " at (" + xPosition + "," + yPosition + ") has " + getEnergy() + "");
	}

	public void draw(SimGraphics G) {
		G.drawFastRoundRect(Color.gray);
	}

	public void step() {
		
		tryMove();
		energy--;
		rgsSpace.eatGrassAt(xPosition, yPosition);

	}
	
	private boolean tryMove(){

		boolean hasMoved = false; 
		
		ArrayList<Integer> directions = new ArrayList<Integer>();
		directions.add(new Integer(0));
		directions.add(new Integer(1));
		directions.add(new Integer(2));
		directions.add(new Integer(3));
		
		Object2DGrid grid = rgsSpace.getCurrentRabbitSpace();
		
		int newX;
		int newY;
		
		while(!hasMoved && directions.size()>0){
			
			int d = setVxVy();
			int t = directions.indexOf(new Integer(d));
			
			while(t<0){
				 d = setVxVy();
				 t = directions.indexOf(new Integer(d));					
			}
			newX = xPosition + vX; //tentative position
			newY = yPosition+ vY;
			newX = (newX + grid.getSizeX()) % grid.getSizeX(); //torus
			newY = (newY + grid.getSizeY()) % grid.getSizeY();
			
			hasMoved = rgsSpace.moveRabbitAt(xPosition, yPosition, newX, newY);
			
			directions.remove(t);
									
		}
				
		return hasMoved;
	}
}