package homework1

fun factorialRec(n: Int): Int = if (n <= 1) 1 else n * factorialRec(n - 1)

fun factorialIter(n: Int): Int {
    if (n <= 1) return 1
    var res = n
    for (i in 2 until n) {
        res *= i
    }
    return res
}

fun main() {
    val scan = java.util.Scanner(System.`in`)
    println("To quit enter: 'q'")
    while (true) {
        println("Enter a natural number to compute factorial: ")
        if (scan.hasNextInt()) {
            val n = scan.nextInt()
            if (n < 0) {
                println("Only non-negative integers are allowed.")
            } else {
                println("Recursive func: ${factorialRec(n)}, " +
                        "Iterative func: ${factorialIter(n)}") }
        } else {
            val buf = scan.next()
            if (buf == "q") break
            else println("Unknown input.")
        }
    }
}
