package homework7

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class MatrixTest {
    companion object {
        @JvmStatic
        fun testDataMul() = listOf<Arguments> (
            Arguments.of(Matrix(0, 0), Matrix(0, 0), Matrix(0, 0)),
            Arguments.of(Matrix(1, 1), Matrix(1, 1), Matrix(1, 1)),
            Arguments.of(Matrix(1, 1) { _, _ -> 6},
                Matrix(1, 1) { _, _ -> 3},
                Matrix(1, 1) { _, _ -> 2},
                ),
            Arguments.of(Matrix(1, 2) { _, j -> 3*j + 2},
                Matrix(1, 2) { _, j -> j + 1},
                Matrix(2, 2) { i, j -> i + j},
            ),
            Arguments.of(
                Matrix(3, 3,
                    17, -155, -260,
                    -47, -82, 364,
                    6, 30, 130
                ),
                Matrix(3, 3,
                    1, -10, -13,
                    -20, -1, 13,
                    -2,  8, 0),
                Matrix(3, 3,
                    1, 9, -13,
                    1, 6, 13,
                    -2,  8, 9),
            ),
            Arguments.of(
                Matrix(5, 5,
                    -13, -21, 4, -13, 24,
                    13, 21, -4, 13, -24,
                    31, 3, 40, 27, 36,
                    10, 5, -7, 12, -31,
                    -9, -8, 7, -3, 27
                ),
                Matrix(5, 5,
                    1, 2, 3, 4, 5,
                    -1, -2, -3, -4, -5,
                    1, -2, 3, -4, 5,
                    -5, -2, -3, -3, 1,
                    1, -1, 2, 3, 2
                ), null
            )

        )
    }

    @MethodSource("testDataMul")
    @ParameterizedTest
    fun testMul(expected: Matrix, op1: Matrix, op2: Matrix?) = runBlocking {
        val operand2 = op2 ?: op1
        assertArrayEquals(expected.data, (op1 * operand2).data)
    }
}