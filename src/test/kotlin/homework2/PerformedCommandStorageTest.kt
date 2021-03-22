package homework2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

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
        fun inputDataRead(): List<Arguments> = listOf(
            Arguments.of("empty", listOf<Action>()),
            Arguments.of("typical", listOf(AddAction(0, 1), MoveAction(1, 0)))
        )
    }

    @MethodSource("inputDataCancel")
    @ParameterizedTest(name = "test cancel {index}: {0}")
    fun cancelTest(actions: List<Action>,  ls: MutableList<Int>) {
        val l = ls.toMutableList()
        val pcs = PerformedCommandStorage(l)
        for (action in actions){
            pcs.performStore(action)
        }
        repeat(actions.size) { pcs.cancelAction() }
        assertEquals(ls, l)
    }

    @MethodSource("inputDataRead")
    @ParameterizedTest(name = "test read {index}: {0}")
    fun readJSONTest(testName: String, expected: List<Action>){
        val l = mutableListOf<Int>(1, 2, 3, 4, 5)
        val l1 = l.toMutableList()
        val pcs = PerformedCommandStorage(l)
        pcs.readJSON(this.javaClass.getResource("$testName/$testName.json").path)
        pcs.performAll()
        expected.forEach(){ a -> a.performAction(l1) }
        assertEquals(l1, l)
    }


}