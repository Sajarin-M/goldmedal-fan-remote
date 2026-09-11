package dev.sajarinm.fanremote.ir

enum class FanCommand(val code: Long, val speed: Int? = null, val isToggle: Boolean = false) {
    Power(0x1B7E10EF, isToggle = true),
    Speed1(0x1B7E807F, speed = 1),
    Speed2(0x1B7E40BF, speed = 2),
    Speed3(0x1B7EC03F, speed = 3),
    Speed4(0x1B7E20DF, speed = 4),
    Speed5(0x1B7EA05F, speed = 5),
    Boost(0x1B7EC837),
    Led(0x1B7EE01F, isToggle = true),
    Timer2h(0x1B7E906F),
    Timer4h(0x1B7E08F7),
    Timer8h(0x1B7E28D7),
}
