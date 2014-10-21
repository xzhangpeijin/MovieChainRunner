package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
 * -p [component]    Print the edge list for a component
 * 
 * @author Peijin Zhang
 */
public class Main
{
  private static final int TARGET_LENGTH = 315;

  private static final String SP = File.separator;
  private static final String MOVIE_LIST = "/MovieList.txt";
  private static final String FULL_GRAPH = "/FullGraph.txt";
  private static final String COMPONENTS_DIR = "/components";
  private static final String RESULT_DIR = "results";

  /**
   * Creates the full graph from the input movie list
   */
  public static void makeFullGraph() throws IOException {
    URL movie = Main.class.getResource(MOVIE_LIST);
    if (movie == null) {
      throw new RuntimeException("Movie List not found");
    }

    Graph fullGraph = GraphMaker.makeGraph(movie.getPath(), "FullGraph");
    fullGraph.writeToFile("data" + FULL_GRAPH);
  }

  /**
   * Splits the full graph into connected components
   */
  public static void splitComponents() throws IOException {
    URL fullgraph = Main.class.getResource(FULL_GRAPH);
    if (fullgraph == null) {
      throw new RuntimeException("Full graph not created, run -c first");
    }

    Set<Graph> graphs = GraphSplitter.splitGraph(fullgraph.getPath());
    for (Graph graph : graphs) {
      if (graph.size() >= 10) {
        graph.writeToFile("data" + COMPONENTS_DIR + SP + graph.getName() + ".txt");
      }
    }

    printSummary(graphs);
  }

  private static void printSummary(Set<Graph> graphs) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(new File(
        RESULT_DIR + SP + "ComponentSummary.txt")));

    out.println("Total graphs: " + graphs.size());
    TreeMap<Integer, ArrayList<Graph>> sizes = new TreeMap<Integer, ArrayList<Graph>>();
    for (Graph graph : graphs) {
      int size = graph.size();
      if (!sizes.containsKey(size)) {
        sizes.put(size, new ArrayList<Graph>());
      } 
      sizes.get(size).add(graph);
    }

    for (Entry<Integer, ArrayList<Graph>> entry : sizes.entrySet()) {
      int size = entry.getKey();
      ArrayList<Graph> sizegraphs = entry.getValue();
      out.format("Size %d: %d%n", size, sizegraphs.size());
      StringBuffer buf = new StringBuffer();
      for (int x = 0; x < sizegraphs.size(); x++) {
        if (x != 0) {
          buf.append(" ");
        }
        buf.append(sizegraphs.get(x).getName());
      }
      out.println(buf.toString());
    }

    out.flush();
    out.close();
  }

  /**
   * Starts a thread for each graph to search through
   * Each thread will write results to a result directory in the format [component]Results.txt
   */
  public static void searchGraph(String component) throws IOException {
    URL componentDir = Main.class.getResource(COMPONENTS_DIR);
    if (componentDir == null) {
      throw new RuntimeException("Components not created, run -s first");
    }

    if (component.equals("A")) {      
      File[] componentFiles = new File(componentDir.getPath()).listFiles();
      for (File file : componentFiles) {
        String path = file.getCanonicalPath();
        if (path.contains(".txt")) {
          Graph graph = Graph.readFromFile(path);
          if (graph.size() >= TARGET_LENGTH) {
          	new GraphSearcher(graph, RESULT_DIR, 1).searchGraph();
          }
        }
      }
    } else {
    	new GraphSearcher(componentDir.getPath() + SP + component, RESULT_DIR).searchGraph();
    }
  }

  public static void main(String[] args) throws Exception {
    // Run without command line 
    args = new String[]{"-r", "Component4.txt"};

    if (Arrays.binarySearch(args, "-h") >= 0) {
      System.out.println("Options:");
      System.out.println("-h                Usage info");
      System.out.println("-c                Create full graph from movie list");
      System.out.println("-s                Split full graph into components");
      System.out.println("-r [component]    Run on given component");
      System.out.println("-p [component]    Print the edge list for a component");
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
