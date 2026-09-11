package dev.sajarinm.fanremote.ir

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IrProtocolTest {
    private val expectedCodes =
        mapOf(
            FanCommand.Power to 0x1B7E10EFL,
            FanCommand.Speed1 to 0x1B7E807FL,
            FanCommand.Speed2 to 0x1B7E40BFL,
            FanCommand.Speed3 to 0x1B7EC03FL,
            FanCommand.Speed4 to 0x1B7E20DFL,
            FanCommand.Speed5 to 0x1B7EA05FL,
            FanCommand.Boost to 0x1B7EC837L,
            FanCommand.Led to 0x1B7EE01FL,
            FanCommand.Timer2h to 0x1B7E906FL,
            FanCommand.Timer4h to 0x1B7E08F7L,
            FanCommand.Timer8h to 0x1B7E28D7L,
        )

    @Test
    fun `production frames preserve every command and timing constraint`() {
        assertEquals(FanCommand.entries.toSet(), expectedCodes.keys)
        assertEquals(38_000, IrProtocol.CarrierFrequencyHz)
        assertEquals(2L, IrProtocol.InterFrameDelayMs)
        expectedCodes.forEach { (command, code) ->
            assertEquals(code, command.code)
            val frames = IrProtocol.frames(command)
            val toggle = command == FanCommand.Power || command == FanCommand.Led
            assertEquals(if (toggle) 1 else 2, frames.size)
            frames.forEachIndexed { frameIndex, frame ->
                assertEquals(67, frame.size)
                assertEquals(9_000, frame[0])
                assertEquals(4_500, frame[1])
                assertEquals(if (frameIndex == 0) 560 else 1_000, frame.last())
                assertTrue(frame.all { it > 0 })
                assertTrue(frame.sum() < 2_000_000)
                (2..64 step 2).forEach { assertEquals(560, frame[it]) }
                (3..65 step 2).forEach { assertTrue(frame[it] == 560 || frame[it] == 1_690) }
                val decoded =
                    (3..65 step 2).fold(0L) { value, index ->
                        (value shl 1) or if (frame[index] == 1_690) 1L else 0L
                    }
                assertEquals(code, decoded)
            }
        }
    }

    @Test
    fun `speed one starts with most significant bits and preserves repeated payload`() {
        val frames = IrProtocol.frames(FanCommand.Speed1)
        // 0x1B begins 00011011, transmitted most-significant bit first.
        val prefix =
            intArrayOf(
                9_000,
                4_500,
                560,
                560,
                560,
                560,
                560,
                560,
                560,
                1_690,
                560,
                1_690,
                560,
                560,
                560,
                1_690,
                560,
                1_690,
            )
        assertArrayEquals(prefix, frames[0].take(prefix.size).toIntArray())
        assertArrayEquals(frames[0].dropLast(1).toIntArray(), frames[1].dropLast(1).toIntArray())
    }
}
