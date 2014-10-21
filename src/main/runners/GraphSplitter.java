package main.runners;

import java.io.IOException;
import java.util.Set;

import main.utils.Graph;

/**
 * Splits a given graph into its connected components
 * 
 * @author Peijin
 */
public class GraphSplitter
{
  public static Set<Graph> splitGraph(Graph graph) {
    return null;
  }
  
  public static Set<Graph> splitGraph(String path) throws IOException {
    return splitGraph(Graph.readFromFile(path));
  }
}

