package com.kellyfj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class designed to load task dependencies from a file
 * and resolve them into dependency order
 * @author kellyfj
 */
public class SchedulerDependencyResolver {

	/**
	 * We represent each task by an integer number from 1, 2, 3...N where N is the total number of tasks. 
	 * The first line of input specifies the number N of tasks and the number M of rules, such that N ² 100 and M ² 100 .
	 * The rest of the input consists of M rules, one in each line, specifying dependencies using the following
	 * 
	 * syntax: T0 k T1 T2 T3
	 * 
	 * This rule means that task number T0 (child) depends on k tasks T1 ... Tk  (parent)
	 * Example:
	 * 5 4
	 * 3 2 1 5
	 * 2 2 5 3
	 * 4 1 3
	 * 5 1 1
	 */
	public static List<Node> loadDependenciesFromFile(String filename) throws IOException {		
		List<Node> list = new ArrayList<Node>();
		 BufferedReader reader = null;
		try {
	        reader = new BufferedReader(new InputStreamReader(SchedulerDependencyResolver.class.getClassLoader().getResourceAsStream(filename)));
	        String line;
	        boolean readFirstLine= false;
	        int numRulesExpected=0;
	        int rulesRead=0;
	        int numTasks=0;
	        while ((line = reader.readLine()) != null) {
	        	//System.out.println(line);
	        	if(!readFirstLine) {
	        		String[] s = line.split(" ");
	        		if(s.length != 2) {
	        			throw new IOException("First line of file is not of form <NumTasks> <NumRules> "+ line);
	        		}

	        		numTasks = new Integer(s[0]);
	        		if(numTasks > 100)
	        			throw new IOException("Num Tasks cannot be greater than 100 : " + numTasks);
	        		
	        		numRulesExpected = new Integer(s[1]);
	        		if(numRulesExpected > 100)
	        			throw new IOException("Num Rules cannot be greater than 100 : " + numTasks);

	        		readFirstLine = true;
	        		//Now create all the task nodes with id in range 1 to numTasks
	        		for(int i=0; i<numTasks; i++) {
	        			Node n = new Node(i+1);
	        			list.add(n);	        			
	        		}
	        	} else {
	        		rulesRead++;
	        		if(rulesRead > numRulesExpected)
	        			throw new IOException("The following rule line was not expected "+line);
	        		
	        		//Reading the rules
	        		String[] s = line.split(" ");
	        		if(s.length < 3) {
	        			throw new IOException("Rule line is not of form <TaskID> <NumEdges> <parentTaskID>: "+line);
	        		}
	        		int childNodeIndex = new Integer(s[0]);
	        		if(childNodeIndex <0 || childNodeIndex > list.size()) {
	        			throw new IOException("child task id is not in the expected range:" + line);
	        		}
	        		Node child = list.get(childNodeIndex-1);
	        		
	        		int numParents = new Integer(s[1]);
	        		for(int i=0; i<numParents; i++) {
	        			int tmpIndex = 2+i;
	        			if(tmpIndex >= s.length) {
	        				throw new IOException("number of parents less than expected " + line);
	        			}
	        			int parentNodeIndex = new Integer(s[tmpIndex]);
	        			if(parentNodeIndex <0 || parentNodeIndex > list.size()) {
		        			throw new IOException("parent task id is not in the expected range:" + line);
		        		}
	        			Node parent = list.get(parentNodeIndex-1);
	        			parent.addChildEdge(child);
	        		}
	        	}
	        
	        }
        	if(rulesRead < numRulesExpected)
    			throw new IOException("Read fewer rules than expected");
    		
	    } finally{
	    	if(reader != null) {
	    		reader.close();
	    	}
	    }
		
		return list;
	}

	/**
	 * Given a head node(task) of a directed acyclic graph return a list 
	 * of nodes that are in order of (task) execution
	 * 
	 * @param head
	 * @return
	 */
	public static List<Node> getNodesInDependencyOrder(Node head) {
		List<Node> ret = new ArrayList<Node>();
		dependencyOrderHelper(head, ret, new HashSet<Node>());
		Collections.reverse(ret); //Need pre-order traversal
		return ret;
	}
	
	/**
	 * Helper method that does effectively a post-order traversal of the directed acycylic graph (DAG)
	 * @param headNode
	 * @param resolved
	 * @param unresolved
	 */
	private static void dependencyOrderHelper(Node headNode, List<Node> resolved, Set<Node> unresolved) {
		unresolved.add(headNode);
		List<Node> edges = headNode.getChildEdges();
		for(Node edge : edges) {
			if(!resolved.contains(edge)) {
				 dependencyOrderHelper(edge, resolved, unresolved);
		   	}
		 }
		resolved.add(headNode);
		unresolved.remove(headNode);
	}
	
	/**
	 * Print the list to StdOut
	 */
	static void printList(List<Node> l) {
		
		for(Node n : l) {
			System.out.print(n.getId() + " ");
		}
		System.out.println("");
	}
	
	/**
	 * Normally I would use JUnit but I am using this main method for a tester
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* Sample Input
		3 2 1 5
		2 2 5 3
		4 1 3
		5 1 1
	      (1) ------
	        \      |
	         \	   V
		      \   (5)
		       \ / |
		       (3) |
		      /  \ |
		    (4)   (2)
		*/
		/*
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		
		n1.addChildEdge(n5);
		n1.addChildEdge(n3);

		n5.addChildEdge(n3);
		n5.addChildEdge(n2);
		n3.addChildEdge(n2);
		n3.addChildEdge(n4);
		
		List<Node> l = new ArrayList<Node>();
		l.add(n1);
		l.add(n2);
		l.add(n3);
		l.add(n4);
		l.add(n5);
		List<Node> depOrder = SchedulerDependencyResolver.getNodesInDependencyOrder(n1);		
		SchedulerDependencyResolver.printList(depOrder);
		*/
		try {
			List<Node> fromFile = SchedulerDependencyResolver.loadDependenciesFromFile("input.txt");
			List<Node> depOrder =SchedulerDependencyResolver.getNodesInDependencyOrder(fromFile.get(0));
			System.out.println("In dependency order:");
			SchedulerDependencyResolver.printList(depOrder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
