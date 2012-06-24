
package queenfinal;
import java.util.*;
import java.lang.reflect.Array;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;


 class Queens2Final {
	private int[] board;

	public Queens2Final(int[] board) {
		this.board = board;
	}

	public void setBoard(int[] board) {
		this.board = board;
	}

	public int[] getBoard() {
		return this.board;
	}
}

class EightQueen {
        public static int genes_final[] = new int[8];
	private int[] board;
	private int size;
	private ArrayList allSolutions = null;
        private static Random rand = new Random();
	private static final int NUM_QUEENS = 8;
	private static final int POPULATION_SIZE = 50;
	private static int num_generation = 0;
	/**
	 * Each element of the board[] represents a row and the value of each element
	 * specifies the column number where the queen is placed on that particular row.
	 * This method is used to retrieve the board array.
	 *
	 * @return
	 */
	public int[] getBoard() {
		return board;
	}

	/**
	 * The ArrayList allSolutions store all possible solutions. External classes use
	 * this method to retrieve the solutions.
	 *
	 * @return
	 */
	public ArrayList getAllSolutions() {
		return this.allSolutions;
	}

	/**
	 * Constructor of EightQueen. Perform initialization operation.
	 *
	 * @param size
	 */
	public EightQueen(int size) {
		this.size = size;
		board = new int[this.size];
		this.allSolutions = new ArrayList();
                Individual population[] = new Individual[POPULATION_SIZE];

		for(int i=0;i<population.length;++i) {
			int a[] = new int[NUM_QUEENS];
			for(int j=0;j<NUM_QUEENS;++j) {
				int k = rand.nextInt(NUM_QUEENS) + 1;	// nextInt =[0,8[ + 1 = [1,8]
				if(contains(k,a)) { --j; }				// if(repeated) reroll
				else { a[j] = k; }
			}
			population[i] = new Individual(a);
		}

		//1. initial call
		let_there_be_light(population);
	}

        private void let_there_be_light (Individual []population) {

		// 2. evaluate each individual through the function of adaptability
		int total_fitness = 0;
		for(int i = 0; i < POPULATION_SIZE; ++i) {
			int adapt = fitness(population[i]);
			population[i].setAdabtability(adapt);
			total_fitness += adapt;
		}

		// 3. Reapeat
		// 		3.1. select the most suitable individuals for reproduction
		Individual parents[] = selection(population,total_fitness);

		//		3.2. generate the new population by crossover and mutation
		Individual children[] = crossover(parents);
		children = mutation(children);

		// 		3.3. Replacement ( substitution ) of the population
		Individual new_generation[] = replace(population, parents, children);

		// 4. end
		for(Individual i : new_generation) {
			if(perfect(i.getGenes())) {
				System.out.println(i);
				return;
			}
		}
		System.out.println("Generation nº: "+(++num_generation));
		let_there_be_light(new_generation);

	}

	/**
	 * method that performs the function of Adabtability
	 *
	 * @param e - individual to evaluate
	 * @return	- Adaptibility value entre [0, 100[
	 */
	private int fitness(Individual e) {
		int g[] = e.getGenes();
		int fitness = 99;	// Assume that the individual is perfect


		// Discounts the number of imperfections (max. 49)
		fitness -= diagonal_intersection(g);

		return fitness;
	}

	/**
	 *method of the individuals responsible for selecting parents
	 *
	 * @param pop		- population from which individuals are chosen
	 * @param total_fitness	- population from which individuals are chosen
	 * @return		- array of parents
	 */
	private Individual[] selection(Individual pop[], int total_fitness) {
		final int NUM_parents = (int)(((double)POPULATION_SIZE)*0.6);
		Individual parents[] = new Individual[NUM_parents];
		for(int i=0;i<NUM_parents;++i) {
			int r = rand.nextInt(total_fitness);
			int s = 0;
			for(Individual idv : pop) {
				s += idv.getAdabtability();
				if(s>=r) { parents[i] = idv; break; }
			}
		}
		return parents;
	}

