package pl.autokat.models

import android.graphics.Bitmap
import pl.autokat.components.SharedPreference

class ModelCatalyst(
    var id: Int,
    var idPicture: String,
    var urlPicture: String,
    var thumbnail: Bitmap?,
    var name: String,
    var brand: String,
    var platinum: Float,
    var palladium: Float,
    var rhodium: Float,
    var type: String,
    var weight: Float
) {
    fun countPricePln(): Float {
        val courseUsdPlnFromConfiguration: String =
            SharedPreference.getKeyFromFile(SharedPreference.USD_PLN)
        val courseUsdPln: Float =
            if (courseUsdPlnFromConfiguration.isEmpty()) 0.0F else courseUsdPlnFromConfiguration.toFloat()
        val pricePlatinum: Float = this.countPriceElement(
            this.platinum, this.weight, courseUsdPln,
            SharedPreference.getKeyFromFile(SharedPreference.PLATIUNUM),
            SharedPreference.getKeyFromFile(SharedPreference.MINUS_PLATINIUM)
        )
        val pricePalladium: Float = this.countPriceElement(
            this.palladium, this.weight, courseUsdPln,
            SharedPreference.getKeyFromFile(SharedPreference.PALLADIUM),
            SharedPreference.getKeyFromFile(SharedPreference.MINUS_PALLADIUM)
        )
        var priceRhodium: Float = this.countPriceElement(
            this.rhodium, this.weight, courseUsdPln,
            SharedPreference.getKeyFromFile(SharedPreference.RHODIUM),
            SharedPreference.getKeyFromFile(SharedPreference.MINUS_RHODIUM)
        )
        priceRhodium *= (0.9).toFloat()
        val discount: Float =
            ((SharedPreference.getKeyFromFile(SharedPreference.DISCOUNT)).toFloat() / ((100).toFloat()))
        val courseEurPlnFromConfiguration: String =
            SharedPreference.getKeyFromFile(SharedPreference.EUR_PLN)
        val courseEurPln: Float =
            if (courseEurPlnFromConfiguration.isEmpty()) 0.0F else courseEurPlnFromConfiguration.toFloat()
        val fiveEuroPerKilogram: Float = (((5.0F).toFloat()) * courseEurPln) * this.weight
        return ((pricePlatinum + pricePalladium + priceRhodium) * discount) - fiveEuroPerKilogram
    }

    private fun countPriceElement(
        gramsPerKilogram: Float,
        weightOfCatalyst: Float,
        courseUsdPln: Float,
        coursePerGramInput: String,
        minusPlnFromCourseInput: String
    ): Float {
        val coursePerGram: Float =
            if (coursePerGramInput.isEmpty()) 0.0F else coursePerGramInput.toFloat()
        val minusPlnFromCourse: Float =
            if (minusPlnFromCourseInput.isEmpty()) 0.0F else minusPlnFromCourseInput.toFloat()
        return gramsPerKilogram * weightOfCatalyst * ((coursePerGram * courseUsdPln) - minusPlnFromCourse)
    }
}