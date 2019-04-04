package com.edoctor.presentation.app.account

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.AccountModule
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.account.AccountPresenter.Event
import com.edoctor.presentation.app.account.AccountPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.google.android.material.textfield.TextInputEditText
import com.tbruyelle.rxpermissions.RxPermissions
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AccountFragment : BaseFragment<AccountPresenter, ViewState, Event>("AccountFragment") {

    companion object {
        const val DATE_OF_BIRTH_TIMESTAMP_PARAM = "date_of_birth_timestamp"
        const val IS_MALE_PARAM = "is_male"
        const val CATEGORY_NUMBER_PARAM = "category_number"
        const val BLOOD_GROUP_PARAM = "blood_group"

        private const val REQUEST_CAMERA = 10135
        private const val REQUEST_GALLERY = 10136
    }

    @Inject
    override lateinit var presenter: AccountPresenter

    override val layoutRes: Int = R.layout.fragment_account

    private var dateOfBirthTimestamp: Long? = null
    private var isMale: Boolean? = null
    private var categoryNumber: Int? = null
    private var bloodGroup: Int? = null

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var contentLayout: ConstraintLayout

    private lateinit var imageView: ImageView
    private lateinit var imageViewPlaceholder: ImageView
    private lateinit var cityEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var dateOfBirthEditText: TextInputEditText
    private lateinit var genderEditText: TextInputEditText

    private lateinit var bloodGroupEditText: TextInputEditText

    private lateinit var labelCareer: TextView
    private lateinit var yearsOfExperienceEditText: TextInputEditText
    private lateinit var categoryEditText: TextInputEditText
    private lateinit var specializationEditText: TextInputEditText
    private lateinit var clinicalInterestsEditText: TextInputEditText
    private lateinit var educationEditText: TextInputEditText
    private lateinit var workExperienceEditText: TextInputEditText
    private lateinit var trainingsEditText: TextInputEditText

    private lateinit var logOutButton: Button
    private lateinit var saveButton: Button

    private val doctorEditTexts by lazy {
        listOf(
            yearsOfExperienceEditText, categoryEditText, specializationEditText, clinicalInterestsEditText,
            educationEditText, workExperienceEditText, trainingsEditText
        )
    }

    private val patientEditTexts by lazy {
        listOf(bloodGroupEditText)
    }

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(AccountModule()).inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dateOfBirthTimestamp = savedInstanceState?.get(DATE_OF_BIRTH_TIMESTAMP_PARAM) as? Long
        isMale = savedInstanceState?.get(IS_MALE_PARAM) as? Boolean
        categoryNumber = savedInstanceState?.get(CATEGORY_NUMBER_PARAM) as? Int
        bloodGroup = savedInstanceState?.get(BLOOD_GROUP_PARAM) as? Int
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.run {
            contentLayout = findViewById(R.id.content_layout)
            swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
            imageView = findViewById(R.id.image_view)
            imageViewPlaceholder = findViewById(R.id.image_view_placeholder)
            fullNameEditText = findViewById(R.id.full_name)
            dateOfBirthEditText = findViewById(R.id.date_of_birth)
            genderEditText = findViewById(R.id.gender)
            cityEditText = findViewById(R.id.city)
            bloodGroupEditText = findViewById(R.id.blood_group)
            labelCareer = findViewById(R.id.label_career)
            yearsOfExperienceEditText = findViewById(R.id.years_of_experience)
            categoryEditText = findViewById(R.id.category)
            specializationEditText = findViewById(R.id.specialization)
            clinicalInterestsEditText = findViewById(R.id.clinical_interests)
            educationEditText = findViewById(R.id.education)
            workExperienceEditText = findViewById(R.id.work_experience)
            trainingsEditText = findViewById(R.id.trainings)
            logOutButton = findViewById(R.id.log_out_button)
            saveButton = findViewById(R.id.save_button)
        }

        contentLayout.hide()

        initializeListeners(view)
    }

    @SuppressLint("SimpleDateFormat")
    private fun initializeListeners(view: View) {
        swipeRefreshLayout.setOnRefreshListener {
            presenter.refreshAccount()
        }

        imageView.setOnClickListener {
            showImagePickerOptions()
        }
        imageViewPlaceholder.setOnClickListener {
            showImagePickerOptions()
        }

        dateOfBirthEditText.setOnClickListener {
            val nowCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            DatePickerDialog(
                view.context,
                { _, year, month, dayOfMonth ->
                    nowCalendar.set(year, month, dayOfMonth, 0, 0, 0)
                    dateOfBirthTimestamp = nowCalendar.timeInMillis.javaTimeToUnixTime()
                    dateOfBirthEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(nowCalendar.time))
                },
                nowCalendar.get(Calendar.YEAR) - 20,
                nowCalendar.get(Calendar.MONTH),
                nowCalendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = nowCalendar.timeInMillis
                show()
            }
        }

        genderEditText.setOnClickListener {
            PopupMenu(genderEditText.context, genderEditText).apply {
                menuInflater.inflate(R.menu.gender, menu)
                setOnMenuItemClickListener { item ->
                    genderEditText.setText(item.title)
                    isMale = item.itemId == R.id.male
                    true
                }
                show()
            }
        }

        categoryEditText.setOnClickListener {
            PopupMenu(categoryEditText.context, categoryEditText).apply {
                menuInflater.inflate(R.menu.doctor_category, menu)
                setOnMenuItemClickListener { item ->
                    categoryEditText.setText(if (item.itemId == R.id.none) null else item.title)
                    categoryNumber = when (item.itemId) {
                        R.id.second -> 2
                        R.id.first -> 1
                        R.id.highest -> 0
                        else -> null
                    }
                    true
                }
                show()
            }
        }

        bloodGroupEditText.setOnClickListener {
            PopupMenu(bloodGroupEditText.context, bloodGroupEditText).apply {
                menuInflater.inflate(R.menu.blood_group, menu)
                setOnMenuItemClickListener { item ->
                    bloodGroupEditText.setText( item.title)
                    bloodGroup = when (item.itemId) {
                        R.id.first_negative -> 0
                        R.id.first_positive -> 1
                        R.id.second_negative -> 2
                        R.id.second_positive -> 3
                        R.id.third_negative -> 4
                        R.id.third_positive -> 5
                        R.id.fourth_negative -> 6
                        R.id.fourth_positive -> 7
                        else -> null
                    }
                    true
                }
                show()
            }
        }

        logOutButton.setOnClickListener {
            presenter.logOut()
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun render(viewState: ViewState) {
        isMale = viewState.account?.isMale
        dateOfBirthTimestamp = viewState.account?.dateOfBirthTimestamp
        categoryNumber = (viewState.account as? DoctorModel)?.category
        bloodGroup = (viewState.account as? PatientModel)?.bloodGroup

        swipeRefreshLayout.isRefreshing = viewState.isLoading

        saveButton.setOnClickListener {
            if (!viewState.isLoading && viewState.account != null) {
                presenter.updateAccount(
                    fullName = fullNameEditText.text?.toString()?.takeIfNotEmpty(),
                    city = cityEditText.text?.toString()?.takeIfNotEmpty(),
                    dateOfBirthTimestamp = dateOfBirthTimestamp,
                    isMale = isMale,
                    bloodGroup = bloodGroup,
                    yearsOfExperience = yearsOfExperienceEditText.text?.toString()?.toIntOrNull(),
                    category = categoryNumber,
                    specialization = specializationEditText.text?.toString()?.takeIfNotEmpty(),
                    clinicalInterests = clinicalInterestsEditText.text?.toString()?.takeIfNotEmpty(),
                    education = educationEditText.text?.toString()?.takeIfNotEmpty(),
                    workExperience = workExperienceEditText.text?.toString()?.takeIfNotEmpty(),
                    trainings = trainingsEditText.text?.toString()?.takeIfNotEmpty()
                )
            }
        }

        if (viewState.selectedAvatar != null) {
            Glide.with(imageView.context)
                .load(viewState.selectedAvatar)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.lightLightGrey)
                        .dontAnimate()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .into(imageView)
        } else {
            Glide.with(imageView.context)
                .load(viewState.account?.relativeImageUrl)
                .apply(
                    RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.lightLightGrey)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                )
                .into(imageView)
        }

        if (viewState.account != null) {
            fullNameEditText.setText(viewState.account.fullName)
            cityEditText.setText(viewState.account.city)
            genderEditText.setText(
                when (viewState.account.isMale) {
                    true -> getString(R.string.male)
                    false -> getString(R.string.female)
                    else -> null
                }
            )

            val dateOfBirthTimestamp = viewState.account.dateOfBirthTimestamp
            dateOfBirthEditText.setText(
                if (dateOfBirthTimestamp != null) {
                    SimpleDateFormat("dd.MM.yyyy").format(Date(dateOfBirthTimestamp.unixTimeToJavaTime()))
                } else {
                    null
                }
            )

            if (viewState.account is DoctorModel) {
                labelCareer.show()
                doctorEditTexts.forEach { (it.parent as View).show() }
                patientEditTexts.forEach { (it.parent as View).hide() }
                yearsOfExperienceEditText.setText(viewState.account.yearsOfExperience?.toString())
                categoryEditText.setText(when (viewState.account.category) {
                    0 -> getString(R.string.highest_category)
                    1 -> getString(R.string.first_category)
                    2 -> getString(R.string.second_category)
                    else -> null
                })
                specializationEditText.setText(viewState.account.specialization)
                clinicalInterestsEditText.setText(viewState.account.clinicalInterests)
                educationEditText.setText(viewState.account.education)
                workExperienceEditText.setText(viewState.account.workExperience)
                trainingsEditText.setText(viewState.account.trainings)
            } else if (viewState.account is PatientModel) {
                labelCareer.hide()
                doctorEditTexts.forEach { (it.parent as View).hide() }
                patientEditTexts.forEach { (it.parent as View).show() }
                bloodGroupEditText.setText(
                    when (viewState.account.bloodGroup) {
                        0 -> getString(R.string.first_negative)
                        1 -> getString(R.string.first_positive)
                        2 -> getString(R.string.second_negative)
                        3 -> getString(R.string.second_positive)
                        4 -> getString(R.string.third_negative)
                        5 -> getString(R.string.third_positive)
                        6 -> getString(R.string.fourth_negative)
                        7 -> getString(R.string.fourth_positive)
                        else -> null
                    }
                )
            }

            contentLayout.show()
        } else {
            contentLayout.hide()
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowSessionException -> activity?.onSessionException()
            is Event.ShowNoNetworkException -> context.toast(getString(R.string.network_error_message))
            is Event.ShowImageUploadException -> context.toast(R.string.image_upload_error_message)
            is Event.ShowUnknownException -> context.toast(R.string.unhandled_error_message)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.put(DATE_OF_BIRTH_TIMESTAMP_PARAM, dateOfBirthTimestamp)
        outState.put(IS_MALE_PARAM, isMale)
        outState.put(CATEGORY_NUMBER_PARAM, categoryNumber)
        outState.put(BLOOD_GROUP_PARAM, bloodGroup)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val activity = activity
        if (resultCode == Activity.RESULT_OK && activity != null) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    (data?.extras?.get("data") as? Bitmap)?.let {
                        presenter.onImageSelected(it, activity.cacheDir)
                    }
                }
                REQUEST_GALLERY -> {
                    MediaStore.Images.Media.getBitmap(activity.contentResolver, data?.data)?.let {
                        presenter.onImageSelected(it, activity.cacheDir)
                    }
                }
            }
        }
    }

    private fun showImagePickerOptions() {
        PopupMenu(imageView.context, imageView).apply {
            menuInflater.inflate(R.menu.image_picker, menu)
            setOnMenuItemClickListener { item ->
                val activity = activity
                if (activity != null) {
                    when (item.itemId) {
                        R.id.gallery -> {
                            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                            galleryIntent.type = Constants.PICK_IMAGE_INTENT_TYPE
                            startActivityForResult(Intent.createChooser(galleryIntent, null), REQUEST_GALLERY)
                        }
                        R.id.camera -> {
                            RxPermissions.getInstance(activity)
                                .request(CAMERA)
                                .onErrorReturn { false }
                                .subscribe { granted ->
                                    if (granted) {
                                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                        if (cameraIntent.resolveActivity(activity.packageManager) != null) {
                                            startActivityForResult(cameraIntent, REQUEST_CAMERA)
                                        }
                                    }
                                }
                        }
                    }
                }
                true
            }
            show()
        }
    }

}