import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

	private int xPosition;
	private int yPosition;
	private int energy;
	private static int IDNumber = 0;
	private int ID;
	
	public RabbitsGrassSimulationAgent(int startEnergy)
	{
		xPosition = -1;
		yPosition = -1;
		energy = startEnergy;
		
		IDNumber++;
		ID = IDNumber;
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
	
	public String getID()
	{
		return "A-"+ ID;
	}
	
	public int getEnergy()
	{
		return energy;
	}
	
	public void setEnergy(int newEnergy)
	{
		energy = newEnergy;
	}

	public void report(){
		System.out.println(	getID() + " at (" + xPosition + "," 
							+ yPosition + ") has " + getEnergy() + "");
	}
		
	public void draw(SimGraphics G){
		G.drawFastRoundRect(Color.gray);
	}
	
	 public void step() {
		 energy--;
	 }
}