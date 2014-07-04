/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.events;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a> 
 */
@Singleton
public class EventManagerProducer
{
   private EventManagerImpl manager;

   @Produces
   @Singleton
   public EventManagerImpl produce()
   {
      return manager;
   }

   public void setEventManager(EventManagerImpl manager)
   {
      this.manager = manager;
   }
}
