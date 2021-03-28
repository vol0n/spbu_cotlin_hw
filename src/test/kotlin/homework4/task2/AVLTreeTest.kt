package homework4.task2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.IllegalStateException

internal typealias N = TestNode<Int>
internal class AVLTreeTest{
    companion object {
        @JvmStatic
        fun inputDataRotateLeft() = listOf<Arguments>(
            //no rotation should happen, no left child
            Arguments.of(listOf(10), 10, setOf(N(10))),
            //rotation is possible, but the rotating node has only one child
            Arguments.of(listOf(20, 30), 20, setOf(N(30, 20), N(20))),
            //rotation is possible, the rotated node has two children
            Arguments.of(listOf(20, 10, 30), 20, setOf(N(30, 20), N(20, 10), N(10)))
        )

        @JvmStatic
        fun inputDataRotateRight() = listOf<Arguments>(
            //no rotation should happen, no left child
            Arguments.of(listOf(10), 10, setOf(N(10))),
            //rotation is possible, but the rotating node has only one child
            Arguments.of(listOf(30, 20), 30, setOf(N(20, null, 30), N(30))),
            //rotation is possible, the rotated node has two children
            Arguments.of(listOf(20, 10, 30), 20,
            setOf(N(10, null, 20), N(20, null, 30), N(30)))
        )

        @JvmStatic
        fun inputDataPut() = listOf<Arguments>(
            //Put one node
            Arguments.of(listOf(10), setOf(N(10))),
            //Put two nodes
            Arguments.of(listOf(1, 10), setOf(N(1, null, 10), N(10))),
            //Put three nodes, balancing required
            Arguments.of(listOf(10, 20, 30), setOf(N(20, 10, 30), N(10), N(30))),
            //More nodes, balancing required
            Arguments.of(listOf(40, 30, 50, 45, 60, 44, 48), setOf(N(45, 40, 50), N(50, 48, 60),
            N(30), N(44), N(60), N(48), N(40, 30, 44)))
        )

        @JvmStatic
        fun inputDataRemove() = listOf<Arguments>(
            //remove leaf
            Arguments.of(listOf(40, 30, 50, 45, 60, 44, 48), 60, setOf(N(45, 40, 50), N(40, 30, 44),
            N(50, 48), N(30), N(44), N(48))),
            //remove node 45 with two children, where the successor of the 45 is his right child
            Arguments.of(listOf(40, 30, 50, 33, 45, 60, 44, 48), 45, setOf(N(40, 30, 50), N(50, 48, 60),
            N(30, null, 33), N(48, 44), N(44), N(60), N(33))),
            //remove root, the successor is not a right child
            Arguments.of(listOf(40, 30, 50, 33, 45, 60, 44, 48), 40, setOf(N(44, 30, 50), N(50, 45, 60),
                N(30, null, 33), N(45, null, 48), N(33), N(48), N(60)))
        )

        @JvmStatic
        fun inputDataContains() = listOf<Arguments>(
            Arguments.of(listOf<Int>(), 5, false),
            Arguments.of(listOf(1), 4, false),
            Arguments.of(listOf(2, 3, 4), 4, true)
        )
    }

    @Test
    fun oneEntryTest(){
        val t = AVLTree<Int, String>()
        val key = 10
        t.put(key, "")
        assertEquals(setOf(key), t.keys)

        t.remove(key)
        assertEquals(emptySet<Int>(), t.keys)
    }

    @Test
    fun emptyRemoveTest(){
        val t = AVLTree<Int, String>()
        t.remove(100)
        assertEquals(emptySet<Int>(), t.keys)
    }

    @MethodSource("inputDataRotateLeft")
    @ParameterizedTest
    fun testRotateLeft(elems: List<Int>, key: Int, expected: Set<N>){
        val t = AVLTree<Int, String>()
        elems.forEach { x -> t.put(x, "") }
        try {
            t.rotateLeft(t.root?.find(key)!!)
        }
        catch (e: IllegalStateException){}
        assertEquals(expected, t.getStructure())
    }

    @MethodSource("inputDataRotateRight")
    @ParameterizedTest
    fun testRotateRight(elems: List<Int>, key: Int, expected: Set<N>){
        val t = AVLTree<Int, String>()
        elems.forEach { x -> t.put(x, "") }
        try {
            t.rotateRight(t.root?.find(key)!!)
        }
        catch (e: IllegalStateException){}
        assertEquals(expected, t.getStructure())
    }

    @MethodSource("inputDataPut")
    @ParameterizedTest
    fun testPut(keys: List<Int>, expected: Set<N>){
        val t = AVLTree<Int, String>()
        keys.forEach { x -> t.put(x, "") }
        assertEquals(expected, t.getStructure())
    }


    @MethodSource("inputDataRemove")
    @ParameterizedTest
    fun testRemove(keys: List<Int>, keyToRemove: Int, expected: Set<N>){
        val t = AVLTree<Int, String>()
        keys.forEach { x -> t.put(x, "") }
        t.remove(keyToRemove)
        assertEquals(expected, t.getStructure())
    }

    @MethodSource("inputDataContains")
    @ParameterizedTest
    fun testContains(keys: List<Int>, keyToCheck: Int, expected: Boolean){
        val t = AVLTree<Int, String>()
        keys.forEach { x -> t.put(x, "") }
        assertEquals(expected, t.containsKey(keyToCheck))
    }
}