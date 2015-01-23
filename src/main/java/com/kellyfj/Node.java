package com.kellyfj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Node in our Graph of dependencies
 * If an edge exists it 
 * @author kellyfj
 */
public class Node implements Comparable<Node>{
	
	private int id;
	private List<Node> edges = new ArrayList<Node>();
	
	public Node(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Node> getChildEdges() {
		//Sort to ensure lower numbers are listed first for Nodes/Tasks at the same level
		Collections.sort(edges, Collections.reverseOrder());
		return edges;
	}
	public void addChildEdge(Node n) {
		edges.add(n);
	}
	
	@Override
	public int compareTo(Node that) {
		 if (this == that) return 0;

		 //primitive numbers follow this form
		 if (this.id < that.id) return -1;
		 if (this.id > that.id) return 1;
		 return 0;
	}

	
}
