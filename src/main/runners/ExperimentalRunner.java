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

import main.Main;
import main.utils.Graph;
import main.utils.GraphUtils;
import main.walkers.DeterministicWalker;
import main.walkers.ExperimentalWalker;
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
public class ExperimentalRunner {
  // Start search at 250
  private static final int INITIAL_CUTOFF = 250;

  public static void runExperimental() throws IOException {
    int threads = Runtime.getRuntime().availableProcessors();
    Graph component = Graph.readGraph(
        ExperimentalRunner.class.getResource("/CompleteComponent.txt"));
    Graph full = Graph.readGraph(
        ExperimentalRunner.class.getResource("/ReducedGraph.txt"));
    
    List<Integer> initStates = new ArrayList<Integer>();
    for (int x = 0; x < component.size(); x++) {
      if (component.getInEdges(x).size() > 0 && component.getOutEdges(x).size() > 0) {
        initStates.add(x);
      }
    }
    
    AtomicInteger maxLength = new AtomicInteger(INITIAL_CUTOFF);
    Lock fileLock = new ReentrantLock();

    System.out.println("Starting Experimental Run");
    for (int x = 0; x < 1; x++) {
      Walker walker = new ExperimentalWalker(component, full, initStates, 
          "results/ExperimentalResults.txt", maxLength, fileLock);
      Thread search = new Thread(walker);
      search.start();
    }
  }  
}
