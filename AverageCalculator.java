
public class AverageCalculator {
	public static void main(String[] args) {
		int[] nums = {5, 8, 12, 4, 10};
		double sum = 0;
		double average = 0;
		for (int i = 0; i < nums.length; i++) {
		    sum += nums[i];
		}
		System.out.println("Average: " + (average = sum / nums.length));
	}
}