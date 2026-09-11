plugins {
    id("com.ncorti.ktfmt.gradle") version "0.22.0"
    id("com.android.application") version "8.11.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}

ktfmt { kotlinLangStyle() }
