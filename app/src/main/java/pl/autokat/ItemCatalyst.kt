package pl.autokat

class ItemCatalyst(var id: Int,
                   var idPicture: Int,
                   var name: String,
                   var brand: String,
                   var platinum: Float, var palladium: Float,
                   var rhodium: Float, var type: String, var weight: Float) {

    fun countPriceElement(grams: Float, course: Float) : Float{
        return grams * course * MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_USD_PLN).toFloat()
    }

    fun countPricePln() : Float{
        val priceRhodium = this.countPriceElement(this.rhodium, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_RHODIUM).toFloat())
        val pricePalladium = this.countPriceElement(this.palladium, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PALLADIUM).toFloat())
        val pricePlatinum = this.countPriceElement(this.platinum, MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_PLATIUNUM).toFloat())

        return ((priceRhodium + pricePalladium + pricePlatinum) * (MySharedPreferences.getKeyFromFile(MyConfiguration.MY_SHARED_PREFERENCES_KEY_DISCOUNT).toInt()/100))
    }
}