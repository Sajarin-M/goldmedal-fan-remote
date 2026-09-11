package dev.sajarinm.fanremote.ui

import android.view.HapticFeedbackConstants
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sajarinm.fanremote.R
import dev.sajarinm.fanremote.ir.FanCommand

private val SpeedKeys =
    listOf(
        listOf(FanCommand.Speed1, FanCommand.Speed2),
        listOf(FanCommand.Speed3, FanCommand.Speed4),
        listOf(FanCommand.Speed5, FanCommand.Boost),
    )

private val TimerKeys = listOf(FanCommand.Timer2h, FanCommand.Timer4h, FanCommand.Timer8h)

@StringRes
private fun FanCommand.label(): Int =
    when (this) {
        FanCommand.Power -> R.string.power
        FanCommand.Speed1 -> R.string.speed_one
        FanCommand.Speed2 -> R.string.speed_two
        FanCommand.Speed3 -> R.string.speed_three
        FanCommand.Speed4 -> R.string.speed_four
        FanCommand.Speed5 -> R.string.speed_five
        FanCommand.Boost -> R.string.boost
        FanCommand.Led -> R.string.led_toggle
        FanCommand.Timer2h -> R.string.timer_two_hours
        FanCommand.Timer4h -> R.string.timer_four_hours
        FanCommand.Timer8h -> R.string.timer_eight_hours
    }

@StringRes
private fun FanCommand.shortLabel(): Int? =
    when (this) {
        FanCommand.Timer2h -> R.string.timer_two_hours_short
        FanCommand.Timer4h -> R.string.timer_four_hours_short
        FanCommand.Timer8h -> R.string.timer_eight_hours_short
        else -> null
    }

@Composable
fun RemoteScreen(state: RemoteState, onSend: (FanCommand) -> Boolean) {
    val colors = MaterialTheme.colorScheme
    // The ViewModel drops overlapping taps; avoid flashing the whole keypad as disabled.
    val enabled = state.available
    val view = LocalView.current
    val ledDescription = stringResource(R.string.led_toggle)
    // Haptics acknowledge accepted taps and respect the device’s touch-feedback setting.
    val tap: (FanCommand) -> Unit = { command ->
        if (onSend(command)) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
    Surface(Modifier.fillMaxSize(), color = colors.background) {
        Box(Modifier.safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Column(
                Modifier.widthIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = { tap(FanCommand.Power) },
                        enabled = enabled,
                        modifier = Modifier.size(84.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.tertiary,
                            ),
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_power),
                            stringResource(R.string.power),
                            modifier = Modifier.size(30.dp),
                        )
                    }
                    Button(
                        onClick = { tap(FanCommand.Led) },
                        enabled = enabled,
                        modifier =
                            Modifier.size(84.dp).semantics { contentDescription = ledDescription },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.onSurface,
                            ),
                    ) {
                        Column(
                            Modifier.clearAndSetSemantics {},
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_light),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                            )
                            Text(
                                stringResource(R.string.led),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    SpeedKeys.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            row.forEach { command ->
                                RemoteKey(command, enabled, modifier = Modifier.size(88.dp)) {
                                    tap(command)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outlineVariant)
                    Text(
                        stringResource(R.string.timers),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onSurfaceVariant,
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outlineVariant)
                }
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimerKeys.forEach { command ->
                        RemoteKey(
                            command,
                            enabled,
                            modifier = Modifier.weight(1f).heightIn(min = 80.dp),
                        ) {
                            tap(command)
                        }
                    }
                }
                val error =
                    when {
                        !state.available -> stringResource(R.string.unavailable)
                        state.failed -> stringResource(R.string.send_failed)
                        else -> null
                    }
                if (error != null) {
                    Text(
                        error,
                        color = colors.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier =
                            Modifier.padding(top = 24.dp).semantics {
                                liveRegion = LiveRegionMode.Polite
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun RemoteKey(
    command: FanCommand,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val label = stringResource(command.label())
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics { contentDescription = label },
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(4.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (command == FanCommand.Boost) colors.primaryContainer
                    else colors.surfaceVariant,
                contentColor =
                    if (command == FanCommand.Boost) colors.onPrimaryContainer else colors.onSurface,
            ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
    ) {
        if (command == FanCommand.Boost) {
            Column(
                Modifier.clearAndSetSemantics {},
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_boost),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    stringResource(R.string.boost),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        } else if (command.speed != null) {
            Text(
                command.speed.toString(),
                modifier = Modifier.clearAndSetSemantics {},
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
            )
        } else {
            val short = command.shortLabel()
            if (short != null) {
                Text(
                    stringResource(short),
                    modifier = Modifier.clearAndSetSemantics {},
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            } else {
                Text(
                    label,
                    modifier = Modifier.clearAndSetSemantics {},
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RemotePreview() {
    FanRemoteTheme { RemoteScreen(RemoteState(available = true), { true }) }
}
