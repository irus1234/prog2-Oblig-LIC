package uy.edu.um.tad.heap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de MyHeapImpl<Integer>")
class MyHeapImplTest {

    private MyHeapImpl<Integer> heap;

    @BeforeEach
    void setUp() {
        heap = new MyHeapImpl<>();
    }

    // =========================================================
    // size()
    // =========================================================
    @Nested
    @DisplayName("size()")
    class SizeTests {

        @Test
        @DisplayName("Heap recién creado tiene tamaño 0")
        void emptyHeap_sizeIsZero() {
            assertEquals(0, heap.size());
        }

        @Test
        @DisplayName("Heap con un elemento tiene tamaño 1")
        void oneElement_sizeIsOne() {
            heap.insert(10);
            assertEquals(1, heap.size());
        }

        @Test
        @DisplayName("Heap con varios elementos reporta tamaño correcto")
        void multipleElements_sizeIsCorrect() {
            for (int i = 1; i <= 20; i++) {
                heap.insert(i);
            }

            assertEquals(20, heap.size());
        }
    }

    // =========================================================
    // get()
    // =========================================================
    @Nested
    @DisplayName("get() en MinHeap")
    class GetMinHeapTests {

        @Test
        @DisplayName("get() en heap vacío lanza RuntimeException")
        void emptyHeap_getThrowsException() {
            assertThrows(RuntimeException.class, () -> heap.get());
        }

        @Test
        @DisplayName("get() con un elemento retorna ese elemento")
        void oneElement_getReturnsElement() {
            heap.insert(10);
            assertEquals(10, heap.get());
            assertEquals(1, heap.size());
        }

        @Test
        @DisplayName("get() retorna el mínimo entre varios elementos")
        void multipleElements_getReturnsMinimum() {
            heap.insert(8);
            heap.insert(3);
            heap.insert(15);
            heap.insert(1);
            heap.insert(20);

            assertEquals(1, heap.get());
            assertEquals(5, heap.size());
        }
    }

    // =========================================================
    // insert()
    // =========================================================
    @Nested
    @DisplayName("insert() en MinHeap")
    class InsertMinHeapTests {

        @Test
        @DisplayName("Insertar un elemento lo deja como raíz")
        void emptyHeap_insertOneElement() {
            heap.insert(10);

            assertEquals(1, heap.size());
            assertEquals(10, heap.get());
        }

        @Test
        @DisplayName("Insertar dos elementos deja el menor como raíz")
        void oneElement_insertSmallerElementUpdatesRoot() {
            heap.insert(10);
            heap.insert(5);

            assertEquals(2, heap.size());
            assertEquals(5, heap.get());
        }

        @Test
        @DisplayName("Insertar varios elementos mantiene el mínimo en la raíz")
        void multipleElements_insertMaintainsMinimumAtRoot() {
            int[] values = {12, 7, 20, 3, 15, 1, 9};

            for (int value : values) {
                heap.insert(value);
            }

            assertEquals(7, heap.size());
            assertEquals(1, heap.get());
        }

        @Test
        @DisplayName("Insertar hasta 20 elementos mantiene el mínimo correctamente")
        void twentyElements_insertMaintainsMinimum() {
            int[] values = {
                    20, 5, 17, 3, 8,
                    14, 1, 19, 6, 10,
                    2, 13, 7, 16, 4,
                    11, 18, 9, 12, 15
            };

            for (int value : values) {
                heap.insert(value);
            }

            assertEquals(20, heap.size());
            assertEquals(1, heap.get());
        }
    }

    // =========================================================
    // remove()
    // =========================================================
    @Nested
    @DisplayName("remove() en MinHeap")
    class removeMinHeapTests {

        @Test
        @DisplayName("remove() en heap vacío lanza EmptyHeapException")
        void emptyHeap_removeThrowsException() {
            assertThrows(EmptyHeapException.class, () -> heap.remove());
        }

        @Test
        @DisplayName("remove() con un elemento retorna el elemento y deja el heap vacío")
        void oneElement_removeReturnsElementAndLeavesHeapEmpty() throws EmptyHeapException {
            heap.insert(10);

            Integer removed = heap.remove();

            assertEquals(10, removed);
            assertEquals(0, heap.size());
            assertThrows(RuntimeException.class, () -> heap.get());
        }

        @Test
        @DisplayName("remove() elimina el mínimo y actualiza la raíz")
        void multipleElements_removeReturnsMinimumAndUpdatesRoot() throws EmptyHeapException {
            heap.insert(8);
            heap.insert(3);
            heap.insert(15);
            heap.insert(1);
            heap.insert(20);

            assertEquals(1, heap.remove());
            assertEquals(3, heap.get());
            assertEquals(4, heap.size());
        }

