package com.artemchep.horlogo.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Artem Chepurnoy
 */
data class ConfigPickerItem(
        val key: String,
        val color: Int,
        val title: String
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<ConfigPickerItem> {
        override fun createFromParcel(parcel: Parcel) = ConfigPickerItem(parcel)
        override fun newArray(size: Int): Array<ConfigPickerItem?> = arrayOfNulls(size)
    }

    constructor(parcel: Parcel) : this(
            key = parcel.readString(),
            color = parcel.readInt(),
            title = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeString(key)
            writeInt(color)
            writeString(title)
        }
    }

    override fun describeContents(): Int = 0

}
