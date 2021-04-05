package homework5

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ParseTreeTest {
    companion object {
        @JvmStatic
        fun inputDataCompute() = listOf<Arguments>(
            Arguments.of("test1", -11),
            Arguments.of("test2", 25),
            Arguments.of("test3", 40),
        )

        @JvmStatic
        fun inputDataPrint() = listOf<Arguments>(
            Arguments.of("test1", """
                *
                ...+
                ......-1
                ......-10
                .../
                ......2
                ......2
            """.trimIndent()),
            Arguments.of("test2", """
                *
                ...5
                ...+
                ......-
                ........./
                ............40
                ............-8
                .........10
                ......20
            """.trimIndent()),
            Arguments.of("test3", """
                +
                ...20
                ...20
            """.trimIndent())
        )
    }

    @MethodSource("inputDataCompute")
    @ParameterizedTest
    fun testParse(testName: String, expected: Int) {
       ParseTree(this.javaClass.getResource("$testName.txt").path).apply {
           println(this)
           assertEquals(expected, this.compute())
       }
    }

    @MethodSource("inputDataPrint")
    @ParameterizedTest
    fun testPrint(testName: String, expected: String) {
        ParseTree(this.javaClass.getResource("$testName.txt").path).apply {
            assertEquals(expected, this.toString())
        }
    }
}