package com.artemchep.horlogo.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author Artem Chepurnoy
 */
@Parcelize
data class ConfigPickerItem(
    val key: String,
    val color: Int,
    val title: String
) : Parcelable
