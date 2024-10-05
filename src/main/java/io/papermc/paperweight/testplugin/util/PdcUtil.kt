package io.papermc.paperweight.testplugin.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

var PersistentDataContainer.hungry : Int
  get() {
    return this.getOrDefault(NamespacedKey.fromString("hungry")!!, PersistentDataType.INTEGER,0)
  }
  set(value) {
    this.set(NamespacedKey.fromString("hungry")!!, PersistentDataType.INTEGER,value)
  }
