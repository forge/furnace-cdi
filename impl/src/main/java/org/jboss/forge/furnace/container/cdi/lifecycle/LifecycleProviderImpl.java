package org.jboss.forge.furnace.container.cdi.lifecycle;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.cdi.impl.AddonProducer;
import org.jboss.forge.furnace.container.cdi.impl.AddonRegistryProducer;
import org.jboss.forge.furnace.container.cdi.impl.AddonRepositoryProducer;
import org.jboss.forge.furnace.container.cdi.impl.ContainerServiceExtension;
import org.jboss.forge.furnace.container.cdi.impl.FurnaceProducer;
import org.jboss.forge.furnace.container.cdi.impl.ServiceRegistryImpl;
import org.jboss.forge.furnace.container.cdi.impl.ServiceRegistryProducer;
import org.jboss.forge.furnace.container.cdi.util.BeanManagerUtils;
import org.jboss.forge.furnace.container.cdi.weld.AddonResourceLoader;
import org.jboss.forge.furnace.container.cdi.weld.ModularURLScanner;
import org.jboss.forge.furnace.container.cdi.weld.ModularWeld;
import org.jboss.forge.furnace.container.cdi.weld.ModuleScanResult;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.lifecycle.AddonLifecycleProvider;
import org.jboss.forge.furnace.services.ServiceRegistry;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;

public class LifecycleProviderImpl implements AddonLifecycleProvider
{
   private static final Logger logger = Logger.getLogger(LifecycleProviderImpl.class.getName());

   private Furnace furnace;
   private AddonRegistry addonRegistry;

   private ServiceRegistry serviceRegistry;
   private BeanManager manager;
   private ModularWeld weld;

   @Override
   public void initialize(Furnace furnace, AddonRegistry registry, Addon self)
   {
      this.furnace = furnace;
      this.addonRegistry = registry;
   }

   @Override
   public void start(Addon addon)
   {
      ResourceLoader resourceLoader = new AddonResourceLoader(addon);
      ModularURLScanner scanner = new ModularURLScanner(resourceLoader, "META-INF/beans.xml");
      ModuleScanResult scanResult = scanner.scan();

      if (!scanResult.getDiscoveredResourceUrls().isEmpty())
      {
         weld = new ModularWeld(scanResult);
         WeldContainer container = weld.initialize();

         manager = container.getBeanManager();
         Assert.notNull(manager, "BeanManager was null");

         AddonRepositoryProducer repositoryProducer = BeanManagerUtils.getContextualInstance(manager,
                  AddonRepositoryProducer.class);
         repositoryProducer.setRepository(addon.getRepository());

         FurnaceProducer forgeProducer = BeanManagerUtils.getContextualInstance(manager, FurnaceProducer.class);
         forgeProducer.setFurnace(furnace);

         AddonProducer addonProducer = BeanManagerUtils.getContextualInstance(manager, AddonProducer.class);
         addonProducer.setAddon(addon);

         AddonRegistryProducer addonRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                  AddonRegistryProducer.class);
         addonRegistryProducer.setRegistry(addonRegistry);

         ContainerServiceExtension extension = BeanManagerUtils.getContextualInstance(manager,
                  ContainerServiceExtension.class);
         ServiceRegistryProducer serviceRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                  ServiceRegistryProducer.class);
         serviceRegistry = new ServiceRegistryImpl(furnace.getLockManager(), addon, manager, extension);
         serviceRegistryProducer.setServiceRegistry(serviceRegistry);

         ServiceRegistry registry = BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class);
         Assert.notNull(registry, "Service registry was null.");
      }
   }

   @Override
   public void postStartup(Addon addon)
   {
      if (manager != null)
         manager.fireEvent(new PostStartup());
   }

   @Override
   public void stop(Addon addon)
   {
      try
      {
         if (manager != null)
            manager.fireEvent(new PreShutdown());
      }
      catch (Exception e)
      {
         logger.log(Level.SEVERE, "Failed to execute pre-Shutdown event.", e);
      }
      finally
      {
         if (weld != null)
            weld.shutdown();
      }
   }

   @Override
   public ServiceRegistry getServiceRegistry(Addon addon)
   {
      return serviceRegistry;
   }

}