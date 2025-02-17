package net.azisaba.rcgacha.extension

import java.io.File

fun File.getChildFile(name: String): File = File(this, name)
