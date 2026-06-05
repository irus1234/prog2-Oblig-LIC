package uy.edu.um.tad.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de MyLinkedListImpl<String>")
class MyLinkedListImplTest {

    private MyLinkedListImpl<String> list;

    @BeforeEach
    void setUp() {
        list = new MyLinkedListImpl<>();
    }

    // =========================================================
    // isEmpty()
    // =========================================================
    @Nested
    @DisplayName("isEmpty()")
    class IsEmptyTests {

        @Test
        @DisplayName("Lista recién creada debe ser vacía")
        void emptyList_isEmptyReturnsTrue() {
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("Lista con un elemento no debe ser vacía")
        void oneElement_isEmptyReturnsFalse() {
            list.add("alfa");
            assertFalse(list.isEmpty());
        }

        @Test
        @DisplayName("Lista con varios elementos no debe ser vacía")
        void multipleElements_isEmptyReturnsFalse() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            assertFalse(list.isEmpty());
        }
    }

    // =========================================================
    // size()
    // =========================================================
    @Nested
    @DisplayName("size()")
    class SizeTests {

        @Test
        @DisplayName("Lista vacía tiene tamaño 0")
        void emptyList_sizeIsZero() {
            assertEquals(0, list.size());
        }

        @Test
        @DisplayName("Lista con un elemento tiene tamaño 1")
        void oneElement_sizeIsOne() {
            list.add("alfa");
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Lista con varios elementos reporta tamaño correcto")
        void multipleElements_sizeIsCorrect() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.add("delta");
            assertEquals(4, list.size());
        }
    }

    // =========================================================
    // add(T value) / getFirst()
    // =========================================================
    @Nested
    @DisplayName("add() y getFirst()")
    class AddTests {

        @Test
        @DisplayName("Agregar a lista vacía: first y last apuntan al mismo nodo")
        void emptyList_addSetsFirstAndLast() {
            list.add("alfa");
            assertNotNull(list.getFirst());
            assertEquals("alfa", list.getFirst().getValue());
            assertEquals("alfa", list.getLast().getValue());
        }

        @Test
        @DisplayName("Agregar un segundo elemento mantiene el orden (first no cambia)")
        void oneElement_addSecondKeepsOrder() {
            list.add("alfa");
            list.add("beta");
            assertEquals("alfa", list.getFirst().getValue());
            assertEquals("beta", list.getLast().getValue());
        }

        @Test
        @DisplayName("Agregar null no modifica la lista")
        void addNull_doesNotModifyList() {
            list.add(null);
            assertTrue(list.isEmpty());
            assertEquals(0, list.size());
        }

