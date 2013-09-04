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
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.spi.ExportedInstance;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.PublishedService;

@RunWith(Arquillian.class)
public class CDIExportedInstanceImplTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(PublishedService.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testCDIExportedInstanceExposesServiceTypeAndSourceAddon() throws Exception
   {
      boolean found = false;
      for (Addon addon : registry.getAddons())
      {
         ExportedInstance<PublishedService> instance = addon.getServiceRegistry()
                  .getExportedInstance(PublishedService.class);
         if (instance != null)
         {
            found = true;
            Assert.assertEquals(PublishedService.class, instance.getActualType());
            Assert.assertEquals(addon, instance.getSourceAddon());
            break;
         }
      }
      Assert.assertTrue("Could not locate service in any addon.", found);
   }
}
