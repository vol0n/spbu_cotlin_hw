package homework7

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.measureTimeMillis

@Serializable
data class TestPerCoroutine(val arraySize: Int, val timeArray: List<Double>)

@Serializable
data class GlobalTest(val coroutineNums: List<Int>, val tests: List<TestPerCoroutine>)

@Serializable
data class TestData(val coroutineNums: List<Int>, val arraySizes: List<Int>)

// data[l, m], data[m+1, r] are sorted, merge them into sorted data[l, r]
fun merge(data: IntArray, l: Int, m: Int, r: Int) {
    val left = IntArray(m - l + 1) { i -> data[i + l] }
    val right = IntArray(r - m) { i -> data[i + m + 1] }
    var i = 0
    var j = 0
    var k = l
    while (i < left.size && j < right.size) {
        if (left[i] < right[j]) {
            data[k] = left[i++]
        } else {
            data[k] = right[j++]
        }
        k++
    }

    while (i < left.size)
        data[k++] = left[i++]

    while (j < right.size)
        data[k++] = right[j++]
}

suspend fun sort(data: IntArray, l: Int, r: Int, numberOfCoroutines: Int) {
    if (l < r) {
        val mid = (l + r) / 2
        if (numberOfCoroutines != 0) {
            coroutineScope {
                launch { sort(data, l, mid, numberOfCoroutines / 2) }
                sort(data, mid + 1, r, numberOfCoroutines - numberOfCoroutines / 2)
            }
        } else {
            sort(data, l, mid, 0)
            sort(data, mid + 1, r, 0)
        }
        merge(data, l, mid, r)
    }
}

suspend fun test(testName: String) {
    val testData = Json.decodeFromString<TestData>(object {}.javaClass.getResource(testName).readText())
    val rand = java.util.Random()

    val file = File("results.json")
    val tests = mutableListOf<TestPerCoroutine>()
    var time: Long
    for (arraySize in testData.arraySizes) {
        val timeArray = mutableListOf<Double>()
        for (threadNum in testData.coroutineNums) {
            val test = IntArray(arraySize) { _ -> rand.nextInt() }
            time = measureTimeMillis {
                sort(test, 0, test.lastIndex, threadNum)
            }
            @Suppress("MagicNumber")
            timeArray.add(time.toDouble() / 1e3)
        }
        tests.add(TestPerCoroutine(arraySize, timeArray))
    }
    file.writeText(
        Json { prettyPrint = true }.encodeToString(GlobalTest(testData.coroutineNums, tests))
    )
    buildGraph()
    file.delete()
}

fun main() = runBlocking {
    test("testingData.json")
}
