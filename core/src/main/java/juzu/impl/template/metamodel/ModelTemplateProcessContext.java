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

package juzu.impl.template.metamodel;

import juzu.impl.compiler.ElementHandle;
import juzu.impl.compiler.ProcessingContext;
import juzu.impl.template.ast.ASTNode;
import juzu.impl.template.compiler.Template;
import juzu.impl.template.compiler.ProcessContext;
import juzu.impl.template.compiler.ProcessPhase;
import juzu.impl.utils.Content;
import juzu.impl.utils.Path;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class ModelTemplateProcessContext extends ProcessContext
{

   /** . */
   private TemplateMetaModel templateMetaModel;

   /** . */
   private final ProcessingContext env;

   /** . */
   private final Map<Path, Template> templates;

   ModelTemplateProcessContext(
      TemplateMetaModel templateMetaModel,
      Map<Path, Template> templates,
      ProcessingContext env)
   {
      this.templateMetaModel = templateMetaModel;
      this.env = env;
      this.templates = templates;

   }

   Collection<Template> resolve(final TemplateMetaModel metaModel)
   {
      ElementHandle.Field handle = templateMetaModel.getRefs().iterator().next().getHandle();
      return env.executeWithin(handle, new Callable<Collection<Template>>()
      {
         public Collection<Template> call() throws Exception
         {
            Set<Path> keys = new HashSet<Path>(templates.keySet());
            ProcessPhase phase = new ProcessPhase(ModelTemplateProcessContext.this, templates);
            phase.resolveTemplate(metaModel.getPath());
            Map<Path, Template> copy = new HashMap<Path, Template>(templates);
            copy.keySet().removeAll(keys);
            return copy.values();
         }
      });
   }

   protected Content resolveResource(Path path)
   {
      TemplatesMetaModel tmm = templateMetaModel.getTemplates();
      ElementHandle.Package context = tmm.getApplication().getHandle();
      return env.resolveResource(context, tmm.resolve(path));
   }

   @Override
   protected Template resolveTemplate(Path originPath, Path path)
   {
//      throw new CompilationException(MetaModelError.TEMPLATE_ILLEGAL_PATH, path);

      // Resolve the template fqn and the template name
      String fqn = templateMetaModel.getTemplates().getQN().getValue();

      // Get source
      Content content = resolveResource(path);
      if (content == null)
      {
         throw TemplateMetaModel.TEMPLATE_NOT_RESOLVED.failure(fqn);
      }

      // Parse to AST
      ASTNode.Template templateAST;
      try
      {
         templateAST = ASTNode.Template.parse(content.getCharSequence());
      }
      catch (juzu.impl.template.ast.ParseException e)
      {
         throw TemplateMetaModel.TEMPLATE_SYNTAX_ERROR.failure(path);
      }

      // Add template to application
      return new Template(
         originPath,
         templateAST,
         path,
         content.getLastModified());
   }
}