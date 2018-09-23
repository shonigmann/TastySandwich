import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {

	private int xPosition;
	private int yPosition;

	public void draw(SimGraphics arg0) {
		// TODO Auto-generated method stub
		
	}

	public int getX() {
		return xPosition;
	}
	
	public void setX(int newXPosition) {
		xPosition = newXPosition;
	}

	public int getY() {
		return yPosition;
	}
	
	public void setY(int newYPosition) {
		yPosition = newYPosition;
	}

}