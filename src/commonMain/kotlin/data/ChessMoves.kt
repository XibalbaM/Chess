package fr.xibalba.chess.data

import kotlin.math.max

class ChessMoveSimple<T : ChessPosition>(val from: T, val to: T, val chessPiece: ChessPiece<T>, val type: ChessMoveTypes) : ChessMove<T> {
    override fun execute(board: ChessBoard<T>) {
        board.dumbMove(from, to)
        chessPiece.type.doAfterMove(ChessMoveCallbackContext(board, chessPiece, this))
    }

    override fun toString(): String = "$from -> $to"
}
fun <T : ChessPosition> ChessMoveComputeFunctionContext<T>.move(to: T) {
    println(position)
    println(to)
    if (to != position && board.hasPosition(to) && board.getPieceAt(to) == null) {
        moves.add(ChessMoveSimple(position, to, piece, ChessMoveTypes.MOVE))
    }
}
fun <T : ChessPosition> ChessMoveComputeFunctionContext<T>.capture(to: T) {
    if (to != position && board.hasPosition(to) && board.getPieceAt(to).let { it != null && it.color != piece.color}) {
        moves.add(ChessMoveSimple(position, to, piece, ChessMoveTypes.TAKE))
    }
}
fun <T : ChessPosition> ChessMoveComputeFunctionContext<T>.captureOrMove(to: T) {
    move(to)
    capture(to)
}

fun ChessMoveComputeFunctionContext<ChessPositionRect>.orthogonalMove() {
    for (i in (position.x + 1) until (board as ChessBoardRect).width) {
        val newPos = ChessPositionRect(i, position.y)
        if (board.getPieceAt(newPos) == null) {
            move(newPos)
        } else {
            capture(newPos)
            break
        }
    }
    for (i in (position.x - 1) downTo 0) {
        val newPos = ChessPositionRect(i, position.y)
        if (board.getPieceAt(newPos) == null) {
            move(newPos)
        } else {
            capture(newPos)
            break
        }
    }
    for (i in (position.y + 1) until board.height) {
        val newPos = ChessPositionRect(position.x, i)
        if (board.getPieceAt(newPos) == null) {
            move(newPos)
        } else {
            capture(newPos)
            break
        }
    }
    for (i in (position.y - 1) downTo 0) {
        val newPos = ChessPositionRect(position.x, i)
        if (board.getPieceAt(newPos) == null) {
            move(newPos)
        } else {
            capture(newPos)
            break
        }
    }
}

fun ChessMoveComputeFunctionContext<ChessPositionRect>.diagonalMove() {
    for (i in 1 until max((board as ChessBoardRect).width, board.height)) {
        val newPos = ChessPositionRect(position.x + i, position.y + i)
        if (board.hasPosition(newPos)) {
            if (board.getPieceAt(newPos) == null) {
                move(newPos)
            } else {
                capture(newPos)
                break
            }
        }
    }
    for (i in 1 until max(board.width, board.height)) {
        val newPos = ChessPositionRect(position.x + i, position.y - i)
        if (board.hasPosition(newPos)) {
            if (board.getPieceAt(newPos) == null) {
                move(newPos)
            } else {
                capture(newPos)
                break
            }
        }
    }
    for (i in 1 until max(board.width, board.height)) {
        val newPos = ChessPositionRect(position.x - i, position.y + i)
        if (board.hasPosition(newPos)) {
            if (board.getPieceAt(newPos) == null) {
                move(newPos)
            } else {
                capture(newPos)
                break
            }
        }
    }
    for (i in 1 until max(board.width, board.height)) {
        val newPos = ChessPositionRect(position.x - i, position.y - i)
        if (board.hasPosition(newPos)) {
            if (board.getPieceAt(newPos) == null) {
                move(newPos)
            } else {
                capture(newPos)
                break
            }
        }
    }
}

fun ChessMoveComputeFunctionContext<ChessPositionRect>.knightMove() {
    captureOrMove(ChessPositionRect(position.x + 1, position.y + 2))
    captureOrMove(ChessPositionRect(position.x + 2, position.y + 1))
    captureOrMove(ChessPositionRect(position.x + 2, position.y - 1))
    captureOrMove(ChessPositionRect(position.x + 1, position.y - 2))
    captureOrMove(ChessPositionRect(position.x - 1, position.y - 2))
    captureOrMove(ChessPositionRect(position.x - 2, position.y - 1))
    captureOrMove(ChessPositionRect(position.x - 2, position.y + 1))
    captureOrMove(ChessPositionRect(position.x - 1, position.y + 2))
}