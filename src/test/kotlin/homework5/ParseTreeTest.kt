package homework5

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ParseTreeTest {
    companion object {
        @JvmStatic
        fun inputData() = listOf<Arguments>(
            Arguments.of("test1", -11),
            Arguments.of("test2", 25),
            Arguments.of("test3", 40),
        )
    }

    @MethodSource("inputData")
    @ParameterizedTest
    fun testParse(testName: String, expected: Int) {
       ParseTree(this.javaClass.getResource("$testName.txt").path).apply {
           assertEquals(expected, this.compute())
       }
    }
}