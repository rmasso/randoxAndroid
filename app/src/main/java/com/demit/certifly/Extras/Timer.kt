package com.demit.certifly.Extras

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object Timer {
    private lateinit var timer: CountDownTimer
    private val timerLiveData by lazy { MutableLiveData<Long>() }
    val MAX_TIME = 61000L//15 minutes
    private val TICK_INTERVAL = 1000L

    fun startTimer(): LiveData<Long> {
        //If already a timer is running top it to avoid memory leak.
        stopTimer()
        timer = object : CountDownTimer(MAX_TIME, TICK_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                timerLiveData.postValue(millisUntilFinished)
            }
            override fun onFinish() {
                timerLiveData.postValue(0L)
            }

        }
        timer.start()

        return timerLiveData
    }

    fun stopTimer() {
        if (this::timer.isInitialized)
            timer.cancel()
    }
}