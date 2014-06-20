import java.io.*;
import java.util.*;

/**
 * Subway Class는 main 함수를 가지고 있다. 
 * 
 * @author kangdongho
 *
 */

public class Subway {
	
	// HashMap은 LinkedList를 저장한다. 각 LinkedList들은 Vertex를 저장한다. key값으로는 역의 이름을 이용한다. 
	private static HashMap<String, LinkedList<Vertex>> stationHashMap = new HashMap<String, LinkedList<Vertex>>();
	// 두번째 HashMap는 key값으로 역의 ID 값을 이용한다. 역간 연결을 정의할 때 쓰인다. 
	private static HashMap<String, Vertex> idHashMap = new HashMap<String, Vertex>();
	
	/**
	 * 지하철 노선도 파일을 입력받고 콘솔에서 커맨드를 입력받아 해당 커맨드를 실행한다. 
	 * @param args
	 */
	public static void main(String args[])
	{
		// 먼저 지하철 노선도 정보들을 입력받는다. 
		String dataFile = args[0];
		subwayRouteMap(dataFile);
			
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				
		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;

				command(input);
			}
			catch (IOException e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}
	
	/**
	 * JAVA 파일 실행시 함께 입력받은 inputFile.txt 로부터 지하철 노선도에 대한 정보들을 입력받고 
	 * 이 것을 토대로 그래프를 구성한다. 
	 * @param inputFile		지하철노선도에 대한 정보를 가지고 있는 txt 파일
	 */
	
	private static void subwayRouteMap(String inputFile)
	{		
		try {
	    	// FileReader를 통해서 inputFile 파일을 읽어들인다. 
	        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	        String stringLine;
	        
	        while ((stringLine = reader.readLine()) != null) {
        		// blank line을 발견할때까지 줄 단위로 string을 읽어낸다.
	        	if(stringLine.isEmpty())
	        		break;
	        	
	        	String stationInfo[] = stringLine.split(" "); 
	        	
	        	String stationId = stationInfo[0];
	        	String stationName = stationInfo[1];
	        	String stationLine = stationInfo[2];
	        	
	        	// Vertex 생성 뒤 HashMap에 넣어둔다.
	        	// 중복되는 key값이 없으면 새로운 linkedList를 만들어서 넣고, 중복된 key가 있으면 기존 linkedList에 넣는다.
	        	Vertex stationVertex = new Vertex(stationId, stationName, stationLine);
	        	LinkedList<Vertex> vertexList;
	        	
	        	if(stationHashMap.containsKey(stationName)) {
	        		vertexList = stationHashMap.get(stationName);
	        		
	        		// 이름이 같은 역이 있으면 환승역이다. 환승처리를 해준다. 
	        		// 기존에 vertexList에 들어있었던 역들과 새로 입력된 역간에 양방향 edge를 이어준다. 
	        		
	        		for(int i = 0; i < vertexList.size(); i++) {
	        			Vertex transStation = vertexList.get(i);
	        			
	        			Edge transEdge1 = new Edge(stationVertex, 5, true);
	        			transStation.addEdge(transEdge1);
	        			Edge transEdge2 = new Edge(transStation, 5, true);
	        			stationVertex.addEdge(transEdge2);
	        		}
	        	}
	        	else 
	        		vertexList = new LinkedList<Vertex>();
	        	
	        	vertexList.add(stationVertex);
	        	stationHashMap.put(stationName, vertexList);
	        	idHashMap.put(stationId, stationVertex);
	        	
	        	// System.out.println("name: " + stationName + "\tid: " + stationId);
	        }
	        
        	while((stringLine = reader.readLine()) != null) {
        		// blank line 이하로 다시 줄 단위로 string을 읽어낸다.         		
        		String intervalInfo[] = stringLine.split(" "); 
        		
        		String startStationID = intervalInfo[0];
        		String targetStationID = intervalInfo[1];
        		long weight = Long.parseLong(intervalInfo[2]);
        		
        		Vertex startVertex = idHashMap.get(startStationID);
        		Vertex targetVertex = idHashMap.get(targetStationID);
        		
        		Edge intervalEdge = new Edge(targetVertex, weight, false);
        		
        		// startVertex와 targetVertex 사이에 edge를 생성하고 이를 startVertex의 adjacencies에 추가 
        		startVertex.addEdge(intervalEdge);
        	}
	        
	        reader.close();  
	    } catch (IOException e) {			          
	    	  System.err.println(e); // FileReading에 에러가 있으면 에러메시지 출력 
	          System.exit(1);  
	    }		
	}

