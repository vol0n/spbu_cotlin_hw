package homework3


import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Path


class Task_3KtTest {
    companion object{
        @JvmStatic
        fun inputData(): List<Arguments> = listOf(
              Arguments.of("noFunctions"),
              Arguments.of("yamlTest"),
              Arguments.of("identicalFuncs")
        )
    }

    @TempDir
    lateinit var tmpDirForGenerated: Path

    @MethodSource("inputData")
    @ParameterizedTest(name = "test {index}: {0}")
    fun generateTestFileTest(testDirName: String) {
        this.javaClass.getResourceAsStream("/$testDirName/$testDirName.txt")
        println(tmpDirForGenerated.toString())
        generateTestFile(testDirName, tmpDirForGenerated.toString())
        assertEquals(
            this.javaClass.getResource("$testDirName/$testDirName.txt").readText(),
            File("${tmpDirForGenerated}/$testDirName/${testDirName.capitalize()}.kt").readText()
        )
    }
}