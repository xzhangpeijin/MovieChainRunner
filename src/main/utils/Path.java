package main.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Class for representing a path
 * 
 * @author Peijin Zhang
 */
public class Path {
  private final Graph graph;
  private final Set<Integer> visited;
  private final List<Integer> path;
  
  public Path(Graph graph) {
    this.graph = graph;
    this.visited = new HashSet<Integer>();
    this.path = new LinkedList<Integer>();
  }
  
  public void clear() {
    visited.clear();
    path.clear();
  }
  
  public boolean contains(int vertex) {
    return visited.contains(vertex);
  }
  
  public void appendForward(int vertex) {
    visited.add(vertex);
    path.add(vertex);
  }
  
  public void appendBackward(int vertex) {
    visited.add(vertex);
    path.add(0, vertex);
  }
  
  public int size() {
    return path.size();
  }
  
  public Set<Integer> getVisited() {
    return visited;
  }
  
  /**
   * Returns a list of the movie names in this path
   */
  public List<String> toStringPath() {
    List<String> names = new ArrayList<String>(path.size());
    List<String> vertices = graph.getVertices();
    for (int vertex : path) {
      names.add(vertices.get(vertex));
    }
    return names;
  }
  
  /*
   * Result file format
   * 
   * Paths come in 3 lines: Line 1: Length of the path Line 2: Seed used to
   * generate the path Line 3: Space separated vertices for the path
   * 
   * Paths in the file are guaranteed to be in strictly increasing path length
   */
  
  /**
   * Writes a path to file
   * 
   * Path is appended instead of overwriting so as to prevent file corruption in concurrency
   */
  public void writeToFile(String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(new File(filename), true));
    out.println("Path Length: " + path.size());

    StringBuffer buf = new StringBuffer();
    for (int x = 0; x < path.size(); x++) {
      if (x != 0) {
        buf.append(" ");
      }
      buf.append(path.get(x));
    }
    out.println(buf.toString());

    out.flush();
    out.close();
  }
  
  /**
   * Reads a path from file
   * 
   * Expects path file to contain paths in strictly increasing length
   */
  public static Path readFromFile(Graph graph, String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
    
    Path path = new Path(graph);
    
    String pathvertices = null;
    while (br.readLine() != null) {
      pathvertices = br.readLine();
    }
    
    for (String vertex : pathvertices.split(" ")) {
      path.appendForward(Integer.parseInt(vertex));
    }
    
    br.close();
    return path;
  }
}
