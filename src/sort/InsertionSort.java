package sort;

import java.util.List;

public final class InsertionSort {
    private InsertionSort() {
    }

    //sorts the list in place: takes the next value and shifts bigger values before it one step right
    //fast for small and almost sorted lists - exactly what the scan line intersections are
    public static void sort(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            int value = list.get(i);
            int j = i;
            //j > 0 must be first, otherwise list.get(-1) is called when value is the smallest
            while (j > 0 && list.get(j - 1) > value) {
                list.set(j, list.get(j - 1));
                j--;
            }
            list.set(j, value);
        }
    }
}
