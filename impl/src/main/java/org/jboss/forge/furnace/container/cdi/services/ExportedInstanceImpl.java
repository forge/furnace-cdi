/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.services;

import java.util.concurrent.Callable;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.container.cdi.impl.WeldServiceRegistry;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.proxy.ClassLoaderInterceptor;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.forge.furnace.spi.ExportedInstance;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.ClassLoaders;

public class ExportedInstanceImpl<R> implements ExportedInstance<R>
{

   private final Addon addon;
   private final BeanManager manager;
   private CreationalContext<R> context;

   private final Bean<R> requestedBean;
   private final Class<R> requestedType;
   private final Class<? extends R> actualType;

   public ExportedInstanceImpl(Addon addon, BeanManager manager, Bean<R> requestedBean, Class<R> requestedType,
            Class<? extends R> actualType)
   {
      Assert.notNull(addon, "Source addon must not be null.");
      Assert.notNull(manager, "Bean manager must not be null.");
      Assert.notNull(requestedBean, "Requested bean must not be null.");
      Assert.notNull(requestedType, "Requested type must not be null.");
      Assert.notNull(actualType, "Actual type must not be null.");
      this.addon = addon;
      this.manager = manager;
      this.requestedBean = requestedBean;
      this.requestedType = requestedType;
      this.actualType = actualType;
   }

   @Override
   @SuppressWarnings("unchecked")
   public R get()
   {
      Callable<Object> task = new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            context = manager.createCreationalContext(requestedBean);
            Object delegate = manager.getReference(requestedBean, actualType, context);
            return Proxies.enhance(addon.getClassLoader(), delegate, new ClassLoaderInterceptor(addon.getClassLoader(),
                     delegate));
         }
      };

      try
      {
         return (R) ClassLoaders.executeIn(addon.getClassLoader(), task);
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to get instance of [" + actualType + "] with proxy for ClassLoader ["
                  + addon.getClassLoader() + "]", e);
      }
   }

   @Override
   public Class<? extends R> getActualType()
   {
      return actualType;
   }

   @Override
   public Addon getSourceAddon()
   {
      return addon;
   }

   @SuppressWarnings("unchecked")
   public Object get(final InjectionPoint injectionPoint)
   {
      // FIXME remove the need for this method (which is currently still not working right for producer methods that
      // require an InjectionPoint
      Callable<Object> task = new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            Bean<R> bean = (Bean<R>) manager.resolve(manager.getBeans(actualType,
                     WeldServiceRegistry.getQualifiersFrom(actualType)));
            context = manager.createCreationalContext(bean);
            Object delegate = manager.getInjectableReference(injectionPoint, context);
            return Proxies.enhance(addon.getClassLoader(), delegate, new ClassLoaderInterceptor(addon.getClassLoader(),
                     delegate));
         }
      };

      try
      {
         return ClassLoaders.executeIn(addon.getClassLoader(), task);
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to get instance of [" + actualType + "] from addon [" + addon + "]", e);
      }
   }

   @Override
   public void release(R instance)
   {
      context.release();
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("ExportedInstanceImpl [");
      if (requestedType != null)
         builder.append("requestedType=").append(requestedType).append(", ");
      if (actualType != null)
         builder.append("actualType=").append(actualType).append(", ");
      if (addon.getClassLoader() != null)
         builder.append("addon.getClassLoader()=").append(addon.getClassLoader());
      builder.append("]");
      return builder.toString();
   }

}
