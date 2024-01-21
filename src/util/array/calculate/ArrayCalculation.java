package util.array.calculate;

public class ArrayCalculation {
    public static <T extends Number> double getSum(T[][] array) {
        double sum = 0;
        for (T[] ts : array) {
            for (T t : ts) {
                sum += t.doubleValue();
            }
        }
        return sum;
    }

    public static <T extends Number> double getMean(T[][] array) {
        return getSum(array) / (array.length * array[0].length);
    }

    public static <T extends Number> double getVariance(T[][] array) {
        double mean = getMean(array);
        double sum = 0;
        for (T[] ts : array) {
            for (T t : ts) {
                sum += Math.pow(t.doubleValue() - mean, 2);
            }
        }
        return sum / (array.length * array[0].length);
    }
}
