package com.balance.sample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RangeDTO(
    @SerializedName("value")
    var value: Int = 0,
    @SerializedName("desc")
    var desc: String? = null
) : Serializable {

}
