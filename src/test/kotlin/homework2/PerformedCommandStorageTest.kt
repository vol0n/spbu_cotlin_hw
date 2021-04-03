package homework2

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Path

internal class PerformedCommandStorageTest{
    companion object{
        @JvmStatic
        fun inputDataCancel(): List<Arguments> = listOf(
           Arguments.of(
               listOf(
               MoveAction(0, 3),
               MoveAction(3, 0),
               AddAction(1, 2)
           ),
               mutableListOf<Int>(3, 9, 4, 8)
           ),
            Arguments.of(listOf(AddAction(0, 1), MoveAction(2, 1)), mutableListOf<Int>()),
        )

        @JvmStatic
        fun inputDataTo(): List<Arguments> = listOf(
            Arguments.of("empty", listOf<Action>()),
            Arguments.of("typical", listOf(AddAction(0, 1), MoveAction(1, 0)))
        )
    }

    @MethodSource("inputDataCancel")
    @ParameterizedTest(name = "test cancel {index}: {0}")
    fun cancelTest(actions: List<Action>, expectedList: MutableList<Int>) {
        val actualList = expectedList.toMutableList()
        val pcs = PerformedCommandStorage(actualList)
        for (action in actions){
            pcs.performStore(action)
        }
        repeat(actions.size) { pcs.cancelAction() }
        assertEquals(expectedList, actualList)
    }

    @Test
    fun readEmptyJsonTest() {
        PerformedCommandStorage(mutableListOf()).apply {
            readJSON(
                PerformedCommandStorageTest::class.java.getResource("empty/empty.json").path
            )
            performAll()
            assertEquals(emptyList<Int>(), this.list)
        }
    }

    @Test
    fun readTypicalJsonTest() {
        PerformedCommandStorage((0..5).toMutableList()).apply {
            readJSON(
                PerformedCommandStorageTest::class.java.getResource("typical/typical.json").path
            )
            performAll()
            assertEquals(listOf(0, 0, 1, 2, 3, 4, 5), this.list)
        }
    }

    @TempDir
    lateinit var tempDirForGeneratedJson: Path

    @MethodSource("inputDataTo")
    @ParameterizedTest(name = "test read {index}: {0}")
    fun toJSONTest(testName: String, actions: List<Action>){
        val mapper = ObjectMapper()
        val pcs = PerformedCommandStorage(mutableListOf())
        actions.forEach { x -> pcs.store(x) }

        pcs.toJSON("$tempDirForGeneratedJson/$testName.json")

        assertEquals(
            mapper.readTree(this.javaClass.getResource("$testName/$testName.json")),
            mapper.readTree(File("$tempDirForGeneratedJson/$testName.json"))
        )
    }
}