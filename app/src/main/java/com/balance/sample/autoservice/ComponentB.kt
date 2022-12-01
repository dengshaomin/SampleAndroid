package com.balance.sample.autoservice

import android.content.Context
import com.google.auto.service.AutoService

@AutoService(IComponent::class)
class ComponentB : IComponent {

    override fun getName(): String {
        return this.javaClass.simpleName
    }

    override fun action(
        context: Context,
        actionName: String,
        params: MutableMap<String, Any>
    ): Any? {
        return null
    }
}