import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.Histogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
//import uchicago.src.sim.space.Object2DGrid;
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
	private static final int GRID_SIZE = 30;

	// Grass Parameters
	private static final int AMOUNT_GRASS = 40;
	private static final int GRASS_GROWTH_RATE = 30;

	// Rabbit Parameters
	private static final int AMOUNT_RABBITS = 30;
	private static final int BIRTH_THRESHOLD = 20;
	private static final int RABBIT_START_ENERGY = 20;
	private static final int MAX_RABBITS = GRID_SIZE * GRID_SIZE;
	private static final int GRAPH_UPDATE_PERIOD = 1;
	private static final int NUM_BINS = 30;
	private static final int LOWER_BOUND = 0;
	private static final int UPPER_BOUND = 50;
	private static final int BIRTH_ENERGY_COST = 15;

	// VARIABLES
	private int gridSize = GRID_SIZE;
	private int amountRabbits = AMOUNT_RABBITS;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int grassGrowthRate = GRASS_GROWTH_RATE;
	private int rabbitEnergy = RABBIT_START_ENERGY;
	private int maxRabbits = MAX_RABBITS;
	private int currentNumberRabbits = AMOUNT_RABBITS;
	private int stopNow = 0;
	private static RabbitsGrassSimulationModel model; // testing this out to see
														// if i can
														// programmatically end
														// the simulation when
														// all the rabbits die.

	private Schedule schedule;

	private OpenSequenceGraph populationAmountInSpace;
	private Histogram rabbitsEnergyDistribution;

	private RabbitsGrassSimulationSpace rgsSpace; // changed to rgs from rabbit
													// as the space also
													// includes grass. not just
													// rabbits

	private ArrayList<RabbitsGrassSimulationAgent> rabbitList;

	private DisplaySurface displaySurf;

	class rabbitsInSpace implements DataSource, Sequence {
		public Object execute() {
			return new Double(getSValue());
		}

		public double getSValue() {
			return (double) getTotalRabbits();
		}
	}

	class grassInSpace implements DataSource, Sequence {
		public Object execute() {
			return new Double(getSValue());
		}

		public double getSValue() {
			return (double) getTotalGrass();
		}
	}

	class rabbitsEnergy implements BinDataSource {
		public double getBinValue(Object o) {
			RabbitsGrassSimulationAgent rabbit = (RabbitsGrassSimulationAgent) o;
			if (rabbit == null)
				return new Double(0);
			else
				return (double) rabbit.getEnergy();
		}
	}

	public String getName() {
		return "Rabbits Grass Model";
	}

	public void setup() {
		System.out.close();
		System.out.println("Running setup");
		rgsSpace = null;
		rabbitList = new ArrayList<RabbitsGrassSimulationAgent>();
		schedule = new Schedule(1); // Schedule with a step interval of 1

		if (populationAmountInSpace != null) {
			populationAmountInSpace.dispose();
		}
		populationAmountInSpace = null;

		if (displaySurf != null) {
			displaySurf.dispose();
		}
		displaySurf = null;

		if (rabbitsEnergyDistribution != null) {
			rabbitsEnergyDistribution.dispose();
		}
		rabbitsEnergyDistribution = null;

		displaySurf = new DisplaySurface(this, "Carry Drop Model Window 1");
		populationAmountInSpace = new OpenSequenceGraph("Amount of Rabbits In Space", this);
		rabbitsEnergyDistribution = new Histogram("Rabbit Energy", NUM_BINS, (double) LOWER_BOUND,
				(double) UPPER_BOUND);

		registerDisplaySurface("Rabbit Grass Simulation Model Window 1", displaySurf);
		this.registerMediaProducer("Plot", populationAmountInSpace);
	}

	public void begin() {
		buildModel();
		buildSchedule();
		buildDisplay();
		
		stopNow = 0; //needed this to keep things from stopping if you run the model multiple times in a row

		displaySurf.display();
		populationAmountInSpace.display();
		rabbitsEnergyDistribution.display();
	}

	public void buildModel() {
		System.out.println("Running BuildModel");
		rgsSpace = new RabbitsGrassSimulationSpace(gridSize);
		tryGrowGrass(AMOUNT_GRASS);

		for (int i = 0; i < amountRabbits; i++) {
			addNewRabbit();
		}

		for (int i = 0; i < rabbitList.size(); i++) {
			RabbitsGrassSimulationAgent rgsa = rabbitList.get(i);
			rgsa.report();
		}
	}

	public void buildSchedule() {
		System.out.println("Running BuildSchedule");

		class RabbitsGrassSimulationStep extends BasicAction {
			public void execute() {
				SimUtilities.shuffle(rabbitList);
				boolean pregnant;
				reapDeadRabbits();
				int currentRabbitPopulationSize = rabbitList.size();
				for (int i = 0; i < currentRabbitPopulationSize; i++) {
					RabbitsGrassSimulationAgent rgsa = (RabbitsGrassSimulationAgent) rabbitList.get(i);
					pregnant = rgsa.step(birthThreshold);
					if (pregnant && countRabbits() < maxRabbits) // if rabbit
																	// exceeds
																	// birth
																	// threshold,
																	// it can
																	// reproduce.
																	// Only
																	// reproduce
																	// if there
																	// is space
																	// for new
																	// rabbits.
					{
						addNewRabbit();
						rgsa.setEnergy(rgsa.getEnergy() - BIRTH_ENERGY_COST);
					}
				}

				// after rabbits move, grass has a chance to grow again. Only
				// spread while there are still rabbits.
				tryGrowGrass(grassGrowthRate);

				displaySurf.updateDisplay();
				//System.out.println("max Rabbits = "+maxRabbits); //for debugging
				
				if (stopNow > 10) { //check for condition before the condition is changed so we get one extra time step delay
					model.stop();
				}
				// end simulation if appropriate
				if (countRabbits() == 0) {
					if(countGrass() == maxRabbits || grassGrowthRate == 0){ //this should let the grass fill in before ending the simulation.
						//System.out.println("Grass Count = "+countGrass()+"; max Rabbits = "+maxRabbits + "; grass growth rate: "+grassGrowthRate);//for debugging
						stopNow++;
					}
				}


			}
		}

		schedule.scheduleActionBeginning(0, new RabbitsGrassSimulationStep());

		class RabbitsGrassSimulationCount extends BasicAction {
			public void execute() {
				populationAmountInSpace.step();
			}
		}

		schedule.scheduleActionAtInterval(GRAPH_UPDATE_PERIOD, new RabbitsGrassSimulationCount());

		class SimulationUpdateRabbitEnergy extends BasicAction {
			public void execute() {
				rabbitsEnergyDistribution.step();
			}
		}

		schedule.scheduleActionAtInterval(GRAPH_UPDATE_PERIOD, new SimulationUpdateRabbitEnergy());
	}

	public void buildDisplay() {
		System.out.println("Running BuildDisplay");

		ColorMap map = new ColorMap();

		map.mapColor(0, Color.white);
		map.mapColor(1, Color.green);

		Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);

		Object2DDisplay displayRabbits = new Object2DDisplay(rgsSpace.getCurrentRabbitSpace());
		displayRabbits.setObjectList(rabbitList);

		displaySurf.addDisplayableProbeable(displayGrass, "Grass");
		displaySurf.addDisplayableProbeable(displayRabbits, "Rabbits");

		populationAmountInSpace.addSequence("Rabbits in Space", new rabbitsInSpace());
		populationAmountInSpace.addSequence("Grass in Space", new grassInSpace());
		rabbitsEnergyDistribution.createHistogramItem("Rabbit Energy", rabbitList, new rabbitsEnergy());

	}

	private int countRabbits() {
		int numberRabbits = 0;
		for (int i = 0; i < rabbitList.size(); i++) {
			RabbitsGrassSimulationAgent bunny = (RabbitsGrassSimulationAgent) rabbitList.get(i);
			if (bunny.getEnergy() > 0)
				numberRabbits++;
		}
		System.out.println("Number of rabbits is " + numberRabbits);
		setCurrentNumberRabbits(numberRabbits);
		return numberRabbits;
	}

	private void addNewRabbit() {
		// Create new rabbit and store it in the list
		RabbitsGrassSimulationAgent bunny = new RabbitsGrassSimulationAgent(rabbitEnergy);
	
		if(rgsSpace.addRabbit(bunny)){
			rabbitList.add(bunny);
		}
	}

	/**
	 * Find all the rabbits in the space whose energy is strictly lower than 1,
	 * remove them from the space and then remove them from the rabbitList of
	 * this Model
	 * 
	 * @return the amount of dead rabbits
	 */
	private int reapDeadRabbits() {
		int count = 0;
		for (int i = (rabbitList.size() - 1); i >= 0; i--) {
			RabbitsGrassSimulationAgent rgsa = rabbitList.get(i);
			if (rgsa.getEnergy() < 1) {
				rgsSpace.removeRabbitAt(rgsa.getX(), rgsa.getY());
				rabbitList.remove(i);
				count++;
			}
		}
		setCurrentNumberRabbits(getCurrentNumberRabbits()-count);
		return count;
	}

	public int getGridSize() {
		return gridSize;
	}

	private void tryGrowGrass(int amountGrass) {
		int i = 0;
		int totalGrass = countGrass();
		while (i < amountGrass && totalGrass < maxRabbits) {
			rgsSpace.spreadGrass(); // modified such that function call only
									// places one grass tile
			totalGrass++;
			i++;
		}
	}

	public void setGridSize(int newGridSize) {
		// scales the maximum number of rabbits according to the selected grid
		// size
		if (newGridSize < 1) {
	
			JOptionPane.showMessageDialog(null,
					"Error: Positive Grid Size Required. System value unchanged.",
					"Warning: Existential Crisis", JOptionPane.INFORMATION_MESSAGE);
		} 
		else{
			gridSize = newGridSize;
			setMaxRabbits(gridSize * gridSize);
		}
	}

	public String[] getInitParam() {
		String[] initParams = { "GridSize", "AmountRabbits", "BirthThreshold", "GrassGrowthRate" };
		return initParams;
	}

	public Schedule getSchedule() {
		// TODO Auto-generated method stub
		return schedule;
	}

	public int getAmountRabbits() {
		return amountRabbits;
	}

	public void setAmountRabbits(int newAmountRabbits) {
		if (newAmountRabbits > maxRabbits) {
			JOptionPane.showMessageDialog(null,
					"Error: Too many rabbits. Please select a value less than: " + maxRabbits + ". Value unchanged.",
					"Warning: Rabbit Overload", JOptionPane.INFORMATION_MESSAGE);
		} else
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
	
	public void setCurrentNumberRabbits(int curNumRab){
		currentNumberRabbits = curNumRab;
	}
	
	public int getCurrentNumberRabbits()
	{
		return currentNumberRabbits;
	}

	public int countGrass() {
		int numberGrass = 0;
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {

				if (rgsSpace.isThereGrassAt(i, j)) {
					numberGrass += 1;
				}
			}
		}
		return numberGrass;
	}

	public int getTotalRabbits() {
		return rabbitList.size();
	}

	public int getTotalGrass() {
		return countGrass();
	}

	public static void main(String[] args) {

		System.out.println("Rabbit skeleton");
		SimInit init = new SimInit();
		model = new RabbitsGrassSimulationModel();
		init.loadModel(model, "", false);
	}

}