	/**
	 * 그래프 생성이 완료되면 최단경로 혹은 최소환승경로를 찾는 커맨드를 입력받아 실행한다. 
	 * 최소환승경로 커맨드는 입력할 때 !와 함께 입력된다. 
	 * @param input		커맨드 입력 
	 */
	private static void command(String input)
	{	
		String commands[] = input.split(" ");
		String startStation = commands[0];
		String targetStation = commands[1];
		
		// 출발역이 환승역이거나 도착역이 환승역일 수 있다.  
		LinkedList<Vertex> startVertices = stationHashMap.get(startStation);
		LinkedList<Vertex> targetVertices = stationHashMap.get(targetStation);

		if(commands.length == 2) {
			// 최단경로를 구한다.
			
			DijkstraAlgorithm.computePaths(startVertices);
			DijkstraAlgorithm.getShortestPathTo(targetVertices);
		}
		else if(commands.length == 3) {
			if(commands[2].compareTo("!") == 0) {
				// 최소환승경로를 구한다. 
				
				DijkstraAlgorithm.computeMinTransferPaths(startVertices);
				DijkstraAlgorithm.getShortestPathTo(targetVertices);
			}	
		}
		
		// 한번 command를 실행하고 나서는 graph의 모든 distance들과 계산된 shortestPath들을 초기화해준다. 
		graphInit();
	}
	
	private static void graphInit() {
		for(Vertex v : idHashMap.values()) {
			v.setDistance(Long.MAX_VALUE);
			v.setPrevious(null);
		}
	}
}

/**
 * Graph의 Vertex 클래스이다. 하나의 vertex가 하나의 Station을 표현한다. 
 * Comparable을 implements 한다. 즉 vertex끼리는 distance 변수의 크기를 비교할 수 있다. 
 * 비교를 할 수 있도록 만드는 것은 PriorityQueue를 사용하기 위함이다. PriorityQueue를 이용하면 가장 작은 
 * distance 값을 가지고 있는 vertex를 매우 빠르게 찾을 수 있다. 
 * <p>
 * <p>
 * modification from http://www.algolist.com/code/java/Dijkstra's_algorithm
 * 
 * @author kangdongho
 * 
 */

class Vertex implements Comparable<Vertex>{
	
	private final String stationId;
	private final String stationName;
	private final String stationLine;
	private ArrayList<Edge> adjacencies; // 연결되어있는 edge들 
	private long distance;
	private Vertex previous;
	    
    public Vertex(String stationId, String stationName, String stationLine) {
		super();
		this.stationId = stationId;
		this.stationLine = stationLine;
		this.stationName = stationName;
		this.adjacencies = new ArrayList<Edge>();
		this.distance = Long.MAX_VALUE;
	}
    
	public String getStationId() {
		return stationId;
	}
	public String getStationLine() {
		return stationLine;
	}
	public String getStationName() {
		return stationName;
	}
	public long getDistance() {
		return distance;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
	public ArrayList<Edge> getAdjacencies() {
		return adjacencies;
	}
	public void setAdjacencies(ArrayList<Edge> adjacencies) {
		this.adjacencies = adjacencies;
	}
    public Vertex getPrevious() {
		return previous;
	}
	public void setPrevious(Vertex previous) {
		this.previous = previous;
	}
	
	public void addEdge(Edge newEdge) {
		this.adjacencies.add(newEdge);
	}
	public int compareTo(Vertex other)
	{
	    return Long.compare(this.distance, other.getDistance());
	}
}

/**
 * Graph의 Edge 클래스이다. 
 * <p>
 * <p>
 * modification from http://www.algolist.com/code/java/Dijkstra's_algorithm
 * 
 * @author kangdongho
 *
 */

class Edge {
	// modification from http://www.algolist.com/code/java/Dijkstra's_algorithm
	private final Vertex target;
	private final long weight;
	private final Boolean isTransfer; // 환승 Edge인지 아닌지 판단한다. 
	
    public Edge(Vertex target, long weight, Boolean isTransfer)
    {
    	this.target = target; 
    	this.weight = weight;
    	this.isTransfer = isTransfer;
    }
   
	public Vertex getTarget() {
		return target;
	}
	public long getWeight() {
		return weight;
	}
	public Boolean getTransfer() {
		return isTransfer;
	}
}

/**
 * Dijkstra 알고리즘으로 시작점으로부터 모든 vertex까지의 최단 시간을 계산해놓는다. 
 * 기본적으로 PriorityQueue가 이미 방문한 set {S}의 역할을 한다. 
 * PriorityQueue를 쓰는 것은 가장 distance가 짧은 Vertex를 찾기 쉽게 하기 위함이다. 
 * <p>
 * <p>
 * modification from http://www.algolist.com/code/java/Dijkstra's_algorithm
 * 
 * @author	kangdongho
 * 
 */

class DijkstraAlgorithm {	
	// modification from http://www.algolist.com/code/java/Dijkstra's_algorithm
	
	/**
	 * source Vertex로 부터 graph의 다른 모든 vertex 까지의 최단 경로를 구한다. 
	 * @param sourceList		sourceList는 출발역의 리스트이다. 환승역일 가능성이 있으므로 리스트로 받는다. 
	 */
	
