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

package juzu.plugin.portlet.impl;

import juzu.Scope;
import juzu.impl.plugin.PluginDescriptor;
import juzu.impl.inject.BeanDescriptor;
import juzu.impl.common.Tools;

import javax.portlet.PortletPreferences;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class PortletDescriptor extends PluginDescriptor {

  /** . */
  public static PortletDescriptor INSTANCE = new PortletDescriptor();

  private PortletDescriptor() {
  }

  @Override
  public Iterable<BeanDescriptor> getBeans() {
    return Tools.list(
        BeanDescriptor.createFromProviderType(PortletPreferences.class, Scope.REQUEST, null, PortletPreferencesProvider.class)
    );
  }
}
