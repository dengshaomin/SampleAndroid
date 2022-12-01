package com.balance.sample.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OptionsItemsDTO(
    @SerializedName("value")
    var value: String? = null,
    @SerializedName("key")
    var key: String? = null,
    @SerializedName("desc")
    var desc: String? = null,
    @SerializedName("default_selected")
    var default_selected: Boolean = false,
    @SerializedName("action_type")
    var action_type: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("deselected_toast_msg")
    var deselected_toast_msg: String? = null,
    @SerializedName("user_input_title")
    var user_input_title: String? = null,
    @SerializedName("user_input_placeholder")
    var user_input_placeholder:String?=null,
    @SerializedName("user_input_confirm_title")
    var user_input_confirm_title:String?=null,
    @SerializedName("unsupport_alert_msg")
    var unsupport_alert_msg:String?=null,
    @SerializedName("user_input_unit")
    var user_input_unit:String?=null,
) : Serializable {
    var user_selected: Boolean = false
}
