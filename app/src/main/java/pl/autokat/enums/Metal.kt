package pl.autokat.enums

enum class Metal {
    PLATINUM,
    PALLADIUM,
    RHODIUM;

    fun getSymbol(): MetalSymbol {
        return when (this) {
            PLATINUM -> {
                MetalSymbol.PT
            }
            PALLADIUM -> {
                MetalSymbol.PD
            }
            RHODIUM -> {
                MetalSymbol.RH
            }
        }
    }
}