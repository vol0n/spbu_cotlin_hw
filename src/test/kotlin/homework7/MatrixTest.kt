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
            Arguments.of(Matrix(1, 1) { _, _ -> 3}, Matrix(1, 1) { _, _ -> 2},
                Matrix(1, 1) { _, _ -> 6}),
            Arguments.of(Matrix(1, 2) { _, j -> j + 1}, Matrix(2, 2) { i, j -> i + j},
                Matrix(1, 2) { _, j -> 3*j + 2})
        )
    }

    @MethodSource("testDataMul")
    @ParameterizedTest
    fun testMul(op1: Matrix, op2: Matrix, expected: Matrix) = runBlocking {
        assertArrayEquals(expected.data, (op1 * op2).data)
    }
}