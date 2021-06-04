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
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Serializable
data class BenchmarkParameters(val resourcesNums: List<Int>, val arraySizes: List<Int>)

@ExperimentalTime
class Benchmark(
    private val sorter: Sorter,
    inputPath: String = this::class.java.getResource("benchmarkParameters.json").path
) {
    @Serializable
    private data class DataFrame(val resourcesNum: List<Int>, val arraySize: List<Int>, val time: List<Double>)
    private var objForSerialization: DataFrame
    private var dataFrame: Map<String, Any>

    init {
        val input = Json.decodeFromString<BenchmarkParameters>(File(inputPath).readText())
        val rand = java.util.Random()

        val resourcesNumbers = MutableList(input.arraySizes.size * input.resourcesNums.size) { 0 }
        val arraySize = MutableList(input.arraySizes.size * input.resourcesNums.size) { 0 }
        val time = MutableList(input.arraySizes.size * input.resourcesNums.size) { 0.0 }
        var i = 0
        for (size in input.arraySizes) {
            for (resourcesNumber in input.resourcesNums) {
                val test = IntArray(size) { rand.nextInt() }
                time[i] = measureTime {
                    sorter.sort(test, resourcesNumber)
                }.toDouble(DurationUnit.SECONDS)
                arraySize[i] = size
                resourcesNumbers[i] = resourcesNumber
                i++
            }
        }
        objForSerialization = DataFrame(resourcesNumbers, arraySize, time)
        dataFrame = mapOf<String, Any>(
            sorter.resourcesKind.kind to resourcesNumbers.map { it.toString() },
            "arraySize" to arraySize, "time" to time)
    }

    fun saveMeasurementsAsJSON(measurementsPath: String = "measurements") = File("$measurementsPath.json")
        .writeText(
            Json.encodeToString(objForSerialization)
        )

    fun plot(location: String = "Performance-plot") = buildPlot(dataFrame, location)

    fun plotFromJSON(measurementsPath: String, location: String = "Performance-plot") {
        buildPlot(
            Json.decodeFromString<DataFrame>(File(measurementsPath).readText()).run {
                mapOf(
                    "resourcesNum" to this.resourcesNum.map { it.toString() },
                    "arraySize" to this.arraySize,
                    "time" to this.time
                )
            },
            location
        )
    }

    private fun buildPlot(dataFrame: Map<String, Any>, location: String = "Performance-plot") {
        val p = letsPlot(dataFrame) { x = "arraySize"; color = sorter.resourcesKind.kind } +
                geomPath { y = "time" } +
                geomPoint(size = PointSize) { y = "time" } +
                ylab("time, (s)") +
                xlab("number of elements in array") +
                scaleColorDiscrete(name = "number of ${sorter.resourcesKind.kind}",
                    guide = guideLegend(ncol = ncolInGuide)) +
                ggtitle("Multithreaded merge sort performance")
        ggsave(p, "$location.png")
    }

    companion object {
        const val PointSize = 2.0
        const val ncolInGuide = 2
    }
}

@ExperimentalTime
fun main() {
     val bench = Benchmark(sorter = ParallelMergeSort)
     bench.plot()
     bench.saveMeasurementsAsJSON()
}
