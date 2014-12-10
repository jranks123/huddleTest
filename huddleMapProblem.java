import java.io.*;
import java.util.*;
import java.lang.*;


class Edge {
	String destinationNode;
	Integer weight;

	Edge(String d, Integer w){
		destinationNode = d;
		weight = w;
	}
}

class Node implements Comparable<Node>, Cloneable{
	String nodeName;
	ArrayList<Edge> outEdges;
	double cost;
	boolean known;

	Node(String curNode){
		outEdges = new ArrayList<Edge>();
		nodeName = curNode;
		cost = Double.POSITIVE_INFINITY;
		known = false;
	}

	void addEdge(String destNode, Integer weight){
		Edge edge = new Edge(destNode, weight);
		outEdges.add(edge);
	}

	ArrayList<Edge> getEdges(){
		return outEdges;
	}

	double getCost(){
		return cost;
	}

	public Object clone(){
		try{
	    	return super.clone();
	   	}
	    catch( CloneNotSupportedException e )
	  	{
	    	return null;
	    }
     } 

    //makes Node sortable by cost
	@Override
	public int compareTo(Node o) {
		if (cost > o.getCost()){
			return 1;
		}else if(cost < o.getCost()){
			return -1;
		}else{
			return 0;
		}
	}
}


class Route{
	String path;
	double weight;

	Route(String p, double w){
		path = p;
		weight = w;
	}

	double getWeight(){
		return weight;
	}
}

//my main Datastructure composes of a map of all nodes and a map of all edges. This allows the program to 
//efficiently return all edges belonging to a node, the distance between 2 nodes or an instance of node.
class HashList{
	Map<String, Node> nodes;
	Map<String, Integer> edges;

	HashList(Map<String, Node> n, Map<String, Integer> e){
		nodes = n;
		edges = e;
	}

	HashList(Map<String, Node> n, Map<String, Integer> e, String l){
		nodes = n;
		edges = e;
	}

	Integer getDistance(String start, String end){
		return edges.get(new String(start+end));
	}

	Node getNode(String nodeName){
		return nodes.get(nodeName);
	}

	ArrayList<Edge> getNodeEdges(String nodeName){
		return getNode(nodeName).getEdges();
	}

}

public class huddleMapProblem {

	//Returns all routes between 2 nodes that are less than or equal to maxPath and hae a weight of less than maxWeight.
	public static ArrayList<Route> exploreGraph(HashList graph, String start, Route curRoute, double maxPath, double maxWeight, ArrayList<Route> routes){
		//This try/catch deals with the case when there is a node with no out edges
		try{
			ArrayList<Edge> outEdges = graph.getNodeEdges(start);
			Route newRoute;
			double newWeight;
			if (curRoute.path.length() <= maxPath && curRoute.weight < maxWeight ){
				for(Edge e : outEdges){
					newWeight = curRoute.weight + e.weight;
					if(newWeight < maxWeight){
						newRoute = new Route(curRoute.path + e.destinationNode, curRoute.weight + e.weight);
						routes.add(newRoute);
						routes = exploreGraph(graph, e.destinationNode, newRoute, maxPath, maxWeight, routes);
					}
				}
			}
		}
		catch(NullPointerException e){
		}
		return routes;
	}



	//Returns the Node with the lost cost
	public static Node getLowestNode(Map<String, Node> nodes){
		ArrayList<Node> unknownNodes = new ArrayList<Node>();
		for ( Node n : nodes.values() ) {
			if(n.known == false){
    			unknownNodes.add(n);
    		}
    	}
    	return Collections.min(unknownNodes);
	}

	//Clones every value in a Map of nodes and returs a new Map with those values
	public static Map<String, Node> createNodeMapClone(Map<String, Node> nodes){
		Map<String, Node> newNodes = new HashMap<String, Node>();
		Node newNode;
		for ( Node n : nodes.values() ) {
			newNode = (Node)n.clone();
			newNodes.put(new String(newNode.nodeName), newNode);
		    }
		return newNodes;
	}


	//Print the distance between 2 nodes, or NO SUCH ROUTE if no route exisits
	public static void routeLength(String route, HashList graph){
		String[] lineArray = route.split("(?!^)");
		Integer routeLength = 0;
		for(int i = 0; i < lineArray.length-1; i++){
			try{
				routeLength += graph.getDistance(lineArray[i], lineArray[i+1]);
			}
			catch(NullPointerException e){
				System.out.println("NO SUCH ROUTE");
				return;
			}
		}
		System.out.println(Integer.toString(routeLength));
	}


