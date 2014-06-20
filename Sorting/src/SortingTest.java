import java.io.*;
import java.util.Random; // 난수 발생때 이용 

public class SortingTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r')
			{
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			else
			{
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true)
			{
				int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

				String command = br.readLine();

				long t = System.currentTimeMillis();
				switch (command.charAt(0))
				{
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom)
				{
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				}
				else
				{
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
					for (int i = 0; i < newvalue.length; i++)
					{
						System.out.println(newvalue[i]);
					}
				}

			}
		}
		catch (IOException e)
		{
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value)
	{
		// TODO : Bubble Sort 를 구현하라.
		// value는 정렬안된 숫자들의 배열이며 value.length 는 배열의 크기가 된다.
		// 결과로 정렬된 배열은 리턴해 주어야 하며, 두가지 방법이 있으므로 잘 생각해서 사용할것.
		// 주어진 value 배열에서 안의 값만을 바꾸고 value를 다시 리턴하거나
		// 같은 크기의 새로운 배열을 만들어 그 배열을 리턴할 수도 있다.
		
		boolean sorted = false; // sort가 다 완료될 때까지 sorting 반복한다. 
		
		for( int pass = 1; (pass < value.length) && !sorted; pass ++)
		{
			// 두개씩 오른쪽으로 한 자리씩 이동하며 검사한다.
			// 끝까지 도달했을 때를 1 pass 라고 한다. 
			// 한 차례 pass가 지나면, 오른쪽에 제일 큰 수가 위치하게 된다. 
			// sort가 다 완료될때까지 pass를 지속한다. 
			
			sorted = true; // sort가 완료됐다고 가정한다. 
			
			for(int i = 0; i < value.length-pass ; i ++)
			{
				// 배열의 첫번째 수부터 (전체개수 - pass -1) 번째 수까지 검사 및 자리 바꾸기 한다. 
				
				// int nextIndex = i + 1;
								
				if(value[i] > value[i+1])
				{
					// 만약 검사하는 두 숫자 중 앞의 숫자가 더 크면 자리를 바꿔준다. 
					
					int temp = value[i];
					value[i] = value[i+1];
					value[i+1] = temp;
					
					sorted = false; // 자리를 바꿨다는 것은 정렬이 다 안됐다는 것을 의미한다. 
					
					// 만약 sorted가 true 인 상태로 pass 가 끝나면 이미 정렬이 다 됐다는 것을 의미한다. 
				}
			}
			
			// 이 지점에서 맨 오른쪽에 제일 큰 수가 위치하게 된다. 
		}
		
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value)
	{
		// TODO : Insertion Sort 를 구현하라.

		for(int unsorted = 1; unsorted < value.length; unsorted ++)
		{
			// 처음부터 한자리씩 검사한다. 
			
			int nextItem = value[unsorted]; // 비교하는 숫자 중 뒤의 숫자를 저장해 놓는다. 
			int loc = unsorted; // insert 할 위치  
			
			while((loc > 0) && value[loc-1] > nextItem)
			{
				// 숫자를 비교한다. 뒤(insert 할 위치)에 작은 수가 와 있으면 그 자리에 앞의 숫자를 shift 해서 넣어준다. 
				// 그리고 앞의 숫자 자리로 insert 할 위치를 옮긴다. (insert 할 위치를 -1 해준다.)
				// insert 할 위치가 맨 앞으로 올때 까지 반복한다. 
				value[loc] = value[loc-1];
				loc --; 
			}
			
			// 숫자 비교를 통해서 shift를 끝내면 저장했던 숫자를 insert 해준다. 
			value[loc] = nextItem;
		}
		
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value)
	{
		// TODO : Heap Sort 를 구현하라.
		
		int n = value.length;
		int temp;
		
		// build initial heap 
		for(int i = n/2 - 1; i >= 0; i--) {
			// leaf node 들은 percolate down 할 필요가 없다.
			// 0 ~ (n/2 - 1) 까지만 percolate down 해주면 heap 이 생성됨. 
			value = percolateDown(value, i, n);
		}
		
		// delete one by one
		for(int i = n-2; i >= 0; i--) {
			// 제일 큰 수 (index 0)와 맨마지막 수를 swap 한다. 
			// 제일 큰 수를 맨 뒤로 미뤄놓는다. (sorting)
			temp = value[i+1];
			value[i+1] = value[0];
			value[0] = temp;
			
			// 그 후 맨 위에서부터 percolateDown
			value = percolateDown(value, 0, i+1);
		}
		
		return (value);
	}
	
	private static int[] percolateDown (int[] inputArray, int i, int size) {
		int child = 2 * i + 1; // leftChild의 index 
		int rightChild = 2 * i + 2; // rightChild의 index
		int temp;
		
		if(child <= size - 1) {
			// 왼쪽 child의 index가 size보다 커질때까지 (leaf node가 될때까지) 계속 한다. 
			if((rightChild <= size - 1) && (inputArray[child] < inputArray[rightChild])) {
				// rightChild의 index가 size보다 작거나 같고 leftChild가 rightChild 보다 작을 때
				// 둘 중 큰 값으로 percolate 한다. 
				child = rightChild;
			}
			if(inputArray[i] < inputArray[child]) {
				// 부모 노드가 자식 노드보다 작은 경우 
				// 둘을 swqp 해준다. 
				temp = inputArray[i];
				inputArray[i] = inputArray[child];
				inputArray[child] = temp;
				
				// 그 후에 자식 노드에서부터 다시 percolateDown
				inputArray = percolateDown(inputArray, child, size);
			}
		}
		
		return inputArray;
	}
	
	
	

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoMergeSort(int[] value)
	{
		// TODO : Merge Sort 를 구현하라.
		
		int first = 0; // 주어진 배열의 첫번째 index 
		int last = value.length - 1; // 주어진 배열의 마지막 index 
		int[] tempArray = new int[value.length]; // tempArray는 valueArray와 같은 크기의 array 이다. 
		
		// tempArray는 valueArray의 일부분을 정렬하여 저장했다가 valueArray에 정렬된 결과를 돌려준다. 
		
		// mergesort 해준다. 
		value = mergeSort(value, tempArray, first, last);
		
		return (value);
	}
	
	private static int[] mergeSort(int[] inputArray, int[] tempArray, int first, int last)
	{
		// mergesort method 
		
		if (first < last) {
			int mid = (first + last) / 2;
			
			// recursive 하게 앞 절반과 뒤 절반을 각각 mergesort 해준다. 
			inputArray = mergeSort(inputArray, tempArray, first, mid);
			inputArray = mergeSort(inputArray, tempArray, mid + 1, last);
			
			// mergesort의 결과를 merge 해준다. 
			inputArray = merge(inputArray, tempArray, first, mid, last);
		}
		
		return inputArray;
	}
	
	private static int[] merge(int[] inputArray, int[] tempArray, int first, int mid, int last)
	{
		// subArray1과 subArray2는 각각 메모리를 따로 할당하는 것이 아니라 inputArray 안에서 index로 분리한다. 
		int first1 = first; 
		int last1 = mid;
		int first2 = mid + 1;
		int last2 = last;
		int index = first; // tempArray의 index. first부터 last까지 따라간다.  
		
		while((first1 <= last1) && (first2 <= last2)) {
			// subArray1과 subArray2 에 둘다 숫자가 남아 있는 경우 loop
			
			if(inputArray[first1] < inputArray[first2]) {
				// 만약 subArray1의 index가 가르키고 있는 숫자가 subArray2의 index가 가르키고 있는 숫자보다 작은경우. 
				// tempArray에 subArray1의 숫자를 넣는다. (작은 숫자부터 넣어준다)
				tempArray[index++] = inputArray[first1++];
			}
			else {
				// subArray1의 index가 가르키고 있는 숫자가 subArray2의 index가 가르키고 있는 숫자보다 큰경우.
				// tempArray에 subArray2의 숫자를 넣는다. 
				tempArray[index++] = inputArray[first2++];
			}
		}
		
		while(first1 <= last1) {
			// 만약 subArray1에만 숫자가 남은 경우. 
			// subArray1의 숫자들을 그대로 넣어준다. 
			tempArray[index++] = inputArray[first1++];
		}
		
		while(first2 <= last2) {
			// 만약 subArray2에만 숫자가 남은 경우. 
			// subArray2의 숫자를 그대로 넣어준다. 
			tempArray[index++] = inputArray[first2++];
		}
		
		// tempArray에는 first부터 last까지 subArray1과 subArray2가 정렬되어 merge된 값이 들어있다. 
		// 이를 inputArray에 복사해준다.
		for(int i = first; i <= last; i++) {
			inputArray[i] = tempArray[i];
		}
		
		return inputArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoQuickSort(int[] value)
	{
		// TODO : Quick Sort 를 구현하라.
	
		int first = 0; // 첫번째 index 
		int last = value.length - 1; // 마지막 index 
		
		value = quickSort(value, first, last);		
		
		return (value);
	}
	
	private static int[] quickSort(int[] inputArray, int first, int last)
	{
		// quicksort method
		
		if(first < last) {
			// 먼저 partition 해준다. 
			int pivotIndex = partition(inputArray, first, last);
			
			// partition 한 subArray를 각각 recursive 하게 quickSort 해준다. 
			inputArray = quickSort(inputArray, first, pivotIndex - 1);
			inputArray = quickSort(inputArray, pivotIndex + 1, last);
		}
		
		return inputArray;
	}
	
	private static int partition(int[] inputArray, int first, int last)
	{
		// partition method
		
		Random random = new Random(); // random pivotIndex 생성을 위한 난수 발생. 
		int pivotIndex = random.nextInt(last - first + 1) + first; 
		int pivotItem = inputArray[pivotIndex]; // partition의 pivotItem은 랜덤 index로 잡는다.
		int tempItem; // swap 하는데 사용할 tempItem;
		
		// 먼저 precondition 구현을 위해 pivotItem을 Array의 맨앞에 둔다. 
		tempItem = pivotItem;
		inputArray[pivotIndex] = inputArray[first];
		inputArray[first] = tempItem;
		
		// 이하는 precondition으로 partition 구현 
		int lastS1 = first; // S1의 마지막 index
		
		for( int firstUnknown = first + 1; firstUnknown <= last; firstUnknown++ ) {
			
			if(inputArray[firstUnknown] < pivotItem) {
				// firstUnknown이 pivotItem 보다 크기가 작은 경우
				// firstUnknown와 lastS1의 다음 item을 swap 한다. (S1이 하나 늘어남) 
				lastS1 ++;
				tempItem = inputArray[firstUnknown];
				inputArray[firstUnknown] = inputArray[lastS1];
				inputArray[lastS1] = tempItem;
			}
		}
		
		// S1과 S2를 나눈 후에는 pivotItem을 올바른 위치에 삽입한다. 
		tempItem = inputArray[first];
		inputArray[first] = inputArray[lastS1];
		inputArray[lastS1] = tempItem;
		
		return lastS1;		
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoRadixSort(int[] value)
	{
		// TODO : Radix Sort 를 구현하라.
		int maxDigit = 0;
		
		for(int i = 0; i < value.length; i++)
		{
			// 제일 큰 자리수를 찾아준다. 
			int digit = (int) Math.log10(Math.abs(value[i])) + 1;
			
			if(maxDigit < digit) maxDigit = digit;
		}
		
		for(int i = 0; i < maxDigit; i++) {
			value = stableSort(value, i);
		}
				
		return value;
	}
	
	private static int[] stableSort(int[] inputArray, int radix)
	{
		// modification from http://codingloverlavi.blogspot.in/2014/04/radix-sort-in-java.html
		// radix 자리번째 숫자를 기준으로 stable sort 해주는 method
		int n = inputArray.length; // 입력된 array의 원소 개수 
		int[] counter; // radix번째 자리수의 각 숫자별로 개수를 저장해주는 array
		int[] sortedArray = new int[n]; // stableSort 결과를 저장하는 array 

		counter = new int[19]; // int 배열로 초기화 해준다. 
			
		for(int i = 0; i < n; i++) {
			// 각 자리 숫자별로 그룹을 만들때 그룹당 원소가 몇개인지 세어준다. 
			
			int k = digitAt(inputArray[i], radix); // 숫자의 radix번째 자리수 (-9~9)
			k += 9;
			counter[k] ++;
		}
		
		for(int i = 1; i< 19; i++) { 
			counter[i] += counter[i-1]; 
		}
		
		for(int i = n-1; i >= 0; i--) {
			// 뒤에서부터 array를 정렬해준다. (뒤에서부터 해주는 것은 index를 맞춰주기 위함)
			sortedArray[--counter[digitAt(inputArray[i], radix) + 9]] = inputArray[i];
		}
		
		return sortedArray;	
	}
	
	private static int digitAt(int inputInt, int radix)
	{
		// inputInt의 radix 자리 숫자를 리턴해주는 method
		// radix는 10의 지수로 입력된다. (1의 자리수면 0, 10의 자리수면 1 ...)
		if(inputInt > 0) {
			// inputInt가 양수일 때 
			
			int n = (int)(inputInt/Math.pow(10, radix)) % 10;
			return n;
		}
		else {
			// inputInt가 음수일 때 
			// 자리수 숫자에 -를 붙여서 리턴해준다. 
			
			inputInt = Math.abs(inputInt);
			int n = (int)(inputInt/Math.pow(10, radix)) % 10;
			return -n;
		}
	}
}