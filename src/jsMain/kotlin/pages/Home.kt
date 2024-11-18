package fr.xibalba.chess.web.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.varabyte.kobweb.core.Page
import fr.xibalba.chess.data.ChessBoardRect
import fr.xibalba.chess.web.components.ChessBoardRectComponent

@Page("/index")
@Composable
fun Home() {
    val board = ChessBoardRect.createNormal()
    key(board) {
        ChessBoardRectComponent(board)
    }
}