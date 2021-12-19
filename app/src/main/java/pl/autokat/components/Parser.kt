package pl.autokat.components

import org.json.JSONObject

class Parser {
    companion object {
        @Suppress("RegExpRedundantEscape")
        fun parseToJsonFromResultDocsApi(text: String): JSONObject {
            return JSONObject("\\{.*\\}".toRegex().find(text)!!.value)
        }

        fun parseUrlOfPicture(urlShared: String, width: Int, height: Int): String {
            var url = urlShared
            val resultRegex = ".*/d/".toRegex().find(url)!!.value
            url = url.substring(resultRegex.length)
            val pictureIdFromGoogle = url.substring(0, url.indexOf('/'))
            return "https://lh3.googleusercontent.com/u/0/d/$pictureIdFromGoogle=w$width-h$height"
        }

        fun parseSearchingString(input: String): List<String> {
            var searchString = ("\\*{2,}").toRegex().replace(input.trim(), "*")
            searchString = ("\\s{2,}").toRegex().replace(searchString, " ")
            return if (searchString.isEmpty()) mutableListOf() else searchString.split(" ")
        }

        fun parseStringToInt(input: String): Int {
            var result = ("\\s+").toRegex().replace(input, "")
            result = result.replace(',', '.')
            result =
                if (result.indexOf(",") != -1) result.substring(0, result.indexOf(",")) else result
            var resultInt = 0
            try {
                resultInt = result.toInt()
            } catch (e: Exception) {
                //noting
            }
            resultInt = if (resultInt < 0) 0 else resultInt
            return resultInt
        }

        @Suppress("ReplaceCallWithBinaryOperator")
        fun parseStringBooleanToInt(input: String): Int {
            val result = ("[^a-zA-Z]").toRegex().replace(input, "")
            if (result.equals("tak")) return 1
            return 0
        }
    }
}