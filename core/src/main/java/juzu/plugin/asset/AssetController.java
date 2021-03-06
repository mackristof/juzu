/*
 * Copyright 2013 eXo Platform SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package juzu.plugin.asset;

import juzu.asset.AssetLocation;
import juzu.impl.asset.AssetManager;
import juzu.impl.asset.AssetServer;

import javax.inject.Inject;

/**
 * The asset controller.
 *
 * @author Julien Viet
 */
public class AssetController {

  /** . */
  private final AssetManager manager;

  @Inject
  public AssetController(AssetManager manager) {
    this.manager = manager;
  }

  /**
   * <p>Returns the URL of an asset located in the assets package of the application.</p>
   *
   * <p>If the controller is not under the scope of a request, null is returned.</p>
   *
   * @param path the path
   * @return the asset url
   * @throws NullPointerException when the path argument is null
   */
  public String url(String path) throws NullPointerException {
    return AssetServer.renderAssetURL(AssetLocation.APPLICATION, path);
  }

  /**
   * <p>Returns the URL of an asset.</p>
   *
   * <p>If the controller is not under the scope of a request, null is returned.</p>
   *
   * @param location the asset location
   * @param uri the asset uri
   * @return the asset url
   * @throws NullPointerException when the path argument is null
   */
  public String url(AssetLocation location, String uri) throws NullPointerException {
    return AssetServer.renderAssetURL(location, uri);
  }
}
