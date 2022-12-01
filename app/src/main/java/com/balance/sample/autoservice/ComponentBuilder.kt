package com.balance.sample.autoservice

import android.content.Context

class ComponentBuilder(
    val context: Context,
    val componentName: String,
    val action: String,
    val params: MutableMap<String, Any> = mutableMapOf()
) {

}