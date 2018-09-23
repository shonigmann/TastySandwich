import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * 
 * @author
 */

public class RabbitsGrassSimulationSpace {
	private Object2DGrid rabbitsSpace;

	public RabbitsGrassSimulationSpace(int gridSize) {
		rabbitsSpace = new Object2DGrid(gridSize, gridSize);
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				rabbitsSpace.putObjectAt(i, j, new Integer(0));
			}
		}
	}

	public void spreadRabbits(int amountRabbits) {
		// todo
	}

	public void spreadGrass(int amountGrass) {
		int i = 0;
		while (i < amountGrass) {
			// Choose coordinates
			int x = (int) (Math.random() * (rabbitsSpace.getSizeX()));
			int y = (int) (Math.random() * (rabbitsSpace.getSizeY()));

			// Get the value of the object at those coordinates
			boolean thereIsGrass = isThereGrassAt(x, y);

			// A tile having grass is represented by the integer 1
			if (!thereIsGrass) {
				rabbitsSpace.putObjectAt(x, y, new Integer(1));
				i++;
			}
		}
	}
	
	public boolean isThereGrassAt(int x, int y) {
		if (rabbitsSpace.getObjectAt(x, y) != null) {
			return ((Integer) rabbitsSpace.getObjectAt(x, y)).intValue() == 1;
		} else {
			return false;
		}
	}
	
	public Object2DGrid getCurrentRabbitsSpace() {
		return rabbitsSpace;
	}
}
