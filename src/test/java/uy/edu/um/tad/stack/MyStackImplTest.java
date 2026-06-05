package uy.edu.um.tad.stack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de MyStackImpl<String>")
class MyStackImplTest {

    private MyStackImpl<String> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStackImpl<>();
    }

    // =========================================================
    // isEmpty()
    // =========================================================
    @Nested
    @DisplayName("isEmpty()")
    class IsEmptyTests {

        @Test
        @DisplayName("Pila recién creada debe ser vacía")
        void emptyStack_isEmptyReturnsTrue() {
            assertTrue(stack.isEmpty());
        }

        @Test
        @DisplayName("Pila con un elemento no debe ser vacía")
        void oneElement_isEmptyReturnsFalse() {
            stack.push("alfa");
            assertFalse(stack.isEmpty());
        }

        @Test
        @DisplayName("Pila con varios elementos no debe ser vacía")
        void multipleElements_isEmptyReturnsFalse() {
            stack.push("alfa");
            stack.push("beta");
            stack.push("gamma");
            assertFalse(stack.isEmpty());
        }
    }

    // =========================================================
    // size()
    // =========================================================
    @Nested
    @DisplayName("size()")
    class SizeTests {

        @Test
        @DisplayName("Pila vacía tiene tamaño 0")
        void emptyStack_sizeIsZero() {
            assertEquals(0, stack.size());
        }

        @Test
        @DisplayName("Pila con un elemento tiene tamaño 1")
        void oneElement_sizeIsOne() {
            stack.push("alfa");
            assertEquals(1, stack.size());
        }

        @Test
        @DisplayName("Pila con varios elementos reporta tamaño correcto")
        void multipleElements_sizeIsCorrect() {
            stack.push("alfa");
            stack.push("beta");
            stack.push("gamma");
            assertEquals(3, stack.size());
        }
    }

    // =========================================================
    // push(T value)
    // =========================================================
    @Nested
    @DisplayName("push(T value)")
    class PushTests {

        @Test
        @DisplayName("Push en pila vacía agrega el elemento al tope")
        void emptyStack_pushSetsTop() {
            stack.push("alfa");
            assertFalse(stack.isEmpty());
            assertEquals(1, stack.size());
            assertEquals("alfa", stack.peek());
        }

        @Test
        @DisplayName("Push en pila con un elemento deja al nuevo elemento en el tope")
        void oneElement_pushUpdatesTop() {
            stack.push("alfa");
            stack.push("beta");
            assertEquals(2, stack.size());
            assertEquals("beta", stack.peek());
        }

        @Test
        @DisplayName("Push en pila con varios elementos conserva comportamiento LIFO")
        void multipleElements_pushKeepsLifoOrder() {
            stack.push("alfa");
            stack.push("beta");
            stack.push("gamma");
            stack.push("delta");

            assertEquals(4, stack.size());
            assertEquals("delta", stack.peek());
        }
    }

    // =========================================================
    // peek()
    // =========================================================
    @Nested
    @DisplayName("peek()")
    class PeekTests {

        @Test
        @DisplayName("Peek en pila vacía retorna null")
        void emptyStack_peekReturnsNull() {
            assertNull(stack.peek());
        }

        @Test
        @DisplayName("Peek en pila de un elemento retorna ese elemento")
        void oneElement_peekReturnsTop() {
            stack.push("alfa");
            assertEquals("alfa", stack.peek());
            assertEquals(1, stack.size());
        }

        @Test
        @DisplayName("Peek en pila con varios elementos retorna el último apilado")
        void multipleElements_peekReturnsLastPushed() {
            stack.push("alfa");
            stack.push("beta");
            stack.push("gamma");

            assertEquals("gamma", stack.peek());
            assertEquals(3, stack.size());
        }
    }

    // =========================================================
    // pop()
    // =========================================================
    @Nested
    @DisplayName("pop()")
    class PopTests {

        @Test
        @DisplayName("Pop en pila vacía lanza EmptyStackException")
        void emptyStack_popThrowsException() {
            assertThrows(EmptyStackException.class, () -> stack.pop());
        }

        @Test
        @DisplayName("Pop en pila de un elemento retorna el elemento y deja la pila vacía")
        void oneElement_popReturnsElementAndLeavesEmpty() throws EmptyStackException {
            stack.push("alfa");

            String removed = stack.pop();

            assertEquals("alfa", removed);
            assertTrue(stack.isEmpty());
            assertEquals(0, stack.size());
            assertNull(stack.peek());
        }

        @Test
        @DisplayName("Pop en pila con varios elementos retorna el último apilado y actualiza el tope")
        void multipleElements_popReturnsLastPushedAndUpdatesTop() throws EmptyStackException {
            stack.push("alfa");
            stack.push("beta");
            stack.push("gamma");

            String removed = stack.pop();

            assertEquals("gamma", removed);
            assertEquals(2, stack.size());
            assertEquals("beta", stack.peek());
        }

        @Test
        @DisplayName("Varios pops respetan el orden LIFO")
        void multiplePops_followLifoOrder() throws EmptyStackException {
            stack.push("alfa");
            stack.push("beta");
            stack.push("gamma");

            assertEquals("gamma", stack.pop());
            assertEquals("beta", stack.pop());
            assertEquals("alfa", stack.pop());
            assertTrue(stack.isEmpty());
        }
    }
}