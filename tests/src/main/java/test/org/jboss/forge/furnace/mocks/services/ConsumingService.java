package test.org.jboss.forge.furnace.mocks.services;

import javax.inject.Inject;

public class ConsumingService
{
   @Inject
   private PublishedService service;

   public String getMessage()
   {
      return "I am ConsumingService. Remote service says [" + service.getMessage() + "]";
   }

   public ClassLoader getClassLoader()
   {
      return getClass().getClassLoader();
   }

   public int getRemoteHashCode()
   {
      return service.hashCode();
   }
}
