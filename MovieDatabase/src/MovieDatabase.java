import java.io.*;

public class MovieDatabase
{
	//장르를 저장하는 LinkedList의 헤드노드
	public static Node genreHead;
	
	public static void main(String args[])
	{
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
			catch (Exception e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	private static void command(String input)
	{
		// TODO : 아래 문장을 삭제하고 구현해라.
		//System.out.println("<< command 함수에서 " + input + " 명령을 처리할 예정입니다 >>");
		
		String[] inputSplit = input.split("%");
		
		if(inputSplit[0].contains("INSERT"))
		{
			String inputGenreTitle = inputSplit[1];
			String inputMovieTitle = inputSplit[3];
			
			insert(inputGenreTitle, inputMovieTitle);
		}
		
		else if(inputSplit[0].contains("DELETE"))
		{
			//String inputCommand = inputSplit[0];
			String inputGenreTitle = inputSplit[1];
			String inputMovieTitle = inputSplit[3];
			
			//System.out.println("Delete Movie");
			delete(inputGenreTitle, inputMovieTitle);
		
		}
		
		else if(inputSplit[0].contains("SEARCH"))
		{
			String searchString = inputSplit[1];

			search(searchString);
		}
		
		else if(inputSplit[0].contains("PRINT"))
		{
			//System.out.println("Print All Movies");
			print();
		}
	}
	
	public static void insert(String inputGenre, String inputMovie)
	{
		Node genreNode = findNode(genreHead, inputGenre);
		
		//이미 존재하는 장르인 경우엔 그 안에 movie 삽입하고 아니면 새로 장르를 생성 
		if(genreNode != null)
		{
			//이미 존재하는 장르 
			//장르 안에 새로운 영화 노드 생성
			Node newMovieNode = new Node(inputMovie);
			
			if(findNode(genreNode.getSubNodeHead(), inputMovie) == null)
			{
				//생성하려는 영화 data가 존재하지 않는 경우만 노드를 만들어준다. 
				genreNode.setSubNodeHead(insertNode(genreNode.getSubNodeHead(), newMovieNode));				
			}
		}
		
		else
		{
			//존재하지 않는 장르
			//장르를 새로 생성 
			genreNode = new Node(inputGenre);
			
			//장르 안에 새로운 영화 노드 생성 
			Node movieHead = new Node(inputMovie);
			genreNode.setSubNodeHead(movieHead);
			
			genreHead = insertNode(genreHead, genreNode);
		}
	}
	
	public static Node insertNode(Node headNode, Node newNode)
	{
		Node currNode = headNode;
		Node prevNode = null;
		
		if(headNode == null)
		{
			//만약 비어있는 LinkedList이면 Head로 새로운 노드 지정. 
			headNode = newNode;
		}
		
		else
		{
			while(true)
			{				
				if(currNode == null)
				{					
					//currNode가 마지막 노드를 지남  
					prevNode.setNext(newNode);
					newNode.setPrev(prevNode);
					break;
				}
				
				else
				{
					//currNode가 tail을 지나지 않은 경우 
					//currNode의 item과 newNode의 item을 비교한다.					
	
					if(currNode.getItem().compareTo(newNode.getItem()) > 0)
					{
						//currGenreNode의 genreTitle이 inputGenreTitle보다 사전에서 뒤에오는 경우						
						
						if(currNode == headNode)
						{
							//조건을 만족하는 Node가 Head인 경우 (새 노드가 맨앞에 와야하는 경우)							
							newNode.setNext(currNode);
							currNode.setPrev(newNode);
							headNode = newNode;
						
							break;
						}
						
						else
						{
							//조건을 만족하는 currNode가 Head가 아닌 경우 (일반적 케이스) 
							//currNode와 전 노드 사이에 새로운 노드를 insert 한다. 				    
							newNode.setNext(currNode);
						    newNode.setPrev(currNode.getPrev());
							currNode.getPrev().setNext(newNode);
						    currNode.setPrev(newNode);
						    						
							break;
						}
					}
				}
				
				//조건 만족하는 node가 아니면 다음 노드로 이동 
				prevNode = currNode;
				currNode = currNode.getNext();
			}
		}
		
		return headNode;

	}
	
	public static void delete(String inputGenre, String inputMovie)
	{
		Node genreNode = findNode(genreHead, inputGenre);

		//입력한 장르가 존재하는 장르인지 확인.
		if(genreNode != null)
		{
			//이미 존재하는 장르이면 해당 장르 노드의 영화 노드를 검색해서 삭제한다. 
			Node movieNode; 
			
			movieNode = findNode(genreNode.getSubNodeHead(), inputMovie);
			
			if(movieNode != null)
			{
				if(movieNode == genreNode.getSubNodeHead())
				{
					//해당 노드가 headNode 인 경우  
					genreNode.setSubNodeHead(movieNode.getNext());
				}
				
				else
				{
					//해당 노드가 headNode가 아닌경우
					if(movieNode.getNext() == null)
					{
						//해당 노드가 마지막 노드인 경우 
						movieNode.getPrev().setNext(null);
					}
					else
					{
						//해당 노드가 중간 노드인 경우 
						movieNode.getPrev().setNext(movieNode.getNext());
						movieNode.getNext().setPrev(movieNode.getPrev());
					}
				}
			}
			
			//삭제가 완료 된후 해당 장르가 비었으면 장르 노드도 삭제 
			if(genreNode.getSubNodeHead() == null)
			{
				if(genreNode == genreHead)
				{
					genreHead = genreNode.getNext();
				}
				
				else if(genreNode.getNext() == null)
				{
					genreNode.getPrev().setNext(null);
				}
				else
				{
					genreNode.getPrev().setNext(genreNode.getNext());
					genreNode.getNext().setPrev(genreNode.getPrev());
				}
				
				//System.out.println("delete genre node");
			}
		}
	}
	
	public static void search(String inputString)
	{
		Node currGenreNode = genreHead;
		int numResult = 0;
		
		//GenreLinkedList를 돌면서 MovieLinkedList에 저장된 값 중 inputString 포함하고 있는 값들을 출력 
		while(currGenreNode != null)
		{
			Node currMovieNode = currGenreNode.getSubNodeHead();
			
			while(currMovieNode != null)
			{
				if(currMovieNode.getItem().contains(inputString))
				{
					System.out.println("(" + currGenreNode.getItem() + ", " + currMovieNode.getItem() + ")");
					numResult ++;
				}
				currMovieNode = currMovieNode.getNext();
			}
			
			currGenreNode = currGenreNode.getNext();
		}
		
		if(numResult == 0)
			System.out.println("EMPTY");
	}
	
	public static void print()
	{	
		Node currGenreNode = genreHead; //Genre LinkedList의 첫번째 노드 
		
		//만약 DB가 비어있으면 EMPTY 출력 
		if(genreHead == null)
			System.out.println("EMPTY");
		
		//GenreLinkedList의 GenreNode들에서 MovieLinkedList에 저장된 값들을 출력 
		while(currGenreNode != null)
		{
			Node currMovieNode = currGenreNode.getSubNodeHead();
		
			while(currMovieNode != null)
			{
			System.out.println("(" + currGenreNode.getItem() + ", " + currMovieNode.getItem() + ")");
			currMovieNode = currMovieNode.getNext();
			}
			
			currGenreNode = currGenreNode.getNext();
		}
	}
	
	//인풋한 이름과 동일한 노드를 찾고 없으면 null 리턴 
	public static Node findNode(Node headNode, String inputString)
	{
		Node currNode = headNode; //LinkedList의 첫번째 노드 
			
		while(currNode != null)
		{	
			if(currNode.getItem().compareTo(inputString) == 0)
					return currNode;
			
			currNode = currNode.getNext();
		}
				
		return null; //찾지 못하면 null return
	}
}

//LinkedList의 Node 클래스 
class Node 
{
	private Node next;
	private Node prev;
	private Node subNodeHead; //서브노드의 헤드 (무비노드는 장르노드의 서브노드) 
	private String item;  
	
	public Node(String inputString)
	{
		item = inputString;
		next = null;
		prev = null;
		subNodeHead = null;
	}
	
	public void setNext(Node nextNode)
	{
		next = nextNode;
	}
	
	public Node getNext()
	{
		return next;
	}
	
	public void setPrev(Node prevNode)
	{
		prev = prevNode;
	}
	
	public Node getPrev()
	{
		return prev;
	}
	
	public void setSubNodeHead(Node inputSubNodeHead)
	{
		subNodeHead = inputSubNodeHead;
	}
	
	public Node getSubNodeHead()
	{
		return subNodeHead;
	}
	
	public String getItem()
	{
		return item;
	}
}