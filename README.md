MovieChainRunner
================

CMU 15221 Project

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
* Additional graphs - Graphs after processing

Results will be outputted under /results/[Graph]Results.txt

####Graph file format

First line is the name of the graph

Second line is an integer n, the size of the graph (number of vertices)

Next n lines are the names of vertices, where the ith line is the name of the ith vertex

Next n lines are space separated integers, representing the edges

If an integer x is on the ith line, then there exists an edge from i to x in the graph
