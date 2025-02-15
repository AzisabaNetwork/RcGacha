package net.azisaba.rcgacha.util

import net.azisaba.rcgacha.extension.addPrefix
import net.azisaba.rcgacha.extension.fail
import net.azisaba.rcgacha.extension.success
import net.azisaba.rcgacha.extension.toComponent
import net.azisaba.rcgacha.extension.warn
import net.kyori.adventure.text.Component

val prefixComponent = Component.text("[RcGacha]")

fun successComponent(message: String) = message.toComponent().success()

fun warnComponent(message: String) = message.toComponent().warn()

fun failComponent(message: String) = message.toComponent().fail()

fun prefixed(message: String) = message.toComponent().addPrefix()

fun prefixedSuccess(message: String) = successComponent(message).addPrefix()

fun prefixedWarn(message: String) = warnComponent(message).addPrefix()

fun prefixedFail(message: String) = failComponent(message).addPrefix()
