MovieChainRunner
================

CMU 15221 Project

Usage Info:

```
 * -h                Print usage info
 * -c                Create full graph from movie list
 * -s                Split full graph into components
 * -r [graph]        Run on given graph
 * -p [graph]        Print a given graph to graphml format
```

All files are under /data

* MovieList - Input of all movie titles
* FullGraph - Initial graph 
* Additional graphs - Graphs after processing

Results will be outputted under /results/[Graph]Results.txt

Note: All txt files under the components directory are treated as components when running -r A

Graph format:

First line is the name of the graph

Second line is an integer n, the size of the graph (number of vertices)

Next n lines are the names of vertices, where the ith line is the name of the ith vertex

Next n lines are space separated integers, representing the edges

If an integer x is on the ith line, then there exists an edge from i to x in the graph


