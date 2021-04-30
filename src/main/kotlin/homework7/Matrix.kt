package homework7

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Matrix(val nrows: Int, val ncols: Int, init: (Int, Int) -> Int = { _, _ -> 0 }) {
    var data = Array(nrows) { i -> IntArray(ncols) { j -> init(i, j) } }
    fun dot(rowIndex: Int, colIndex: Int, other: Matrix) = other
        .data
        .foldIndexed(0) { sum, idx, row -> sum + row[colIndex] * data[rowIndex][idx] }
    suspend operator fun times(other: Matrix): Matrix {
        require(ncols == other.nrows)
        val res = Matrix(nrows, other.ncols)
        coroutineScope {
            for (i in 0 until nrows) {
                for (j in 0 until other.ncols) {
                    launch {
                        res.data[i][j] = dot(i, j, other)
                    }
                }
            }
        }
        return res
    }

    override fun toString() = data.joinToString("\n") { row -> row.joinToString(" ") }
}
