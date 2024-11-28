package fr.xibalba.chess.data

import androidx.compose.runtime.Immutable

@Immutable
interface ChessMove<T : ChessPosition> {
    fun execute(board: ChessBoard<T>): ChessBoard<T>?

    override fun toString(): String

    fun getTargetPosition(): List<Pair<ChessMoveTypes, T>> {
        return when (this) {
            is ChessMoveSimple -> listOf(type to to)
            is ChessMoveMultiple -> moves.map { it.getTargetPosition() }.flatten()
            else -> throw IllegalStateException("Unknown ChessMove type")
        }
    }
}
@Immutable
class ChessMoveMultiple<T : ChessPosition>(val moves: List<ChessMove<T>>) : ChessMove<T> {
    override fun execute(board: ChessBoard<T>): ChessBoard<T>? {
        return moves.fold<ChessMove<T>, ChessBoard<T>?>(board) { acc, move -> if (acc == null) null else move.execute(acc) }
    }

    override fun toString(): String = moves.joinToString(", ")
}

enum class ChessMoveTypes {
    MOVE,
    TAKE
}

typealias ChessMoveComputeFunction<T> = ChessMoveComputeFunctionContext<T>.() -> Unit
@Immutable
class ChessMoveComputeFunctionContext<T : ChessPosition>(val board: ChessBoard<T>, val piece: ChessPiece<T>, val position: T) {
    val moves = mutableListOf<ChessMove<T>>()
    fun build() = moves
}

typealias ChessMoveCallback<T> = ChessMoveCallbackContext<T>.() -> Unit
@Immutable
class ChessMoveCallbackContext<T : ChessPosition>(val board: ChessBoard<T>, val piece: ChessPiece<T>, val move: ChessMove<T>)