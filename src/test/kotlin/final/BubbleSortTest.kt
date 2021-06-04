package final

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BubbleSortTest {
    companion object {
        @JvmStatic
        fun testData() = listOf<Arguments>(
            Arguments.of(listOf<Int>(), listOf<Int>()),
            Arguments.of(listOf(1), listOf(1)),
            Arguments.of(listOf(10, -19, -200), listOf(-200, -19, 10))
        )
    }

    @ParameterizedTest
    @MethodSource("testData")
    fun testBubbleSortNaturalComparator(actual: List<Int>, expected: List<Int>) {
        assertEquals(expected, actual.bubbleSort { it })
    }
}