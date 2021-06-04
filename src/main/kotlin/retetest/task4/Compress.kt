package retetest.task4

fun ByteArray.compress(): ByteArray {
    if (this.isEmpty()) return ByteArray(0)
    if (this.size == 1) return byteArrayOf(1, this[0])
    var res = ByteArray(0)
    this.foldIndexed(1) { index, count, currentByte ->
        val isCurrentEqualToNext = if (index < this.size - 1) currentByte == this[index + 1] else false
        when {
            isCurrentEqualToNext -> count + 1
            else -> {
                res += count.toByte()
                res += currentByte
                1
            }
        }
    }
    return res
}

fun ByteArray.decompress(): ByteArray {
    if (this.isEmpty()) return ByteArray(0)
    require(this.size % 2 == 0) { "In order to decompress using this method, byte array must have even size." }
    var res = ByteArray(0)
    this.mapIndexed { index, count ->
        if (index % 2 == 0) {
            repeat(count.toInt()) {
                res += this[index + 1]
            }
        }
    }
    return res
}
