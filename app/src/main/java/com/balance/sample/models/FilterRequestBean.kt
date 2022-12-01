package com.balance.sample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FilterRequestBean(
    @SerializedName("key")
    var key: String? = null,
    @SerializedName("value")
    var value: String? = null,
) : Serializable {

}
