/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.addons;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.PlainBean;
import test.org.jboss.forge.furnace.mocks.PlainInterface;
import test.org.jboss.forge.furnace.mocks.ServiceBean;
import test.org.jboss.forge.furnace.mocks.ServiceInterface;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class AddonRegistryTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(PlainInterface.class, PlainBean.class, ServiceInterface.class, ServiceBean.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRegistry addonRegistry;

   @Test
   public void testAddonRegistryShouldReturnServicesWithoutExportedAnnotation() throws Exception
   {
      Imported<PlainInterface> services = addonRegistry.getServices(PlainInterface.class);
      Assert.assertFalse(services.isUnsatisfied());
      Assert.assertFalse(services.isAmbiguous());
      Assert.assertTrue(services.iterator().hasNext());
      Assert.assertNotNull(services.iterator().next());
      Assert.assertNotNull(services.get());
   }

   @Test
   public void testAddonRegistryShouldReturnImportedWithExportedAnnotation() throws Exception
   {
      Imported<ServiceInterface> services = addonRegistry.getServices(ServiceInterface.class);
      Assert.assertFalse(services.isUnsatisfied());
      Assert.assertFalse(services.isAmbiguous());
      Assert.assertNotNull(services.get());
      Assert.assertNotNull(services.iterator().next());
      Assert.assertTrue(services.iterator().hasNext());
   }

}
