package com.balance.sample.models

import com.google.gson.annotations.SerializedName

class FilterComponentModel {
    @SerializedName("filters")
    var filters: MutableList<FilterComponentData>? = null
}