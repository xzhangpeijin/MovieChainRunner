package main.runners;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.utils.Graph;
import main.utils.GraphUtils;
import main.walkers.DeterministicWalker;
import main.walkers.FastWalker;
import main.walkers.SlowWalker;
import main.walkers.Walker;

/**
 * Searches for the longest path in a given graph
 * 
 * Writes all intermediate results to file
 * 
 * Runs one thread per core in random walk unless specified
 * 
 * @author Peijin Zhang
 */
@SuppressWarnings("unused")
public class GraphSearcher {
  // Start search at 250
  private static final int INITIAL_CUTOFF = 250;

  private final String filename;
  private final Graph graph;
  private final int threads;

  public GraphSearcher(Graph graph, String output, int threads) {
    this.graph = graph;
    this.filename = output + File.separator + graph.getName() + "Results.txt";
    this.threads = threads;
  }

  public GraphSearcher(Graph graph, String output) {
    this(graph, output, Runtime.getRuntime().availableProcessors());
  }

  public GraphSearcher(URL url, String output, int threads)
      throws IOException {
    this(Graph.readGraph(url), output, threads);
  }

  public GraphSearcher(URL url, String output) throws IOException {
    this(Graph.readGraph(url), output);
  }

  public void searchGraph() {
    // Do some pre-processing to save each subthread the work
    
    List<Integer> initStates = new ArrayList<Integer>();
    for (int x = 0; x < graph.size(); x++) {
      if (graph.getInEdges(x).size() > 0 && graph.getOutEdges(x).size() > 0) {
        initStates.add(x);
      }
    }

    // Uncomment if using FastWalker
//    Set<Integer>[] fReachable = new Set[graph.size()];
//    Set<Integer>[] bReachable = new Set[graph.size()];
//    
//    for (int x = 0; x < graph.size(); x++) {
//      fReachable[x] = GraphUtils.searchForward(graph, x);
//      bReachable[x] = GraphUtils.searchBackward(graph, x);
//    }

    AtomicInteger maxLength = new AtomicInteger(INITIAL_CUTOFF);
    Lock fileLock = new ReentrantLock();

    System.out.println("Started Searching");
    for (int x = 0; x < threads; x++) {
      // Edit here to change walker type
      Walker walker;
      
      walker = new SlowWalker(graph, initStates, filename, maxLength, fileLock);
      
//      walker = new DeterministicWalker(graph, initStates, filename, maxLength, fileLock);    

//    walker = new FastWalker(graph, initStates, fReachable, bReachable,
//    filename, maxLength, fileLock);
      
      Thread search = new Thread(walker);
      search.start();
    }
  }
  
}
