package fr.xibalba.chess.data

interface ChessBoard<T : ChessPosition> {
    fun getPieceAt(position: T): ChessPiece<T>?
    fun setPieceAt(position: T, piece: ChessPiece<T>?)
    fun getPieces(): List<ChessPieceWithPosition<T>>
    fun hasPosition(position: T) : Boolean
    fun getMovesAt(position: T): List<ChessMove<T>> {
        val piece = getPieceAt(position) ?: return emptyList()
        return piece.getMoves(position, this)
    }
    fun dumbMove(from: T, to: T) {
        println("Dumb move from $from to $to")
        val piece = getPieceAt(from)
        if (piece != null) {
            setPieceAt(from, null)
            setPieceAt(to, piece)
        }
    }
}
data class ChessBoardRect(val width: Int, val height: Int) : ChessBoard<ChessPositionRect> {
    private val pieces = Array(width) { Array<ChessPiece<ChessPositionRect>?>(height) { null } }
    override fun getPieceAt(position: ChessPositionRect): ChessPiece<ChessPositionRect>? {
        if (position.x < 0 || position.x >= pieces.size || position.y < 0 || position.y >= pieces[0].size) {
            return null
        }
        return pieces.getOrNull(position.x)?.getOrNull(position.y)
    }
    override fun setPieceAt(position: ChessPositionRect, piece: ChessPiece<ChessPositionRect>?) {
        if (position.x < 0 || position.x >= pieces.size || position.y < 0 || position.y >= pieces[0].size) {
            return
        }
        pieces.getOrNull(position.x)?.set(position.y, piece)
    }

    override fun getPieces(): List<ChessPieceWithPosition<ChessPositionRect>> {
        return pieces.flatMapIndexed { x, row ->
            row.mapIndexedNotNull { y, piece ->
                piece?.let { ChessPieceWithPosition(it, ChessPositionRect(x, y)) }
            }
        }
    }

    override fun hasPosition(position: ChessPositionRect): Boolean {
        return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height
    }

    fun setForBlackAndWhite(whitePos: ChessPositionRect, blackPos: ChessPositionRect, piece: ChessPiece<ChessPositionRect>) {
        val whitePiece = piece.copy(color = ChessPieceColor.WHITE)
        val blackPiece = piece.copy(color = ChessPieceColor.BLACK)
        setPieceAt(whitePos, whitePiece)
        setPieceAt(blackPos, blackPiece)
    }

    companion object {
        fun createNormal(): ChessBoardRect {
            val board = ChessBoardRect(8, 8)
            for (i in 0 until 8) {
                board.setForBlackAndWhite(ChessPositionRect(i, 1), ChessPositionRect(i, 6), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.PAWN, emptyMap()))
            }
            board.setForBlackAndWhite(ChessPositionRect(0, 0), ChessPositionRect(0, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.ROOK, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(1, 0), ChessPositionRect(1, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.KNIGHT, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(2, 0), ChessPositionRect(2, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.BISHOP, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(3, 0), ChessPositionRect(3, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.QUEEN, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(4, 0), ChessPositionRect(4, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.KING, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(5, 0), ChessPositionRect(5, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.BISHOP, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(6, 0), ChessPositionRect(6, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.KNIGHT, emptyMap()))
            board.setForBlackAndWhite(ChessPositionRect(7, 0), ChessPositionRect(7, 7), ChessPiece(ChessPieceColor.WHITE, ChessPieceTypeRect.ROOK, emptyMap()))

            return board
        }
    }
}

interface ChessPosition {
    fun verticalSymmetry(width: Int): ChessPosition

    override fun toString(): String
}
data class ChessPositionRect(val x: Int, val y: Int) : ChessPosition {
    override fun verticalSymmetry(height: Int): ChessPositionRect = ChessPositionRect(x, height - y - 1)

    override fun toString(): String = "($x, $y)"
}