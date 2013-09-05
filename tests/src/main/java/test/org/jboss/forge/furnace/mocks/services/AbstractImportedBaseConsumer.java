package test.org.jboss.forge.furnace.mocks.services;

import javax.inject.Inject;

import org.jboss.forge.furnace.services.Imported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractImportedBaseConsumer
{
   @Inject
   private Imported<ExportedService> service;

   public ExportedService getService()
   {
      return service.get();
   }
}
