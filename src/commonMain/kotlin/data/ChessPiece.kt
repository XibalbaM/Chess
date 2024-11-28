package fr.xibalba.chess.data

import androidx.compose.runtime.Immutable

@Immutable
class ChessPiece<T : ChessPosition>(val color: ChessPieceColor, val type: ChessPieceType<T>) {
    fun getMoves(position: T, board: ChessBoard<T>): List<ChessMove<T>> {
        val context = ChessMoveComputeFunctionContext(board, this, position)
        type.listMoves(context)
        return context.build()
    }
}

@Immutable
value class ChessPieceColor(val isWhite: Boolean) {
    val isBlack get() = !isWhite

    fun opposite() = ChessPieceColor(!isWhite)

    override fun toString(): String = if (isWhite) "white" else "black"
    fun toShortString() = if (isWhite) "w" else "b"

    companion object {
        val WHITE = ChessPieceColor(true)
        val BLACK = ChessPieceColor(false)
    }
}

@Immutable
class ChessPieceType<T : ChessPosition>(val name: String, val listMoves: ChessMoveComputeFunction<T>, val doAfterMove: ChessMoveCallback<T> = {}) {
    companion object
}
typealias ChessPieceTypeRect = ChessPieceType<ChessPositionRect>