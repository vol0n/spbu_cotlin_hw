package homework6

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

fun IntArray.print() {
    this.forEach { x -> print("$x ") }
    println()
}

internal class ParallelMergeSortKtTest {
    companion object {
        @JvmStatic
        fun inputDataSort() = listOf<Arguments>(
            Arguments.of(intArrayOf(), intArrayOf()),
            Arguments.of(intArrayOf(1), intArrayOf(1)),
            Arguments.of(intArrayOf(1, 2, 3), intArrayOf(1, 2, 3)),
            Arguments.of(intArrayOf(4, 2, 1, 0), intArrayOf(0, 1, 2, 4)),
            Arguments.of(intArrayOf(10, 1, 1, -5, 2, 3), intArrayOf(-5, 1, 1, 2, 3, 10))
        )
    }

    @MethodSource("inputDataSort")
    @ParameterizedTest
    fun testSortOneThread(actual: IntArray, expected: IntArray) {
        sort(actual, 0, actual.lastIndex, 1)
        assertArrayEquals(expected, actual)
    }

    @MethodSource("inputDataSort")
    @ParameterizedTest
    fun testSortMultiThread(actual: IntArray, expected: IntArray) {
        sort(actual, 0, actual.lastIndex, 2)
        assertArrayEquals(expected, actual)
        sort(actual, 0, actual.lastIndex, 5)
        assertArrayEquals(expected, actual)
        sort(actual, 0, actual.lastIndex, 10)
        assertArrayEquals(expected, actual)
    }
}