	//Prints the number of routes that meet a certain criteria. If maxWeight is Double.POSITIVE_INFINITY
	//then the number of junctions in the route will be the limiting variable and if maxPath is Double.DOUBLE_INFINITY 
	//then the weight of the route will be the limiting variable.If the operator variable is '==' it will return 
	//paths that exactly meet the maximum limiting variable, otherwise it it will 
	//paths that are equal or less than the limiting variable.
	public static void getNumberOfRoutes(String testType, String operator, double max, String start, String end, HashList graph){
		double maxPath; 
		double maxWeight;
		if(testType.equals("JunctionTest")){
			maxPath = max;
			maxWeight =  Double.POSITIVE_INFINITY;
		}else if(testType.equals("WeightTest")){
			maxPath =  Double.POSITIVE_INFINITY;
			maxWeight = max;
		}else{
			System.out.println(new String("Invalid test case entered"));
			return;
		}
		ArrayList<Route> routes = new ArrayList<Route>();
		Route curRoute = new Route(start, 0);
		routes = exploreGraph(graph, start, curRoute, maxPath, maxWeight, routes);
		Integer count = 0;
		for(Route r : routes){
			if(r.path.endsWith(end)){
				if(operator.equals("equals")){
					//System.out.println("Hello");
					if (r.path.length() == maxPath+1){
						count++;
					}
				}else if (operator.equals("upto")){
					count++;
				}else{
					System.out.println("Invalid operator entered");
					return;
				}
			}
		}
		System.out.println(Integer.toString(count));
	}

	//Print shortest route between 2 nodes
	public static void getShortestRoute(String start, String end, HashList oldGraph){
		Map<String, Node> nodes = createNodeMapClone(oldGraph.nodes);
		HashList graph = new HashList(nodes, oldGraph.edges, start);
		graph.getNode(start).cost = 0;
		ArrayList<Edge> outEdges = new ArrayList<Edge>();
		int numUnknownNodes = graph.nodes.size();
		int roundNumber = 1;
		Node lowestNode;
		while(numUnknownNodes > 0){
			//This deals with test case 9 by setting the cost of the start node to infinity and know to false
			//after the first iteration 
			if(roundNumber == 2 && start.equals(end)){
				graph.getNode(start).cost = Double.POSITIVE_INFINITY;
				graph.getNode(start).known = false;
			}
			lowestNode = getLowestNode(graph.nodes);
			graph.getNode(lowestNode.nodeName).known = true;
			numUnknownNodes--;
			roundNumber++;
			outEdges = graph.getNodeEdges(lowestNode.nodeName);
			for(Edge e: outEdges){
				//This try/catch deals with the case when there is a node with no out edges
				try{
					graph.getNode(e.destinationNode).cost = Math.min(graph.getNode(e.destinationNode).cost, 
						graph.getDistance(lowestNode.nodeName,e.destinationNode)+graph.getNode(lowestNode.nodeName).cost);
				}
				catch(NullPointerException exception){
				}
			}
		}
		double result = graph.getNode(end).cost;

		System.out.println((int)result);
	}

	public static HashList readInputFile(String arg) throws IOException, FileNotFoundException{
		File file= new File(arg);
	    BufferedReader in = new BufferedReader(new FileReader(file));
	    String line;
	   	Map<String, Node> nodes = new HashMap<String, Node>();
	   	Map<String, Integer> edges = new HashMap<String, Integer>();
	    while ((line = in.readLine()) != null) {
	    	if(line.length() >  0){
		    	String start = line.substring(0,1);
				String end = line.substring(1,2);
				int weight = Integer.parseInt(line.substring(2));
				//prevents duplicate edges
				if (edges.get(start+end) == null){		        	
					edges.put( new String(start+end), weight);
		        	//prevents duplicate nodes
			        if (nodes.get(start) == null){
			        	nodes.put( new String(start), new Node(start));
			        }
			        nodes.get(start).addEdge(end, weight);
			    }
			}
	    }
	   	return new HashList(nodes, edges);
	}

	public static ArrayList<String> readTestFile(String arg) throws IOException, FileNotFoundException{
		File file= new File(arg);
	    BufferedReader in = new BufferedReader(new FileReader(file));
	    String line;
	    ArrayList<String> tests = new ArrayList<String>();
	    while ((line = in.readLine()) != null) {
	    	if(line.length() >  0){
		    	tests.add(line);
			}
	    }
	   	return tests;
	}

	//This reads in the data from an input file and fills the datastructure with the information
	public static void main(String[] args)  throws IOException, FileNotFoundException{
	    HashList graph = readInputFile(args[0]);
	    ArrayList<String> tests = readTestFile(args[1]);
	    runTests(graph, tests);	

	}

	//runs the tests
	public static void runTests(HashList graph, ArrayList<String> tests){

		for(String t: tests){
			String[] lineArray = t.split(" ");
			String testType = lineArray[0];
			if(lineArray[0].equals("routeLength")){
					 routeLength(lineArray[1], graph);
			}else if(lineArray[0].equals("getNumberOfRoutes")){
					 getNumberOfRoutes(lineArray[1], lineArray[2], Integer.parseInt(lineArray[3]), lineArray[4], lineArray[5], graph);
			}else if(lineArray[0].equals("getShortestRoute")){
					 getShortestRoute(lineArray[1], lineArray[2], graph);
            }

		}
	}
}


