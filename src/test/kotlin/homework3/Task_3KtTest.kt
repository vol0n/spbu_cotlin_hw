package homework3

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class Task_3KtTest {
    companion object{
        @JvmStatic
        fun inputData(): List<Arguments> = listOf(
            Arguments.of("noFunctions.yaml", "noFunctions.txt", "noFunctions"),
            Arguments.of("identicalFuncs.yaml", "identicalFuncs.txt", "identicalFuncs"),
            Arguments.of("yamlTest.yaml", "yamlTest.txt", "yamlTest")
        )
    }

    @MethodSource("inputData")
    @ParameterizedTest(name = "test {index}: {0}")
    fun generateTestFileTest(testConfigName: String, expectedName: String, packageName: String) {
        val pathToResources = "./src/test/resources/homework3"
        generateTestFile("$pathToResources/yamlConfigs/$testConfigName", "$pathToResources/GeneratedTests")
        assertEquals(
            File("$pathToResources/expected/$expectedName").readText(),
            File("$pathToResources/GeneratedTests/$packageName/${packageName.capitalize()}.kt").readText()
        )
    }
}