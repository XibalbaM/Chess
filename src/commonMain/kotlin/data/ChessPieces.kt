package fr.xibalba.chess.data

val ChessPieceType.Companion.PAWN: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "pawn",
        {
            if (piece.color.isWhite) {
                move(ChessPositionRect(position.x, position.y + 1))
                capture(ChessPositionRect(position.x + 1, position.y + 1))
                capture(ChessPositionRect(position.x - 1, position.y + 1))
                if (position.y == 1) {
                    move(ChessPositionRect(position.x, position.y + 2))
                }
            } else {
                move(ChessPositionRect(position.x, position.y - 1))
                capture(ChessPositionRect(position.x + 1, position.y - 1))
                capture(ChessPositionRect(position.x - 1, position.y - 1))
                if (position.y == 6) {
                    move(ChessPositionRect(position.x, position.y - 2))
                }
            }
        }
    )

val ChessPieceType.Companion.ROOK: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "rook",
        {
            orthogonalMove()
        },
        memory = ChessPieceMemory.generator
    )

val ChessPieceType.Companion.KNIGHT: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "knight",
        {
            knightMove()
        }
    )

val ChessPieceType.Companion.BISHOP: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "bishop",
        {
            diagonalMove()
        }
    )

val ChessPieceType.Companion.QUEEN: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "queen",
        {
            orthogonalMove()
            diagonalMove()
        }
    )

val ChessPieceType.Companion.KING: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "king",
        {
            captureOrMove(ChessPositionRect(position.x + 1, position.y))
            captureOrMove(ChessPositionRect(position.x + 1, position.y + 1))
            captureOrMove(ChessPositionRect(position.x, position.y + 1))
            captureOrMove(ChessPositionRect(position.x - 1, position.y + 1))
            captureOrMove(ChessPositionRect(position.x - 1, position.y))
            captureOrMove(ChessPositionRect(position.x - 1, position.y - 1))
            captureOrMove(ChessPositionRect(position.x, position.y - 1))
            captureOrMove(ChessPositionRect(position.x + 1, position.y - 1))

            if (!ChessPiece.MEMORY[piece as ChessPiece<ChessPosition>]!!.hasMoved) {
                val rook1 = if (piece.color.isWhite) board.getPieceAt(ChessPositionRect(0, position.y)) else board.getPieceAt(ChessPositionRect(7, position.y))
                val rook2 = if (piece.color.isWhite) board.getPieceAt(ChessPositionRect(7, position.y)) else board.getPieceAt(ChessPositionRect(0, position.y))
                if (rook1 != null && !ChessPiece.MEMORY[rook1 as ChessPiece<ChessPosition>]!!.hasMoved) {
                    val kingPos = ChessPositionRect(2, position.y)
                    val rookStartingPos = ChessPositionRect(0, position.y)
                    val rookEndPos = ChessPositionRect(3, position.y)
                    if (board.getPieceAt(ChessPositionRect(1, position.y)) == null && board.getPieceAt(kingPos) == null && board.getPieceAt(rookEndPos) == null) {
                        moves.add(castleMove(piece, rook1, position, kingPos, rookStartingPos, rookEndPos))
                    }
                }
                if (rook2 != null && !ChessPiece.MEMORY[rook2 as ChessPiece<ChessPosition>]!!.hasMoved) {
                    val kingPos = ChessPositionRect(6, position.y)
                    val rookStartingPos = ChessPositionRect(7, position.y)
                    val rookEndPos = ChessPositionRect(5, position.y)
                    if (board.getPieceAt(rookEndPos) == null && board.getPieceAt(kingPos) == null) {
                        moves.add(castleMove(piece, rook2, position, kingPos, rookStartingPos, rookEndPos))
                    }
                }
            }
        },
        memory = ChessPieceMemory.generator
    )
fun castleMove(king: ChessPiece<ChessPositionRect>, rook: ChessPiece<ChessPositionRect>, kingStartingPos: ChessPositionRect, kingEndingPos: ChessPositionRect, rookStartingPos: ChessPositionRect, rookEndPos: ChessPositionRect): ChessMove<ChessPositionRect> {
    return ChessMoveMultiple(listOf(
        ChessMoveSimple(kingStartingPos, kingEndingPos, king, ChessMoveTypes.MOVE),
        ChessMoveSimple(rookStartingPos, rookEndPos, rook, ChessMoveTypes.MOVE)
    ))
}