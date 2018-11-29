import java.util.*

fun addPath(mapString: String): String {
    val board = mapString.toBoard()

    val min = 0 to 0
    val max = mapString.split("\n")
        .let { it.size to it.first().length }

    val start = board['S']!!.single()
    val target = board['X']!!.single()
    val walls = board['B'].orEmpty()

    val frontier = PriorityQueue<Node>()

    // Add starting point to frontier
    Node(
        current = start,
        target = target,
        g = .0,
        origin = null
    ).let(frontier::add)


    return search(
        frontier = frontier,
        excluded = walls,
        min = min,
        max = max,
        target = target
    )?.getPath()
        .orEmpty()
        .map(Node::coords)
        .let(mapString::markPath)
}

private tailrec fun search(
    frontier: PriorityQueue<Node>,
    excluded: Set<Pair<Int, Int>>,
    min: Pair<Int, Int>,
    max: Pair<Int, Int>,
    target: Pair<Int, Int>
): Node? {
    if (frontier.isEmpty()) return null

    val current = frontier.poll()

    val newFrontiers = current.getBorders(
        min = min,
        max = max,
        target = target,
        excluded = excluded
    )

    val final = newFrontiers.find { it.coords == target }

    if (final != null) return final

    frontier.setAll(newFrontiers)

    return search(
        frontier = frontier,
        excluded = excluded.plus(current.coords),
        max = max,
        min = min,
        target = target
    )
}

fun <E : Comparable<E>> PriorityQueue<E>.setAll(elements: Collection<E>) {
    elements.map(::set)
}


fun <E : Comparable<E>> PriorityQueue<E>.set(element: E) {
    val index = indexOf(element)

    // if new element is not already present just add
    if (index < 0) {
        add(element)
        return
    }

    // remove all equal elements with lower priority (there should be max 1)
    val isRemoved = removeIf { element == it && element < it } // TODO make it more readable

    // if element was removed add new one
    if (isRemoved)
        add(element)
}

@Suppress("IfThenToElvis")
tailrec fun Node.getPath(acc: List<Node> = emptyList()): List<Node> =
    if (origin == null) acc + this // found starting point
    else origin.getPath(acc + this)

private fun String.markPath(
    path: Collection<Pair<Int, Int>>
): String {
    return split("\n")
        .mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, char ->
                when {
                    path.contains(rowIndex to columnIndex) -> '*'
                    else -> char
                }
            }
        }
        .joinToString(separator = "\n") {
            it.joinToString(separator = "")
        }
}

fun String.toBoard() =
    split("\n")
        .mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, char ->
                char to (rowIndex to columnIndex)
            }
        }
        .flatten()
        .filter { it.first != '.' }
        .groupBy { it.first }
        .mapValues { (_, value) -> value.map { it.second }.toSet() }
