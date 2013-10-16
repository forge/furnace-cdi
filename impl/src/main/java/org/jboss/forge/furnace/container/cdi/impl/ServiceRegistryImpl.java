package org.jboss.forge.furnace.container.cdi.impl;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Qualifier;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.container.cdi.services.ExportedInstanceImpl;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.spi.ExportedInstance;
import org.jboss.forge.furnace.spi.ServiceRegistry;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.Assert;

public class ServiceRegistryImpl implements ServiceRegistry
{
   private final Class<?>[] services;

   private final BeanManager manager;

   private final Addon addon;

   private static final Logger log = Logger.getLogger(ServiceRegistryImpl.class.getName());

   private final LockManager lock;

   private Set<Class<?>> servicesSet;

   private ClassLoader addonClassLoader;

   private Map<Integer, Class<?>> classCache = new WeakHashMap<Integer, Class<?>>();
   private Map<Integer, ExportedInstance<?>> instanceCache = new WeakHashMap<Integer, ExportedInstance<?>>();
   private Map<Integer, Set<ExportedInstance<?>>> instancesCache = new WeakHashMap<Integer, Set<ExportedInstance<?>>>();

   public ServiceRegistryImpl(LockManager lock, Addon addon, BeanManager manager,
            Set<Class<?>> services)
   {
      this.lock = lock;
      this.addon = addon;
      this.manager = manager;
      // Copy set to avoid any reference pointers
      Set<Class<?>> copy = new LinkedHashSet<Class<?>>();
      copy.addAll(services);
      this.services = new ArrayList<Class<?>>(copy).toArray(new Class<?>[copy.size()]);
      this.servicesSet = Collections.unmodifiableSet(new LinkedHashSet<Class<?>>(Arrays.asList(this.services)));

      // Extracted for performance optimization
      this.addonClassLoader = addon.getClassLoader();
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> ExportedInstance<T> getExportedInstance(String clazz)
   {
      Class<T> type;
      try
      {
         type = (Class<T>) loadAddonClass(clazz);
         return getExportedInstance(type);
      }
      catch (ClassNotFoundException e)
      {
         return null;
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> ExportedInstance<T> getExportedInstance(final Class<T> requestedType)
   {
      Assert.notNull(requestedType, "Requested Class type may not be null");
      Addons.waitUntilStarted(addon);

      ExportedInstance<T> result = (ExportedInstance<T>) instanceCache.get(requestedType.hashCode());
      if (result == null)
      {
         final Class<T> actualLoadedType;
         try
         {
            actualLoadedType = loadAddonClass(requestedType);
         }
         catch (ClassNotFoundException cnfe)
         {
            log.fine("Class " + requestedType.getName() + " is not present in this addon [" + addon + "]");
            return null;
         }

         Set<Bean<?>> beans = manager.getBeans(actualLoadedType, getQualifiersFrom(actualLoadedType));
         if (!beans.isEmpty())
         {
            result = new ExportedInstanceImpl<T>(
                     addon,
                     manager, (Bean<T>)
                     manager.resolve(beans),
                     actualLoadedType,
                     actualLoadedType
                     );
            instanceCache.put(requestedType.hashCode(), result);
         }
      }
      return result;
   }

   @Override
   public boolean hasService(String clazz)
   {
      try
      {
         Class<?> type = loadAddonClass(clazz);
         return hasService(type);
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
   }

   @Override
   public boolean hasService(Class<?> clazz)
   {
      Addons.waitUntilStarted(addon);
      Class<?> type;
      try
      {
         type = loadAddonClass(clazz);
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
      for (Class<?> service : services)
      {
         if (type.isAssignableFrom(service))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> Set<ExportedInstance<T>> getExportedInstances(String clazz)
   {
      try
      {
         Class<T> type = (Class<T>) loadAddonClass(clazz);
         return getExportedInstances(type);
      }
      catch (ClassNotFoundException e)
      {
         return Collections.emptySet();
      }
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> requestedType)
   {
      Addons.waitUntilStarted(addon);

      Class<T> requestedLoadedType;
      try
      {
         requestedLoadedType = loadAddonClass(requestedType);
      }
      catch (ClassNotFoundException e)
      {
         log.fine("Class " + requestedType.getName() + " is not present in this addon [" + addon + "]");
         return Collections.emptySet();
      }

      Set<ExportedInstance<T>> result = (Set) instancesCache.get(requestedLoadedType.hashCode());

      if (result == null)
      {
         result = new HashSet<ExportedInstance<T>>();
         for (int i = 0; i < services.length; i++)
         {
            Class<?> type = services[i];
            if (requestedLoadedType.isAssignableFrom(type))
            {
               Set<Bean<?>> beans = manager.getBeans(type, getQualifiersFrom(type));
               Class<? extends T> assignableClass = (Class<? extends T>) type;
               for (Bean<?> bean : beans)
               {
                  result.add(new ExportedInstanceImpl<T>(
                           addon,
                           manager,
                           (Bean<T>) bean,
                           requestedLoadedType,
                           assignableClass
                           ));

               }
            }
            instancesCache.put(requestedLoadedType.hashCode(), (Set) result);
         }
      }
      return result;
   }

   @Override
   public Set<Class<?>> getExportedTypes()
   {
      return servicesSet;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> Set<Class<T>> getExportedTypes(Class<T> type)
   {
      Set<Class<T>> result = new HashSet<Class<T>>();
      for (Class<?> serviceType : services)
      {
         if (type.isAssignableFrom(serviceType))
            result.add((Class<T>) serviceType);
      }
      return result;
   }

   /**
    * Ensures that the returned class is loaded from this {@link Addon}
    */
   @SuppressWarnings("unchecked")
   private <T> Class<T> loadAddonClass(Class<T> actualType) throws ClassNotFoundException
   {
      final Class<T> type;
      if (actualType.getClassLoader() == addonClassLoader)
      {
         type = actualType;
      }
      else
      {
         type = (Class<T>) loadAddonClass(actualType.getName());
      }
      return type;
   }

   private Class<?> loadAddonClass(String className) throws ClassNotFoundException
   {
      Class<?> cached = classCache.get(className.hashCode());
      if (cached == null)
      {
         Class<?> result = Class.forName(className, false, addonClassLoader);
         // potentially not thread-safe
         classCache.put(className.hashCode(), result);
         cached = result;
      }

      return cached;
   }

   @Override
   public String toString()
   {
      return services.toString();
   }

   /**
    * Returns the annotation qualifiers from a type
    */
   public static Annotation[] getQualifiersFrom(final Class<?> c)
   {
      Set<Annotation> annotations = new HashSet<Annotation>();
      for (Annotation annotation : c.getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(Qualifier.class))
         {
            annotations.add(annotation);
         }
      }
      return annotations.toArray(new Annotation[annotations.size()]);
   }

}
