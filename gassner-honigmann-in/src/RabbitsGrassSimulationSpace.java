import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationSpace {
	private Object2DGrid grassSpace;
	private Object2DGrid rabbitSpace;

	private static final int GRASS_ENERGY = 4; // assuming eating grass gives
												// 10 energy

	public RabbitsGrassSimulationSpace(int gridSize) {
		grassSpace = new Object2DGrid(gridSize, gridSize);
		rabbitSpace = new Object2DGrid(gridSize, gridSize);

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				grassSpace.putObjectAt(i, j, new Integer(0));
			}
		}
	}

	public int eatGrassAt(int x, int y) {
		boolean grass = isThereGrassAt(x, y);
		grassSpace.putObjectAt(x, y, new Integer(0));
		if (grass) {
			return GRASS_ENERGY;
		} else {
			return 0;
		}
	}

	public void spreadGrass() { //changing this function to only place one tile of grass at a time, that way we can make sure we won't get trapped in a loop
		//precondition: there must be space for a new tile of grass
		boolean grassPlaced = false;
		while (!grassPlaced) {
			// Choose coordinates
			int x = (int) (Math.random() * (grassSpace.getSizeX()));
			int y = (int) (Math.random() * (grassSpace.getSizeY()));

			// Get the value of the object at those coordinates
			boolean thereIsGrass = isThereGrassAt(x, y);
			// A tile having grass is represented by the integer 1
			if (!thereIsGrass) {
				grassSpace.putObjectAt(x, y, new Integer(1));
				grassPlaced = true;
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

	public boolean isCellOccupied(int x, int y) {
		boolean retVal = false;
		if (rabbitSpace.getObjectAt(x, y) != null)
			retVal = true;
		return retVal;
	}

	/**
	 * Randomly add the RabbitsGrassSimulationAgent bunny somewhere in the
	 * rabbitSpace
	 * 
	 * @param bunny
	 *            RabbitsGrassSimulationAgent to be added to the rabbitSpace
	 * @return true... 
	 */
	public boolean addRabbit(RabbitsGrassSimulationAgent bunny) {
		boolean rabbitPlaced = false;
		
		while ((rabbitPlaced == false)) {
			int x = (int) (Math.random() * rabbitSpace.getSizeX());
			int y = (int) (Math.random() * rabbitSpace.getSizeY());

			if (isCellOccupied(x, y) == false) {
				rabbitSpace.putObjectAt(x, y, bunny);
				bunny.setXY(x, y);
				bunny.setRabbitsGrassSimulationSpace(this);
				rabbitPlaced = true;
			}
		}

		return rabbitPlaced;
	}

	public void removeRabbitAt(int x, int y) {
		rabbitSpace.putObjectAt(x, y, null);
	}
	
	public boolean moveRabbitAt(int x, int y, int newX, int newY){
		boolean hasMoved = false;
		if(!isCellOccupied(newX,newY)){
			RabbitsGrassSimulationAgent rgs = (RabbitsGrassSimulationAgent)rabbitSpace.getObjectAt(x, y);
			removeRabbitAt(x,y);
			rgs.setXY(newX,newY);
			rabbitSpace.putObjectAt(newX, newY, rgs);
			hasMoved = true;
		}
		return hasMoved;
	}
	
}
