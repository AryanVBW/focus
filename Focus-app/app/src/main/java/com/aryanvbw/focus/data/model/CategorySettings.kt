package com.aryanvbw.focus.data.model

data class CategorySettings(
    val masterEnabled: Boolean = true,
    val socialMediaEnabled: Boolean = false,
    val entertainmentEnabled: Boolean = false,
    val newsMediaEnabled: Boolean = false,
    val adultContentEnabled: Boolean = false
)
