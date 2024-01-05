package pl.autokat.components

import com.github.kittinunf.fuel.Fuel
import com.kizitonwose.calendarview.utils.yearMonth
import org.json.JSONObject
import pl.autokat.enums.Currency
import pl.autokat.enums.Metal
import pl.autokat.models.ModelCourse
import java.net.UnknownHostException
import java.time.LocalDate
import java.util.*

class Course {
    companion object {
        private const val MY_CATALYST_VALUES_URL_USD_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/usd"
        private const val MY_CATALYST_VALUES_URL_EUR_PLN =
            "https://api.nbp.pl/api/exchangerates/rates/a/eur"
        private const val MY_CATALYST_VALUES_URL_CATALYST = "https://kitco-gcdn-prod.stellate.sh"

        private fun getUrlNBP(currency: Currency, date: LocalDate?): String {
            val basicURL = when (currency) {
                Currency.USD -> {
                    MY_CATALYST_VALUES_URL_USD_PLN
                }
                Currency.EUR -> {
                    MY_CATALYST_VALUES_URL_EUR_PLN
                }
                else -> {
                    ""
                }
            }
            if (date == null) {
                return "$basicURL?format=json"
            }
            return "$basicURL/$date?format=json"
        }

        private fun getCourse(
            currency: Currency,
            savingToSharedPreferences: Boolean
        ): Pair<String, String> {
            val (_, response, result) = Fuel.get(
                getUrlNBP(
                    currency,
                    null
                )
            ).responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val rate: JSONObject = JSONObject(result.get()).getJSONArray("rates").getJSONObject(0)
            val value = rate.getString("mid").replace(',', '.')
            val valueDate = rate.getString("effectiveDate")
            if (savingToSharedPreferences) {
                when (currency) {
                    Currency.USD -> {
                        SharedPreference.setKey(SharedPreference.USD_PLN, value)
                        SharedPreference.setKey(SharedPreference.USD_PLN_DATE, valueDate)
                    }
                    Currency.EUR -> {
                        SharedPreference.setKey(SharedPreference.EUR_PLN, value)
                        SharedPreference.setKey(SharedPreference.EUR_PLN_DATE, valueDate)
                    }
                    else -> {
                        //
                    }
                }
            }
            return Pair(value, valueDate)
        }

        private fun getCourse(
            metal: Metal,
            savingToSharedPreferences: Boolean
        ): Pair<String, String> {
            val timestamp = Date().time.div(1000).toString()
            val metalString = metal.toString().lowercase()
            val metalSymbol = metal.getSymbol()
            val (_, response, result) =  Fuel.post(MY_CATALYST_VALUES_URL_CATALYST)
                .header(mapOf("Content-Type" to "application/json; charset=utf-8"))
                .body("{\"query\":\"fragment MetalFragment on Metal { symbol currency results { ...MetalQuoteFragment } } fragment MetalQuoteFragment on Quote { mid unit } query AllMetalsQuote(\$currency: String!, \$timestamp: Int) { $metalString: GetMetalQuote( symbol: \\\"$metalSymbol\\\" timestamp: \$timestamp currency: \$currency ) { ...MetalFragment } }\",\"variables\":{\"timestamp\":$timestamp,\"currency\":\"USD\"}}")
                .responseString()
            if (response.statusCode != 200) throw UnknownHostException()
            val obj: JSONObject = JSONObject(result.get()).getJSONObject("data").getJSONObject(metalString).getJSONArray("results").getJSONObject(0)
            val value = obj.getDouble("mid").div(Configuration.OZ_VALUE).toString() .replace(',', '.')
            val valueDate = LocalDate.now().toString()
            if (savingToSharedPreferences) {
                when (metal) {
                    Metal.PLATINUM -> {
                        SharedPreference.setKey(SharedPreference.PLATINUM, value)
                        SharedPreference.setKey(SharedPreference.PLATINUM_DATE, valueDate)
                    }
                    Metal.PALLADIUM -> {
                        SharedPreference.setKey(SharedPreference.PALLADIUM, value)
                        SharedPreference.setKey(SharedPreference.PALLADIUM_DATE, valueDate)
                    }
                    Metal.RHODIUM -> {
                        SharedPreference.setKey(SharedPreference.RHODIUM, value)
                        SharedPreference.setKey(SharedPreference.RHODIUM_DATE, valueDate)
                    }
                }
            }
            return Pair(value, valueDate)
        }

        @Suppress("ReplaceCallWithBinaryOperator")
        fun getValues(
            database: Database,
            savingToSharedPreferences: Boolean = isCoursesSelected() == false
        ) {
            val (usdPln, usdDate) = getCourse(
                Currency.USD,
                savingToSharedPreferences
            )
            val (eurPln, eurDate) = getCourse(
                Currency.EUR,
                savingToSharedPreferences
            )
            val (platinum, platinumDate) = getCourse(
                Metal.PLATINUM,
                savingToSharedPreferences,
            )
            val (palladium, palladiumDate) = getCourse(
                Metal.PALLADIUM,
                savingToSharedPreferences
            )
            val (rhodium, rhodiumDate) = getCourse(
                Metal.RHODIUM,
                savingToSharedPreferences
            )
            if (usdDate.equals(eurDate) && eurDate.equals(platinumDate) && platinumDate.equals(
                    palladiumDate
                ) && palladiumDate.equals(rhodiumDate)
            ) {
                val commonDate = Formatter.formatStringDate(eurDate)
                val localDate = Parser.parseStringDateToLocalDate(commonDate)
                try {
                    database.insertCourses(
                        ModelCourse(
                            platinum,
                            palladium,
                            rhodium,
                            eurPln,
                            usdPln,
                            commonDate,
                            localDate.yearMonth.toString()
                        )
                    )
                } catch (_: Exception) {
                    //
                }
            }
            SharedPreference.setKey(
                SharedPreference.UPDATE_COURSE_TIMESTAMP,
                Date().time.toString()
            )
        }

        fun saveSelectedCourses(modelCourse: ModelCourse) {
            val date = Parser.parseStringDateToLocalDate(modelCourse.date)
            SharedPreference.setKey(SharedPreference.ACTUAL_COURSES_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.USD_PLN, modelCourse.usdPln)
            SharedPreference.setKey(SharedPreference.USD_PLN_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.EUR_PLN, modelCourse.eurPln)
            SharedPreference.setKey(SharedPreference.EUR_PLN_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.PLATINUM, modelCourse.platinum)
            SharedPreference.setKey(SharedPreference.PLATINUM_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.PALLADIUM, modelCourse.palladium)
            SharedPreference.setKey(SharedPreference.PALLADIUM_DATE, date.toString())
            SharedPreference.setKey(SharedPreference.RHODIUM, modelCourse.rhodium)
            SharedPreference.setKey(SharedPreference.RHODIUM_DATE, date.toString())
        }

        fun isCoursesSelected(): Boolean {
            val actualCoursesChoice =
                SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_CHOICE)
            val actualCoursesDate = SharedPreference.getKey(SharedPreference.ACTUAL_COURSES_DATE)
            return actualCoursesChoice == "0" && actualCoursesDate.isNotEmpty()
        }

        fun calculateCoursesToPln(courseInDollar: String, dollarCourse: String): String {
            if (courseInDollar.isEmpty()) return (0.00).toString()
            return (courseInDollar.toFloat() * (if (dollarCourse.isEmpty()) (0.0F) else dollarCourse.toFloat())).toString()
        }
    }
}