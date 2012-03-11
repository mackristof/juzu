/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.juzu.impl.application.metadata;

import org.juzu.impl.controller.descriptor.ControllerDescriptor;
import org.juzu.impl.metadata.BeanDescriptor;
import org.juzu.impl.metadata.Descriptor;
import org.juzu.impl.plugin.Plugin;
import org.juzu.impl.request.LifeCyclePlugin;
import org.juzu.impl.template.metadata.TemplatesDescriptor;
import org.juzu.impl.utils.JSON;
import org.juzu.impl.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ApplicationDescriptor extends Descriptor
{

   /** . */
   private final Class<?> applicationClass;
   
   /** . */
   private final String packageName;

   /** . */
   private final String name;

   /** . */
   private final List<Class<? extends LifeCyclePlugin>> plugins;

   /** . */
   private final Class<?> packageClass;

   /** . */
   private final ControllerDescriptor controller;

   /** . */
   private final TemplatesDescriptor templates;

   /** . */
   private final Map<String, Descriptor> pluginDescriptors;

   public ApplicationDescriptor(Class<?> applicationClass, List<Class<? extends LifeCyclePlugin>> plugins)
   {
      // Load config
      JSON config;
      InputStream in = null;
      try
      {
         in = applicationClass.getResourceAsStream("config.json");
         String s = Tools.read(in);
         config = (JSON)JSON.parse(s);
      }
      catch (IOException e)
      {
         throw new AssertionError(e);
      }
      finally
      {
         Tools.safeClose(in);
      }

      //
      Class<?> packageClass;
      try
      {
         packageClass = applicationClass.getClassLoader().loadClass(applicationClass.getPackage().getName() + ".package-info");
      }
      catch (ClassNotFoundException e)
      {
         AssertionError ae = new AssertionError("Cannot load package class");
         ae.initCause(e);
         throw ae;
      }
      
      //
      HashMap<String, Descriptor> pluginDescriptors = new HashMap<String, Descriptor>(); 
      HashMap<String, Plugin> pluginMap = new HashMap<String, Plugin>(); 
      for (Plugin plugin : ServiceLoader.load(Plugin.class))
      {
         pluginMap.put(plugin.getName(), plugin);
      }
      
      //
      for (String name : config.names())
      {
         Plugin plugin = pluginMap.get(name);
         if (plugin == null)
         {
            throw new UnsupportedOperationException("Handle me gracefully : missing plugin " + name);
         }
         JSON pluginConfig = config.getJSON(name);
         try
         {
            Descriptor pluginDescriptor = plugin.loadDescriptor(applicationClass.getClassLoader(), pluginConfig);
            pluginDescriptors.put(name, pluginDescriptor);
         }
         catch (Exception e)
         {
            AssertionError ae = new AssertionError("Cannot load config");
            ae.initCause(e);
            throw ae;
         }
      }

      //
      this.applicationClass = applicationClass;
      this.name = applicationClass.getSimpleName();
      this.packageName = applicationClass.getPackage().getName();
      this.templates = (TemplatesDescriptor)pluginDescriptors.get("template");
      this.plugins = plugins;
      this.packageClass = packageClass;
      this.controller = (ControllerDescriptor)pluginDescriptors.get("controller");
      this.pluginDescriptors = pluginDescriptors;
   }

   @Override
   public Iterable<BeanDescriptor> getBeans()
   {
      ArrayList<BeanDescriptor> beans = new ArrayList<BeanDescriptor>();
      for (Descriptor descriptor : pluginDescriptors.values())
      {
         Tools.addAll(beans, descriptor.getBeans());
      }
      return beans;
   }

   public Class<?> getPackageClass()
   {
      return packageClass;
   }

   public Class<?> getApplicationClass()
   {
      return applicationClass;
   }
   
   public ClassLoader getApplicationLoader()
   {
      return applicationClass.getClassLoader();
   }

   public String getPackageName()
   {
      return packageName;
   }

   public String getName()
   {
      return name;
   }

   public ControllerDescriptor getController()
   {
      return controller;
   }

   public TemplatesDescriptor getTemplates()
   {
      return templates;
   }

   public List<Class<? extends LifeCyclePlugin>> getPlugins()
   {
      return plugins;
   }
}