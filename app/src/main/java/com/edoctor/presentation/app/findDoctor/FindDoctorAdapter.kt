package com.edoctor.presentation.app.findDoctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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

        private val imageView by rootView.lazyFind<ImageView>(R.id.image_view)
        private val name by rootView.lazyFind<TextView>(R.id.name)
        private val specialization by rootView.lazyFind<TextView>(R.id.specialization)
        private val category by rootView.lazyFind<TextView>(R.id.category)

        fun bind(doctor: DoctorModel) {
            name.text = doctor.fullName
            specialization.text = doctor.specialization
            category.text = rootView.context.run {
                when (doctor.category) {
                    0 -> getString(R.string.doctor_highest_category)
                    1 -> getString(R.string.doctor_first_category)
                    2 -> getString(R.string.doctor_second_category)
                    else -> getString(R.string.doctor_no_category)
                }
            }
            Glide.with(rootView.context)
                .load(doctor.relativeImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.lightLightGrey)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                )
                .into(imageView)

            rootView.setOnClickListener {
                onDoctorClickListener(doctor)
            }
        }

    }

}