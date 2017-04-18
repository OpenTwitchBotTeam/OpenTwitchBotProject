package io.github.otbproject.otb.plugin.test

import io.github.otbproject.otb.plugin.base.{Dependency, Plugin, PluginIdentifier, PluginInitializer}

class PSharedClassInfo1 extends TestPluginInfo {
  override type P = PSharedClass
  override val identifier: PluginIdentifier[P] = PluginIdentifier(classOf[P], classOf[P].getSimpleName + "1")

  override def createPlugin(initializer: PluginInitializer): P = new PSharedClass(initializer, this)
  override val requiredDependencies: Set[Dependency[_ <: Plugin]] = Set()
}