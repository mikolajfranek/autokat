package pl.autokat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.autokat.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private lateinit var fragmentResultBinding: FragmentResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO


        //activityResultBinding = ActivityResultBinding.inflate(layoutInflater)

        fragmentResultBinding.catalystEmpty.visibility = View.VISIBLE
    }


    //TODO - bindowanie nie działa do fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentResultBinding = FragmentResultBinding.inflate(inflater, container, false)
        //set variables in Binding
        return binding.getRoot()
    }



}