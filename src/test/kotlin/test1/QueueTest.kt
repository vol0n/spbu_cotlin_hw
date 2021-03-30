package test1

import homework2.Action
import homework2.AddAction
import homework2.MoveAction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.IllegalStateException

internal class MyQueueTest {
    companion object{
        @JvmStatic
        fun inputData(): List<Arguments> = listOf(
            Arguments.of(listOf(1 to 3, 10 to 5, 6 to 2), listOf(5, 2, 3)),
            Arguments.of(listOf<Pair<Int, Int>>(), listOf<Int>())
        )
    }

    @MethodSource("inputData")
    @ParameterizedTest(name = "test cancel {index}: {0}")
    fun queueTest(testData: List<Pair<Int, Int>>, expected: List<Int>){
        val d = MyQueue<Int, Int>()
        testData.forEach {x -> d.enqueue(x.first, x.second)}
        val actual = mutableListOf<Int>()
        repeat(testData.size) {
            actual.add(d.rool())
        }
        assertEquals(expected, actual)
    }

    @Test
    fun emptyTest(){
        val d = MyQueue<Double, String>()
        assertThrows<IllegalStateException>{ d.peek() }
        assertThrows<IllegalStateException>{ d.rool() }
        assertThrows<IllegalStateException>{ d.remove() }
    }


}