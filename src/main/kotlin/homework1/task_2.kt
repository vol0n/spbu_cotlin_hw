package homework1

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
