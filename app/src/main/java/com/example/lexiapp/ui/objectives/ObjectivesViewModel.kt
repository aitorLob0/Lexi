package com.example.lexiapp.ui.objectives

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexiapp.domain.model.Objective
import com.example.lexiapp.domain.useCases.ObjectivesUseCases
import com.example.lexiapp.domain.service.FireStoreService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class ObjectivesViewModel @Inject constructor(
    private val objectivesUseCases: ObjectivesUseCases,
    private val fireStoreService: FireStoreService
) : ViewModel() {

    private val _objectives = MutableLiveData<List<Objective>>()
    val objectives: LiveData<List<Objective>> = _objectives

    private val _daysLeft = MutableLiveData<Int>()
    val daysLeft: LiveData<Int> = _daysLeft

    init {
        loadObjectives()
    }

    fun getLastMondayDate(): String {
        val timeZone = ZoneId.of("America/Argentina/Buenos_Aires")
        val currentDate = LocalDate.now(timeZone)
        val lastMonday = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val lastMondayDate = lastMonday.format(dateFormatter)
        return lastMondayDate
    }

    fun loadObjectives() {
        val today = LocalDate.now(ZoneId.of("America/Argentina/Buenos_Aires"))
        val nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
        val nextMondayDateTime = nextMonday.atStartOfDay(ZoneId.of("America/Argentina/Buenos_Aires"))
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid
        val lastMondayDate= getLastMondayDate()
        if (uid != null) {
            viewModelScope.launch {
                val objectives = fireStoreService.getObjectives(uid, lastMondayDate)
                _objectives.value = objectives

                val daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")), nextMondayDateTime)
                _daysLeft.value = daysLeft.toInt()
            }
        }
    }

    fun saveObjectivesToFirestore(email: String) {
        viewModelScope.launch {
            val objectivesExist = fireStoreService.checkObjectivesExist(email)
            if (!objectivesExist) {
                val today = LocalDate.now()
                val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val objectives = objectivesUseCases.getObjectives(monday)

                fireStoreService.saveObjectives(email, objectives ?: emptyList())
            }
        }
    }
}
