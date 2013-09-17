/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.events;

import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Singleton;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.cdi.impl.AddonProducer;
import org.jboss.forge.furnace.container.cdi.util.BeanManagerUtils;
import org.jboss.forge.furnace.event.EventManager;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.util.AddonFilters;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class CrossContainerObserverMethod
{
   private ThreadLocal<Deque<InboundEvent>> stack;

   public void handleEvent(@Observes @Any Object event, EventMetadata metadata, BeanManager manager)
   {
      try
      {
         initStack();

         Addon self = BeanManagerUtils.getContextualInstance(manager, AddonProducer.class).produceCurrentAddon();
         if (self != null && !(event instanceof InboundEvent))
         {
            Set<Annotation> qualifiers = metadata.getQualifiers();
            if (!isLocal(qualifiers) && !onStack(event, qualifiers))
               try
               {
                  AddonRegistry addonRegistry = BeanManagerUtils.getContextualInstance(manager, AddonRegistry.class);
                  for (Addon addon : addonRegistry.getAddons(AddonFilters.allStarted()))
                  {
                     if (!self.getId().equals(addon.getId()))
                     {
                        EventManager remoteEventManager = addon.getEventManager();
                        if (remoteEventManager != null)
                        {
                           remoteEventManager.fireEvent(event, qualifiers.toArray(new Annotation[qualifiers.size()]));
                        }
                     }
                  }
               }
               catch (Exception e)
               {
                  throw new ContainerException("Problems encountered during propagation of event [" + event
                           + "] with qualifiers [" + qualifiers + "]", e);
               }
         }
         else if (event instanceof InboundEvent)
         {
            try
            {
               push((InboundEvent) event);
               manager.fireEvent(((InboundEvent) event).getEvent(), ((InboundEvent) event).getQualifiers());
            }
            finally
            {
               pop((InboundEvent) event);
            }
         }
         else
         {
            System.out.println("Another scenario");
         }
      }
      finally
      {
         cleanupStack();
      }
   }

   private boolean isLocal(Set<Annotation> qualifiers)
   {
      for (Annotation annotation : qualifiers)
      {
         if (annotation instanceof Local)
            return true;
      }
      return false;
   }

   private boolean onStack(Object event, Set<Annotation> qualifiers)
   {
      InboundEvent peek = peek();
      if (peek != null && peek.equals(new InboundEvent(event, qualifiers.toArray(new Annotation[qualifiers.size()]))))
         return true;
      return false;
   }

   private void cleanupStack()
   {
      if (stack != null && stack.get() != null && stack.get().isEmpty())
         stack.remove();
   }

   private void initStack()
   {
      if (stack == null)
         stack = new ThreadLocal<Deque<InboundEvent>>();
      if (stack.get() == null)
         stack.set(new ArrayDeque<InboundEvent>());
   }

   private InboundEvent peek()
   {
      return this.stack.get().peek();
   }

   private InboundEvent pop(InboundEvent event)
   {
      return this.stack.get().pop();
   }

   private void push(InboundEvent event)
   {
      this.stack.get().push(event);
   }
}
