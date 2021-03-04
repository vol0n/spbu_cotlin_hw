package homework1

fun compareChunk(idx: Int, source: String, substr: String): Boolean {
    var i = idx
    var j = 0
    while (i < substr.length) {
        if (source[i] != substr[j]) {
            return false
        } else {
            i++; j++
        }
    }
    return true
}

@Suppress("ReturnCount")
fun countOccurrences(where: String, what: String): Int {
    if (what.isEmpty()) return 1
    if (where.isEmpty()) return 0
    return where.foldIndexed(0) { idx, count, _ ->
        count + if (idx < where.length - what.length + 1 && compareChunk(idx, where, what)) 1 else 0
    }
}

fun main() {
    val scan = java.util.Scanner(System.`in`)
    println("Enter the string in which to search: ")
    val str1 = scan.nextLine()
    println("Enter the substring to find: ")
    val str2 = scan.nextLine()
    println(countOccurrences(str1, str2))
}