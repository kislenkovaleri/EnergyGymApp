plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.google.services) apply false
    id("androidx.room") version "2.6.1" apply false
}