/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.lifecycle;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.cdi.events.EventManagerImpl;
import org.jboss.forge.furnace.container.cdi.events.EventManagerProducer;
import org.jboss.forge.furnace.container.cdi.events.LocalLiteral;
import org.jboss.forge.furnace.container.cdi.impl.AddonProducer;
import org.jboss.forge.furnace.container.cdi.impl.AddonRegistryProducer;
import org.jboss.forge.furnace.container.cdi.impl.AddonRepositoryProducer;
import org.jboss.forge.furnace.container.cdi.impl.ContainerBeanRegistrant;
import org.jboss.forge.furnace.container.cdi.impl.ContainerServiceExtension;
import org.jboss.forge.furnace.container.cdi.impl.FurnaceProducer;
import org.jboss.forge.furnace.container.cdi.impl.ServiceRegistryProducer;
import org.jboss.forge.furnace.container.cdi.impl.WeldServiceRegistry;
import org.jboss.forge.furnace.container.cdi.util.BeanManagerUtils;
import org.jboss.forge.furnace.container.cdi.weld.AddonResourceLoader;
import org.jboss.forge.furnace.container.cdi.weld.ModularURLScanner;
import org.jboss.forge.furnace.container.cdi.weld.ModuleScanResult;
import org.jboss.forge.furnace.event.EventManager;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;
import org.jboss.forge.furnace.lifecycle.AddonLifecycleProvider;
import org.jboss.forge.furnace.lifecycle.ControlType;
import org.jboss.forge.furnace.spi.ServiceRegistry;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.weld.config.ConfigurationKey;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class WeldAddonLifecycleProvider implements AddonLifecycleProvider
{
   private Furnace furnace;
   private AddonRegistry addonRegistry;

   private WeldServiceRegistry serviceRegistry;
   private BeanManager manager;
   private EventManagerImpl eventManager;
   private Addon container;

   private WeldContainer weldContainer;

   @Override
   public void initialize(Furnace furnace, AddonRegistry registry, Addon container)
   {
      this.furnace = furnace;
      this.addonRegistry = registry;
      this.container = container;
   }

   @Override
   public void start(Addon addon) throws Exception
   {
      ResourceLoader resourceLoader = new AddonResourceLoader(addon);
      ModularURLScanner scanner = new ModularURLScanner(resourceLoader, "META-INF/beans.xml");
      ModuleScanResult scanResult = scanner.scan();

      if (!scanResult.getDiscoveredResourceUrls().isEmpty())
      {
         ContainerServiceExtension serviceExtension = new ContainerServiceExtension(container, addon);
         ContainerBeanRegistrant registrantExtension = new ContainerBeanRegistrant();

         weldContainer = new Weld(addon.getId().getName()).setResourceLoader(resourceLoader)
                  .addExtension(serviceExtension).addExtension(registrantExtension)
                  .property(ConfigurationKey.PRELOADER_THREAD_POOL_SIZE.get(), 0)
                  .property(ConfigurationKey.CONCURRENT_DEPLOYMENT.get(), false)
                  .initialize();

         manager = weldContainer.getBeanManager();
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

         ServiceRegistryProducer serviceRegistryProducer = BeanManagerUtils.getContextualInstance(manager,
                  ServiceRegistryProducer.class);
         serviceRegistry = new WeldServiceRegistry(furnace.getLockManager(), addon, manager,
                  serviceExtension.getServices());
         serviceRegistryProducer.setServiceRegistry(serviceRegistry);
         Assert.notNull(BeanManagerUtils.getContextualInstance(manager, ServiceRegistry.class),
                  "InboundEvent registry was null.");

         EventManagerProducer eventManagerProducer = BeanManagerUtils.getContextualInstance(manager,
                  EventManagerProducer.class);
         eventManager = new EventManagerImpl(addon, manager);
         eventManagerProducer.setEventManager(eventManager);
         Assert.notNull(BeanManagerUtils.getContextualInstance(manager, EventManager.class),
                  "InboundEvent registry was null.");
      }
   }

   @Override
   public void postStartup(Addon addon)
   {
      if (eventManager != null)
         eventManager.fireEvent(new PostStartup(addon), new LocalLiteral());
   }

   @Override
   public void preShutdown(Addon addon)
   {
      if (eventManager != null)
         eventManager.fireEvent(new PreShutdown(addon), new LocalLiteral());
   }

   @Override
   public void stop(Addon addon)
   {
      if (weldContainer != null)
      {
         weldContainer.shutdown();
      }
      serviceRegistry.close();
      serviceRegistry = null;
      eventManager = null;
   }

   @Override
   public EventManager getEventManager(Addon addon)
   {
      return eventManager;
   }

   @Override
   public ServiceRegistry getServiceRegistry(Addon addon)
   {
      return serviceRegistry;
   }

   @Override
   public ControlType getControlType()
   {
      return ControlType.DEPENDENTS;
   }
}