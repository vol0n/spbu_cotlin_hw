
package homework6
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class TestPerThread(val arraySize: Int, val timeArray: List<Double>)

@Serializable
data class GlobalTest(val threadsNums: List<Int>, val tests: List<TestPerThread>)

@Serializable
data class TestData(val threadsNums: List<Int>, val arraySizes: List<Int>)

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

fun sort(data: IntArray, l: Int, r: Int, numberOfThreads: Int) {
    if (l < r) {
        val mid = (l + r) / 2
        if (numberOfThreads != 1) {
            val th = Thread { sort(data, l, mid, numberOfThreads / 2) }
            th.start()
            sort(data, mid + 1, r, numberOfThreads - numberOfThreads / 2)
            th.join()
        } else {
            sort(data, l, mid, 1)
            sort(data, mid + 1, r, 1)
        }
        merge(data, l, mid, r)
    }
}

fun test(testName: String) {
    val testData = Json.decodeFromString<TestData>(object {}.javaClass.getResource(testName).readText())
    val rand = java.util.Random()
    val file = File("results.json")
    val tests = mutableListOf<TestPerThread>()
    var startTime: Long
    var stopTime: Long
    for (arraySize in testData.arraySizes) {
        val timeArray = mutableListOf<Double>()
        for (threadNum in testData.threadsNums) {
            val test = IntArray(arraySize) { _ -> rand.nextInt() }
            startTime = System.nanoTime()
            sort(test, 0, test.lastIndex, threadNum)
            stopTime = System.nanoTime()
            @Suppress("MagicNumber")
            timeArray.add((stopTime - startTime).toDouble() / 1e9)
        }
        tests.add(TestPerThread(arraySize, timeArray))
    }
    file.writeText(
        Json { prettyPrint = true }.encodeToString(GlobalTest(testData.threadsNums, tests))
    )
}

fun main() {
    test("testingData.json")
}
