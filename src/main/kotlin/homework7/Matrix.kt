package homework7

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Matrix(private val nrows: Int, private val ncols: Int, init: (Int, Int) -> Int = { _, _ -> 0 }) {
    val data = Array(nrows) { i -> IntArray(ncols) { j -> init(i, j) } }

    private fun dot(rowIndex: Int, colIndex: Int, other: Matrix) = other
        .data
        .foldIndexed(0) { idx, sum, row -> sum + row[colIndex] * data[rowIndex][idx] }

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

    constructor(nrows: Int, ncols: Int, vararg elems: Int) : this(nrows, ncols,
        { i, j ->
            require(elems.size == ncols * nrows) {
                "Can't create matrix $nrows x $ncols with ${elems.size} elements"
            }
            elems[i * nrows + j]
        }
    )
}
