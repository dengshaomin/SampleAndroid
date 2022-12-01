package com.balance.sample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SliderDTO(
    @SerializedName("default_value")
    var default_value: Int = 0,
    @SerializedName("is_opposite")
    var is_opposite: Boolean = false,
    @SerializedName("min")
    var min: RangeDTO? = null,
    @SerializedName("max")
    var max: RangeDTO? = null
) : Serializable {

}
