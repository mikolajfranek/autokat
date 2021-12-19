package pl.autokat.components

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup

class UserInterface {
    companion object {
        fun changeStatusLayout(linearLayout: ViewGroup, isEnabled: Boolean) {
            for (i in 0 until linearLayout.childCount) {
                val view = linearLayout.getChildAt(i)
                view.isEnabled = isEnabled
            }
        }

        fun colorText(input: String, search: String): SpannableString {
            val spannable = SpannableString(input)
            for (item in Parser.parseSearchingString(search)) {
                val regex = item.replace("*", ".*")
                var startIndex = 0
                regex.toRegex(
                    options = mutableSetOf(
                        RegexOption.IGNORE_CASE,
                        RegexOption.MULTILINE,
                        RegexOption.DOT_MATCHES_ALL
                    )
                ).findAll(input, 0).toList().forEach { x ->
                    val i = input.indexOf(x.value, startIndex, true)
                    startIndex = i + 1
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#FF00FF")),
                        i,
                        i + x.value.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannable
        }
    }
}