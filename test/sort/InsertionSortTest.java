package sort;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertionSortTest {

    @Test
    void sort_smallestValueLast_movesToStart() {
        //the inner loop has to stop at index 0
        assertSorted(List.of(1, 3, 5, 7), 3, 5, 7, 1);
    }

    @Test
    void sort_duplicates_areKept() {
        //a vertex shared by two edges gives the same x twice
        assertSorted(List.of(2, 4, 4, 9), 4, 9, 4, 2);
    }

    @Test
    void sort_emptyAndSingleValue_staySame() {
        //a row can have no intersections at all
        assertSorted(List.of());
        assertSorted(List.of(5), 5);
    }

    private static void assertSorted(List<Integer> expected, int... values) {
        List<Integer> list = new ArrayList<>();
        for (int v : values)
            list.add(v);
        InsertionSort.sort(list);
        assertEquals(expected, list);
    }
}
