package main.utils;

import java.util.List;
import java.util.Random;


/**
 * Tries to extend a path given a graph that it's built on
 * @author Peijin Zhang
 */
public class PathUtils {
  
  /**
   * Extends the given path by searching out from endpoints
   * 
   * Randomly selects valid extension points
   */
  public static Path extendPath(Path path, Random random) {
    return path;
  }
  
  /**
   * Converts a path from a subgraph into its represntation in a larger graph
   * 
   * REQUIRES: Larger graph must contain the smaller graph as a subgraph
   */
  public static Path convertPath(Graph supergraph, Path path) {
    Path newpath = new Path(supergraph);
    List<String> vertices = supergraph.getVertices();
    
    for (String vertex : path.toStringPath()) {
      newpath.appendForward(vertices.indexOf(vertex));
    }
    
    return newpath;
  }
}
