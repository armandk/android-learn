package com.example.dsi_android_ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Product( val productName: String?=null, val productGtin :String?=null,val productSku: String?=null) : Parcelable