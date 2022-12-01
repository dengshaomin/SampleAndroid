package com.balance.sample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OptionsDTO(
    @SerializedName("option_type")
    var option_type: String? = null,
    @SerializedName("items")
    var items: MutableList<OptionsItemsDTO>? = null,
    @SerializedName("support_expand")
    var support_expand: Boolean? = null,
) : Serializable {

}
