package homework6

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

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

        @JvmStatic
        fun inputThreadsNum() = listOf(1, 2, 5, 10)

        @JvmStatic
        fun combineDataSortAndThreadsNumArgs() = sequence<Arguments> {
            for (arrayArgs in inputDataSort()) {
                for (threadsNumsArg in inputThreadsNum()) {
                    arrayArgs.get().also {
                        yield(Arguments.of(it[0], it[1], threadsNumsArg))
                    }
                }
            }
        }.toList()
    }

    @ParameterizedTest
    @MethodSource("combineDataSortAndThreadsNumArgs")
    fun testSortMtMultiThread(actual: IntArray, expected: IntArray, numberOfThreads: Int) {
        sortMT(actual, numberOfThreads)
        assertArrayEquals(expected, actual)
    }
}
