package homework2

fun <E> MutableList<E>.removeDoubles(): MutableList<E> {
    val bag = mutableSetOf<E>()
    this.reversed()
        .map { x -> if (x !in bag) bag.add(x) else this.remove(x) }
    return this
}
