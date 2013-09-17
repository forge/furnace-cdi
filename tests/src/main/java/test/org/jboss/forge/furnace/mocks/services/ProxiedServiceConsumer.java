package test.org.jboss.forge.furnace.mocks.services;

import javax.inject.Inject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ProxiedServiceConsumer
{
   @Inject
   private ProxiedService service;

   public ProxiedService getService()
   {
      return service;
   }
}
