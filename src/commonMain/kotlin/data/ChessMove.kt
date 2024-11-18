package fr.xibalba.chess.data

interface ChessMove<T : ChessPosition> {
    fun execute(board: ChessBoard<T>)

    override fun toString(): String

    fun getTargetPosition(): List<Pair<ChessMoveTypes, T>> {
        return when (this) {
            is ChessMoveSimple -> listOf(type to to)
            is ChessMoveMultiple -> moves.map { it.getTargetPosition() }.flatten()
            else -> throw IllegalStateException("Unknown ChessMove type")
        }
    }
}
class ChessMoveMultiple<T : ChessPosition>(val moves: List<ChessMove<T>>) : ChessMove<T> {
    override fun execute(board: ChessBoard<T>) {
        moves.forEach { it.execute(board) }
    }

    override fun toString(): String = moves.joinToString(", ")
}

enum class ChessMoveTypes {
    MOVE,
    TAKE
}

typealias ChessMoveComputeFunction<T> = ChessMoveComputeFunctionContext<T>.() -> Unit
class ChessMoveComputeFunctionContext<T : ChessPosition>(val board: ChessBoard<T>, val piece: ChessPiece<T>, val position: T) {
    val moves = mutableListOf<ChessMove<T>>()
    fun build() = moves
}

typealias ChessMoveCallback<T> = ChessMoveCallbackContext<T>.() -> Unit
class ChessMoveCallbackContext<T : ChessPosition>(val board: ChessBoard<T>, val piece: ChessPiece<T>, val move: ChessMove<T>)