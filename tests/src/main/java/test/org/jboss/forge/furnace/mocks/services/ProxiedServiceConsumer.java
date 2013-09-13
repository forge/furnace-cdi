package test.org.jboss.forge.furnace.mocks.services;

import javax.inject.Inject;

import org.jboss.forge.furnace.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public class ProxiedServiceConsumer
{
   @Inject
   private ProxiedService service;

   public ProxiedService getService()
   {
      return service;
   }
}
