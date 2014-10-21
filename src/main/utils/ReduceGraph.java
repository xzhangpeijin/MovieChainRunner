package main.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import main.runners.GraphMaker;

/**
 * Tries to reduce a graph by removing nodes that can't be part of the longest path
 * 
 * @author Peijin Zhang
 */
public class ReduceGraph
{
  public static Graph reduceGraph(Graph graph) {
    String newname = "Reduced" + graph.getName();
    
    boolean removed = true;
    while (removed) {
      removed = false;
      
      List<String> vertices = graph.getVertices();
      
      Set<String> remove = new HashSet<String>();
      for (int x = 0; x < graph.size(); x++) {
        Set<Integer> reached = forwardSearch(graph, x);
        reached.addAll(backwardSearch(graph, x));
        
        if (reached.size() < 315) {
          remove.add(vertices.get(x));
        }
      }
      
      System.out.println(remove.size());
      if (remove.size() > 0) {
        removed = true;
        vertices.removeAll(remove);
        
        graph = GraphMaker.makeGraph(vertices, newname);
      }
    }
    
    return graph;
  }
  
  private static Set<Integer> forwardSearch(Graph graph, int vertex) {
    Set<Integer> reached = new HashSet<Integer>();
    reached.add(vertex);
    
    Queue<Integer> bfs = new LinkedList<Integer>();
    bfs.add(vertex);
    
    while(bfs.size() > 0) {
      int cur = bfs.poll();;
      
      List<Integer> edges = graph.getOutEdges(cur);
      
      for (int edge : edges) {
        if (!reached.contains(edge)) {
          reached.add(edge);
          bfs.add(edge);
        }
      }
    }
    
    return reached;
  }
  
  private static Set<Integer> backwardSearch(Graph graph, int vertex) {
    Set<Integer> reached = new HashSet<Integer>();
    reached.add(vertex);
    
    Queue<Integer> bfs = new LinkedList<Integer>();
    bfs.add(vertex);
    
    while(bfs.size() > 0) {
      int cur = bfs.poll();;
      
      List<Integer> edges = graph.getInEdges(cur);
      
      for (int edge : edges) {
        if (!reached.contains(edge)) {
          reached.add(edge);
          bfs.add(edge);
        }
      }
    }
     
    return reached;
  }
}
