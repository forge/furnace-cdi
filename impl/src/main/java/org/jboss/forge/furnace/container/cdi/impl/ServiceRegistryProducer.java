/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.impl;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.forge.furnace.spi.ServiceRegistry;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a> 
 */
@Singleton
public class ServiceRegistryProducer
{
   private ServiceRegistry registry;

   @Produces
   @Singleton
   public ServiceRegistry produce()
   {
      return registry;
   }

   public void setServiceRegistry(ServiceRegistry registry)
   {
      this.registry = registry;
   }
}
