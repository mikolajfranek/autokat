package pl.autokat

class ItemCatalyst(var id: Int,
                   var idPicture: Int,
                   var name: String?,
                   var brand: String,
                   var platinum: Float, var palladium: Float,
                   var rhodium: Float, var type: String, var weight: Float) {

    fun countPriceElement(gramsPerKilogram: Float, weightOfCatalyst: Float, coursePerGram: Float) : Float{
        return (gramsPerKilogram * coursePerGram * (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN)).toFloat()) * weightOfCatalyst
    }

    fun countPricePln() : Float{
        val pricePlatinum : Float = this.countPriceElement(this.platinum, this.weight, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM).toFloat())
        val pricePalladium : Float = this.countPriceElement(this.palladium, this.weight, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM).toFloat())
        var priceRhodium : Float = this.countPriceElement(this.rhodium,this.weight, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM).toFloat())

        priceRhodium *= (0.9).toFloat()

        val discount : Float = ((MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT)).toFloat()/((100).toFloat()))
        val fiveEuroPerKilogram : Float = (((5).toFloat()) * (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_EUR_PLN)).toFloat()) * this.weight

        val result : Float = ((pricePlatinum + pricePalladium + priceRhodium) * discount) - fiveEuroPerKilogram

        return result
    }
}