package com.balance.sample.autoservice

import android.content.Context
import com.google.auto.service.AutoService

@AutoService(IComponent::class)
class ComponentA : IComponent {
    override fun getName(): String {
        return ComponentConstants.ComponentA.COMPONENT_A
    }

    override fun action(context: Context,actionName: String, params: MutableMap<String, Any>): Any? {
        when(actionName){
            ComponentConstants.ComponentA.METHOD_0-> return "result"
        }
        return null
    }
}