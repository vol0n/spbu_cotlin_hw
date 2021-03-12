package homework1

/**
 * Compare part of [source] starting from [idx] with [substr]
 *
 * @param idx the index of [source] from which the comparison starts.
 * It is assumed that: 0 <= [idx] < source.length - substr.length + 1
 * @param source the String for which [idx] is supported
 * @param substr the String which is to be compared with part of [source]
 * @return true if [source].substring(idx, idx+what.length) == substr false otherwise
 */
fun compareChunk(idx: Int, source: String, substr: String): Boolean {
    var i = idx
    var j = 0
    while (j < substr.length) {
        if (source[i] != substr[j]) {
            return false
        } else {
            i++; j++
        }
    }
    return true
}

/**
 * Extension function counting occurrences of [what]
 *
 * @param what the String which to search in this String instance
 * @return the number of times [what] is included in this String instance
 */
@Suppress("ReturnCount")
fun String.countOccurrences(what: String): Int {
    if (what.isEmpty()) return 1
    if (isEmpty()) return 0
    return foldIndexed(0) { idx, count, _ ->
        count + if (idx < length - what.length + 1 && compareChunk(idx, this, what)) 1 else 0
    }
}

fun main() {
    val scan = java.util.Scanner(System.`in`)
    println("Enter the string in which to search: ")
    val str1 = scan.nextLine()
    println("Enter the substring to find: ")
    val str2 = scan.nextLine()
    println(str1.countOccurrences(str2))
}
