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
import fr.xibalba.chess.data.*
import org.jetbrains.compose.web.css.px
import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

const val SQUARE_SIZE = 50
const val SQUARE_SIZE_D = SQUARE_SIZE.toDouble()

class GameState(val board: ChessBoardRect, val turn: ChessPieceColor)

@Composable
fun ChessBoardRectComponent(startingBoard: ChessBoardRect, gameStateListener: ((GameState) -> Unit)? = null, noTurn: Boolean = false) {
    var gameState by remember { mutableStateOf(GameState(startingBoard, ChessPieceColor.WHITE)) }
    val pieces by remember { mutableStateOf(startingBoard.getPieces()) }
    var focusedPiece by remember { mutableStateOf<ChessPositionRect?>(null) }
    val repainter = remember { CanvasRepainter() }
    Canvas2d(
        width = SQUARE_SIZE * gameState.board.width,
        height = SQUARE_SIZE * gameState.board.height,
        repainter = repainter,
        modifier = Modifier.onClick { repainter.repaint() }) {
        ctx.clearRect(0.0, 0.0, SQUARE_SIZE_D * gameState.board.width, SQUARE_SIZE_D * gameState.board.height)
        chessBoardRectBgComponent(gameState.board.width, gameState.board.height, focusedPiece?.symmetry(gameState.board.width, gameState.board.height, gameState.turn), gameState.turn)
    }
    for (piece in pieces) {
        val callback = remember<(SyntheticMouseEvent) -> Unit> {
            {
                println("Clicked on piece ${gameState.board.getPosition(piece)}")
                focusedPiece = if (piece.color != gameState.turn || focusedPiece == gameState.board.getPosition(piece)) {
                    null
                } else {
                    gameState.board.getPosition(piece)
                }
                repainter.repaint()
            }
        }
        PieceComponent(piece, gameState.board.getPosition(piece)?.symmetry(gameState.board.width, gameState.board.height, gameState.turn), callback)
    }
    PieceMovesComponent(gameState.board, gameState.turn, focusedPiece) {
        repainter.repaint()
        focusedPiece = null
        gameState = GameState(it, if(noTurn) gameState.turn else gameState.turn.opposite())
        gameStateListener?.invoke(gameState)
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
private fun PieceComponent(piece: ChessPiece<ChessPositionRect>, position: ChessPositionRect?, onClick: (SyntheticMouseEvent) -> Unit) {
    if (position == null) {
        return
    }
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
private fun PieceMovesComponent(
    board: ChessBoardRect,
    turn: ChessPieceColor,
    focusedPiecePosition: ChessPositionRect?,
    update: (board: ChessBoardRect) -> Unit
) {
    println("Drawing moves")
    if (focusedPiecePosition != null) {
        val piece = board.getPieceAt(focusedPiecePosition)!!
        if (piece.color != turn) {
            return
        }
        val moves = piece.getMoves(focusedPiecePosition, board).flatMap { move -> move.getTargetPosition().map { move to it } }
        for ((move, target) in moves) {
            val pos = target.second.symmetry(board.width, board.height, turn)
            val x = pos.x * SQUARE_SIZE
            val y = pos.y * SQUARE_SIZE
            Box(Modifier.translate(x.px, y.px).width(SQUARE_SIZE.px).height(SQUARE_SIZE.px).onClick { update(move.execute(board) as ChessBoardRect) }) {
                key("move-${target.first}-${pos}") {
                    Canvas2d(width = SQUARE_SIZE, height = SQUARE_SIZE, minDeltaMs = REPAINT_CANVAS_MANUALLY) {
                        ctx.clearRect(0.0, 0.0, SQUARE_SIZE_D, SQUARE_SIZE_D)
                        when (target.first) {
                            ChessMoveTypes.MOVE -> {
                                ctx.fillStyle = "rgba(0, 0, 0, 0.1)"
                                ctx.beginPath()
                                ctx.arc(SQUARE_SIZE_D / 2, SQUARE_SIZE_D / 2, SQUARE_SIZE_D / 4, 0.0, 2 * PI)
                                ctx.fill()
                            }

                            ChessMoveTypes.TAKE -> {
                                ctx.beginPath()
                                ctx.arc(SQUARE_SIZE_D / 2, SQUARE_SIZE_D / 2, (SQUARE_SIZE_D - 5) / 2, 0.0, 2 * PI)
                                ctx.lineWidth = 5.0
                                ctx.strokeStyle = "rgba(0, 0, 0, 0.1)"
                                ctx.stroke()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ChessPositionRect.symmetry(width: Int, height: Int, color: ChessPieceColor): ChessPositionRect {
    return if (color.isWhite) {
        verticalSymmetry(height)
    } else {
        horizontalSymmetry(width)
    }
}