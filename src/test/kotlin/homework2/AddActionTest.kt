package homework2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class AddActionTest {
    companion object{
        @JvmStatic
        fun inputDataPerform(): List<Arguments> = listOf(
            //Add to empty at correct position
            Arguments.of(mutableListOf<Int>(), 1, 0, listOf(1)),
            //Add to empty list at incorrect position
            Arguments.of(mutableListOf<Int>(), 1, 1, emptyList<Int>()),
            //Add non-empty list at correct position
            Arguments.of(mutableListOf(1, 2, 3), 10, 1, listOf(1, 10, 2, 3)),
            //Add non-empty list at incorrect position
            Arguments.of(mutableListOf(1, 2, 3), 1, 10, listOf(1, 2, 3))
        )

        @JvmStatic
        fun inputDataCancel(): List<Arguments> = listOf(
            Arguments.of(mutableListOf<Int>(), 1, 0, mutableListOf<Int>()),
            Arguments.of(mutableListOf(1, 2), 2, 1, listOf(1)),
            Arguments.of(mutableListOf(1, 2, 3), 3, 4, listOf(1, 2, 3))
        )
    }

    @MethodSource("inputDataPerform")
    @ParameterizedTest
    fun performTest(sourceList: MutableList<Int>, elem: Int, pos: Int, expectedList: List<Int>) {
        AddAction(elem, pos).performAction(sourceList)
        assertEquals(expectedList, sourceList)
    }

    @MethodSource("inputDataCancel")
    @ParameterizedTest
    fun cancelTest(sourceList: MutableList<Int>, elem: Int, pos: Int, expectedList: List<Int>) {
        AddAction(elem, pos).cancelAction(sourceList)
        assertEquals(expectedList, sourceList)
    }
}