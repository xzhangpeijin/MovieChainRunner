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
import main.utils.ReduceGraph;

/**
 * Main class for running MovieChainRunner functions
 * 
 * Options: 
 * -h                 Usage info 
 * -c                 Create full graph from movie list 
 * -s                 Split full graph into components 
 * -d [graph]         Reduce a given graph 
 * -r [graph]         Run on given graph 
 * -p [graph]         Print a graph to graphml format
 * 
 * @author Peijin Zhang
 */
public class Main {
  private static final String SP = File.separator;
  private static final String MOVIE_LIST = "/MovieList.txt";
  private static final String FULL_GRAPH = "/FullGraph.txt";
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
        graph.writeToFile("data" + SP + graph.getName() + ".txt");
      }
    }

    printSummary(graphs);
  }

  private static void printSummary(Set<Graph> graphs) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(new File(RESULT_DIR + SP
        + "ComponentSummary.txt")));

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

  public static void reduceGraph(String component) throws IOException {
    URL componentDir = Main.class.getResource("/" + component + ".txt");
    if (componentDir == null) {
      throw new IllegalArgumentException("Graph does not exist");
    }

    String outputPath = "data" + SP + component + "Reduced.txt";

    Graph input = Graph.readFromFile(componentDir.getPath());
    Graph reduced = ReduceGraph.reduceGraph(input);
    reduced.writeToFile(outputPath);
  }

  /**
   * Starts a thread for each graph to search through Each thread will write
   * results to a result directory in the format [graph]Results.txt
   */
  public static void searchGraph(String component) throws IOException {
    URL componentDir = Main.class.getResource("/" + component + ".txt");
    if (componentDir == null) {
      throw new IllegalArgumentException("Specified graph does not exist");
    }

    new GraphSearcher(componentDir.getPath(), RESULT_DIR).searchGraph();
  }

  public static void main(String[] args) throws Exception {
    // Run without command line
    args = new String[] { "-d", "LargeComponent" };

    if (Arrays.binarySearch(args, "-h") >= 0) {
      System.out.println("Options:");
      System.out.println("-h                Usage info");
      System.out.println("-c                Create full graph from movie list");
      System.out.println("-s                Split full graph into components");
      System.out.println("-d [graph]        Reduce a given graph");
      System.out.println("-r [graph]        Run on given graph");
      System.out.println("-p [graph]        Print a graph to graphml format");
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

    // Reduce a graph
    int dloc = -1;
    if ((dloc = Arrays.binarySearch(args, "-d")) >= 0) {
      try {
        String component = args[dloc + 1];
        Main.reduceGraph(component);
      } catch (IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Must specify graph to run with -d");
      }
    }

    // Search for longest path
    int rloc = -1;
    if ((rloc = Arrays.binarySearch(args, "-r")) >= 0) {
      try {
        String component = args[rloc + 1];
        Main.searchGraph(component);
      } catch (IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Must specify graph to run with -r");
      }
    }

    // Print edge list
    int ploc = -1;
    if ((ploc = Arrays.binarySearch(args, "-p")) >= 0) {
      try {
        String component = args[ploc + 1];

        URL componentDir = Main.class.getResource("/" + component + ".txt");
        if (componentDir == null) {
          throw new IllegalArgumentException("Graph does not exist");
        }

        String outputPath = RESULT_DIR + SP + component + "Graph.graphml";

        Graph.readFromFile(componentDir.getPath()).writeGraphML(outputPath);
      } catch (IndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Must specify graph to run with -p");
      }
    }
  }
}
