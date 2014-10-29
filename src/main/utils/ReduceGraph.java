package main.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.runners.GraphMaker;

/**
 * Tries to reduce a graph by removing nodes that can't be part of the longest
 * path
 * 
 * @author Peijin Zhang
 */
public class ReduceGraph {
  private static final String TEMP_NAME = "TEMP_GRAPH_NAME";

  public static Graph reduceGraph(Graph graph) {
    String newname = "Reduced" + graph.getName();
    graph = thirdPass(graph);
    graph.setName(newname);

    return graph;
  }

  /**
   * First pass reduction
   * 
   * Searches forwards and backwards from each node If < 315 points are reached
   * this way, kill node
   */
  @SuppressWarnings("unused")
  private static Graph firstPass(Graph graph) {

    boolean removed = true;
    while (removed) {
      removed = false;

      List<String> vertices = graph.getVertices();

      Set<String> remove = new HashSet<String>();
      for (int x = 0; x < graph.size(); x++) {
        Set<Integer> reached = GraphUtils.searchForward(graph, x);
        reached.addAll(GraphUtils.searchBackward(graph, x));

        if (reached.size() < GraphUtils.CANDIDATE_CUTOFF) {
          remove.add(vertices.get(x));
        }
      }

      System.out.println(remove.size());
      if (remove.size() > 0) {
        removed = true;
        vertices.removeAll(remove);

        graph = GraphMaker.makeGraph(vertices, TEMP_NAME);
      }
    }

    return graph;
  }

  /**
   * Second pass reduction Finds all vertices going into and out from a vertex
   * 
   * Search forwards from all out vertices Search backwards from all in vertices
   * 
   * If all pairs of reachable points from an in vertex and out vertex are less
   * than 315, kill node
   * 
   * DEPRECATED: This does no better than the first pass algorithm
   */
  @SuppressWarnings({ "unchecked", "unused" })
  private static Graph secondPass(Graph graph) {
    boolean removed = true;
    while (removed) {
      removed = false;

      List<String> vertices = graph.getVertices();

      Set<String> remove = new HashSet<String>();
      for (int x = 0; x < graph.size(); x++) {
        List<Integer> outedges = graph.getOutEdges(x);
        Set<Integer>[] outreached = new Set[outedges.size() + 1];

        for (int y = 0; y < outedges.size(); y++) {
          outreached[y] = GraphUtils.searchForward(graph, outedges.get(y));
        }
        outreached[outedges.size()] = new HashSet<Integer>();

        List<Integer> inedges = graph.getInEdges(x);
        Set<Integer>[] inreached = new Set[inedges.size() + 1];
        for (int y = 0; y < inedges.size(); y++) {
          inreached[y] = GraphUtils.searchBackward(graph, inedges.get(y));
        }
        inreached[inedges.size()] = new HashSet<Integer>();

        boolean keep = false;
        outer: {
          for (int i = 0; i < outreached.length; i++) {
            for (int j = 0; j < inreached.length; j++) {
              Set<Integer> reached = new HashSet<Integer>(outreached[i]);
              reached.addAll(inreached[j]);
              reached.add(x);

              if (reached.size() >= GraphUtils.CANDIDATE_CUTOFF) {
                keep = true;
                break outer;
              }
            }
          }
        }

        if (!keep) {
          remove.add(vertices.get(x));
        }
      }

      System.out.println(remove.size());
      if (remove.size() > 0) {
        removed = true;
        vertices.removeAll(remove);

        graph = GraphMaker.makeGraph(vertices, TEMP_NAME);
      }
    }

    return graph;
  }
  
  /**
   * "Reduces" a graph by taking the intersection of all of its reachability sets
   * 
   * The resulting graph is a strongly connected component where we can simply search
   * for longest paths in. From there, we can extend this "reduced" graph into the
   * real reduced graph by just extending the ends into the reduced set until we can't
   * move anymore.
   */
  private static Graph thirdPass(Graph graph) {
    List<String> vertices = graph.getVertices();
    
    Set<Integer> intersect = new HashSet<Integer>();
    for (int x = 0; x < vertices.size(); x++) {
      intersect.add(x);
    }
    
    for (int x = 0; x < vertices.size(); x++) {
      Set<Integer> reachable = GraphUtils.searchForward(graph, x);
      reachable.addAll(GraphUtils.searchBackward(graph, x));
      
      intersect.retainAll(reachable);
    }
    
    List<String> newvertices = new ArrayList<String>(intersect.size());
    for (int vertex : intersect) {
      newvertices.add(vertices.get(vertex));
    }
    
    return GraphMaker.makeGraph(newvertices, TEMP_NAME);
  }
}
