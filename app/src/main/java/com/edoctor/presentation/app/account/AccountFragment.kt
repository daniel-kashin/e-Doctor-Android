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
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
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

        private const val REQUEST_CAMERA = 10135
        private const val REQUEST_GALLERY = 10136
    }

    @Inject
    override lateinit var presenter: AccountPresenter

    override val layoutRes: Int = R.layout.fragment_account

    private var dateOfBirthTimestamp: Long? = null
    private var isMale: Boolean? = null

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var contentLayout: ConstraintLayout
    private lateinit var imageView: ImageView
    private lateinit var imageViewPlaceholder: ImageView
    private lateinit var cityEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var dateOfBirthEditText: TextInputEditText
    private lateinit var gender: TextInputEditText
    private lateinit var logOutButton: Button
    private lateinit var saveButton: Button

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(AccountModule()).inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dateOfBirthTimestamp = savedInstanceState?.get(DATE_OF_BIRTH_TIMESTAMP_PARAM) as? Long
        isMale = savedInstanceState?.get(IS_MALE_PARAM) as? Boolean
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentLayout = view.findViewById(R.id.content_layout)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        imageView = view.findViewById(R.id.image_view)
        imageViewPlaceholder = view.findViewById(R.id.image_view_placeholder)
        fullNameEditText = view.findViewById(R.id.full_name)
        dateOfBirthEditText = view.findViewById(R.id.date_of_birth)
        gender = view.findViewById(R.id.gender)
        cityEditText = view.findViewById(R.id.city)
        logOutButton = view.findViewById(R.id.log_out_button)
        saveButton = view.findViewById(R.id.save_button)

        contentLayout.hide()

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

        gender.setOnClickListener {
            PopupMenu(gender.context, gender).apply {
                menuInflater.inflate(R.menu.gender, menu)
                setOnMenuItemClickListener { item ->
                    gender.setText(item.title)
                    isMale = item.itemId == R.id.male
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

        swipeRefreshLayout.isRefreshing = viewState.isLoading

        saveButton.setOnClickListener {
            if (!viewState.isLoading && viewState.account != null) {
                presenter.updateAccount(
                    fullName = fullNameEditText.text?.toString()?.takeIfNotEmpty(),
                    city = cityEditText.text?.toString()?.takeIfNotEmpty(),
                    dateOfBirthTimestamp = dateOfBirthTimestamp,
                    isMale = isMale
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
            gender.setText(
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