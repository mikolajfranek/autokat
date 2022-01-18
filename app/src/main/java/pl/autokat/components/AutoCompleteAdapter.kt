package pl.autokat.components

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import pl.autokat.models.ModelHistoryFilter

open class AutoCompleteAdapter<T>(context: Context, textViewResourceId: Int, private val defaultItems: ArrayList<ModelHistoryFilter> = ArrayList())
    : ArrayAdapter<ModelHistoryFilter>(context, textViewResourceId), Filterable {

    internal var items: ArrayList<ModelHistoryFilter>

    init {
        items = defaultItems
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(index: Int): ModelHistoryFilter? {
        return items[index]
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                    items = if (constraint != null && constraint.length > 2) {

                    // Use your API here instead

                    ArrayList(listOf(ModelHistoryFilter(id = 1, name = "John Smith"),
                        ModelHistoryFilter(id = 2, name = "Apple May"),
                        ModelHistoryFilter(id = 3, name = "Kotlin Contact")
                    ).filter { it.name.orEmpty().contains(constraint, true) })
                } else {
                    this@AutoCompleteAdapter.defaultItems
                }

                filterResults.values = items
                filterResults.count = items.size

                //pobierz max X elementow?
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

        }

    }
}
