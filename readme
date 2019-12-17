The following code can be used by specifying the file path to a graph representation in a plain text file in the graph class. As for the format to be used, it must be the following:

Vertices
List of vertices defined only by the depth of the vertex, each new vertex must be on a new line
Edges
List of edges defined by the weight then the vertices listed above by zero-index, each new edge must be on a new line
Regions
List of regions defined by the edges, each new region must be on a new line.

The expected outcome should be a list of edges that are the minimum region spanning tree of the graph. This will output to the terminal. 

Things to be considered about the following repository. There are still bugs in the code preventing proper weight allocation to vertices as they are added to tree. Thus, the incorrect trees are being produced for each minimum inner-outer edge of the graph. Furthermore, this code does not invoke methods to check for possible reduction of the computed trees, so the tree given as the minimum may not in fact be the minimum.

The code in the repository has been in Java only. Class and source files are available. Comments are present to define the steps throughout the many methods, and general outlines are given for methods.

This code has been split into a three tier approach. There are the parts of the graph that compromise the graph defined as Vertex, Edge, and Region. The Graph class has properties derived from the relationships of the aforementioned classes, but it does not have information specific to those class. Then the algorithm takes the components and the properties defined by the Graph class to perform computation on, but it only alters attributes held within the components for bookkeeping purposes.

In this way, each class has a specific role being fulfilled rather than overlapping purposes. To ensure everything works properly, a TestAlgorithm class has and will be used rather than within the algorithm itself. Debugging can be flipped back and forth by setting the DEBUGGING attribute of the Algorithm class to true or false. This prints to a log file for review after the TestAlgorithm has been executed.
