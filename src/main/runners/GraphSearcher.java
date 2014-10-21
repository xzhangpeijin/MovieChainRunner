package main.runners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import main.utils.Graph;
import main.utils.RandomWalk;

/**
 * Searches for the longest path in a given graph
 * 
 * Writes all intermediate results to file
 * 
 * Runs one thread per core in random walk unless specified
 * 
 * @author Peijin Zhang
 */
public class GraphSearcher
{
  private static final int INITIAL_CUTOFF = 39;

  private final String filename;
  private final Graph graph;
  private final int threads;

  public GraphSearcher(Graph graph, String output, int threads)
  {
    this.graph = graph;
    this.filename = output + File.separator + graph.getName() + "Results.txt";
    this.threads = threads;
  }

  public GraphSearcher(Graph graph, String output)
  {
    this(graph, output, Runtime.getRuntime().availableProcessors());
  }

  public GraphSearcher(String path, String output, int threads)
      throws IOException
  {
    this(Graph.readFromFile(path), output, threads);
  }

  public GraphSearcher(String path, String output) throws IOException
  {
    this(Graph.readFromFile(path), output);
  }

  public void searchGraph()
  {
    List<Integer> initStates = new ArrayList<Integer>();
    for (int x = 0; x < graph.size(); x++)
    {
      if (graph.getInEdges(x).size() > 0 && graph.getOutEdges(x).size() > 0)
      {
        initStates.add(x);
      }
    }

    AtomicInteger maxLength = new AtomicInteger(INITIAL_CUTOFF);
    Lock fileLock = new ReentrantLock();

    for (int x = 0; x < threads; x++)
    {
      Thread walker = new Thread(new RandomWalk(graph, initStates, filename,
          maxLength, fileLock));
      walker.start();
    }
  }
}
