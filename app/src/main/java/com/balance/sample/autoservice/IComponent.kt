package com.balance.sample.autoservice

import android.content.Context

interface IComponent {
    fun getName(): String
    fun action(context: Context, actionName: String, params: MutableMap<String, Any>): Any?
}