import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationSpace {
	private Object2DGrid grassSpace;
	private Object2DGrid rabbitSpace;

	public RabbitsGrassSimulationSpace(int gridSize) {
		grassSpace = new Object2DGrid(gridSize, gridSize);
		rabbitSpace = new Object2DGrid(gridSize,gridSize);
		
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				grassSpace.putObjectAt(i, j, new Integer(0));
			}
		}
	}

	public void spreadGrass(int amountGrass) {
		int i = 0;
		while (i < amountGrass) {
			// Choose coordinates
			int x = (int) (Math.random() * (grassSpace.getSizeX()));
			int y = (int) (Math.random() * (grassSpace.getSizeY()));

			// Get the value of the object at those coordinates
			boolean thereIsGrass = isThereGrassAt(x, y);

			// A tile having grass is represented by the integer 1
			if (!thereIsGrass) {
				grassSpace.putObjectAt(x, y, new Integer(1));
				i++;
			}
		}
	}
	
	public boolean isThereGrassAt(int x, int y) {
		if (grassSpace.getObjectAt(x, y) != null) {
			return ((Integer) grassSpace.getObjectAt(x, y)).intValue() == 1;
		} else {
			return false;
		}
	}
	
	public Object2DGrid getCurrentGrassSpace() {
		return grassSpace;
	}	
	
	public Object2DGrid getCurrentRabbitSpace() {
		return rabbitSpace;
	}
	
	public boolean isCellOccupied(int x, int y)
	{
		boolean retVal = false;
		if(rabbitSpace.getObjectAt(x, y) != null) retVal = true;
		return retVal;
	}
	
	/**
	 * Randomly add the RabbitsGrassSimulationAgent bunny somewhere in the rabbitSpace
	 * @param bunny RabbitsGrassSimulationAgent to be added to the rabbitSpace
	 * @return true if the adding was successful, false otherwise
	 */
	/* TODO: Improve function so it'll only return false if the space is already full.
	 * In its current state, it could simply randomly select the same cell several times,
	 * then reach countLimit, and then exit the function returning false
	 */
	
	public boolean addRabbit(RabbitsGrassSimulationAgent bunny)
	{
		boolean retVal = false;
		int count = 0;
		int countLimit = rabbitSpace.getSizeX()*rabbitSpace.getSizeY();
		
		while((retVal == false) && (count < countLimit))
		{
			int x = (int) (Math.random()*rabbitSpace.getSizeX());
			int y = (int) (Math.random()*rabbitSpace.getSizeY());
			
			if (isCellOccupied(x,y)==false)
			{
				rabbitSpace.putObjectAt(x,y,bunny);
				bunny.setXY(x, y);
				bunny.setRabbitsGrassSimulationSpace(this);
				retVal = true;
			}
			count++;
		}
		
		return retVal;
	}
	
	public void removeRabbitAt(int x, int y) {
		rabbitSpace.putObjectAt(x, y, null);
	}
}
