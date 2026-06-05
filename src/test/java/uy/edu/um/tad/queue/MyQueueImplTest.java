package uy.edu.um.tad.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de MyQueueImpl<String>")
class MyQueueImplTest {

    private MyQueueImpl<String> queue;

    @BeforeEach
    void setUp() {
        queue = new MyQueueImpl<>();
    }

    // =========================================================
    // isEmpty()
    // =========================================================
    @Nested
    @DisplayName("isEmpty()")
    class IsEmptyTests {

        @Test
        @DisplayName("Cola recién creada debe ser vacía")
        void emptyQueue_isEmptyReturnsTrue() {
            assertTrue(queue.isEmpty());
        }

        @Test
        @DisplayName("Cola con un elemento no debe ser vacía")
        void oneElement_isEmptyReturnsFalse() {
            queue.enqueue("alfa");
            assertFalse(queue.isEmpty());
        }

        @Test
        @DisplayName("Cola con varios elementos no debe ser vacía")
        void multipleElements_isEmptyReturnsFalse() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");
            assertFalse(queue.isEmpty());
        }
    }

    // =========================================================
    // size()
    // =========================================================
    @Nested
    @DisplayName("size()")
    class SizeTests {

        @Test
        @DisplayName("Cola vacía tiene tamaño 0")
        void emptyQueue_sizeIsZero() {
            assertEquals(0, queue.size());
        }

        @Test
        @DisplayName("Cola con un elemento tiene tamaño 1")
        void oneElement_sizeIsOne() {
            queue.enqueue("alfa");
            assertEquals(1, queue.size());
        }

        @Test
        @DisplayName("Cola con varios elementos reporta tamaño correcto")
        void multipleElements_sizeIsCorrect() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");
            assertEquals(3, queue.size());
        }
    }

    // =========================================================
    // enqueue(T value)
    // =========================================================
    @Nested
    @DisplayName("enqueue(T value)")
    class EnqueueTests {

        @Test
        @DisplayName("Enqueue en cola vacía agrega el elemento")
        void emptyQueue_enqueueAddsElement() {
            queue.enqueue("alfa");

            assertFalse(queue.isEmpty());
            assertEquals(1, queue.size());
            assertTrue(queue.contains("alfa"));
            assertEquals("alfa", queue.get(0));
        }

        @Test
        @DisplayName("Enqueue en cola con un elemento inserta el nuevo al principio")
        void oneElement_enqueueAddsSecondAtBeginning() {
            queue.enqueue("alfa");
            queue.enqueue("beta");

            assertEquals(2, queue.size());
            assertEquals("beta", queue.get(0));
            assertEquals("alfa", queue.get(1));
        }

        @Test
        @DisplayName("Enqueue en cola con varios elementos inserta siempre al principio")
        void multipleElements_enqueueAddsAtBeginning() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");
            queue.enqueue("delta");

            assertEquals(4, queue.size());
            assertEquals("delta", queue.get(0));
            assertEquals("gamma", queue.get(1));
            assertEquals("beta", queue.get(2));
            assertEquals("alfa", queue.get(3));
        }
    }

    // =========================================================
    // dequeue()
    // =========================================================
    @Nested
    @DisplayName("dequeue()")
    class DequeueTests {

        @Test
        @DisplayName("Dequeue en cola vacía lanza EmptyQueueException")
        void emptyQueue_dequeueThrowsException() {
            assertThrows(EmptyQueueException.class, () -> queue.dequeue());
        }

        @Test
        @DisplayName("Dequeue en cola de un elemento retorna el elemento y deja la cola vacía")
        void oneElement_dequeueReturnsElementAndLeavesEmpty() throws EmptyQueueException {
            queue.enqueue("alfa");

            String removed = queue.dequeue();

            assertEquals("alfa", removed);
            assertTrue(queue.isEmpty());
            assertEquals(0, queue.size());
        }

        @Test
        @DisplayName("Dequeue en cola con varios elementos retorna el primero en entrar")
        void multipleElements_dequeueReturnsFirstInserted() throws EmptyQueueException {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");

            String removed = queue.dequeue();

            assertEquals("alfa", removed);
            assertEquals(2, queue.size());
            assertEquals("gamma", queue.get(0));
            assertEquals("beta", queue.get(1));
        }

        @Test
        @DisplayName("Varios dequeue respetan el orden FIFO")
        void multipleDequeues_followFifoOrder() throws EmptyQueueException {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");

            assertEquals("alfa", queue.dequeue());
            assertEquals("beta", queue.dequeue());
            assertEquals("gamma", queue.dequeue());
            assertTrue(queue.isEmpty());
        }
    }

    // =========================================================
    // contains(T value)
    // =========================================================
    @Nested
    @DisplayName("contains(T value)")
    class ContainsTests {

        @Test
        @DisplayName("Contains en cola vacía retorna false")
        void emptyQueue_containsReturnsFalse() {
            assertFalse(queue.contains("alfa"));
        }

        @Test
        @DisplayName("Contains en cola de un elemento encuentra ese elemento")
        void oneElement_containsFindsIt() {
            queue.enqueue("alfa");
            assertTrue(queue.contains("alfa"));
        }

        @Test
        @DisplayName("Contains en cola de un elemento retorna false para valor ausente")
        void oneElement_containsReturnsFalseForMissing() {
            queue.enqueue("alfa");
            assertFalse(queue.contains("omega"));
        }

        @Test
        @DisplayName("Contains en cola con varios elementos encuentra primero, intermedio y último nodo")
        void multipleElements_containsFindsAllPositions() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");
            queue.enqueue("delta");

            assertTrue(queue.contains("delta")); // first físico
            assertTrue(queue.contains("beta"));  // intermedio
            assertTrue(queue.contains("alfa"));  // last físico
        }

        @Test
        @DisplayName("Contains en cola con varios elementos retorna false para valor ausente")
        void multipleElements_containsReturnsFalseForMissing() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");

            assertFalse(queue.contains("omega"));
        }
    }

    // =========================================================
    // get(int i)
    // =========================================================
    @Nested
    @DisplayName("get(int i)")
    class GetTests {

        @Test
        @DisplayName("Get en cola vacía lanza IndexOutOfBoundsException")
        void emptyQueue_getThrowsException() {
            assertThrows(IndexOutOfBoundsException.class, () -> queue.get(0));
        }

        @Test
        @DisplayName("Get(0) en cola de un elemento retorna ese elemento")
        void oneElement_getZeroReturnsElement() {
            queue.enqueue("alfa");
            assertEquals("alfa", queue.get(0));
        }

        @Test
        @DisplayName("Get con índice negativo lanza IndexOutOfBoundsException")
        void negativeIndex_throwsException() {
            queue.enqueue("alfa");
            assertThrows(IndexOutOfBoundsException.class, () -> queue.get(-1));
        }

        @Test
        @DisplayName("Get con índice igual al tamaño lanza IndexOutOfBoundsException")
        void indexEqualToSize_throwsException() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            assertThrows(IndexOutOfBoundsException.class, () -> queue.get(2));
        }

        @Test
        @DisplayName("Get retorna los elementos en el orden físico de la lista")
        void multipleElements_getReturnsElementsInPhysicalOrder() {
            queue.enqueue("alfa");
            queue.enqueue("beta");
            queue.enqueue("gamma");
            queue.enqueue("delta");

            assertEquals("delta", queue.get(0));
            assertEquals("gamma", queue.get(1));
            assertEquals("beta", queue.get(2));
            assertEquals("alfa", queue.get(3));
        }
    }
}