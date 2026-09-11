package dev.sajarinm.fanremote.ui

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sajarinm.fanremote.ir.FanCommand
import dev.sajarinm.fanremote.ir.IrTransmitter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RemoteState(
    val available: Boolean,
    val sending: FanCommand? = null,
    val failed: Boolean = false,
)

class RemoteViewModel(private val transmitter: IrTransmitter) : ViewModel() {
    private val mutableState = MutableStateFlow(RemoteState(available = transmitter.isAvailable))
    val state = mutableState.asStateFlow()

    @MainThread
    fun send(command: FanCommand): Boolean {
        val current = mutableState.value
        if (!current.available || current.sending != null) return false
        mutableState.value = current.copy(sending = command, failed = false)
        viewModelScope.launch {
            try {
                transmitter.send(command)
                mutableState.value = mutableState.value.copy(sending = null)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = mutableState.value.copy(sending = null, failed = true)
            }
        }
        return true
    }
}
