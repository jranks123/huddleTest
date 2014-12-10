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

public class matrix {

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

	//Returns the number of routes that meet a certain criteria. If maxWeight is Double.POSITIVE_INFINITY
	//then the number of junctions in the route will be the limiting variable and if maxPath is Double.DOUBLE_INFINITY 
	//then the weight of the route will be the limiting variable.If the operator variable is '==' it will return 
	//paths that exactly meet the maximum limiting variable, otherwise it it will 
	//paths that are equal or less than the limiting variable.
	public static Integer getNumberOfRoutes(HashList graph, double maxWeight, double maxPath, String start, String end, String operator){
		ArrayList<Route> routes = new ArrayList<Route>();
		Route curRoute = new Route(start, 0);
		routes = exploreGraph(graph, start, curRoute, maxPath, maxWeight, routes);
		Integer count = 0;
		for(Route r : routes){
			if(r.path.endsWith(end)){
				if(operator.equals("==")){
					if (r.path.length() == maxPath+1){
						count++;
					}
				}else{
					count++;
				}
			}
		}
		return count;
	}

	//Return the distance between 2 nodes, or NO SUCH ROUTE if no route exisits
	public static String routeLength(String route, HashList graph){
		String[] lineArray = route.split("-");
		Integer routeLength = 0;
		for(int i = 0; i < lineArray.length-1; i++){
			try{
				routeLength += graph.getDistance(lineArray[i], lineArray[i+1]);
			}
			catch(NullPointerException e){
				return "NO SUCH ROUTE";
			}
		}
		return Integer.toString(routeLength);
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

	//Returns shortest route between 2 nodes
	public static double getShortestRoute(Map<String, Node> nodes, Map<String, Integer> edges, String start, String end){
		nodes = createNodeMapClone(nodes);
		HashList graph = new HashList(nodes, edges, start);
		graph.getNode(start).cost = 0;
		ArrayList<Edge> outEdges = new ArrayList<Edge>();
		int numUnknownNodes = graph.nodes.size();
		int roundNumber = 1;
		Node lowestNode;
		while(numUnknownNodes > 0){
			//This deals with test case 9 by setting the cost of the start node to infinity and know to false
			//after the first iteration 
			if(roundNumber == 2 && start == end){
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

		return result;
	}

	//This reads in the data from an input file and fills the datastructure with the information
	public static void main(String[] args) throws IOException, FileNotFoundException{
	    File file = new File(args[0]);
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
	    HashList graph = new HashList(nodes, edges);
	    runTests(graph, nodes, edges);	

	}

	//runs the tests
	public static void runTests(HashList graph, Map<String, Node> nodes, Map<String, Integer> edges){
		//Test 1
		System.out.println(routeLength("A-B-C", graph));

		//Test 2
		System.out.println(routeLength("A-D", graph));

		//Test 3
		System.out.println(routeLength("A-D-C", graph));

		//Test 4
		System.out.println(routeLength("A-E-B-C-D", graph));

		//Test 5
		System.out.println(routeLength("A-E-D", graph));

		//Test 6
		System.out.println(getNumberOfRoutes(graph, Double.POSITIVE_INFINITY, 3, "C", "C", "<"));

		//Test 7
		System.out.println(getNumberOfRoutes(graph, Double.POSITIVE_INFINITY, 4, "A", "C", "=="));

		//Test 8
		System.out.println((int)getShortestRoute(nodes, edges, "A", "C"));

		//Test 9

		System.out.println((int)getShortestRoute(nodes, edges, "B", "B"));

		//Test 10
		System.out.println(getNumberOfRoutes(graph, 30, Double.POSITIVE_INFINITY, "C", "C", "<"));
	}
}


