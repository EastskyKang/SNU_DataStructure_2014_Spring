import java.io.*; // 입력을 받기 위해 이 라이브러리가 필요하다.
//import java.lang.Object; // String 관련 메소드들을 쓰기 위해서 String 라이브러리를 불러온다.
//import java.lang.reflect.*; // Array 관련 메소드들을 쓰기 위해서 불러온다.
import java.util.Arrays;

public class BigInteger
{	
	public static void main(String args[])
	{
		// 입력을 받기 위한 작업이다.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// quit가 나올 때 까지 입력을 받아야 하므로
		// while(true) 문을 사용하여 계속 반복한다.
		while (true)
		{
			try   // try 와 catch 문은 오류 발생을 감지한다.
			{
				String input = br.readLine();	// 한 줄을 입력 받아 input 문자열에 저장
								
				if (input.compareTo("quit") == 0)
				{
					// quit 라고 입력받았을 경우 종료한다.
					// 종료하려면 while 문을 빠져나와야 하므로 break를 사용한다.
					// System.out.println("프로그램을 종료합니다.");
					break;
				}

				// quit가 아니라면 식을 계산해야 한다.
				calculate(input);
			}
			catch (Exception e)
			{
				// 만약 try { } 안에서 오류가 발생했다면 이곳으로 오게 된다.
				// 이렇게 함으로써 입력을 이상하게 했을 경우 발생하는 오류를 방지한다.
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	// 이 함수에서 입력받은 input 문자열을 이용하여 계산을 실시한다.
	// 위의 main 함수를 완벽하게 이해한 뒤 이 함수의 내용을 자유롭게 구성해보라.
	private static void calculate(String input)
	{		
		// 아래 코드는 입력을 정상적으로 받는지 테스트하는 부분이다.
		// System.out.println("<< calculate 함수에서 " + input + "을 계산할 예정입니다 >>");
		
		String inputWorked; // input을 프로그램이 이해할 수 있게 처리해준다. 
		String resultIntegerString; // 최종 연산결과값이 저장될 String
		
		// input에 들어온 공백들을 제거해준다.
		inputWorked = input.replace(" ", "");
		
		// 맨앞에 부호가 없으면 +를 붙여준다. 
		// 따라서 2개의 bigInteger 모두 부호를 포함하게 된다. 
		if(!inputWorked.startsWith("+") && !inputWorked.startsWith("-"))
			inputWorked = "+" + inputWorked;

		// 중복된 연산자는 치환해준다. (+-, ++, -+, --)
		inputWorked = inputWorked.replace("+-", "-");
		inputWorked = inputWorked.replace("-+", "-");
		inputWorked = inputWorked.replace("++", "+");
		inputWorked = inputWorked.replace("--", "+");

		// 아래부터 본격적인 연산 처리.
		// 곱셈, 뺄셈, 덧셈 순서로 분기 처리한다. 
		if(inputWorked.contains("*"))
		{
			// * 연산자가 발견되면 곱셈 연산 
			// 곱셈 연산자를 기준으로 String을 분리해준다. (integer1, integer2)
			// integer1과 integer2는 부호를 포함하고 있을 수 있다.
			String[] integers = inputWorked.split("\\*");
			String integer1 = integers[0];
			String integer2 = integers[1];
			
			// integer1 과 integer2를 곱하기 한다. (부호는 일단 제외) 
			resultIntegerString = mul(unsignStringInteger(integer1), unsignStringInteger(integer2));
			
			// 부호를 붙여줌. (음수의 경우)
			if(integer1.contains("+") && integer2.contains("-") || 
					integer1.contains("-") && integer2.contains("+"))
			{ 
				//곱의 결과가 음수 
				resultIntegerString = "-" + resultIntegerString;				
			}
		}
		
		else
		{
			//곱셈 연산자가 없으면 덧셈 연산과 뺄셈 연산 처리 			
			if(inputWorked.contains("-"))
			{
				//뺄셈 연산자가 발견되면 -A+B / -A-B / +A-B 셋중 하나.
				if(inputWorked.contains("+"))
				{
					// -A+B 이거나 +A-B 이다. 
					// 각각의 경우를 나눠서 처리해줌. 
					if(inputWorked.startsWith("-"))
					{
						//-A+B의 케이스 
						//+ 기준으로 integer1, integer2를 분리해준다. 
						String[] integers = inputWorked.split("\\+");
						String integer1 = integers[0];
						String integer2 = integers[1];
						
						// 뺄셈 연산 
						resultIntegerString = sub(unsignStringInteger(integer1), unsignStringInteger(integer2));
						
						// A, B의 크기를 비교해서 부호를 붙여준다. 
						if(compareIntegers(unsignStringInteger(integer1), unsignStringInteger(integer2)) == 1)
						{
							//A>B 이므로 이 경우엔 부호가 음수가 된다. 
							resultIntegerString = "-" + resultIntegerString;
						}
					}
					else 
					{
						//+A-B의 케이스 
						String[] integers = inputWorked.split("\\-");
						String integer1 = integers[0];
						String integer2 = integers[1];
											
						// 뺄셈 연산 
						resultIntegerString = sub(unsignStringInteger(integer1), unsignStringInteger(integer2));
						
						// A, B의 크기를 비교해서 부호를 붙여준다. 
						if(compareIntegers(unsignStringInteger(integer1), unsignStringInteger(integer2)) == -1)
						{
							//A<B 이므로 이 경우엔 부호가 음수가 된다. 
							resultIntegerString = "-" + resultIntegerString;
						}
					}
				}
				
				else 
				{
					//-A-B 이다.
					//-(A+B)로 처리해준다. 
					String[] integers = inputWorked.split("\\-");
					String integer1 = integers[1];
					String integer2 = integers[2];
					
					resultIntegerString = add(unsignStringInteger(integer1), unsignStringInteger(integer2));
					resultIntegerString = "-" + resultIntegerString;
				}
			}
			
			else
			{
				//뺄셈 연산자가 발견되지 않으면 +A+B
				String[] integers = inputWorked.split("\\+");
				String integer1 = integers[1];
				String integer2 = integers[2];
				
				resultIntegerString = add(unsignStringInteger(integer1), unsignStringInteger(integer2));
			}
		}
		
		//결과 값을 출력해준다. 
		System.out.println(resultIntegerString);
		//System.out.println("length : " + resultIntegerString.length());
	}
	
	//덧셈 연산 메소드 
	private static String add(String stringInteger1, String stringInteger2)
	{
		//먼저 stringInteger1과 stringInteger2 크기 비교. (integer1이 더 커야함)
		if(compareIntegers(stringInteger1, stringInteger2) == -1)
		{
			String temp;
			temp = stringInteger1;
			stringInteger1 = stringInteger2;
			stringInteger2 = temp;
		}
		
		//결과값을 저장할 String
		String resultString;
		
		//integer1과 integer2의 길
		int lengthOfInteger1 = stringInteger1.length();
		int lengthOfInteger2 = stringInteger2.length();
		
		//연산의 편의를 위해서 string을 뒤집어줌.
		String reversedStringInteger1 = reverseString(stringInteger1);
		String reversedStringInteger2 = reverseString(stringInteger2);
		String reversedStringResult;
		
		//reversedInteger1과 reversedInteger2, 연산결과값을 저장하는 배열.
		char[] charReversedInteger1 = reversedStringInteger1.toCharArray();
		char[] charReversedInteger2 = reversedStringInteger2.toCharArray();
		int[] intReversedSumResult = new int[lengthOfInteger1 + 1];	//연산결과를 0번째 index부터 저장한 배열 (나중에 뒤집어 줘야함.)
		
		for(int i = 0; i < lengthOfInteger1; i++)
		{
			//각 숫자의 i번째 자리수 
			int positionalNumberOfInteger1 = Character.getNumericValue(charReversedInteger1[i]);
			int positionalNumberOfInteger2;
			int sumOfPositionalNumber;

			if(i < lengthOfInteger2)  
				positionalNumberOfInteger2 = Character.getNumericValue(charReversedInteger2[i]);
			else  //integer2의 자리수를 넘어가면 0으로 처리 
				positionalNumberOfInteger2 = 0;
			
			//자리수들의 합 (받아올림도 반영)
			sumOfPositionalNumber = intReversedSumResult[i] + positionalNumberOfInteger1 + positionalNumberOfInteger2;
			
			//자리수들의 합을 10으로 나눈 나머지를 배열에 저장하고 받아올려야 하면 배열의 다음 자리에 1저장. 
			intReversedSumResult[i] = sumOfPositionalNumber % 10;
			intReversedSumResult[i+1] = sumOfPositionalNumber / 10;
		}
		
		//계산 결과를 String으로 바꿔줌.
		reversedStringResult = Arrays.toString(intReversedSumResult).replace(", ", "").replace("[", "").replace("]", "");
		
		//reversedStringResult를 다시 뒤집어줌.
		resultString = reverseString(reversedStringResult);
		
		//앞에 붙은 필요없는 0을 없애줌 (Regex 이용)
		resultString = resultString.replaceFirst("^0+(?!$)", "");
				
		return resultString;
	}
	
	//뺄셈 연산 메소드 
	private static String sub(String stringInteger1, String stringInteger2)
	{
		//먼저 stringInteger1과 stringInteger2 크기 비교. (integer1이 더 커야함)
		if(compareIntegers(stringInteger1, stringInteger2) == -1)
		{
			String temp;
			temp = stringInteger1;
			stringInteger1 = stringInteger2;
			stringInteger2 = temp;
		}
		
		//결과값을 저장할 String
		String resultString;
		
		//integer1과 integer2의 길
		int lengthOfInteger1 = stringInteger1.length();
		int lengthOfInteger2 = stringInteger2.length();
		
		//연산의 편의를 위해서 string을 뒤집어줌.
		String reversedStringInteger1 = reverseString(stringInteger1);
		String reversedStringInteger2 = reverseString(stringInteger2);
		String reversedStringResult;
		
		//reversedInteger1과 reversedInteger2, 연산결과값을 저장하는 배열.
		char[] charReversedInteger1 = reversedStringInteger1.toCharArray();
		char[] charReversedInteger2 = reversedStringInteger2.toCharArray();
		int[] intReversedSubResult = new int[lengthOfInteger1];	//연산결과를 0번째 index부터 저장한 배열 (나중에 뒤집어 줘야함.)
		
		for(int i = 0; i < lengthOfInteger1; i++)
		{
			//각 숫자의 i번째 자리수 
			int positionalNumberOfInteger1 = Character.getNumericValue(charReversedInteger1[i]);
			int positionalNumberOfInteger2;
			int subOfPositionalNumber;

			if(i < lengthOfInteger2)  
				positionalNumberOfInteger2 = Character.getNumericValue(charReversedInteger2[i]);
			else  //integer2의 자리수를 넘어가면 0으로 처리 
				positionalNumberOfInteger2 = 0;
			
			//자리수들의 합 (받아내림도 반영)
			if(positionalNumberOfInteger1 >= positionalNumberOfInteger2)
				subOfPositionalNumber = positionalNumberOfInteger1 - positionalNumberOfInteger2;
			else 
			{
				//받아내림 
				subOfPositionalNumber = positionalNumberOfInteger1 + 10 - positionalNumberOfInteger2;
				charReversedInteger1[i+1]--;
			}
			
			//자리수들의 차를 결과값 배열에 저장 
			intReversedSubResult[i] = subOfPositionalNumber;
		}
		
		//계산 결과를 String으로 바꿔줌.
		reversedStringResult = Arrays.toString(intReversedSubResult).replace(", ", "").replace("[", "").replace("]", "");
		
		//reversedStringResult를 다시 뒤집어줌.
		resultString = reverseString(reversedStringResult);
		
		//앞에 붙은 필요없는 0을 없애줌 (Regex 이용)
		resultString = resultString.replaceFirst("^0+(?!$)", "");
				
		return resultString;
	}
	
	//곱셈 연산 메소드
	private static String mul(String stringInteger1, String stringInteger2)
	{
		//결과값을 저장할 String
		String resultString = "0";
		String subResultString;
		
		//integer1과 integer2의 길이 
		int lengthOfInteger2 = stringInteger2.length();
		
		//연산의 편의를 위해서 string을 뒤집어줌.
		String reversedStringInteger2 = reverseString(stringInteger2);
		
		//String을 Char로 저장하는 배열.
		char[] charReversedInteger2 = reversedStringInteger2.toCharArray();
		
		for(int i=0; i < lengthOfInteger2 ; i++)
		{
			int positionalNumberOfInteger2 = Character.getNumericValue(charReversedInteger2[i]);

			//integer2의 한자리와 integer1의 곱
			subResultString = subMul(stringInteger1, positionalNumberOfInteger2);
			
			//자리수를 맞춰줌. (뒤에 0을 붙여줌.)
			for(int j=0; j< i ; j++)
				subResultString = subResultString + "0";
			
			//integer1과 각 자리수와의 곱 결과들을 모두 합해줌.
			resultString = add(resultString, subResultString);
		}
				
		return resultString;
	}

	//곱셈 연산을 하기 위해서 n자리 수와 한자리 수 곱셈 메소드를 만들어 준다.
	private static String subMul(String stringInteger1, int n)
	{
		//결과값을 저장할 String
		String resultString;
		
		//integer1과 integer2의 길이 
		int lengthOfInteger1 = stringInteger1.length();
		
		//연산의 편의를 위해서 string을 뒤집어줌.
		String reversedStringInteger1 = reverseString(stringInteger1);
		String reversedStringResult;
		
		//reversedInteger2를 저장하는 배열.
		char[] charReversedInteger1 = reversedStringInteger1.toCharArray();
		int[] intReversedMulResult = new int[lengthOfInteger1 + 1];	//연산결과를 0번째 index부터 저장한 배열 (나중에 뒤집어 줘야함.)

		for(int i = 0; i < lengthOfInteger1 ; i++)
		{
			int positionalNumberOfInteger1 = Character.getNumericValue(charReversedInteger1[i]);
			int mulOfPositionalNumber = intReversedMulResult[i] + positionalNumberOfInteger1 * n;
			
			//자리수들의 곱을 10으로 나눈 나머지를 배열에 저장하고 받아올림을 다음 자리에 저장. 
			intReversedMulResult[i] = mulOfPositionalNumber % 10;
			intReversedMulResult[i+1] = mulOfPositionalNumber / 10;
		}
		
		//계산 결과를 String으로 바꿔줌.
		reversedStringResult = Arrays.toString(intReversedMulResult).replace(", ", "").replace("[", "").replace("]", "");
				
		//reversedStringResult를 다시 뒤집어줌. 
		resultString = reverseString(reversedStringResult);
		
		//앞에 붙은 필요없는 0을 없애줌 (Regex 이용)
		resultString = resultString.replaceFirst("^0+(?!$)", "");
		
		return resultString;
	}
	
	//String을 뒤집어주는 메소드
	//계산의 편의를 위해서 String을 뒤집어준다. 
	private static String reverseString(String originalString)
	{
		StringBuffer stringBuffer = new StringBuffer(originalString);
		StringBuffer reversedStringBuffer = stringBuffer.reverse();
		
		return reversedStringBuffer.toString();
	}
	
	//절대값 크기비교 메소드 
	//전자가 크면 1, 같으면 0, 후자가 크면 -1
	private static int compareIntegers(String stringInteger1, String stringInteger2)
	{
		int lengthOfInteger1 = stringInteger1.length();
		int lengthOfInteger2 = stringInteger2.length();
		
		char[] charInteger1 = stringInteger1.toCharArray();
		char[] charInteger2 = stringInteger2.toCharArray();

		int positionalNumberOfInteger1;
		int positionalNumberOfInteger2;
		
		//먼저 길이 비교
		if(lengthOfInteger1 > lengthOfInteger2)
			return 1;
		
		else if(lengthOfInteger1 < lengthOfInteger2)
			return -1;
		
		//길이가 같으면 한자리씩 비교
		for(int i = 0; i< lengthOfInteger1 ; i++)
		{
			positionalNumberOfInteger1 = Character.getNumericValue(charInteger1[i]);
			positionalNumberOfInteger2 = Character.getNumericValue(charInteger2[i]);
			
			if(positionalNumberOfInteger1 > positionalNumberOfInteger2)
				return 1;
			
			else if(positionalNumberOfInteger2 > positionalNumberOfInteger1)
				return -1;
		}
		
		//모든 자리수가 같으면 두 수는 크기가 같은 수
		return 0;
	}
	
	//stringInteger의 부호를 제거해줌. 
	private static String unsignStringInteger(String stringInteger)
	{
		if(stringInteger.startsWith("+") || stringInteger.startsWith("-"))
		{
			//부호를 포함하고 있으면 부호를 없애준다. 
			stringInteger = stringInteger.replaceAll("\\+", "");
			stringInteger = stringInteger.replaceAll("\\-", "");			
		}
		
		return stringInteger;	
	}
}