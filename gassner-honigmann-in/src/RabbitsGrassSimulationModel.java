import java.awt.Color;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;

import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;

/**
 * Class that implements the simulation model for the rabbits grass simulation.
 * This is the first class which needs to be setup in order to run Repast
 * simulation. It manages the entire RePast environment and the simulation.
 *
 * @author
 */

public class RabbitsGrassSimulationModel extends SimModelImpl {

	private static final int GRID_SIZE = 10;
	private static final int AMOUNT_RABBITS = 20;
	private static final int AMOUNT_GRASS = 20;
	private static final int BIRTH_THRESHOLD = 15;
	private static final int GRASS_GROWTH_RATE = 2;

	private int gridSize = GRID_SIZE;
	private int amountRabbits = AMOUNT_RABBITS;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int grassGrowthRate = GRASS_GROWTH_RATE;

	private Schedule schedule;

	private RabbitsGrassSimulationSpace rabbitsSpace;

	private DisplaySurface displaySurf;

	public static void main(String[] args) {

		System.out.println("Rabbit skeleton");
		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		init.loadModel(model, "", false);
	}

	public void begin() {
		buildModel();
		buildSchedule();
		buildDisplay();
		
		displaySurf.display();
	}

	public String[] getInitParam() {
		String[] initParams = { "GridSize", "AmountRabbits", "BirthThreshold", "GrassGrowthRate" };
		return initParams;
	}

	public String getName() {
		return "Rabbits Grass Model";
	}

	public Schedule getSchedule() {
		// TODO Auto-generated method stub
		return schedule;
	}

	public void setup() {
		System.out.println("Running setup");
		rabbitsSpace = null;

		if (displaySurf != null) {
			displaySurf.dispose();
		}
		displaySurf = null;

		displaySurf = new DisplaySurface(this, "Carry Drop Model Window 1");

		registerDisplaySurface("Rabbit Grass Simulation Model Window 1", displaySurf);
	}

	public void buildModel() {
		System.out.println("Running BuildModel");
		rabbitsSpace = new RabbitsGrassSimulationSpace(gridSize);
		rabbitsSpace.spreadRabbits(amountRabbits);
		rabbitsSpace.spreadGrass(AMOUNT_GRASS);
	}

	public void buildSchedule() {
		System.out.println("Running BuildSchedule");
	}

	public void buildDisplay() {
		System.out.println("Running BuildDisplay");

		ColorMap map = new ColorMap();

		map.mapColor(0, Color.white);
		map.mapColor(1, Color.green);

		Value2DDisplay displayGrass = new Value2DDisplay(rabbitsSpace.getCurrentRabbitsSpace(), map);

		displaySurf.addDisplayable(displayGrass, "Grass");
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int newGridSize) {
		gridSize = newGridSize;
	}

	public int getAmountRabbits() {
		return amountRabbits;
	}

	public void setAmountRabbits(int newAmountRabbits) {
		amountRabbits = newAmountRabbits;
	}

	public int getBirthThreshold() {
		return birthThreshold;
	}

	public void setBirthThreshold(int newBirthThreshold) {
		birthThreshold = newBirthThreshold;
	}

	public int getGrassGrowthRate() {
		return grassGrowthRate;
	}

	public void setGrassGrowthRate(int newGrassGrowthRate) {
		grassGrowthRate = newGrassGrowthRate;
	}

}
