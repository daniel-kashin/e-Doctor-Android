package com.infostrategic.edoctor.presentation.app.medcard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.infostrategic.edoctor.R
import com.infostrategic.edoctor.data.entity.remote.model.user.DoctorModel
import com.infostrategic.edoctor.data.entity.remote.model.user.PatientModel
import com.google.android.material.tabs.TabLayout

class MedcardFragment : Fragment() {

    companion object {
        private const val PATIENT_PARAM = "patient"
        private const val DOCTOR_PARAM = "doctor"
        private const val CURRENT_USER_IS_PATIENT_PARAM = "current_user_is_patient"

        fun newInstance(
            patient: PatientModel,
            doctor: DoctorModel?,
            currentUserIsPatient: Boolean
        ) = MedcardFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PATIENT_PARAM, patient)
                putSerializable(DOCTOR_PARAM, doctor)
                putBoolean(CURRENT_USER_IS_PATIENT_PARAM, currentUserIsPatient)
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

        val patient = arguments!!.getSerializable(PATIENT_PARAM) as PatientModel
        val doctor = arguments!!.getSerializable(PATIENT_PARAM) as? DoctorModel
        val currentUserIsPatient = arguments!!.getBoolean(CURRENT_USER_IS_PATIENT_PARAM)

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager>(R.id.view_pager)

        tabLayout.setupWithViewPager(viewPager)

        viewPager.adapter = MedcardPagerAdapter(
            childFragmentManager,
            listOf(getString(R.string.tab_events), getString(R.string.tab_parameters)),
            patient,
            doctor,
            currentUserIsPatient
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}