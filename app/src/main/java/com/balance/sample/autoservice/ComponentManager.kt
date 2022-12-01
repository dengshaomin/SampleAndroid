package com.balance.sample.autoservice

import java.util.*

object ComponentManager {
    private var components = mutableMapOf<String, IComponent>()

    init {
        loadComponents()
    }

    private fun loadComponents() {
        val list = ServiceLoader.load(IComponent::class.java, javaClass.classLoader)
        list.toList().map {
            components.put(it.getName(), it)
        }
    }

    fun action(componentBuilder: ComponentBuilder): Any? {
        components[componentBuilder.componentName]?.apply {
            return this.action(
                componentBuilder.context,
                componentBuilder.action,
                componentBuilder.params
            )
        }
        return null
    }
}