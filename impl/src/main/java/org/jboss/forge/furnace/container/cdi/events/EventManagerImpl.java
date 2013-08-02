/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.events;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.event.EventException;
import org.jboss.forge.furnace.event.EventManager;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EventManagerImpl implements EventManager
{
   private Addon addon;
   private BeanManager manager;

   public EventManagerImpl(Addon addon, BeanManager manager)
   {
      this.addon = addon;
      this.manager = manager;
   }

   @Override
   public void fireEvent(Object event, Annotation... qualifiers) throws EventException
   {
      try
      {
         manager.fireEvent(new InboundEvent(event, qualifiers));
      }
      catch (Throwable e)
      {
         throw new EventException("Could not propagate event to addon [" + addon + "]", e);
      }
   }
}
