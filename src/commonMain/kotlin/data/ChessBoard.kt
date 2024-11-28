package fr.xibalba.chess.data

import androidx.compose.runtime.Immutable

@Immutable
interface ChessBoard<T : ChessPosition> {
    fun getPieceAt(position: T): ChessPiece<T>?
    fun setPieceAt(position: T, piece: ChessPiece<T>?): ChessBoard<T>?
    fun getPieces(): List<ChessPiece<T>>
    fun getPosition(piece: ChessPiece<T>): T?
    fun hasPosition(position: T) : Boolean
    fun getMovesAt(position: T): List<ChessMove<T>> {
        val piece = getPieceAt(position) ?: return emptyList()
        return piece.getMoves(position, this)
    }
    fun dumbMove(from: T, to: T): ChessBoard<T>? {
        println("Dumb move from $from to $to")
        val piece = getPieceAt(from)
        if (piece != null) {
            var newBoard = setPieceAt(from, null)
            newBoard = newBoard?.setPieceAt(to, piece)
            return newBoard
        }
        return null
    }
}
@Immutable
class ChessBoardRect(val width: Int, val height: Int, val pieces: List<List<ChessPiece<ChessPositionRect>?>> = List(width) { List<ChessPiece<ChessPositionRect>?>(height) { null } }) : ChessBoard<ChessPositionRect> {
    override fun getPieceAt(position: ChessPositionRect): ChessPiece<ChessPositionRect>? {
        if (position.x < 0 || position.x >= pieces.size || position.y < 0 || position.y >= pieces[0].size) {
            return null
        }
        return pieces.getOrNull(position.x)?.getOrNull(position.y)
    }
    override fun setPieceAt(position: ChessPositionRect, piece: ChessPiece<ChessPositionRect>?): ChessBoardRect? {
        if (position.x < 0 || position.x >= pieces.size || position.y < 0 || position.y >= pieces[0].size) {
            return null
        }
        val newPieces = pieces.mapIndexed { x, row ->
            if (x == position.x) {
                row.mapIndexed { y, p ->
                    if (y == position.y) {
                        piece
                    } else {
                        p
                    }
                }
            } else {
                row
            }
        }
        return ChessBoardRect(width, height, newPieces)
    }

    override fun getPieces(): List<ChessPiece<ChessPositionRect>> {
        return pieces.flatMap { row ->
            row.mapNotNull { piece ->
                piece
            }
        }
    }

    override fun getPosition(piece: ChessPiece<ChessPositionRect>): ChessPositionRect? {
        pieces.forEachIndexed { x, row ->
            row.forEachIndexed { y, p ->
                if (p == piece) {
                    return ChessPositionRect(x, y)
                }
            }
        }
        return null
    }

    override fun hasPosition(position: ChessPositionRect): Boolean {
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height
    }

    companion object {
        fun createNormal(): ChessBoardRect {
            val pieces = MutableList(8) { MutableList<ChessPiece<ChessPositionRect>?>(8) { null } }
            for (i in 0 until 8) {
                pieces.setForBlackAndWhite(ChessPositionRect(i, 1), ChessPositionRect(i, 6), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.PAWN))
            }
            pieces.setForBlackAndWhite(ChessPositionRect(0, 0), ChessPositionRect(0, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.ROOK))
            pieces.setForBlackAndWhite(ChessPositionRect(1, 0), ChessPositionRect(1, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.KNIGHT))
            pieces.setForBlackAndWhite(ChessPositionRect(2, 0), ChessPositionRect(2, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.BISHOP))
            pieces.setForBlackAndWhite(ChessPositionRect(3, 0), ChessPositionRect(3, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.QUEEN))
            pieces.setForBlackAndWhite(ChessPositionRect(4, 0), ChessPositionRect(4, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.KING))
            pieces.setForBlackAndWhite(ChessPositionRect(5, 0), ChessPositionRect(5, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.BISHOP))
            pieces.setForBlackAndWhite(ChessPositionRect(6, 0), ChessPositionRect(6, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.KNIGHT))
            pieces.setForBlackAndWhite(ChessPositionRect(7, 0), ChessPositionRect(7, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.ROOK))

            return ChessBoardRect(8, 8, pieces)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChessBoardRect) return false

        if (width != other.width) return false
        if (height != other.height) return false
        if (pieces != other.pieces) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + pieces.hashCode()
        return result
    }
}

@Immutable
interface ChessPosition {
    fun verticalSymmetry(width: Int): ChessPosition

    override fun toString(): String
}
data class ChessPositionRect(val x: Int, val y: Int) : ChessPosition {
    override fun verticalSymmetry(height: Int): ChessPositionRect = ChessPositionRect(x, height - y - 1)

    override fun toString(): String = "($x, $y)"
}



fun MutableList<MutableList<ChessPiece<ChessPositionRect>?>>.setForBlackAndWhite(whitePos: ChessPositionRect, blackPos: ChessPositionRect, piece: ChessPiece<ChessPositionRect>) {
    val whitePiece = ChessPiece(ChessPieceColor.WHITE, piece.type)
    val blackPiece = ChessPiece(ChessPieceColor.BLACK, piece.type)
    this[whitePos.x][whitePos.y] = whitePiece
    this[blackPos.x][blackPos.y] = blackPiece
}