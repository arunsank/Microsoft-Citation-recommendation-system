'''
References:
http://www.cs.princeton.edu/~chazelle/courses/BIB/pagerank.htm
http://dpk.io/pagerank
'''
f = open("paper_ref_ml.txt")
c = 0
fg = open("PaperRefGraph","w")
papers = {}
graph = []
for l in f:
	c += 1	
	x = l.split()
	x[0] = x[0].strip(' \t\n\r')
	fg.write(x[0]+'\n')
	#print str(x[0])
	x[1] = x[1].rstrip('\n')
	graph.append([x[0],x[1]])

    #Code to build graph of nodes having values as total number of in-nodes and total number of out-nodes.
    #Not needed for now
#	papers[x[0]] = [papers.get(x[0],[0,0])[0] + 1,papers.get(x[0],[0,0])[1]]
#	papers[x[1]] = [papers.get(x[1],[0,0])[0] ,papers.get(x[1],[0,0])[1]+1]
#	if x[0] in papers:
#		papers[x[0]][0].append(x[1])
#		papers[x[0]][1]+=1
#	else:
#		papers[x[0]] = [[x[1]],1,[],0]
#	if x[1] in papers:
#		papers[x[1]][2].append(x[0])
#		papers[x[1]][3]+=1
#	else:
#		papers[x[1]] = [[],0,[x[0]],1]    
#for k,v in papers.items():
#    fg.write(str(k)+':'+str(v)+'\n')

f.close()
fg.close()
fpr = open("prank.txt","w")

#Function to calculate page-rank
def pagerank(graph, damping=0.85, epsilon=1.0e-8):
    inlink_map = {}
    outlink_counts = {}
    
    def new_node(node):
        if node not in inlink_map: inlink_map[node] = set()
        if node not in outlink_counts: outlink_counts[node] = 0
    
    for tail_node, head_node in graph:
        new_node(tail_node)
        new_node(head_node)
        if tail_node == head_node: continue
        
        if tail_node not in inlink_map[head_node]:
            inlink_map[head_node].add(tail_node)
            outlink_counts[tail_node] += 1
    
    all_nodes_len = len(set(inlink_map.keys()))
    for node, outlink_count in outlink_counts.iteritems():
        if outlink_count == 0:
            outlink_counts[node] = all_nodes_len
            for l_node in inlink_map: inlink_map[l_node].add(node)
    
    initial_value = 1 / all_nodes_len
    ranks = {}
    for node in inlink_map.keys(): ranks[node] = initial_value
    
    new_ranks = {}
    delta = 1.0
    n_iterations = 0
    while delta > epsilon:
        new_ranks = {}
        for node, inlinks in inlink_map.iteritems():
            new_ranks[node] = ((1 - damping) / all_nodes_len) + (damping * sum(ranks[inlink] / outlink_counts[inlink] for inlink in inlinks))
        delta = sum(abs(new_ranks[node] - ranks[node]) for node in new_ranks.keys())
        ranks, new_ranks = new_ranks, ranks
        n_iterations += 1
    
    return ranks, n_iterations

print "Graph reading complete"
prank = pagerank(graph)
for k,v in prank[0].iteritems():
	fpr.write(str(k)+':'+str(v)+'\n')
fpr.close()	