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
 * Graph represented as adjacency lists
 * 
 * Contains two lists, one for in edges and one for out
 * 
 * @author Peijin Zhang
 */
public class Graph {
  private static final String NO_NAME = "NamelessGraph";

  // indeges[x] contains y if there exists an edge (y,x)
  private final List<Integer>[] inedges;
  // outedges[x] contains y if there exists an edge (x,y)
  private final List<Integer>[] outedges;

  private final List<String> vertices;

  private final int size;
  private String name;

  @SuppressWarnings("unchecked")
  public Graph(List<String> vertices, String name) {
    this.name = name;
    this.size = vertices.size();
    this.inedges = new List[size];
    this.outedges = new List[size];
    this.vertices = new ArrayList<String>(vertices);

    for (int x = 0; x < size; x++) {
      inedges[x] = new LinkedList<Integer>();
      outedges[x] = new LinkedList<Integer>();
    }
  }

  public Graph(List<String> vertices) {
    this(vertices, NO_NAME);
  }

  public void addEdge(String from, String to) {
    addEdge(vertices.indexOf(from), vertices.indexOf(to));
  }

  public void addEdge(int from, int to) {
    inedges[to].add(from);
    outedges[from].add(to);
  }

  public int size() {
    return size;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getVertices() {
    return new ArrayList<String>(vertices);
  }

  public List<Integer> getBothEdges(String vertex) {
    return getBothEdges(vertices.indexOf(vertex));
  }

  public List<Integer> getBothEdges(int vertex) {
    Set<Integer> result = new HashSet<Integer>(inedges[vertex]);
    result.addAll(outedges[vertex]);
    return new LinkedList<Integer>(result);
  }

  public List<Integer> getInEdges(String vertex) {
    return getInEdges(vertices.indexOf(vertex));
  }

  public List<Integer> getInEdges(int vertex) {
    return new LinkedList<Integer>(inedges[vertex]);
  }

  public List<Integer> getOutEdges(String vertex) {
    return getOutEdges(vertices.indexOf(vertex));
  }

  public List<Integer> getOutEdges(int vertex) {
    return new LinkedList<Integer>(outedges[vertex]);
  }
  
  public boolean containsEdge(String from, String to) {
    return containsEdge(vertices.indexOf(from), vertices.indexOf(to));
  }
  
  public boolean containsEdge(int from, int to) {
    return outedges[from].contains(to);
  }
  
  /*
   * GraphML writing format:
   * 
   * GraphML is a graph format based on XML that's used for YED to display graphs
   */

  /**
   * Writes graph to graphml format
   */
  public void writeGraphML(String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(new File(filename)));

    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.print("<graphml");
    out.print(" xmlns=\"http://graphml.graphdrawing.org/xmlns\"");
    out.print(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
    out.print(" xmlns:y=\"http://www.yworks.com/xml/graphml\"");
    out.print(" xmlns:yed=\"http://www.yworks.com/xml/yed/3\"");
    out.print(" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns ");
    out.println(" http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd\">");
    out.println("\t<key for=\"node\" id=\"d1\" yfiles.type=\"nodegraphics\"/>");
    out.format("\t<graph id=\"%s\" edgedefault=\"directed\">%n", name);

    for (int x = 0; x < size; x++) {
      out.format("\t\t<node id=\"%s\">%n", vertices.get(x));
      out.println("\t\t\t<data key=\"d1\">");
      out.println("\t\t\t\t<y:ShapeNode>");
      out.format(
          "\t\t\t\t\t<y:NodeLabel visible=\"true\" "
              + "autoSizePolicy=\"content\" fontFamily=\"Dialog\" "
              + "fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" "
              + "hasLineColor=\"false\" modelName=\"custom\" textColor=\"#000000\">"
              + "%s</y:NodeLabel>%n", x);
      out.println("\t\t\t\t</y:ShapeNode>");
      out.println("\t\t\t</data>");
      out.println("\t\t</node>");
    }

    for (int x = 0; x < size; x++) {
      for (int y = 0; y < outedges[x].size(); y++) {
        out.format("\t\t<edge source=\"%s\" target=\"%s\"/>%n",
            vertices.get(x), vertices.get(outedges[x].get(y)));
      }
    }

    out.println("\t</graph>");
    out.println("</graphml>");

    out.flush();
    out.close();
  }

  /*
   * Graph serialization specification:
   * 
   * First line - Graph name Second line - n (Integer representing graph size IE
   * number of vertices)
   * 
   * Next n lines - Names of the n vertices
   * 
   * Next n lines - Space separated integers, each integer represents an edge
   * from vertex i to that integer. IE if on line 3 we have "1 5", then (3,1)
   * and (3,5) are edges
   */

  /**
   * Writes graph to file
   */
  public void writeToFile(String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(new File(filename)));

    out.println(name);
    out.println(size);

    for (int x = 0; x < size; x++) {
      out.println(vertices.get(x));
    }

    for (int x = 0; x < size; x++) {
      StringBuffer buf = new StringBuffer();
      for (int e : outedges[x]) {
        buf.append(e);
        buf.append(" ");
      }
      out.println(buf.toString().trim());
    }

    out.flush();
    out.close();
  }

  /**
   * Reads graph from file
   */
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
      String nextline = br.readLine();
      if (nextline.length() > 0) {
        String[] edges = nextline.split(" ");
        for (int y = 0; y < edges.length; y++) {
          graph.addEdge(x, Integer.parseInt(edges[y]));
        }
      }
    }

    br.close();

    return graph;
  }
}
