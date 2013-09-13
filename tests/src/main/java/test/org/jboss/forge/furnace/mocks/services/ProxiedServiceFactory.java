package test.org.jboss.forge.furnace.mocks.services;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ProxiedServiceFactory
{
   @Produces
   @ApplicationScoped
   public ProxiedService getProxiedService()
   {
      return new ProxiedServiceImpl();
   }
}
