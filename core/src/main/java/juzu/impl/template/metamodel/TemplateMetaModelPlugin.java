package juzu.impl.template.metamodel;

import juzu.impl.application.metamodel.ApplicationMetaModel;
import juzu.impl.application.metamodel.ApplicationMetaModelPlugin;
import juzu.impl.application.metamodel.ApplicationsMetaModel;
import juzu.impl.compiler.AnnotationData;
import juzu.impl.compiler.CompilationException;
import juzu.impl.compiler.ElementHandle;
import juzu.impl.metamodel.MetaModel;
import juzu.impl.metamodel.MetaModelProcessor;
import juzu.impl.spi.template.TemplateProvider;
import juzu.impl.template.compiler.Template;
import juzu.impl.utils.JSON;
import juzu.impl.utils.Path;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class TemplateMetaModelPlugin extends ApplicationMetaModelPlugin
{

   /** . */
   public static final Pattern PATH_PATTERN = Pattern.compile("([^/].*/|)([^./]+)\\.([a-zA-Z]+)");

   /** . */
   private static final Pattern PROVIDER_PKG_PATTERN = Pattern.compile(
      "juzu\\.impl\\.spi\\.template\\.([^.]+)(?:\\..+)?"
   );

   /** . */
   Map<String, TemplateProvider> providers;

   @Override
   public void init(ApplicationsMetaModel applications)
   {
   }

   @Override
   public void postActivateApplicationsMetaModel(ApplicationsMetaModel applications)
   {
      // Discover the template providers
      ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class, TemplateProvider.class.getClassLoader());
      Map<String, TemplateProvider> providers = new HashMap<String, TemplateProvider>();
      for (TemplateProvider provider : loader)
      {
         // Get extension
         String pkgName = provider.getClass().getPackage().getName();

         //
         Matcher matcher = PROVIDER_PKG_PATTERN.matcher(pkgName);
         if (matcher.matches())
         {
            String extension = matcher.group(1);
            providers.put(extension, provider);
         }
      }
      
      //
      this.providers = providers;
   }

   @Override
   public void postConstruct(ApplicationMetaModel application)
   {
      application.addChild(TemplatesMetaModel.KEY, new TemplatesMetaModel());
   }

   @Override
   public void processAnnotation(ApplicationMetaModel application, Element element, String fqn, AnnotationData data) throws CompilationException
   {
      if (fqn.equals("juzu.Path"))
      {
         if (element instanceof VariableElement)
         {
            VariableElement variableElt = (VariableElement)element;
            MetaModel.log.log("Processing template declaration " + variableElt.getEnclosingElement() + "#"  + variableElt);

            //
            TemplatesMetaModel at = application.getChild(TemplatesMetaModel.KEY);

            //
            Path path = Path.parse((String)data.get("value"));
            ElementHandle.Field handle = ElementHandle.Field.create(variableElt);
            at.add(handle, path);
         }
         else if (element instanceof TypeElement)
         {
            // We ignore it on purpose
         }
         else
         {
            throw MetaModelProcessor.ANNOTATION_UNSUPPORTED.failure(element);
         }
      }
   }

   @Override
   public void prePassivate(ApplicationMetaModel model)
   {
      MetaModel.log.log("Passivating template resolver for " + model.getHandle());
      model.getTemplates().resolver.prePassivate();
   }

   @Override
   public void prePassivate(ApplicationsMetaModel applications)
   {
      MetaModel.log.log("Passivating templates");
      this.providers = null;
   }

   @Override
   public void postProcessEvents(ApplicationMetaModel application)
   {
      MetaModel.log.log("Processing templates of " + application.getHandle());
      application.getTemplates().resolver.process(this, application.model.env);
   }

   @Override
   public JSON getDescriptor(ApplicationMetaModel application)
   {
      JSON config = new JSON();
      ArrayList<String> templates = new ArrayList<String>();
      TemplatesMetaModel a = application.getTemplates();
      for (Template template : a.resolver.getTemplates())
      {
         Path resolved = a.resolve(template.getPath());
         templates.add(resolved.getFQN().getName());
      }
      config.map("templates", templates);
      config.set("package", a.getQN().toString());
      return config;
   }
}