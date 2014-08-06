import os
import random
import networkx as nx
import matplotlib.pyplot as plt

n = 20000
d = 6
p = 0.4

G = nx.connected_watts_strogatz_graph(n, d, p)

edges = G.edges()[:]
random.shuffle(edges)
for edge in edges:
    print edge[0], edge[1], 1

"""
# draw graph
pos = nx.shell_layout(G)
print G.number_of_nodes()
print G.number_of_edges()
print G.edges()
nx.draw(G, pos)
# show graph
plt.show()
"""
