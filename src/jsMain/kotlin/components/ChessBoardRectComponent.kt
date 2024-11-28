package fr.xibalba.chess.web.components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.translate
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.components.graphics.*
import fr.xibalba.chess.data.ChessBoardRect
import fr.xibalba.chess.data.ChessMoveTypes
import fr.xibalba.chess.data.ChessPiece
import fr.xibalba.chess.data.ChessPieceColor
import fr.xibalba.chess.data.ChessPosition
import fr.xibalba.chess.data.ChessPositionRect
import org.jetbrains.compose.web.css.px
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

const val SQUARE_SIZE = 50
const val SQUARE_SIZE_D = SQUARE_SIZE.toDouble()

@Composable
fun ChessBoardRectComponent(board: ChessBoardRect, color: ChessPieceColor = ChessPieceColor.WHITE) {
    var board by remember { mutableStateOf(board) }
    var pieces by remember { mutableStateOf(board.getPieces()) }
    var focusedPiece by remember { mutableStateOf<ChessPositionRect?>(null) }
    val repainter = remember { CanvasRepainter() }
    Canvas2d(
        width = SQUARE_SIZE * board.width,
        height = SQUARE_SIZE * board.height,
        repainter = repainter,
        modifier = Modifier.onClick { repainter.repaint() }) {
        ctx.clearRect(0.0, 0.0, SQUARE_SIZE_D * board.width, SQUARE_SIZE_D * board.height)
        chessBoardRectBgComponent(board.width, board.height, focusedPiece?.verticalSymmetryIfWhite(board.height, color), color)
    }
    for (piece in pieces) {
        val callback = remember<(SyntheticMouseEvent) -> Unit> {
            {
                println("Clicked on piece ${board.getPosition(piece)}")
                focusedPiece = if (focusedPiece == board.getPosition(piece)) {
                    null
                } else {
                    board.getPosition(piece)
                }
                repainter.repaint()
            }
        }
        PieceComponent(piece, board.getPosition(piece)!!.verticalSymmetryIfWhite(board.height, color), color, callback)
    }
    PieceMovesComponent(board, color, focusedPiece) {
        repainter.repaint()
        focusedPiece = null
        board = it
    }
}

private fun RenderScope<CanvasRenderingContext2D>.chessBoardRectBgComponent(width: Int, height: Int, focusedPiece: ChessPositionRect?, color: ChessPieceColor) {
    println("Drawing board")
    for (x in 0 until width) {
        for (y in 0 until height) {
            val color = if ((x + y) % 2 == if (color.isWhite) 0 else 1) {
                if (focusedPiece == ChessPositionRect(x, y)) {
                    "#f6ea70"
                } else {
                    "#f0d9b5"
                }
            } else {
                if (focusedPiece == ChessPositionRect(x, y)) {
                    "#dbc34a"
                } else {
                    "#b58863"
                }
            }
            ctx.fillStyle = color
            ctx.fillRect(x * SQUARE_SIZE_D, y * SQUARE_SIZE_D, SQUARE_SIZE_D, SQUARE_SIZE_D)
        }
    }
    // Draw the coordinates
    ctx.font = "12px Arial"
    for (x in 1..width) {
        ctx.fillStyle = if (x % 2 == if (color.isWhite) 1 else 0) "#f0d9b5" else "#b58863"
        ctx.fillText("${if (color.isWhite) 'a' + x - 1 else 'h' - x + 1}", x * SQUARE_SIZE_D - 8, height * SQUARE_SIZE_D - 2.5)
    }
    for (y in 0 until height) {
        ctx.fillStyle = if (y % 2 == if (color.isWhite) 0 else 1) "#b58863" else "#f0d9b5"
        ctx.fillText("${if (color.isWhite) height - y else y + 1}", 2.5, (y * SQUARE_SIZE_D) + 12.5)
    }
}

@Composable
private fun PieceComponent(piece: ChessPiece<ChessPositionRect>, position: ChessPositionRect, color: ChessPieceColor, onClick: (SyntheticMouseEvent) -> Unit) {
    println("Drawing piece " + piece.color + piece.type.name)
    val x = position.x * SQUARE_SIZE
    val y = position.y * SQUARE_SIZE
    val imagePath = "/pieces/${piece.color.toShortString()}${piece.type.name}.png"
    Box(Modifier.translate(x.px, y.px).width(SQUARE_SIZE.px).height(SQUARE_SIZE.px).onClick(onClick)) {
        Image(
            src = imagePath, width = SQUARE_SIZE, height = SQUARE_SIZE, description = "${piece.color} ${piece.type.name}"
        )
    }
}

@Composable
private fun PieceMovesComponent(board: ChessBoardRect, color: ChessPieceColor, focusedPiecePosition: ChessPositionRect?, update: (board: ChessBoardRect) -> Unit) {
    println("Drawing moves")
    if (focusedPiecePosition != null) {
        val piece = board.getPieceAt(focusedPiecePosition)!!
        val moves = piece.getMoves(focusedPiecePosition, board).flatMap { move -> move.getTargetPosition().map { move to it } }
        for ((move, target) in moves) {
            val pos = target.second.verticalSymmetryIfWhite(board.height, color)
            val x = pos.x * SQUARE_SIZE
            val y = pos.y * SQUARE_SIZE
            when (target.first) {
                ChessMoveTypes.MOVE -> {
                    Box(Modifier.translate(x.px, y.px).width(SQUARE_SIZE.px).height(SQUARE_SIZE.px).onClick { update(move.execute(board) as ChessBoardRect) }) {
                        Canvas2d(width = SQUARE_SIZE, height = SQUARE_SIZE, minDeltaMs = REPAINT_CANVAS_MANUALLY) {
                            ctx.fillStyle = "rgba(0, 0, 0, 0.1)"
                            ctx.beginPath()
                            ctx.arc(SQUARE_SIZE_D / 2, SQUARE_SIZE_D / 2, SQUARE_SIZE_D / 4, 0.0, 2 * PI)
                            ctx.fill()
                        }
                    }
                }

                ChessMoveTypes.TAKE -> {

                }
            }
        }
    }
}

private fun ChessPositionRect.verticalSymmetryIfWhite(height: Int, color: ChessPieceColor): ChessPositionRect {
    return if (color.isWhite) {
        verticalSymmetry(height)
    } else {
        this
    }
}