package retest.task2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.IllegalStateException

internal data class MyInt(val value: Int): ArithmeticAvailable<MyInt> {
    override operator fun plus(rhs: MyInt) = MyInt(value + rhs.value)
    override fun minus(rhs: MyInt) = MyInt(value - rhs.value)
    override fun times(rhs: MyInt) = MyInt(value * rhs.value)
    override fun isZero() = value == 0
}

internal fun List<Int>.toMyInt() = Vector( this.map { MyInt(it) }.toList() )

internal class VectorTest {
    companion object {
        @JvmStatic
        fun myIntTestDataIsAllZero() = listOf<Arguments>(
            Arguments.of(emptyList<Int>().toMyInt(), false),
            Arguments.of(listOf(1).toMyInt(), false),
            Arguments.of(listOf(0).toMyInt(), true),
            Arguments.of(listOf(0, 0, 0, 0).toMyInt(), true)
        )

        @JvmStatic
        fun myIntTestDataTimes() = listOf<Arguments>(
            Arguments.of(listOf(1).toMyInt(), listOf(1).toMyInt(), MyInt(1)),
            Arguments.of(listOf(1, 2, 3, 4).toMyInt(), listOf(1, 2, 3, 4).toMyInt(), MyInt(30)),
            Arguments.of(listOf(1, 2, 1, 2).toMyInt(), listOf(-1, -2, 1, 2).toMyInt(), MyInt(0))
        )

        @JvmStatic
        fun myIntTestDataPlus() = listOf<Arguments>(
            Arguments.of(Vector(emptyList<MyInt>()), Vector(emptyList<MyInt>()), Vector(emptyList<MyInt>())),
            Arguments.of(listOf(1).toMyInt(), listOf(1).toMyInt(), listOf(2).toMyInt()),
            Arguments.of(listOf(1, 2, 3, 4).toMyInt(), listOf(1, 2, 3, 4).toMyInt(), listOf(2, 4, 6, 8).toMyInt()),
            Arguments.of(listOf(1, 2, 1, 2).toMyInt(), listOf(-1, -2, 1, 2).toMyInt(), listOf(0,  0, 2, 4).toMyInt())
        )

        @JvmStatic
        fun myIntTestDataMinus() = listOf<Arguments>(
            Arguments.of(Vector(emptyList<MyInt>()), Vector(emptyList<MyInt>()), Vector(emptyList<MyInt>())),
            Arguments.of(listOf(1).toMyInt(), listOf(1).toMyInt(), listOf(0).toMyInt()),
            Arguments.of(listOf(1, 2, 3, 4).toMyInt(), listOf(1, 2, 3, 4).toMyInt(), listOf(0, 0, 0, 0).toMyInt()),
            Arguments.of(listOf(1, 2, 1, 2).toMyInt(), listOf(-1, -2, 1, 2).toMyInt(), listOf(2,  4, 0, 0).toMyInt())
        )
    }

    @Test
    fun testEmptyTimes() = assertThrows(IllegalStateException::class.java) {
        Vector(emptyList<MyInt>()) * Vector(emptyList())
    }

    @MethodSource("myIntTestDataTimes")
    @ParameterizedTest
    fun <T: ArithmeticAvailable<T>> testTimes(lhs: Vector<T>, rhs: Vector<T>, expected: T) {
        assertEquals(expected, lhs * rhs)
    }

    @MethodSource("myIntTestDataPlus")
    @ParameterizedTest
    fun <T: ArithmeticAvailable<T>> testPlus(lhs: Vector<T>, rhs: Vector<T>, expected: Vector<T>) {
        assertEquals(expected.data, (lhs + rhs).data)
    }

    @MethodSource("myIntTestDataMinus")
    @ParameterizedTest
    fun <T: ArithmeticAvailable<T>> testMinus(lhs: Vector<T>, rhs: Vector<T>, expected: Vector<T>) {
        assertEquals(expected.data, (lhs - rhs).data)
    }

    @MethodSource("myIntTestDataIsAllZero")
    @ParameterizedTest
    fun <T: ArithmeticAvailable<T>> testIsAllZero(op: Vector<T>, expected: Boolean) {
        assertEquals(expected, op.isAllZero())
    }
}