        @Test
        @DisplayName("Agregar varios elementos conserva el orden de inserción")
        void multipleElements_addPreservesOrder() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            assertEquals("alfa",  list.getFirst().getValue());
            assertEquals("gamma", list.getLast().getValue());
            assertEquals(3, list.size());
        }
    }

    // =========================================================
    // get(int position)
    // =========================================================
    @Nested
    @DisplayName("get(int position)")
    class GetTests {

        @Test
        @DisplayName("get() en lista vacía lanza IndexOutOfBoundsException")
        void emptyList_getThrowsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        }

        @Test
        @DisplayName("get(0) en lista de un elemento retorna ese elemento")
        void oneElement_getZeroReturnsElement() throws IndexOutOfBoundsException {
            list.add("alfa");
            assertEquals("alfa", list.get(0));
        }

        @Test
        @DisplayName("get() con índice negativo lanza IndexOutOfBoundsException")
        void negativeIndex_throwsException() {
            list.add("alfa");
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        }

        @Test
        @DisplayName("get() con índice igual al tamaño lanza IndexOutOfBoundsException")
        void indexEqualToSize_throwsException() {
            list.add("alfa");
            list.add("beta");
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
        }

        @Test
        @DisplayName("get() retorna el elemento correcto en cada posición")
        void multipleElements_getReturnsCorrectElement() throws IndexOutOfBoundsException {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.add("delta");
            assertEquals("alfa",  list.get(0));
            assertEquals("beta",  list.get(1));
            assertEquals("gamma", list.get(2));
            assertEquals("delta", list.get(3));
        }
    }

    // =========================================================
    // contains(T value)
    // =========================================================
    @Nested
    @DisplayName("contains(T value)")
    class ContainsTests {

        @Test
        @DisplayName("contains() en lista vacía retorna false")
        void emptyList_containsReturnsFalse() {
            assertFalse(list.contains("alfa"));
        }

        @Test
        @DisplayName("contains() encuentra el único elemento de la lista")
        void oneElement_containsFindsIt() {
            list.add("alfa");
            assertTrue(list.contains("alfa"));
        }

        @Test
        @DisplayName("contains() retorna false para valor no existente en lista de un elemento")
        void oneElement_containsReturnsFalseForMissing() {
            list.add("alfa");
            assertFalse(list.contains("omega"));
        }

        @Test
        @DisplayName("contains() encuentra el primero, un intermedio y el último elemento")
        void multipleElements_containsFindsAllPositions() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.add("delta");
            assertTrue(list.contains("alfa"));   // primero
            assertTrue(list.contains("gamma"));  // intermedio
            assertTrue(list.contains("delta"));  // último
        }

        @Test
        @DisplayName("contains() retorna false para valor ausente en lista de varios elementos")
        void multipleElements_containsReturnsFalseForMissing() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            assertFalse(list.contains("omega"));
        }
    }

    // =========================================================
    // remove(T value)
    // =========================================================
    @Nested
    @DisplayName("remove(T value)")
    class RemoveByValueTests {

        @Test
        @DisplayName("remove() en lista vacía no lanza excepción")
        void emptyList_removeDoesNotThrow() {
            assertDoesNotThrow(() -> list.remove("alfa"));
        }

        @Test
        @DisplayName("remove() el único elemento deja la lista vacía")
        void oneElement_removeLastElementLeavesEmptyList() {
            list.add("alfa");
            list.remove("alfa");
            assertTrue(list.isEmpty());
            assertNull(list.getFirst());
            assertNull(list.getLast());
        }

        @Test
        @DisplayName("remove() el primer elemento de varios actualiza first correctamente")
        void multipleElements_removeFirstUpdatesFirst() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.remove("alfa");
            assertEquals("beta", list.getFirst().getValue());
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("remove() el último elemento de varios actualiza last correctamente")
        void multipleElements_removeLastUpdatesLast() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.remove("gamma");
            assertEquals("beta", list.getLast().getValue());
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("remove() un elemento intermedio lo desvincula correctamente")
        void multipleElements_removeMiddleElement() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.remove("beta");
            assertEquals(2, list.size());
            assertFalse(list.contains("beta"));
            assertEquals("alfa",  list.getFirst().getValue());
            assertEquals("gamma", list.getLast().getValue());
        }

        @Test
        @DisplayName("remove() con valor inexistente no modifica la lista")
        void multipleElements_removeNonExistentValueDoesNothing() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.remove("omega");
            assertEquals(3, list.size());
        }
    }

    // =========================================================
    // remove(int index)
    // =========================================================
    @Nested
    @DisplayName("remove(int index)")
    class RemoveByIndexTests {

        @Test
        @DisplayName("remove(index) en lista vacía lanza IndexOutOfBoundsException")
        void emptyList_removeByIndexThrowsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        }

        @Test
        @DisplayName("remove(0) en lista de un elemento retorna el elemento y deja la lista vacía")
        void oneElement_removeByIndexLeavesEmptyList() {
            list.add("alfa");
            String removed = list.remove(0);
            assertEquals("alfa", removed);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("remove(index) con índice negativo lanza IndexOutOfBoundsException")
        void negativeIndex_throwsException() {
            list.add("alfa");
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        }

        @Test
        @DisplayName("remove(index) con índice igual al tamaño lanza IndexOutOfBoundsException")
        void indexEqualToSize_throwsException() {
            list.add("alfa");
            list.add("beta");
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));
        }

        @Test
        @DisplayName("remove(0) en lista de varios elementos retorna el primero y actualiza first")
        void multipleElements_removeIndexZeroUpdatesFirst() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            String removed = list.remove(0);
            assertEquals("alfa", removed);
            assertEquals("beta", list.getFirst().getValue());
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("remove(último índice) retorna el último y actualiza last")
        void multipleElements_removeLastIndexUpdatesLast() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            String removed = list.remove(2);
            assertEquals("gamma", removed);
            assertEquals("beta", list.getLast().getValue());
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("remove(índice intermedio) desvincula el nodo correctamente")
        void multipleElements_removeMiddleIndex() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.add("delta");
            String removed = list.remove(2); // posición 2 → "gamma"
            assertEquals("gamma", removed);
            assertEquals(3, list.size());
            assertFalse(list.contains("gamma"));
        }
    }
    // =========================================================
    // indexOf(T value)
    // =========================================================
    @Nested
    @DisplayName("indexOf(T value)")
    class IndexOfTests {

        // --- Lista vacía ---

        @Test
        @DisplayName("Lista vacía: cualquier valor retorna -1")
        void emptyList_returnsMinusOne() {
            assertEquals(-1, list.indexOf("alfa"));
        }

        // --- Lista con un elemento ---

        @Test
        @DisplayName("Un elemento: buscar el único elemento retorna 0")
        void oneElement_searchExistingElement_returnsZero() {
            list.add("alfa");
            assertEquals(0, list.indexOf("alfa"));
        }

        @Test
        @DisplayName("Un elemento: buscar valor inexistente retorna -1")
        void oneElement_searchMissingValue_returnsMinusOne() {
            list.add("alfa");
            assertEquals(-1, list.indexOf("omega"));
        }

        // --- Lista con varios elementos ---

        @Test
        @DisplayName("Varios elementos: buscar el primer elemento retorna 0")
        void multipleElements_searchFirst_returnsZero() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            assertEquals(0, list.indexOf("alfa"));
        }

        @Test
        @DisplayName("Varios elementos: buscar un elemento intermedio retorna su índice correcto")
        void multipleElements_searchMiddle_returnsCorrectIndex() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            list.add("delta");
            assertEquals(1, list.indexOf("beta"));
            assertEquals(2, list.indexOf("gamma"));
        }

        @Test
        @DisplayName("Varios elementos: buscar el último elemento retorna size - 1")
        void multipleElements_searchLast_returnsLastIndex() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            assertEquals(2, list.indexOf("gamma"));
        }

        @Test
        @DisplayName("Varios elementos: buscar valor inexistente retorna -1")
        void multipleElements_searchMissing_returnsMinusOne() {
            list.add("alfa");
            list.add("beta");
            list.add("gamma");
            assertEquals(-1, list.indexOf("omega"));
        }
    }
}