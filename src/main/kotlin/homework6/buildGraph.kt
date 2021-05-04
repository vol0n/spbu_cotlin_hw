
package homework6

import jetbrains.letsPlot.GGBunch
import jetbrains.letsPlot.Pos
import jetbrains.letsPlot.Stat
import jetbrains.letsPlot.export.ggsave
import jetbrains.letsPlot.geom.geomBar
import jetbrains.letsPlot.label.ggtitle
import jetbrains.letsPlot.label.xlab
import jetbrains.letsPlot.label.ylab
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import jetbrains.letsPlot.letsPlot
import jetbrains.letsPlot.scale.scaleYContinuous
import kotlin.math.log
import kotlin.math.round

fun buildGraph() {
    val tst = Json.decodeFromString<GlobalTest>(File("results.json").readText())

    val d = mutableMapOf<String, Any>(
        "thrNo" to List(tst.threadsNums.size) { idx -> tst.threadsNums[idx].toString() }
    )

    tst.tests.map { d.put("${it.arraySize}", it.timeArray) }
    val p = letsPlot(d) { x = "thrNo" }
    val bunch = GGBunch()
    var plotPos = 0
    @Suppress("MagicNumber")
    for (test in tst.tests) {
        val curp = p +
                geomBar(stat = Stat.identity, position = Pos.dodge, color = "black") { y = "${test.arraySize}" } +
                ylab("Time (s)") + xlab("Number of threads") +
                scaleYContinuous(format = "{} s") +
                ggtitle("Array size: 10^${round(log(test.arraySize.toDouble(), 10.0)).toInt()}")
        bunch.addPlot(curp, 0, plotPos)
        plotPos += 400
    }

    ggsave(bunch, "Performance_graphs.png")
}
