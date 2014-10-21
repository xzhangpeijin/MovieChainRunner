package main.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Graph represented as adjacency lists
 * 
 * Contains two lists, one for in edges and one for out
 * 
 * @author Peijin Zhang
 */
public class Graph
{
  private static final String NO_NAME = "NamelessGraph";
  
  // indeges[x] contains y if there exists an edge (y,x)
  private final ArrayList<Integer>[] inedges;
  // outedges[x] contains y if there exists an edge (x,y)
  private final ArrayList<Integer>[] outedges;
  
  private final ArrayList<String> vertices;
  
  private final String name;
  private final int size;
  
  @SuppressWarnings("unchecked")
  public Graph(ArrayList<String> vertices, String name) {
    this.name = name;
    this.size = vertices.size();
    this.inedges = new ArrayList[size];
    this.outedges = new ArrayList[size];
    this.vertices = new ArrayList<String>(vertices);
    
    for (int x = 0; x < size; x++) {
      inedges[x] = new ArrayList<Integer>();
      outedges[x] = new ArrayList<Integer>();
    }
  }
  
  public Graph(ArrayList<String> vertices) {
    this(vertices, NO_NAME);
  }
  
  public void addEdge(String from, String to) {
    addEdge(vertices.indexOf(from), vertices.indexOf(to));
  }
  
  public void addEdge(int from, int to) {
    inedges[to].add(from);
    outedges[from].add(to);
  }
  
  public int getSize() {
    return size;
  }
  
  public String getName() {
    return name;
  }
  
  /**
   * Writes graph to file
   * Written adjacency list is outedges
   */
  public void writeToFile(String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(new File(filename)));
    
    out.println(name);
    out.println(size);
    for (int x = 0; x < size; x++) {
      out.println(vertices.get(x));
    }
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < outedges[x].size(); y++) {
        if (y != 0) {
          out.print(" ");
        }
        out.print(outedges[x].get(y));
      }
      out.println();
    }
    
    out.flush();
    out.close();
  }
  
  public static Graph readFromFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
    
    String name = br.readLine();
    int size = Integer.parseInt(br.readLine());
    
    ArrayList<String> vertices = new ArrayList<String>();
    for (int x = 0; x < size; x++) {
      vertices.add(br.readLine());
    }
    
    Graph graph = new Graph(vertices, name);
    
    for (int x = 0; x < size; x++) {
      String[] edges = br.readLine().split(" ");
      for (int y = 0; y < edges.length; y++) {
        graph.addEdge(x, Integer.parseInt(edges[y]));
      }
    }
    
    br.close();
    
    return graph;
  }
}
