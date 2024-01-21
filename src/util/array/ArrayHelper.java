package util.array;

import util.array.calculate.ArrayCalculation;

public class ArrayHelper {
    public static <T extends Number> double getVariance(T[][] array) {
        if (array == null) {
            throw new IllegalArgumentException("数组不能为空!");
        }
        return ArrayCalculation.getVariance(array);
    }
}
