package dev.sajarinm.fanremote.ir

object IrProtocol {
    const val CarrierFrequencyHz = 38_000
    const val InterFrameDelayMs = 2L

    fun frames(command: FanCommand): List<IntArray> {
        val first = frame(command.code, trailerMarkUs = 560)
        // Repeating a toggle can undo the requested action. Set commands retain two frames.
        return if (command.isToggle) listOf(first)
        else listOf(first, frame(command.code, trailerMarkUs = 1_000))
    }

    private fun frame(code: Long, trailerMarkUs: Int): IntArray =
        IntArray(67).apply {
            this[0] = 9_000
            this[1] = 4_500
            for (bit in 31 downTo 0) {
                val index = 2 + (31 - bit) * 2
                this[index] = 560
                this[index + 1] = if ((code ushr bit) and 1L == 1L) 1_690 else 560
            }
            // Preserve the tested asymmetric final marks for repeated set commands.
            this[66] = trailerMarkUs
        }
}
