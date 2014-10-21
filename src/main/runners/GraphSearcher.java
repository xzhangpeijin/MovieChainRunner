package main.runners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import main.utils.Graph;
import main.utils.RandomWalk;

/**
 * Searches for the longest path in a given graph
 * 
 * Writes all intermediate results to file
 * 
 * @author Peijin Zhang
 */
public class GraphSearcher implements Runnable
{
  private static final int INITIAL_CUTOFF = 10;
  private final Graph graph;
  private final String filename;

  public GraphSearcher(Graph graph, String output) {
    this.graph = graph;
    this.filename = output + File.separator + graph.getName() + "Results.txt";
  }

  public GraphSearcher(String path, String output) throws IOException {
    this(Graph.readFromFile(path), output);
  }
  
  /*
   * Result file format
   * 
   * Paths come in 3 lines:
   *   Line 1: Length of the path
   *   Line 2: Seed used to generate the path
   *   Line 3: Space separated vertices for the path
   *   
   * Paths in the file are guaranteed to be in strictly increasing path length
   */

  @Override
  public void run()
  {
    int maxlength = INITIAL_CUTOFF;

    RandomWalk walker = new RandomWalk(graph);
    while (true) {
      long seed = System.currentTimeMillis();
      walker.walk(seed);
      if (walker.getSize() > maxlength) {
        maxlength = walker.getSize();
        System.out.println(maxlength);
        try {
          PrintWriter out = new PrintWriter(new FileWriter(new File(filename), true));
          out.println("Length: " + walker.getSize());
          out.println("Seed : " + seed);
          
          StringBuffer buf = new StringBuffer();
          for (int vertex : walker.getPath()) { 
            buf.append(vertex);
            buf.append(" ");
          }
          out.println(buf.toString().trim());

          out.flush();
          out.close();
        } catch (IOException e) {
          System.err.println("Error writing to " + filename);
          e.printStackTrace();
        }
      }
    }
  }
}
