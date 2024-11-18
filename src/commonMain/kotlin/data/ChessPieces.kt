package fr.xibalba.chess.data

val ChessPieceType.Companion.PAWN: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "pawn",
        {
            if (piece.color.isWhite) {
                move(ChessPositionRect(position.x, position.y + 1))
                capture(ChessPositionRect(position.x + 1, position.y + 1))
                capture(ChessPositionRect(position.x - 1, position.y + 1))
            } else {
                move(ChessPositionRect(position.x, position.y - 1))
                capture(ChessPositionRect(position.x + 1, position.y - 1))
                capture(ChessPositionRect(position.x - 1, position.y - 1))
            }
        }
    )

val ChessPieceType.Companion.ROOK: ChessPieceTypeRect
    get() = ChessPieceTypeRect(
        "rook",
        {
            orthogonalMove()
        }
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
        }
    )