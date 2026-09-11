package dev.sajarinm.fanremote.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class IrTransmitter(context: Context) {
    private val manager = context.applicationContext.getSystemService(ConsumerIrManager::class.java)

    val isAvailable: Boolean =
        runCatching {
                manager?.hasIrEmitter() == true &&
                    manager.carrierFrequencies.let { ranges ->
                        ranges.isNullOrEmpty() ||
                            ranges.any {
                                IrProtocol.CarrierFrequencyHz in it.minFrequency..it.maxFrequency
                            }
                    }
            }
            .getOrDefault(false)

    suspend fun send(command: FanCommand) =
        withContext(Dispatchers.IO) {
            check(isAvailable) { "Infrared unavailable" }
            val emitter = checkNotNull(manager)
            IrProtocol.frames(command).forEachIndexed { index, pattern ->
                currentCoroutineContext().ensureActive()
                // Match the short tested gap on the worker thread, never on the UI thread.
                if (index > 0) Thread.sleep(IrProtocol.InterFrameDelayMs)
                emitter.transmit(IrProtocol.CarrierFrequencyHz, pattern)
            }
        }
}
