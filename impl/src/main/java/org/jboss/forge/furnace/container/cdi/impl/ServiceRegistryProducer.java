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
