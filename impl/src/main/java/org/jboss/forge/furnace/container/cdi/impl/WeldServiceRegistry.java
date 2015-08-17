/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Qualifier;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.container.cdi.services.ExportedInstanceImpl;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.spi.ExportedInstance;
import org.jboss.forge.furnace.spi.ServiceRegistry;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class WeldServiceRegistry implements ServiceRegistry
{
   private final Class<?>[] serviceTypes;

   private final BeanManager manager;

   private final Addon addon;

   @SuppressWarnings("unused")
   private final LockManager lock;

   private final Set<Class<?>> servicesSet;

   private final Map<String, ExportedInstance<?>> instanceCache = new WeakHashMap<>();
   private final Map<String, Set<ExportedInstance<?>>> instancesCache = new WeakHashMap<>();

   public WeldServiceRegistry(LockManager lock, Addon addon, BeanManager manager,
            Set<Class<?>> services)
   {
      this.lock = lock;
      this.addon = addon;
      this.manager = manager;
      // Copy set to avoid any reference pointers
      Set<Class<?>> copy = new LinkedHashSet<>();
      copy.addAll(services);
      this.serviceTypes = new ArrayList<>(copy).toArray(new Class<?>[copy.size()]);
      this.servicesSet = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(this.serviceTypes)));
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> ExportedInstance<T> getExportedInstance(String clazz)
   {
      try
      {
         return getExportedInstance((Class<T>) Class.forName(clazz, false, addon.getClassLoader()));
      }
      catch (ClassNotFoundException e)
      {
         return null;
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> ExportedInstance<T> getExportedInstance(final Class<T> clazz)
   {
      Assert.notNull(clazz, "Requested Class type may not be null");
      Addons.waitUntilStarted(addon);

      ExportedInstance<T> result = (ExportedInstance<T>) instanceCache.get(clazz.getName());
      if (result == null)
      {
         try
         {
            result = ClassLoaders.executeIn(addon.getClassLoader(), new Callable<ExportedInstance<T>>()
            {
               @Override
               public ExportedInstance<T> call() throws Exception
               {
                  Set<Bean<?>> beans = manager.getBeans(clazz, getQualifiersFrom(clazz));
                  if (!beans.isEmpty())
                  {
                     ExportedInstance<T> result = new ExportedInstanceImpl<>(
                              addon,
                              manager, (Bean<T>) manager.resolve(beans),
                              clazz,
                              clazz);
                     instanceCache.put(clazz.getName(), result);
                     return result;
                  }
                  return null;
               }
            });
         }
         catch (Exception e)
         {
            throw new ContainerException("Could not get service of type [" + clazz + "] from addon [" + addon
                     + "]", e);
         }

      }
      return result;
   }

   @Override
   public boolean hasService(String clazz)
   {
      try
      {
         return hasService(Class.forName(clazz, false, addon.getClassLoader()));
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
      for (Class<?> service : serviceTypes)
      {
         if (clazz.isAssignableFrom(service))
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
         return getExportedInstances((Class<T>) Class.forName(clazz, false, addon.getClassLoader()));
      }
      catch (ClassNotFoundException e)
      {
         return Collections.emptySet();
      }
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <T> Set<ExportedInstance<T>> getExportedInstances(final Class<T> clazz)
   {
      Addons.waitUntilStarted(addon);

      Set<ExportedInstance<T>> result = (Set) instancesCache.get(clazz.getName());

      if (result == null || result.isEmpty())
      {
         result = new HashSet<>();
         for (int i = 0; i < serviceTypes.length; i++)
         {
            final Class<?> type = serviceTypes[i];
            if (clazz.isAssignableFrom(type))
            {
               try
               {
                  result.addAll(ClassLoaders.executeIn(addon.getClassLoader(), new Callable<Set<ExportedInstance<T>>>()
                  {
                     @Override
                     public Set<ExportedInstance<T>> call() throws Exception
                     {
                        Set<ExportedInstance<T>> result = new HashSet<>();
                        Set<Bean<?>> beans = manager.getBeans(type, getQualifiersFrom(type));
                        Class<? extends T> assignableClass = (Class<? extends T>) type;
                        for (Bean<?> bean : beans)
                        {
                           result.add(new ExportedInstanceImpl<>(
                                    addon,
                                    manager,
                                    (Bean<T>) bean,
                                    clazz,
                                    assignableClass));
                        }
                        return result;
                     }
                  }));
               }
               catch (Exception e)
               {
                  throw new ContainerException("Could not get services of type [" + clazz + "] from addon ["
                           + addon
                           + "]", e);
               }

            }
            instancesCache.put(clazz.getName(), (Set) result);
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
      Set<Class<T>> result = new HashSet<>();
      for (Class<?> serviceType : serviceTypes)
      {
         if (type.isAssignableFrom(serviceType))
            result.add((Class<T>) serviceType);
      }
      return result;
   }

   @Override
   public String toString()
   {
      return serviceTypes.toString();
   }

   /**
    * Returns the annotation qualifiers from a type
    */
   public static Annotation[] getQualifiersFrom(final Class<?> c)
   {
      Set<Annotation> annotations = new HashSet<>();
      for (Annotation annotation : c.getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(Qualifier.class))
         {
            annotations.add(annotation);
         }
      }
      return annotations.toArray(new Annotation[annotations.size()]);
   }

   @Override
   public void close()
   {
      servicesSet.clear();
      instanceCache.clear();
      instancesCache.clear();
   }

}
