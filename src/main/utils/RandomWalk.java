package main.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomWalk
{
  private Graph graph;
  private List<Integer> path;
  private Set<Integer> visited;
  
  private List<Integer> hasout;
  
  public RandomWalk(Graph graph) {
    this.graph = graph;
    path = new ArrayList<Integer>();
    visited = new HashSet<Integer>();
    
    hasout = new ArrayList<Integer>();
    for (int x = 0; x < graph.getSize(); x++) {
      if (graph.getOutEdges(x).size() > 0 && graph.getInEdges(x).size() > 0) {
        hasout.add(x);
      }
    }
  }
  
  public void walk(long seed) {
    path.clear();
    visited.clear();
    
    Random random = new Random(seed);
    
    int start = random.nextInt(hasout.size());
    
    int cur = start;
    visited.add(cur);
    
    List<Integer> outedges = graph.getOutEdges(cur);
    while (outedges.size() > 0) {
      int next = random.nextInt(outedges.size());
      cur = outedges.get(next);
      visited.add(cur);
      path.add(cur);
      
      outedges = graph.getOutEdges(cur);
      outedges.removeAll(visited);
    }
    
    List<Integer> inedges = graph.getInEdges(start);
    inedges.removeAll(visited);
    while (inedges.size() > 0) {
      int next = random.nextInt(inedges.size());
      cur = inedges.get(next);
      visited.add(cur);
      path.add(0, cur);
      
      inedges = graph.getInEdges(cur);
      inedges.removeAll(visited);
    }
  }
  
  public List<Integer> getPath() {
    return path;
  }
  
  public int getSize() {
    return path.size();
  }
}
