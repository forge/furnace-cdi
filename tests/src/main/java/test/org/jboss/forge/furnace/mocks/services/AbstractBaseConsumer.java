package test.org.jboss.forge.furnace.mocks.services;

import javax.inject.Inject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractBaseConsumer
{
   @Inject
   private ExportedService service;

   public ExportedService getService()
   {
      return service;
   }
}
