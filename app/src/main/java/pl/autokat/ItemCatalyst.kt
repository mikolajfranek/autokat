package pl.autokat

class ItemCatalyst(id:Int, idPicture:Int, name:String, brand:String, platinum:Float, palladium:Float, rhodium:Float, type:String, weight:Float) {
    var id : Int = id
    var idPicture : Int = idPicture
    var name : String = name
    var brand : String = brand
    var platinum : Float = platinum
    var palladium : Float = palladium
    var rhodium : Float = rhodium
    var type : String = type
    var weight : Float = weight

    var priceEuro : Float = 0.0f
    var pricePln : Float =  0.0f

    fun countPrice(platinum: Float, palladium: Float, rhodium: Float) {
        this.priceEuro = platinum + palladium + rhodium;
        this.pricePln = this.priceEuro * this.priceEuro;
    }
}