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
