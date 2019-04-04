package com.edoctor.presentation.app.findDoctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.presentation.app.findDoctor.FindDoctorAdapter.FindDoctorViewHolder
import com.edoctor.utils.lazyFind

class FindDoctorAdapter : RecyclerView.Adapter<FindDoctorViewHolder>() {

    var onDoctorClickListener: ((DoctorModel) -> Unit)? = null

    private var doctors: List<DoctorModel> = emptyList()

    internal fun setDoctors(doctors: List<DoctorModel>) {
        this.doctors = doctors
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindDoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_find_doctor, parent, false)

        return FindDoctorViewHolder(view) {
            onDoctorClickListener?.invoke(it)
        }
    }

    override fun onBindViewHolder(holder: FindDoctorViewHolder, position: Int) {
        holder.bind(doctors[position])
    }

    override fun getItemCount() = doctors.size

    class FindDoctorViewHolder(
        private val rootView: View,
        private val onDoctorClickListener: (DoctorModel) -> Unit
    ) : RecyclerView.ViewHolder(rootView) {

        private val doctorEmail by rootView.lazyFind<TextView>(R.id.text_view_doctor_email)

        fun bind(doctor: DoctorModel) {
            doctorEmail.text = doctor.email
            rootView.setOnClickListener {
                onDoctorClickListener(doctor)
            }
        }

    }

}