	public static void computePaths(LinkedList<Vertex> sourceList) {
				
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>(); // PriorityQueue를 써서 검색 시간을 단축한다.
      	
		for(int i = 0; i < sourceList.size(); i++) {
			Vertex source = sourceList.get(i);
			source.setDistance(0);
	        vertexQueue.add(source);	        
		}
		      	
		while (!vertexQueue.isEmpty()) {
		    Vertex u = vertexQueue.poll(); 
		
		    // Visit each edge exiting u
		    for (Edge e : u.getAdjacencies())
		    {
		        Vertex v = e.getTarget();
		        long weight = e.getWeight();
		        long distanceThroughU = u.getDistance() + weight;	// u를 거쳐 v로 들어갔을 때의 distance
		        
		        if (distanceThroughU < v.getDistance()) {
		        	// distanceThroughU가 현재 v의 distance보다 짧으면 대체한다. 
		        	vertexQueue.remove(v);
		        	v.setDistance(distanceThroughU);
		            v.setPrevious(u);
		        	vertexQueue.add(v);
		        }
		    }
		}
	}
	
	/**
	 * source Vertex로 부터 graph의 다른 모든 vertex 까지 환승을 최소한으로 하는 최단경로를 구한다. 
	 * @param sourceList		sourceList는 출발역의 리스트이다. 환승역일 가능성이 있으므로 리스트로 받는다. 
	 */
	
	public static void computeMinTransferPaths(LinkedList<Vertex> sourceList) {
      	
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>(); // PriorityQueue를 써서 검색 시간을 단축한다.
	      	
		for(int i = 0; i < sourceList.size(); i++) {
			Vertex source = sourceList.get(i);
			source.setDistance(0);
		    vertexQueue.add(source);
		}
		
		while (!vertexQueue.isEmpty()) {
		    Vertex u = vertexQueue.poll(); 
		
		    // Visit each edge exiting u
		    for (Edge e : u.getAdjacencies())
		    {
		        Vertex v = e.getTarget();
		        long weight = e.getWeight();
		        
		        if(e.getTransfer()) {
		        	// 만약 환승을 하는 edge라면 기존의 weight에다가 2000000000 를 더해준다. 
		        	// 데이터에서 시작점부터 끝점까지 총 소요시간이 10억 미만이므로 이렇게 처리하면 환승을 최소화하는 쪽으로 알고리즘이 동작한다. 
		        	weight += 2000000000;
		        }
		        long distanceThroughU = u.getDistance() + weight;	// u를 거쳐 v로 들어갔을 때의 distance
		        
		        if (distanceThroughU < v.getDistance()) {
		        	// distanceThroughU가 현재 v의 distance보다 짧으면 대체한다. 
		        	vertexQueue.remove(v);
		        	v.setDistance(distanceThroughU);
		            v.setPrevious(u);
		        	vertexQueue.add(v);
		        }
		    }
		}
	}
	
	/**
	 * start Vertex부터 target Vertex 까지의 최단 경로를 path 리스트에 저장해준다.
	 * @param targetList		targetList는 도착역들의 리스트이다. 환승역일 가능성이 있으므로 LinkedList로 받는다.  
	 */
	public static void getShortestPathTo(LinkedList<Vertex> targetList)
	{
		List<Vertex> path = new ArrayList<Vertex>();
		Vertex target = targetList.get(0); // targetList의 역들은 모두 같은 역이고(환승역), 호선만 다르다. 
		
		for(int i = 0; i < targetList.size(); i++) {
			// 도착시간이 가장 짧은 역을 target으로 해도 무방하다. 
			Vertex vertex = targetList.get(i);
				
			if(target.getDistance() > vertex.getDistance())
				target = vertex;				
		}
		
	    for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious())
	        path.add(vertex);

	    Collections.reverse(path);
	    
	    printPath(path);
	    // 20억으로 나눈 나머지 (최소 환승을 염두에 뒀을 때)의 값이 소요시간이다. 
	    System.out.println(target.getDistance() % 2000000000); 
	}
	
	/**
	 * getShortestPathTo 메소드에서 구한 path를 List로 받아 규약에 따라 출력해준다. 
	 * @param pathList
	 */
	private static void printPath(List<Vertex> pathList) {
		for(int i = 0 ; i < pathList.size() ; i++) {
			Vertex v = pathList.get(i);			
			
			if(i != (pathList.size() - 1) && 
					v.getStationName().compareTo(pathList.get(i+1).getStationName()) == 0) {
				// 환승역인 경우 
				System.out.print("[" + v.getStationName() + "]");
				i ++;
			}
			
			else 
				System.out.print(v.getStationName());
			
			if(i != pathList.size() - 1)
				System.out.print(" ");
		}
		
		System.out.println();
	}
}