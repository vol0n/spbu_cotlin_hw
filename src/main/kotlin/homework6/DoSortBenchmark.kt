package homework6

import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomPath
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.label.ggtitle
import jetbrains.letsPlot.label.xlab
import jetbrains.letsPlot.label.ylab
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.scale.guideLegend
import jetbrains.letsPlot.scale.scaleColorDiscrete
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Serializable
data class BenchmarkParameters(val threadsNums: List<Int>, val arraySizes: List<Int>)

@ExperimentalTime
class Benchmark(inputPath: String = this::class.java.getResource("benchmarkParameters.json").path) {

    @Serializable
    private data class DataFrame(val threadNum: List<Int>, val arraySize: List<Int>, val time: List<Double>)
    private var objForSerialization: DataFrame

    companion object {
        @JvmStatic
        fun plotFromJSON(measurementsPath: String, location: String = "Performance-plot") {
            buildPlot(
                Json.decodeFromString<DataFrame>(File(measurementsPath).readText()).run {
                    mapOf(
                        "threadNum" to this.threadNum.map { it.toString() },
                        "arraySize" to this.arraySize,
                        "time" to this.time
                    )
                },
                location
            )
        }

        @JvmStatic
        private fun buildPlot(dataFrame: Map<String, Any>, location: String = "Performance-plot") {
            val p = letsPlot(dataFrame) { x = "arraySize"; color = "threadNum" } +
                    geomPath { y = "time" } +
                    geomPoint(size = 2.0) { y = "time" } +
                    ylab("time, (s)") +
                    xlab("number of elements in array") +
                    scaleColorDiscrete(name = "number of threads", guide = guideLegend(ncol = 2)) +
                    ggtitle("Multithreaded merge sort performance")
            ggsave(p, "$location.png")
        }
    }

    private var dataFrame: Map<String, Any>

    init {
        val input = Json.decodeFromString<BenchmarkParameters>(File(inputPath).readText())
        val rand = java.util.Random()

        val threadNum = MutableList(input.arraySizes.size * input.threadsNums.size) { 0 }
        val arraySize = MutableList(input.arraySizes.size * input.threadsNums.size) { 0 }
        val time = MutableList(input.arraySizes.size * input.threadsNums.size) { 0.0 }
        var i = 0
        for (size in input.arraySizes) {
            for (threadNo in input.threadsNums) {
                val test = IntArray(size) { _ -> rand.nextInt() }
                time[i] =
                    measureTime {
                        ParallelMergeSort(test, test, threadNo)
                    }.inSeconds
                arraySize[i] = size
                threadNum[i] = threadNo
                i++
            }
        }
        objForSerialization = DataFrame(threadNum, arraySize, time)
        dataFrame = mapOf<String, Any>("threadNum" to threadNum, "arraySize" to arraySize, "time" to time)
    }

    fun saveMeasurementsAsJSON(measurementsPath: String = "measurements") = File("$measurementsPath.json")
        .writeText(
            Json { prettyPrint = true }.encodeToString(objForSerialization)
        )

    fun plot(location: String = "Performance-plot") = buildPlot(dataFrame, location)
}

@ExperimentalTime
fun main() {
    val bench = Benchmark()
    bench.plot()
    bench.saveMeasurementsAsJSON()
}
