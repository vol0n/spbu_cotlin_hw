package homework2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class Task2KtTest {
    companion object{
        @JvmStatic
        fun inputData(): List<Arguments> = listOf(
            Arguments.of(mutableListOf(0, 1, 1, 3, 2, 3, 3, 2, 7, 2, 8, 10), listOf(0, 1, 3, 7, 2, 8, 10)),
            Arguments.of(mutableListOf<Int>(), mutableListOf<Int>()),
            Arguments.of(mutableListOf(1), mutableListOf(1)),
            Arguments.of(mutableListOf(1, 2, 3, 4), mutableListOf(1, 2, 3, 4))
        )
    }

    @MethodSource("inputData")
    @ParameterizedTest(name = "test ")
    fun removeDoublesTest(testData: MutableList<Int>, expectedRes: MutableList<Int>) {
        assertEquals(expectedRes, testData.removeDoubles())
    }
}