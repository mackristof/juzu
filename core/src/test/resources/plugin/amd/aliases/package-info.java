@Modules({
    @Module(@Asset(id = "Foo", value = "foo.js")),
    @Module(
      value = @Asset(id = "Bar", value = "bar.js", depends = {"Foo"}),
      aliases = {"foo"}
    )
})
@juzu.Application
@WithAssets("Bar")
package plugin.amd.aliases;

import juzu.plugin.amd.Module;
import juzu.plugin.amd.Modules;
import juzu.plugin.asset.Asset;
import juzu.plugin.asset.WithAssets;