	/**
	 * method that realizes the crosseover
	 *
	 * @param progs - parents
	 * @return 	- The result of the crossover among parents.
	 */
	private Individual[] crossover(Individual progs[]) {
		Individual xover_result[] = new Individual[progs.length];

		// 2 offspring for each pair of parents
		for(int i=0,k=0;i<progs.length/2;++i) {
			// randomly choose two parents
			Individual p1 = (Individual)Array.get(progs,rand.nextInt(progs.length));
			Individual p2 = (Individual)Array.get(progs,rand.nextInt(progs.length));

			// make the crossing between the two
			int first_point_intersection = rand.nextInt(NUM_QUEENS);	// select the first point of intersection
			int second_point_intersection = rand.nextInt(NUM_QUEENS);	// select the second point of intersection
			if(first_point_intersection>second_point_intersection) {
				int aux = first_point_intersection;
				first_point_intersection = second_point_intersection;
				second_point_intersection = aux;
			}

			// get genes from each parent
			int e1[] = p1.getGenes();
			int e2[] = p2.getGenes();

			// Initialize the genes of the new individuals
			int a1[] = new int[NUM_QUEENS];
			int a2[] = new int[NUM_QUEENS];

			// Fill the new Individuals with:
                            // Inner values ​​of the points of intersection
			for(int j=first_point_intersection;j<second_point_intersection;++j) {
				a1[j] = e1[j];
				a2[j] = e2[j];
			}
			// LEft values of the first crossing point.
			for(int j=0;j<first_point_intersection;++j) {
				a1[j] = pmx(e2[j],a1,e2,first_point_intersection,second_point_intersection);
				a2[j] = pmx(e1[j],a2,e1,first_point_intersection,second_point_intersection);
			}
                            // Values to the right side of the second crossing point.

			for(int j=second_point_intersection;j<NUM_QUEENS;++j) {
				a1[j] = pmx(e2[j],a1,e2,first_point_intersection,second_point_intersection);
				a2[j] = pmx(e1[j],a2,e1,first_point_intersection,second_point_intersection);
			}

			// create two Individuals with the genetic code of the intersection
			Individual idv1 = new Individual(a1);
			Individual idv2 = new Individual(a2);

			xover_result[k++] = idv1;
			xover_result[k++] = idv2;
		}

		return xover_result;
	}

	/**
	 *  randomly select three individuals who will have a mutation.
	 */
	private Individual[] mutation(Individual []desc) {
		for(int i=0;i<3;++i) {
			int k = rand.nextInt(desc.length);
			int g[] = desc[k].getGenes();
			int m = g[0];
			for(int j=0;j<g.length-1;++j) {
				g[j] = g[j+1];
			}
			g[g.length-1] = m;
			desc[k].setGenes(g);
		}
		return desc;
	}

	/**
	 *  replacement of the old population by new generation
         * We use a mix of crossover and direct promotion of the most adaptable of the older generation
	 */
	private Individual[] replace(Individual pop[], Individual progs[], Individual desc[]) {
		Individual []new_generation = new Individual[POPULATION_SIZE];
		for(int i=0;i<desc.length;++i) {
			new_generation[i] = desc[i];
		}

		//direct promotion of the best older generation increasingly ordered
		Arrays.sort(pop);		
		for(int i=desc.length;i<pop.length;++i) {
			new_generation[i] = pop[i];
		}

		return new_generation;
	}

	/**
	 * Check if a value exists within an array.
	 */
	private static boolean contains(int i, int a[]) {
		for(int k=0;k<a.length;++k) {
			if(a[k]==i) return true;
		}
		return false;
	}