        @Test
        @DisplayName("remove() repetido retorna los elementos en orden ascendente")
        void multipleElements_removeReturnsAscendingOrder() throws EmptyHeapException {
            int[] values = {
                    20, 5, 17, 3, 8,
                    14, 1, 19, 6, 10,
                    2, 13, 7, 16, 4,
                    11, 18, 9, 12, 15
            };

            for (int value : values) {
                heap.insert(value);
            }

            for (int expected = 1; expected <= 20; expected++) {
                assertEquals(expected, heap.remove());
            }

            assertEquals(0, heap.size());
        }
    }

    // =========================================================
    // Constructor con array
    // =========================================================
    @Nested
    @DisplayName("Constructor con array en MinHeap")
    class ArrayConstructorMinHeapTests {

        @Test
        @DisplayName("Construir heap desde array vacío deja tamaño 0")
        void emptyArrayConstructor_sizeIsZero() {
            Integer[] values = {};

            MyHeapImpl<Integer> arrayHeap = new MyHeapImpl<>(values);

            assertEquals(0, arrayHeap.size());
            assertThrows(RuntimeException.class, () -> arrayHeap.get());
        }

        @Test
        @DisplayName("Construir heap desde array con un elemento")
        void oneElementArrayConstructor() {
            Integer[] values = {10};

            MyHeapImpl<Integer> arrayHeap = new MyHeapImpl<>(values);

            assertEquals(1, arrayHeap.size());
            assertEquals(10, arrayHeap.get());
        }

        @Test
        @DisplayName("Construir heap desde array con varios elementos ubica el mínimo en la raíz")
        void multipleElementsArrayConstructor() {
            Integer[] values = {8, 3, 15, 1, 20};

            MyHeapImpl<Integer> arrayHeap = new MyHeapImpl<>(values);

            assertEquals(5, arrayHeap.size());
            assertEquals(1, arrayHeap.get());
        }
    }

    // =========================================================
    // MaxHeap
    // =========================================================
    @Nested
    @DisplayName("Operaciones en MaxHeap")
    class MaxHeapTests {

        @Test
        @DisplayName("MaxHeap vacío: get() lanza RuntimeException")
        void emptyMaxHeap_getThrowsException() {
            MyHeapImpl<Integer> maxHeap = new MyHeapImpl<>(false);

            assertThrows(RuntimeException.class, () -> maxHeap.get());
        }

        @Test
        @DisplayName("MaxHeap con un elemento retorna ese elemento")
        void oneElementMaxHeap_getReturnsElement() {
            MyHeapImpl<Integer> maxHeap = new MyHeapImpl<>(false);

            maxHeap.insert(10);

            assertEquals(1, maxHeap.size());
            assertEquals(10, maxHeap.get());
        }

        @Test
        @DisplayName("MaxHeap retorna el máximo entre varios elementos")
        void multipleElementsMaxHeap_getReturnsMaximum() {
            MyHeapImpl<Integer> maxHeap = new MyHeapImpl<>(false);

            maxHeap.insert(8);
            maxHeap.insert(3);
            maxHeap.insert(15);
            maxHeap.insert(1);
            maxHeap.insert(20);

            assertEquals(5, maxHeap.size());
            assertEquals(20, maxHeap.get());
        }

        @Test
        @DisplayName("MaxHeap remove() repetido retorna los elementos en orden descendente")
        void multipleElementsMaxHeap_removeReturnsDescendingOrder() throws EmptyHeapException {
            MyHeapImpl<Integer> maxHeap = new MyHeapImpl<>(false);

            int[] values = {
                    20, 5, 17, 3, 8,
                    14, 1, 19, 6, 10,
                    2, 13, 7, 16, 4,
                    11, 18, 9, 12, 15
            };

            for (int value : values) {
                maxHeap.insert(value);
            }

            for (int expected = 20; expected >= 1; expected--) {
                assertEquals(expected, maxHeap.remove());
            }

            assertEquals(0, maxHeap.size());
        }

        @Test
        @DisplayName("Constructor con array en MaxHeap ubica el máximo en la raíz")
        void maxHeapArrayConstructor_getReturnsMaximum() {
            Integer[] values = {8, 3, 15, 1, 20};

            MyHeapImpl<Integer> maxHeap = new MyHeapImpl<>(values, false);

            assertEquals(5, maxHeap.size());
            assertEquals(20, maxHeap.get());
        }
    }

    // =========================================================
    // Redimensionamiento interno
    // =========================================================
    @Nested
    @DisplayName("Capacidad interna")
    class CapacityTests {

        @Test
        @DisplayName("Insertar más elementos que la capacidad inicial redimensiona el heap")
        void insertMoreThanInitialCapacity_resizesHeap() {
            MyHeapImpl<Integer> smallHeap = new MyHeapImpl<>(2);

            smallHeap.insert(3);
            smallHeap.insert(2);
            smallHeap.insert(1);

            assertEquals(3, smallHeap.size());
            assertEquals(1, smallHeap.get());
        }
    }
}