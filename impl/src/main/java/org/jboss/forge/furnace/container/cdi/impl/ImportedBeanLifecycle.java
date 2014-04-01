/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonFilter;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.cdi.services.ExportedInstanceImpl;
import org.jboss.forge.furnace.container.cdi.services.LocalServiceInjectionPoint;
import org.jboss.forge.furnace.container.cdi.util.BeanManagerUtils;
import org.jboss.forge.furnace.container.cdi.util.ContextualLifecycle;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.spi.ExportedInstance;
import org.jboss.forge.furnace.spi.ServiceRegistry;
import org.jboss.forge.furnace.util.AddonFilters;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class ImportedBeanLifecycle implements ContextualLifecycle<Object>
{
   private static final AddonFilter ALL_STARTED = AddonFilters.allStarted();
   private final Annotated annotated;
   private final Member member;
   private final InjectionPoint injectionPoint;
   private final BeanManager manager;

   ImportedBeanLifecycle(Annotated annotated, Member member, InjectionPoint injectionPoint,
            BeanManager manager)
   {
      this.annotated = annotated;
      this.member = member;
      this.injectionPoint = injectionPoint;
      this.manager = manager;
   }

   @Override
   public void destroy(Bean<Object> bean, Object instance, CreationalContext<Object> creationalContext)
   {
      creationalContext.release();
   }

   @Override
   public Object create(Bean<Object> bean, CreationalContext<Object> creationalContext)
   {
      Class<?> serviceType = null;
      if (member instanceof Method)
      {
         if (annotated instanceof AnnotatedParameter)
         {
            serviceType = ((Method) member).getParameterTypes()[((AnnotatedParameter<?>) annotated)
                     .getPosition()];
         }
         else
            serviceType = ((Method) member).getReturnType();
      }
      else if (member instanceof Field)
      {
         serviceType = ((Field) member).getType();
      }
      else if (member instanceof Constructor)
      {
         if (annotated instanceof AnnotatedParameter)
         {
            serviceType = ((Constructor<?>) member).getParameterTypes()[((AnnotatedParameter<?>) annotated)
                     .getPosition()];
         }
      }
      else
      {
         throw new ContainerException(
                  "Cannot handle producer for non-Field and non-Method member type: " + member);
      }

      AddonRegistry registry = BeanManagerUtils.getContextualInstance(manager, AddonRegistry.class);

      Object result = null;
      for (Addon addon : registry.getAddons(ALL_STARTED))
      {
         ServiceRegistry serviceRegistry = addon.getServiceRegistry();
         if (serviceRegistry.hasService(serviceType))
         {
            ExportedInstance<?> instance = serviceRegistry.getExportedInstance(serviceType);
            if (instance != null)
            {
               if (instance instanceof ExportedInstanceImpl)
               {
                  // FIXME remove the need for this implementation coupling
                  result = ((ExportedInstanceImpl<?>) instance)
                           .get(new LocalServiceInjectionPoint(injectionPoint, serviceType));
               }
               else
               {
                  result = instance.get();
               }

               if (result != null)
                  break;
            }
         }
      }

      if (result == null)
      {
         throw new IllegalStateException("Addon service [" + serviceType.getName()
                  + "] is not registered.");
      }

      return result;
   }
}