package fr.xibalba.chess.web

import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.MutablePalette

class SitePalette() {
    fun initDefaultPalette(palette: MutablePalette) {
    }
}

object SitePalettes {
    val light = SitePalette()
    val dark = SitePalette()
}

fun ColorMode.toSitePalette(): SitePalette {
    return when (this) {
        ColorMode.LIGHT -> SitePalettes.light
        ColorMode.DARK -> SitePalettes.dark
    }
}

@InitSilk
fun initTheme(ctx: InitSilkContext) {
    SitePalettes.dark.initDefaultPalette(ctx.theme.palettes.dark)
    SitePalettes.light.initDefaultPalette(ctx.theme.palettes.light)
}
