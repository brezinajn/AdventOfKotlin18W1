fun <T> Iterable<T>.cartesianProduct(other: Iterable<T>): Set<Pair<T, T>> =
    flatMap { first ->
        other.map { second -> first to second }
    }.toSet()

fun Int.square() = Math.pow(toDouble(), 2.0)