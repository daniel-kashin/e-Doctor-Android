package com.edoctor.presentation.app.medcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.edoctor.R

class MedcardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_medcard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManager?.let { fragmentManager ->
            view.findViewById<ViewPager>(R.id.view_pager).adapter =
                    MedcardPagerAdapter(
                        fragmentManager,
                        listOf(getString(R.string.tab_events), getString(R.string.tab_parameters))
                    )
        }
    }

}