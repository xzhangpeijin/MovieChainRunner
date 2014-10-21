package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import main.runners.GraphMaker;
import main.runners.GraphSearcher;
import main.runners.GraphSplitter;
import main.utils.Graph;


/**
 * Main class for running MovieChainRunner functions
 * 
 * Options:
 * -h                Usage info
 * -c                Create full graph from movie list
 * -s                Split full graph into components
 * -r [component]    Run on given component
 *                   Use -r A to run on all components with size > TARGET_LENGTH
 * 
 * @author Peijin Zhang
 */
public class Main
{
  private static final int TARGET_LENGTH = 315;

  private static final String SP = File.separator;
  private static final String MOVIE_LIST = "data" + SP + "MovieList.txt";
  private static final String FULL_GRAPH = "data" + SP + "FullGraph.txt";
  private static final String COMPONENTS_DIR = "data" + SP + "components";
  private static final String RESULT_DIR = "results";
  
  public static void makeFullGraph() throws IOException {
    String movie = Main.class.getResource(SP + MOVIE_LIST).getPath();
    Graph fullGraph = GraphMaker.makeGraph(movie);
    fullGraph.writeToFile(FULL_GRAPH);
  }
  
  public static void splitComponents() throws IOException {
    String fullgraph = Main.class.getResource(SP + FULL_GRAPH).getPath();
    if (fullgraph == null) {
      throw new RuntimeException("Full graph not created, run -c first");
    }
    
    Set<Graph> graphs = GraphSplitter.splitGraph(fullgraph);
    int number = 1;
    for (Graph graph : graphs) {
      graph.writeToFile(COMPONENTS_DIR + SP + "Component" + number + ".txt");
      number++;
    }
  }
  
  /**
   * Starts a thread for each graph to search through
   * Each thread will write results to a result directory in the format [component]Results.txt
   */
  public static void searchGraph(String component) throws IOException {
    if (component.equals("A")) {
      File[] componentFiles = new File(COMPONENTS_DIR).listFiles();
      for (File file : componentFiles) {
        String path = file.getCanonicalPath();
        if (path.contains(".txt")) {
          Graph graph = Graph.readFromFile(path);
          if (graph.getSize() >= TARGET_LENGTH) {
            new Thread(new GraphSearcher(graph, RESULT_DIR)).start();
          }
        }
      }
    } else {
      new Thread(new GraphSearcher(COMPONENTS_DIR + SP + component, RESULT_DIR)).start();
    }
  }

  public static void main(String[] args) throws Exception {
    // Run without command line 
    args = new String[]{"-c"};
    
    if (Arrays.binarySearch(args, "-h") >= 0) {
      System.out.println("Options:");
      System.out.println("-h                Usage info");
      System.out.println("-c                Create full graph from movie list");
      System.out.println("-s                Split full graph into components");
      System.out.println("-r <component>    Run on given component");
      return;
    }
    
    // Create full graph
    if (Arrays.binarySearch(args, "-c") >= 0) {
      Main.makeFullGraph();
    }
    
    // Split full graph into components
    if (Arrays.binarySearch(args, "-s") >= 0) {
      Main.splitComponents();
    }
    
    // Search for longest path
    int rloc = -1;
    if ((rloc = Arrays.binarySearch(args, "-r")) >= 0) {
      try {
        String component = args[rloc + 1];
        Main.searchGraph(component);
      } catch (IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Must specify component to run with -r");
      }
    }
  }
}
