package retetest.task4

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class CompressKtTest {
    companion object {
        @JvmStatic
        fun inputDataTest() = listOf<Arguments>(
            Arguments.of("".toByteArray(), "".toByteArray()),
            Arguments.of(ByteArray(1) { Byte.MAX_VALUE }, byteArrayOf(1, Byte.MAX_VALUE )),
            Arguments.of(ByteArray(1) { Byte.MIN_VALUE }, byteArrayOf(1, Byte.MIN_VALUE )),
            Arguments.of(ByteArray(10) { Byte.MAX_VALUE }, byteArrayOf(10.toByte(), Byte.MAX_VALUE)),
            Arguments.of(ByteArray(2) { Byte.MAX_VALUE }, byteArrayOf(2.toByte(), Byte.MAX_VALUE)),
            Arguments.of(byteArrayOf(0, 1, 0, 0, 0, 1, 1, 1, 10), byteArrayOf(1, 0, 1, 1, 3, 0, 3, 1, 1, 10)),
            Arguments.of(byteArrayOf(1, 2, 3), byteArrayOf(1, 1, 1, 2, 1, 3)),
            Arguments.of(byteArrayOf(10, 10, 20, 20), byteArrayOf(2, 10, 2, 20))
        )
    }

    @ParameterizedTest
    @MethodSource("inputDataTest")
    fun testCompress(source: ByteArray, expected: ByteArray) {
        assertArrayEquals(expected, source.compress())
    }

    @ParameterizedTest
    @MethodSource("inputDataTest")
    fun testDecompress(expected: ByteArray, source: ByteArray) {
        assertArrayEquals(expected, source.decompress())
    }

}