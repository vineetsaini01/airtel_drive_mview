package com.visionairtel.drivetest.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<E, UE> : ViewModel() {

    abstract fun event(event: E)
    private val _uiEvents = Channel<UE>()
    val uiEvents = _uiEvents.receiveAsFlow()

    protected fun UE.triggerEvent() = viewModelScope.launch {
        _uiEvents.send(this@triggerEvent)
    }
}