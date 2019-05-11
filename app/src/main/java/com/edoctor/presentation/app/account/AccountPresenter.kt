package com.edoctor.presentation.app.account

import android.graphics.Bitmap
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.AccountRepository
import com.edoctor.data.repository.AuthRepository
import com.edoctor.presentation.app.account.AccountPresenter.Event
import com.edoctor.presentation.app.account.AccountPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import com.edoctor.utils.writeToFile
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class AccountPresenter @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    companion object {
        private const val USERPIC_FILE_NAME = "userpic.tmp"
    }

    init {
        setViewState(ViewState.EMPTY)

        refreshAccount()
    }

    fun refreshAccount() {
        disposables += accountRepository.getCurrentAccount(refresh = false)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { setViewState { copy(isLoading = true) } }
            .doOnSuccess { setViewState { copy(account = it, selectedAvatar = null) } }
            .flatMap {
                accountRepository.getCurrentAccount(refresh = true)
                    .subscribeOn(subscribeScheduler)
                    .observeOn(observeScheduler)
            }
            .subscribe({
                setViewState { copy(account = it, selectedAvatar = null, isLoading = false) }
            }, { throwable ->
                setViewState { copy(isLoading = false) }
                when {
                    throwable.isSessionException() -> sendEvent(Event.ShowSessionException)
                    throwable.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                    else -> sendEvent(Event.ShowUnknownException)
                }
            })
    }

    fun updateAccount(
        fullName: String?,
        city: String?,
        dateOfBirthTimestamp: Long?,
        isMale: Boolean?,
        bloodGroup: Int?,
        isReadyForConsultation: Boolean?,
        isReadyForAudio: Int?,
        yearsOfExperience: Int?,
        category: Int?,
        specialization: String?,
        clinicalInterests: String?,
        education: String?,
        workExperience: String?,
        trainings: String?
    ) {
        val viewState = viewStateSnapshot()
        val oldAccount = viewState.account

        val newAccount = when (oldAccount) {
            is PatientModel -> oldAccount.copy(
                fullName = fullName,
                city = city,
                dateOfBirthTimestamp = dateOfBirthTimestamp,
                isMale = isMale,
                bloodGroup = bloodGroup
            )
            is DoctorModel -> oldAccount.copy(
                fullName = fullName,
                city = city,
                dateOfBirthTimestamp = dateOfBirthTimestamp,
                isMale = isMale,
                isReadyForAudio = isReadyForAudio ?: 0,
                isReadyForConsultation = isReadyForConsultation ?: false,
                yearsOfExperience = yearsOfExperience,
                category = category,
                specialization = specialization,
                clinicalInterests = clinicalInterests,
                education = education,
                workExperience = workExperience,
                trainings = trainings
            )
            else -> return
        }

        disposables += accountRepository.updateAccount(newAccount, viewState.selectedAvatar)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { setViewState { copy(account = newAccount, isLoading = true) } }
            .subscribe({
                setViewState { copy(account = it, selectedAvatar = null, isLoading = false) }
                sendEvent(Event.ShowChangesSavedEvent)
            }, { throwable ->
                setViewState { copy(account = oldAccount, isLoading = false) }
                when {
                    throwable.isSessionException() -> sendEvent(Event.ShowSessionException)
                    throwable.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                    else -> sendEvent(Event.ShowUnknownException)
                }
            })
    }

    fun onImageSelected(bitmap: Bitmap, cacheDirectory: File) {
        disposables += Single
            .fromCallable {
                bitmap.writeToFile(File(cacheDirectory, USERPIC_FILE_NAME))
            }
            .subscribeOn(Schedulers.io())
            .subscribe(
                { setViewState { copy(selectedAvatar = it) } },
                { sendEvent(Event.ShowImageUploadException) }
            )
    }

    fun logOut() {
        disposables += authRepository.logOut()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe {
                sendEvent(Event.ShowSessionException)
            }
    }

    data class ViewState(
        val account: UserModel?,
        val selectedAvatar: File?,
        val isLoading: Boolean
    ) : Presenter.ViewState {
        companion object {
            val EMPTY = ViewState(null, null, true)
        }
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
        object ShowNoNetworkException : Event()
        object ShowImageUploadException : Event()
        object ShowUnknownException : Event()
        object ShowChangesSavedEvent : Event()
    }

}