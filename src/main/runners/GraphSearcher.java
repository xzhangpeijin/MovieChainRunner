package main.runners;

import java.io.IOException;

import main.utils.Graph;

public class GraphSearcher implements Runnable
{
  private final Graph graph;
  private final String output;
  
  public GraphSearcher(Graph graph, String output) {
    this.graph = graph;
    this.output = output;
  }
  
  public GraphSearcher(String path, String output) throws IOException {
    this(Graph.readFromFile(path), output);
  }

  @Override
  public void run()
  {
    
  }
}
