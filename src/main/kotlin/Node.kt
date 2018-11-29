const val STEP_COST = 1.0
const val DIAGONAL_STEP_COST = 1.5

class Node(
    val coords: Pair<Int, Int>,
    val g: Double,
    val h: Double,
    val origin: Node?
) : Comparable<Node> {
    private val f = g + h

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (coords != other.coords) return false

        return true
    }

    override fun hashCode(): Int {
        return coords.hashCode()
    }

    override fun toString(): String {
        return "Node(coords=$coords, g=$g, h=$h, origin=$origin)"
    }

    override fun compareTo(other: Node): Int = when {
        f < other.f -> -1
        f == other.f -> 0
        else -> 1
    }
}

/**
 * Returns non-excluded bordering elements
 */
fun Node.getBorders(
    min: Pair<Int, Int>,
    max: Pair<Int, Int>,
    excluded: Set<Pair<Int, Int>>,
    target: Pair<Int, Int>
): Collection<Node> {
    val yLower = (coords.first - 1).takeIf { it >= min.first }
    val y = coords.first
    val yHigher = (coords.first + 1).takeIf { it < max.first }

    val xLower = (coords.second - 1).takeIf { it >= min.second }
    val x = coords.second
    val xHigher = (coords.second + 1).takeIf { it < max.second }

    val xCoords = listOfNotNull(xLower, x, xHigher)
    val yCoords = listOfNotNull(yLower, y, yHigher)

    return yCoords.cartesianProduct(xCoords)
        .minus(coords)
        .minus(excluded)
        .map {
            Node(
                current = it,
                target = target,
                g = g + getStepCost(it),
                origin = this
            )
        }
}

@Suppress("FunctionName")
fun Node(current: Pair<Int, Int>, target: Pair<Int, Int>, g: Double, origin: Node?) =
    Node(g = g, h = heuristic(current, target), coords = current, origin = origin)


private fun heuristic(current: Pair<Int, Int>, target: Pair<Int, Int>) =
    (current.first - target.first).square()
        .plus((current.second - target.second).square())
        .let { Math.sqrt(it) }

/**
 * Gets the cost of the step. Assumes the step is valid.
 */
private fun Node.getStepCost(target: Pair<Int, Int>) =
    if (target.first == coords.first || target.second == coords.second) STEP_COST
    else DIAGONAL_STEP_COST