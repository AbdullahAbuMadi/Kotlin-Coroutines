package com.abumadi.kotlin_coroutines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TimerViewModel : ViewModel() {

//  //  live data
//    private val _timerLiveData = MutableLiveData<Int>()
//    val timerLiveData = _timerLiveData
//
//    fun startTimer() {
//        viewModelScope.launch {
//            val list= listOf<Int>(1,1,1,2,2,2,3,4,5,6,7,8,8,8,8,9,10)
//            for (i in list){
//                _timerLiveData.value =i
//                delay(1000)}
//        }
//    }
    //state flow
    //can use any operator of flow like filter ...etc
private val _timerStateFlow = MutableStateFlow<Int>(0)//initial value
    val timerStateFlow = _timerStateFlow

    fun startTimer() {
//        viewModelScope.launch {
//            for (i in 1..10){
//                _timerStateFlow.value=i
//                delay(1000)}
//        }
        viewModelScope.launch (Dispatchers.Main){
            val list= listOf<Int>(1,1,1,2,2,2,3,4,5,6,7,8,8,8,8,9,10)//in liveData fire with every record 1,1,1,2,2,2 but in state flow 1,2,3 ,will not fire every record(fire every update)
            for (i in list){
                _timerStateFlow.value=i
                delay(1000)}
        }
    }
    }

