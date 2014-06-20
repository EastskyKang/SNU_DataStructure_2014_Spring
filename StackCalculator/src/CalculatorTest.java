import java.io.*;
import java.util.Stack; 
import java.lang.StringBuilder;
import java.util.EmptyStackException;

public class CalculatorTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("q") == 0)
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
		
		if(errorChecker(input) == false)
		{
			// errorChecker로 1차 검사 
			System.out.println("ERROR");
		}
		
		else
		{
			// errorChecker로 검사 한 뒤 생기는 Runtime Error들을 try catch로 잡는다. 
			
			String postfixInput = infixToPostfix(unaryGen(input)); // unary 변환 마친 input을 postfix로 번역 
			
			try
			{
				long calculationResult = postfixCal(postfixInput); // postfix 계산 

				System.out.println(postfixInput);
				System.out.println(calculationResult);	
			}
			catch (Exception e)
			{
				System.out.println("ERROR");
			}
		}
		
	}
	
	private static boolean errorChecker(String infixInput)
	{
		// infix를 Postfix로 번역하기 이전에 잘못된 input이 아닌지 점검하는 메소드
		// 0으로 나누기, 0으로 나머지 연산, 0^음수, 괄호 짝 안 맞는 경우, 다른 문자 입력 등을 에러처리 해준다. 

		if(infixInput.matches("(.*)\\d\\s+\\d(.*)"))
			// 만약 숫자와 숫자 사이에 공백이 있으면 (연산자가 없으면) 에러이다. 
			return false;
		
		if(infixInput.matches("(.*)[)]\\s*\\d(.*)"))
			// 만약 닫는 괄호와 숫자 사이에 연산자가 없으면 에러이다.
			return false;
		
		if(infixInput.matches("(.*)\\d\\s*[(](.*)"))
			// 만약 숫자와 여는 괄호 사이에 연산자가 없으면 에러이다. 
			return false;
		
		infixInput = infixInput.replaceAll(" ", "");
		infixInput = infixInput.replaceAll("\t", "");
		char[] inputToArray = infixInput.toCharArray(); // infixInput을 배열화한다. 

		// 괄호 짝을 확인한다. 
		int cnt1 = 0;
		int cnt2 = 0;
		
		for(int i = 0; i < inputToArray.length; i++)
		{
			/*
			if(inputToArray[i] == '/' && i != (inputToArray.length - 1))
			{
				// 나누기 연산이 있는 경우 0으로 나머지 연산을 하면 에러처리 한다. 
				if(inputToArray[i+1] == '0')
					return false;
			}
			
			if(inputToArray[i] == '%' && i != (inputToArray.length - 1))
			{
				// 나머지 연산이 있는 경우 0으로 나머지 연산을 하면 에러처리 한다. 
				if(inputToArray[i+1] == '0')
					return false;
			}*/
			
			if(!isOperator(inputToArray[i]) && !Character.isDigit(inputToArray[i]) &&
					inputToArray[i] != '(' && inputToArray[i] != ')')
			{
				// 만약 연산자도 숫자도 아닌 입력이 들어온 경우 
				return false;
			}
			
			if(inputToArray[i] == '~') 
				// ~ 연산자는 나중에 번역과정에서 입력되는 것이지 처음에 입력되면 에러처리 된다.  
				return false;
			
			if(inputToArray[i] == '(') cnt1 ++;
			else if(inputToArray[i] == ')') cnt2 ++;
		
		}
		
		if(cnt1 != cnt2) return false; // 소괄호 짝이 맞지 않는 경우. 
		
		// 특별한 문제가 없으면 true 리턴 
		return true;
	}
	
	private static String unaryGen(String infixInput)
	{
		// infix의 unary (-) 를 (~)로 바꿔준다. 
		infixInput = infixInput.replaceAll(" ", "");
		infixInput = infixInput.replaceAll("\t", "");
		
		char[] inputToArray = infixInput.toCharArray(); // infixInput을 배열화한다. 
		StringBuilder unaryGenBuilder = new StringBuilder();
		
		for(int i = 0; i < inputToArray.length; i++)
		{
			if(inputToArray[i] == '-')
			{
				// - 일때 이 것이 unary 인지 binary 인지 확인한다. 
				if(i == 0 || !(Character.isDigit(inputToArray[i-1]) || inputToArray[i-1]==')'))
				{
					// 맨 앞에 와 있는 - 이거나, - 앞의 문자가 숫자나 닫는 소괄호가 아니면 unary 이다. 
					// -를 ~로 바꾸어 준다.
					inputToArray[i] = '~';
				}
			}
			unaryGenBuilder.append(inputToArray[i]);
		}
				
		return unaryGenBuilder.toString();
	}
	
	private static String infixToPostfix(String infixInput)
	{
		// infix를 Postfix로 번역해주는 메소드 
		// 번역의 알고리즘은 다음과 같다. inputString을 오른쪽으로 한칸한칸 이동하며 검사한다.  
		// operand는 그대로 output으로 내보낸다.
		// stack에는 operator만 저장한다. 
		// operator가 나올때 마다 stack의 top과 비교하고 우선순위가 높으면 스택에 push, 낮으면 output으로 내보낸다.  
		
		Stack<Character> operatorStack = new Stack<Character>(); // 연산자를 임시로 저장할 스택을 생성  
		char[] inputToArray = infixInput.toCharArray(); // infixInput을 배열화한다. 
		StringBuilder postfixBuilder = new StringBuilder();
		
		for(int i = 0; i < inputToArray.length; i++)
		{
			// infixInput의 입력 문자들을 하나씩 오른쪽으로 가면서 검사한다. 
			
			if(Character.isDigit(inputToArray[i]))
			{
				// 검사 문자가 숫자인 경우.
				// 숫자는 ouput으로 내보낸다.				
				while(i < inputToArray.length && Character.isDigit(inputToArray[i]))
				{
					// 붙어있는 숫자가 있으면 한번에 쭉 내보낸다. 
					postfixBuilder.append(inputToArray[i++]);
				}
				
				postfixBuilder.append(' ');
				--i;
			}
			
			else if(inputToArray[i] == '(')
			{
				// 검사 문자가 여는 소괄호인 경우.
				 operatorStack.push(inputToArray[i]);
			}
			
			else if(inputToArray[i] == ')')
			{
				// 검사 문자가 닫는 소괄호인 경우. 
				// 여는 소괄호가 나올때까지 모든 연산자들을 pop 한다. 
				
				while(operatorStack.peek() != '(')
				{
					postfixBuilder.append(operatorStack.pop()).append(' ');
				}

				// 여는 소괄호도 pop한다. 그러나 여는 소괄호는 출력하지 않는다.
				operatorStack.pop();
			}
			
			else if(isOperator(inputToArray[i]))
			{
				// 검사 문자가 operator인 경우
				char operator = inputToArray[i];
				
				if(operator == '~')
				{
					// unary 인 경우 
					operatorStack.push(operator);
				}
				else
				{
					// binary 인 경우 
					if(operator == '^')
					{
						// 거듭제곱의 경우엔 right-associative
						while( !operatorStack.isEmpty() && operatorStack.peek() != '(' && 
								operatorPriority(operator) < operatorPriority(operatorStack.peek()))
						{
							// operatorStack이 비어있지 않고 operatorStack의 top이 소괄호가 아닌 경우 
							// 입력된 operator 의 우선순위가 operatorStack의 top 보다 우선순위가 낮거나 같으면 
							// stack의 top을 pop 한다. 
							
							postfixBuilder.append(operatorStack.pop()).append(' ');
		
						}
						
						// 새로 들어온 operator는 push 해준다.
						operatorStack.push(operator);
					}
					
					else
					{
						while( !operatorStack.isEmpty() && operatorStack.peek() != '(' && 
								operatorPriority(operator) <= operatorPriority(operatorStack.peek()))
						{
							// operatorStack이 비어있지 않고 operatorStack의 top이 소괄호가 아닌 경우 
							// 입력된 operator 의 우선순위가 operatorStack의 top 보다 우선순위가 낮거나 같으면 
							// stack의 top을 pop 한다. 
							
							postfixBuilder.append(operatorStack.pop()).append(' ');
		
						}
						
						// 새로 들어온 operator는 push 해준다.
						operatorStack.push(operator);
					}
				}
			}
		}
		
		// 입력을 전부다 검사한 후에는 stack에 아직 남아있는 operator들을 모두 pop한다.
		while(!operatorStack.empty())
			postfixBuilder.append(operatorStack.pop()).append(' ');

		return postfixBuilder.toString();
	}
	
	private static int operatorPriority(char operator)
	{
		// operator들의 우선순위를 리턴해주는 메소드 
		// 우선순위가 높을수록 큰 값을 리턴해준다 
		
		switch(operator)
		{
		case '+' : 
		case '-' : return 0;
		case '*' :
		case '/' :
		case '%' : return 1;
		case '~' : return 2;
		case '^' : return 3;
		}
		
		return 0;
	}
	
	private static boolean isOperator(char input)
	{
		// 입력된 문자가 operator인지 확인하고 맞으면 true, 아니면 false를 리턴한다. 
		
		return (input == '+' || input == '-' || input == '*' ||
				input == '/' || input == '%' || input == '^' || input == '~'); 
	}
	
	
	private static long postfixCal(String postfix) throws Exception
	{
		// postfix 로 번역된 식을 계산하는 method 

		Stack<Long> calculationStack = new Stack<Long>(); 

		try
		{
			String[] inputToArray = postfix.split(" "); // postfixInput을 공백을 구분선으로 분리하여 배열화한다.  
			
			for(int i = 0; i < inputToArray.length ; i++)
			{
				// input을 한글자씩 검사한다. 
				if(isOperator(inputToArray[i].charAt(0)))
				{
					// unary 연산자이면 - 처리 해준다. 
					if(inputToArray[i].charAt(0) == '~')
					{
						long operand = calculationStack.pop();
						long result = (-1) * operand;
						
						calculationStack.push(result);
					}
					else
					{
						// binary 연산자이면 스택에서 operand 두개를 꺼내서 연산을 해준다.			
						long operand2 = calculationStack.pop();
						long operand1 = calculationStack.pop();
						long result;
						char operator = inputToArray[i].charAt(0);
						
						switch (operator)
						{
						case '+' :
							result = operand1 + operand2;
							break;
						case '-' : 
							result = operand1 - operand2;
							break;
						case '*' :
							result = operand1 * operand2;
							break;
						case '/' :
							if(operand2 == 0)
							{
								// 나누는 수가 0이면 에러.
								throw new Exception();
							}
							else	result = operand1 / operand2;
							break;
						case '%' : 
							if(operand2 == 0)
							{
								// 나누는 수가 0이면 에러.
								throw new Exception();
							}
							else	result = operand1 % operand2;
							break;
						case '^' :
							if(operand1 == 0 && operand2 < 0 )
							{
								// 밑이 0이고 지수가 음수이면 에러가 난다. 
								throw new Exception();
							}
							else result = (long) Math.pow(operand1, operand2);
							break;
						default : // 위의 어느 것도 해당 안됨 (에러) 
							result = -1;
						}
						
						calculationStack.push(result);
					}
				}
				else
				{
					// 피연산자이면 long 타입으로 바꿔서 스택에 저장. 
					long operand = Long.parseLong(inputToArray[i]);
					
					calculationStack.push(operand);
				}
			}
		} 
		catch(EmptyStackException e)
		{
			// 연산자와 피연산자의 짝이 맞지 않는 경우 
			throw e;
		}
		
		// 계산 결과를 리턴한다.
		return calculationStack.peek();
	}
}