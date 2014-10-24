MovieChainRunner
================

Additional information can be found on the project wiki

##Project Specifications

####Usage Info

######Running
```
git clone https://github.com/xzhangpeijin/MovieChainRunner.git
cd MovieChainRunner
java -jar MovieChainRunner.jar <arguments>
```

######Arguments

```
 * -h                Print usage info
 * -c                Create full graph from movie list
 * -s                Split full graph into components
 * -d [graph]        Reduce a given graph
 * -r [graph]        Run on given graph
 * -p [graph]        Print a given graph to graphml format
```

####Data

All files are under /data

* MovieList - Input of all movie titles
* FullGraph - Initial graph 
* Additional files - Graphs after processing

Results will be outputted under /results/[Graph]Results.txt

####Graph file format

First line is the name of the graph

Second line is an integer n, the size of the graph (number of vertices)

Next n lines are the names of vertices, where the ith line is the name of the ith vertex

Next n lines are space separated integers, representing the edges

If an integer x is on the ith line, then there exists an edge from i to x in the graph

####Code structure

`Main.java` Handles running of programs and parsing command line arguments

`runners` Classes for handling main program functions
*  `GraphMaker.java` Creates a graph given a list of movie titles
*  `GraphSearcher.java` Launches a graph search by using one of the walkers/search algorithms 
*  `GraphSplitter.java` Splits a graph into connected components

`utils` General utility classes
*  `Graph.java` Class for representing a graph
*  `Path.java`Class for representing a path

`walkers` Classes for doing actual graph walking and finding longest paths
*  `FastWalker.java` Walks using a pre-generated reachability map which may not be accurate but is fast
*  `SlowWalker.java` Recomputes reachability maps at each walk step
*  `DeterministicWalker.java` Deterministically finds longest paths by picking nodes with largest reachability set
