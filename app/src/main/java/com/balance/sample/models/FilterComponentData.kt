package com.balance.sample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FilterComponentData(
    @SerializedName("key")
    var key: String? = null,
    @SerializedName("desc")
    var desc: String? = null,
    @SerializedName("slider")
    var slider: SliderDTO? = null,
    @SerializedName("option")
    var option: OptionsDTO? = null,
    @SerializedName("not_display_key")
    var not_display_key: String? = null,
    @SerializedName("special_desc")
    var special_desc: String? = null,
    @SerializedName("donot_display_key")
    var donot_display_key: String? = null,
) : Serializable {

}
