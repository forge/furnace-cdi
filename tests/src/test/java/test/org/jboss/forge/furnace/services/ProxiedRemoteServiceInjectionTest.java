/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.services;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.ProxiedService;
import test.org.jboss.forge.furnace.mocks.services.ProxiedServiceBaseClass;
import test.org.jboss.forge.furnace.mocks.services.ProxiedServiceConsumer;
import test.org.jboss.forge.furnace.mocks.services.ProxiedServiceFactory;
import test.org.jboss.forge.furnace.mocks.services.ProxiedServiceImpl;
import test.org.jboss.forge.furnace.mocks.services.ProxiedServiceStrategy;

@RunWith(Arquillian.class)
public class ProxiedRemoteServiceInjectionTest
{
   @Deployment(order = 0)
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("dependency")
               );

      return archive;
   }

   @Deployment(name = "dependency,2", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependency.jar")
               .addClass(ProxiedServiceConsumer.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("provider", true)
               )
               .addBeansXML();

      return archive;
   }

   @Deployment(name = "provider,1", testable = false, order = 2)
   public static ForgeArchive getProviderDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "provider.jar")
               .addClass(ProxiedService.class)
               .addClass(ProxiedServiceImpl.class)
               .addClass(ProxiedServiceBaseClass.class)
               .addClass(ProxiedServiceStrategy.class)
               .addClass(ProxiedServiceFactory.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private ProxiedServiceConsumer consumer;

   @Test
   public void testTransitiveServiceInjection() throws Exception
   {
      Assert.assertNotNull(consumer);
      Assert.assertNotNull(consumer.getService());
   }

   @Test
   public void testProxiedProducedServicesImplementRequestedInterfacesOnly() throws Exception
   {
      ProxiedService service = consumer.getService();
      Assert.assertFalse(service instanceof ProxiedServiceStrategy);
   }

   @Test
   public void testProxiedServiceInjectionsMayBeInvoked() throws Exception
   {
      ProxiedService service = consumer.getService();
      Assert.assertTrue(service.getValue());
   }
}
