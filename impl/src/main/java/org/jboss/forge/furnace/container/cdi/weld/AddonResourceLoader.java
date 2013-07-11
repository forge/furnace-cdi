package org.jboss.forge.furnace.container.cdi.weld;

import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoadingException;

/**
 * A {@link ResourceLoader} that can load classes from an {@link Addon}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonResourceLoader implements ResourceLoader
{
   private final Map<String, Class<?>> classes;

   private Addon addon;

   public AddonResourceLoader(Addon addon)
   {
      this.classes = new ConcurrentHashMap<String, Class<?>>();
      this.addon = addon;
   }

   @Override
   public String toString()
   {
      return addon.getId().toCoordinates();
   }

   @Override
   public Class<?> classForName(String name)
   {
      try
      {
         if (classes.containsKey(name))
         {
            return classes.get(name);
         }
         final Class<?> clazz = addon.getClassLoader().loadClass(name);
         classes.put(name, clazz);
         return clazz;
      }
      catch (NoClassDefFoundError e)
      {
         throw new ResourceLoadingException(e);
      }
      catch (ClassNotFoundException e)
      {
         throw new ResourceLoadingException(e);
      }
      catch (LinkageError e)
      {
         throw new ResourceLoadingException(e);
      }
   }

   public void addAdditionalClass(Class<?> clazz)
   {
      this.classes.put(clazz.getName(), clazz);
   }

   @Override
   public URL getResource(String name)
   {
      try
      {
         return addon.getClassLoader().getResource(name);
      }
      catch (Exception e)
      {
         throw new ResourceLoadingException(e);
      }
   }

   @Override
   public Collection<URL> getResources(String name)
   {
      try
      {
         final HashSet<URL> resources = new HashSet<URL>();
         Enumeration<URL> urls = addon.getClassLoader().getResources(name);
         while (urls.hasMoreElements())
         {
            resources.add(urls.nextElement());
         }
         return resources;
      }
      catch (Exception e)
      {
         throw new ResourceLoadingException(e);
      }

   }

   @Override
   public void cleanup()
   {
   }

}