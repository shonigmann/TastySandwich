import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;

/**
 * Class that implements the simulation model for the rabbits grass simulation.
 * This is the first class which needs to be setup in order to run Repast
 * simulation. It manages the entire RePast environment and the simulation.
 *
 * @author
 */

public class RabbitsGrassSimulationModel extends SimModelImpl {

	// CONSTANTS
	// Display Parameters
	private static final int GRID_SIZE = 20;

	// Grass Parameters
	private static final int AMOUNT_GRASS = 20;
	private static final int GRASS_GROWTH_RATE = 2;

	// Rabbit Parameters
	private static final int AMOUNT_RABBITS = 20;
	private static final int BIRTH_THRESHOLD = 15;
	private static final int RABBIT_START_ENERGY = 30;
	private static final int MAX_RABBITS = GRID_SIZE * GRID_SIZE;

	// VARIABLES
	private int gridSize = GRID_SIZE;
	private int amountRabbits = AMOUNT_RABBITS;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int grassGrowthRate = GRASS_GROWTH_RATE;
	private int rabbitEnergy = RABBIT_START_ENERGY;
	private int maxRabbits = MAX_RABBITS;
	

	private Schedule schedule;

	private RabbitsGrassSimulationSpace rgsSpace; //changed to rgs from rabbit as the space also includes grass. not just rabbits

	private ArrayList<RabbitsGrassSimulationAgent> rabbitList;

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
		System.out.close();
		System.out.println("Running setup");
		rgsSpace = null;
		rabbitList = new ArrayList<RabbitsGrassSimulationAgent>();
	    schedule = new Schedule(1); //Schedule with a step interval of 1
	    
		if (displaySurf != null) {
			displaySurf.dispose();
		}
		displaySurf = null;

		displaySurf = new DisplaySurface(this, "Carry Drop Model Window 1");

		registerDisplaySurface("Rabbit Grass Simulation Model Window 1", displaySurf);
	}

	public void buildModel() {
		System.out.println("Running BuildModel");
		rgsSpace = new RabbitsGrassSimulationSpace(gridSize);
		rgsSpace.spreadGrass(AMOUNT_GRASS);

		for (int i = 0; i < amountRabbits; i++) {
			addNewRabbit();
		}
		
		for(int i = 0; i<rabbitList.size(); i++)
		{
			RabbitsGrassSimulationAgent rgsa = rabbitList.get(i);
			rgsa.report();
		}
	}

	public void buildSchedule() {
		System.out.println("Running BuildSchedule");
		
		class RabbitsGrassSimulationStep extends BasicAction {
			public void execute() {
				SimUtilities.shuffle(rabbitList);
				for (int i = 0; i < rabbitList.size(); i++) {
					RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) rabbitList.get(i);
					rgsa.step();
				}
				
				int deadRabbits = reapDeadRabbits();
//		        for(int i = 0; i < deadRabbits; i++){
//		          addNewRabbit();
//		        }
				
		        displaySurf.updateDisplay();
			}
		}
		
	    schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());
	}

	public void buildDisplay() {
		System.out.println("Running BuildDisplay");

		ColorMap map = new ColorMap();

		map.mapColor(0, Color.white);
		map.mapColor(1, Color.green);

		Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);

		Object2DDisplay displayRabbits = new Object2DDisplay(rgsSpace.getCurrentRabbitSpace());
		displayRabbits.setObjectList(rabbitList);
		
		displaySurf.addDisplayable(displayGrass, "Grass");
		displaySurf.addDisplayable(displayRabbits, "Rabbits");
	}

	private void addNewRabbit(){
		//Create new rabbit and store it in the list
		RabbitsGrassSimulationAgent bunny = new RabbitsGrassSimulationAgent(rabbitEnergy);
		rabbitList.add(bunny);
		rgsSpace.addRabbit(bunny);
	}
	
	/**
	 * Find all the rabbits in the space whose energy is strictly lower than 1, 
	 * remove them from the space and then remove them from the rabbitList of this Model
	 * @return the amount of dead rabbits
	 */
	private int reapDeadRabbits() {
		int count = 0;
		for (int i = (rabbitList.size() - 1); i >= 0; i--) {
			RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) rabbitList.get(i);
			if (rgsa.getEnergy() < 1) {
				rgsSpace.removeRabbitAt(rgsa.getX(), rgsa.getY());
				rabbitList.remove(i);
				count++;
			}
		} 
		return count;
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int newGridSize) {
		gridSize = newGridSize;
		// scales the maximum number of rabbits according to the selected grid size
		setMaxRabbits(gridSize * gridSize); 
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

	public void setRabbitEnergy(int newRabbitEnergy) {
		rabbitEnergy = newRabbitEnergy;
	}

	public int getRabbitEnergy() {
		return rabbitEnergy;
	}

	public void setMaxRabbits(int newMaxRabbits) {
		maxRabbits = newMaxRabbits;
	}

	public int getMaxRabbits() {
		return maxRabbits;
	}
}
