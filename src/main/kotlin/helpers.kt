import java.util.*

fun <T> Iterable<T>.cartesianProduct(other: Iterable<T>): Set<Pair<T, T>> =
    flatMap { first ->
        other.map { second -> first to second }
    }.toSet()

fun Int.square() = Math.pow(toDouble(), 2.0)


/**
 * For each element it adds element to the queue if the same element with higher priority is not present.
 */
fun <E : Comparable<E>> PriorityQueue<E>.setAll(elements: Collection<E>) {
    elements.map(::set)
}

/**
 * Adds element to the queue if the same element with higher priority is not present.
 */
fun <E : Comparable<E>> PriorityQueue<E>.set(element: E) {
    val index = indexOf(element)

    // if new element is not already present just add
    if (index < 0) {
        add(element)
        return
    }

    // remove all equal elements with lower priority
    val isRemoved = removeIf { element == it && element < it }

    // if element was removed add new one
    if (isRemoved)
        add(element)
}