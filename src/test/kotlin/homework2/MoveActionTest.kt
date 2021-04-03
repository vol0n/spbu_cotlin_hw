package homework2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class MoveActionTest {
    companion object {
        @JvmStatic
        fun inputDataPerform() = listOf<Arguments>(
            Arguments.of(mutableListOf<Int>(), 0, 2, emptyList<Int>()),
            Arguments.of(mutableListOf(1, 2), 0, 1, listOf(2, 1)),
            Arguments.of(mutableListOf(1, 2), -1, 1, listOf(1, 2)),
            Arguments.of(mutableListOf(1, 2), 0, 3, listOf(1, 2)),
            Arguments.of(mutableListOf(1, 2, 3, 5), 0, 0, listOf(1, 2, 3, 5)),
            Arguments.of(mutableListOf(1, 2, 3, 5), 3, 0, listOf(5, 1, 2, 3)),
            Arguments.of(mutableListOf(1, 2, 3, 5), 0, 3, listOf(2, 3, 5, 1))
        )

        @JvmStatic
        fun inputDataCancel() = listOf<Arguments>(
            Arguments.of(mutableListOf<Int>(), 0, 1, emptyList<Int>()),
            Arguments.of(mutableListOf(1, 2), 5, 1, listOf(1, 2)),
            Arguments.of(mutableListOf(1, 2), 1, 1, listOf(1, 2)),
            Arguments.of(mutableListOf(1, 2, 3), 2, 1, listOf(1, 3, 2)),
            Arguments.of(mutableListOf(1, 2, 3), 1, 2, listOf(1, 3, 2)),
            )
    }

    @MethodSource("inputDataPerform")
    @ParameterizedTest
    fun performTest(sourceList: MutableList<Int>, i: Int, j: Int, expectedList: List<Int>) {
        MoveAction(i, j).performAction(sourceList)
        assertEquals(expectedList, sourceList)
    }

    @MethodSource("inputDataCancel")
    @ParameterizedTest
    fun cancelTest(sourceList: MutableList<Int>, i: Int, j: Int, expectedList: List<Int>) {
        MoveAction(i, j).cancelAction(sourceList)
        assertEquals(expectedList, sourceList)
    }
}