	/**
	 *  method that validates the value of integration
	 */
	private static int pmx(int i, int a[], int b[], int first_point_intersection, int second_point_intersection) {
		if(!contains(i,a)) {
			return i;
		}
		else {
			int v=0;
			for(int k=first_point_intersection;k<second_point_intersection;++k) {
				if(a[k]==i) {
					v = b[k];
					break;
				}
			}
			if(!contains(v,a))
				return v;
			else
				return pmx(v,a,b,first_point_intersection,second_point_intersection);
		}
	}

	
	private static boolean horizontal_intersection(int g[]) {
		for(int i=0;i<g.length;++i) {
			for(int j=0;j<g.length;++j) {
				if(i!=j && g[i]==g[j]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * method that calculates the number of existing diagonal intersections
	 */
	public static int diagonal_intersection(int g[]) {
		int res=0;
		for(int i=0;i<g.length;++i) {
			int x0 = i, y0 = g[i];

			int xminus = x0-1, xplus = x0+1;
			int yminus = y0-1, yplus = y0+1;
			while(xminus>=0) {
				if((yminus>=0 && g[xminus]==yminus) || (yplus<g.length && g[xminus]==yminus))
					++res;

				--xminus;
				--yminus;
				++yplus;
			}

			yminus = y0-1; yplus = y0+1;
			while(xplus<g.length) {
				if((yminus>=0 && g[xplus]==yminus) || (yplus<g.length && g[xplus]==yplus))
					++res;

				++xplus;
				--yminus;
				++yplus;
			}
		}
		return res;
	}

	/**
	 * Method that defines perfection -- stopping condition
	 *
	 * @param 	- Genes of an individual
	 * @return	- true if the genes of the indv are perfect
	 */
	private boolean perfect(int g[]) {
		
		if(horizontal_intersection(g))
			return false;

		if(diagonal_intersection(g)!=0)
			return false;

		return true;
	}



 class Individual implements Comparable {

	private int genes[];
	private int Adabtability;

	Individual(int i[]) { genes = i; }


	public void setAdabtability(int Adabtability) {
		this.Adabtability = Adabtability;
	}


	public int getAdabtability() {
		return Adabtability;
	}

	public void setGenes(int[] genes) {
		this.genes = genes;
	}

	public int[] getGenes() {
		return genes;
	}

	public String toString() {
                 int k;
                 StringBuffer sb = new StringBuffer("winning genes are : ");
                 
		 for(int i : genes) { sb.append(i);  }
                 genes_final=genes;
                
		return sb.toString();
	}

	public int compareTo(Object arg0) {
		Individual e = (Individual)arg0;
		if(this.Adabtability == e.getAdabtability())	return 0;
		else if(this.Adabtability < e.getAdabtability()) return -1;
		else return 1;
	}

}





	/**
	 * Place a queen in a row and check its validity. If valid then place it in the
	 * next row else backtrack.
	 *
	 * @param row
	 */
	public void place(int row) {
		if (row == size) {
			int[] temp = new int[8];
			System.arraycopy(board, 0, temp, 0, 8);
			allSolutions.add(new Queens2Final(temp));
			return;
		} else {
			for (int i = 0; i < size; i++) {
				board[row] = i;
				if (valid(row)) place(row + 1);
			}
		}
	}

	public boolean valid(int row) {
		for (int i = 0; i < row; i++) {
			if ((board[i] == board[row]) || Math.abs(board[row] - board[i]) == (row - i)) {
				return false;
			}
		}
		return true;
	}
}

class MyCanvas extends Canvas {

	private Queens2Final solution;

	public void setSolution(Queens2Final solution) {
		this.solution = solution;
	}

	public void paint(Graphics g) {
		super.paint(g);
		drawGraph(g);
		int[] board = this.solution.getBoard();
		for (int i = 0; i < 8; i++) {

                               drawQueen(i,EightQueen.genes_final[i]-1);
                    
	}}


	public void drawGraph(Graphics g) {
		g.setColor(Color.GRAY);
		int height = getHeight();
		int width = getWidth();
		int side = height / 8;
		int spacing = 0;
		for (int i = 0; i < 7; i++) {
			spacing += side;
			g.drawLine(0, spacing, width, spacing);
		}
		side = width / 8;
		spacing = 0;
		for (int i = 0; i < 7; i++) {
			spacing += side;
			g.drawLine(spacing, 0, spacing, height);
		}
		colorGray(g);
	}


	private void colorGray(Graphics g) {
		g.setColor(Color.GRAY);
		int side = getWidth() / 8;
		int spacing;
		int alternate = 1;
		for (int i = 0; i < 8; i++) {
			spacing = 0;
			for (int j = 0; j < 4; j++) {
				if (alternate == 1) {
					g.fillRect(spacing, i * getHeight() / 8, side, getHeight() / 8);
				}
				if (alternate == 0) {
					g.fillRect(spacing + side, i * getHeight() / 8, side, getHeight() / 8);
				}
				spacing = spacing + 2 * side;
			}
			alternate = (++alternate) % 2;
		}
	}


	public void drawQueen(int row, int column) {
		int side = (getHeight() / 8 + getWidth() / 8) / 2;
		int center_x = column * getWidth() / 8 + side / 4;
		int center_y = row * getHeight() / 8 + side / 4;
		Graphics g = getGraphics();
		g.setColor(Color.BLUE);
		g.fillOval(center_x, center_y, side / 2, side / 2);
	}
}

class EightQueenFrame extends JFrame implements KeyEventDispatcher {

	private MyCanvas canvas;
	private EightQueen queen;
	private ArrayList list = null;
	private int count = 0;

	/**
	 * Creates the canvas that will be added to the frame.
	 *
	 * @return
	 */
	private MyCanvas createCanvas() {
		MyCanvas canvas = new MyCanvas();
		canvas.setBackground(Color.BLACK);
		return canvas;
	}

	/**
	 * Creates borderlayout for the frame.
	 *
	 * @return
	 */
	private LayoutManager createLayout() {
		LayoutManager bl = new BorderLayout();
		return bl;
	}


	protected void frameInit() {
		super.frameInit();
		Container cp = getContentPane();
		cp.setLayout(createLayout());
		canvas = createCanvas();
		cp.add(canvas, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(590, 610);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Constructor of the class. The solutions are retrieved and stored in an ArrayList.
	 */
	public EightQueenFrame() {
		KeyboardFocusManager manager;
		manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
		queen = new EightQueen(8);
		queen.place(0);
		list = queen.getAllSolutions();
	}


	public synchronized boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getKeyCode() == 38 && e.getID() == 402) {//When the up arrow is released
			if (count >= 1) count--;
			notifyAll();
		}
		if (e.getKeyCode() == 40 && e.getID() == 402) {//When the down arrow is released
			count++;
			notifyAll();
		}
		return false;
	}

	/**
	 * Method to draw the queens on the board. This method just sets the value
	 * of the board and calls repaint of the canvas.Then it waits until it receives
	 * notification to proceed.
	 */
	public synchronized void display() {
		while (true) {
			try {
				canvas.setSolution((Queens2Final) list.get(count + 1));
				setTitle("Eight Queen : Solution " + (count + 1));
				canvas.repaint();
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
  
                EightQueenFrame queenFrame = new EightQueenFrame();
		queenFrame.display();
               
                
	}
}

