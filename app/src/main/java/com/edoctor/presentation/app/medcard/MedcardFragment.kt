package com.edoctor.presentation.app.medcard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.PatientModel

class MedcardFragment : Fragment() {

    companion object {
        private const val PATIENT_PARAM = "patient"

        fun newInstance(patient: PatientModel?) = MedcardFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PATIENT_PARAM, patient)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_medcard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patient = arguments?.getSerializable(PATIENT_PARAM) as? PatientModel

        childFragmentManager.let { fragmentManager ->
            view.findViewById<ViewPager>(R.id.view_pager).adapter =
                    MedcardPagerAdapter(
                        fragmentManager,
                        listOf(getString(R.string.tab_events), getString(R.string.tab_parameters)),
                        patient
                    )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}