package pl.autokat

import android.graphics.Bitmap

class MyItemCatalyst(var id: Int,
                     var idPicture: String,
                     var urlPicture : String,
                     var thumbnail : Bitmap,
                     var name: String?,
                     var brand: String,
                     var platinum: Float, var palladium: Float,
                     var rhodium: Float, var type: String, var weight: Float) {

    fun countPriceElement(gramsPerKilogram: Float, weightOfCatalyst: Float, coursePerGramInput: String) : Float{
        val coursePerGram : Float = if(coursePerGramInput.isEmpty()) 0.0F else coursePerGramInput.toFloat()
        val courseUsdPlnFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)
        val courseUsdPln : Float = if(courseUsdPlnFromConfiguration.isEmpty()) 0.0F else courseUsdPlnFromConfiguration.toFloat()
        return (gramsPerKilogram * coursePerGram * courseUsdPln) * weightOfCatalyst
    }

    fun countPricePln() : Float{
        val pricePlatinum : Float = this.countPriceElement(this.platinum, this.weight, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM))
        val pricePalladium : Float = this.countPriceElement(this.palladium, this.weight, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM))
        var priceRhodium : Float = this.countPriceElement(this.rhodium,this.weight, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM))

        priceRhodium *= (0.9).toFloat()

        val discount : Float = ((MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT)).toFloat()/((100).toFloat()))
        val courseEurPlnFromConfiguration : String = MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN)
        val courseEurPln : Float = if(courseEurPlnFromConfiguration.isEmpty()) 0.0F else courseEurPlnFromConfiguration.toFloat()
        val fiveEuroPerKilogram : Float = (((5).toFloat()) * courseEurPln) * this.weight

        val result : Float = ((pricePlatinum + pricePalladium + priceRhodium) * discount) - fiveEuroPerKilogram

        return result
    }
}