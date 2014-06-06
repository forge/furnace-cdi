/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.addons;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ServiceRegistry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.Aa;
import test.org.jboss.forge.furnace.mocks.BB;

/**
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonRegistryIncompatibleServiceLookupTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClass(Aa.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("test:dep2"),
                        AddonDependencyEntry.create("test:dep1")

               );

      return archive;
   }

   @Deployment(name = "test:dep2,2", testable = false, order = 2)
   public static ForgeArchive getDeploymentDep2()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClass(BB.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );
      return archive;
   }

   @Deployment(name = "test:dep1,1", testable = false, order = 1)
   public static ForgeArchive getDeploymentDep1()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClass(BB.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private AddonRegistry addonRegistry;

   @Test
   public void testServiceWithExpectedObjectsDifferentClassLoaders() throws Exception
   {

      AddonId depOneId = AddonId.from("test:dep1", "1");
      AddonId depTwoId = AddonId.from("test:dep2", "2");

      Addon depOne = addonRegistry.getAddon(depOneId);
      Addon depTwo = addonRegistry.getAddon(depTwoId);

      ServiceRegistry depOneServiceRegistry = depOne.getServiceRegistry();
      ServiceRegistry depTwoServiceRegistry = depTwo.getServiceRegistry();

      Assert.assertFalse(depOneServiceRegistry.hasService(Aa.class));
      Assert.assertFalse(depOneServiceRegistry.hasService(Aa.class.getName()));
      Assert.assertFalse(depTwoServiceRegistry.hasService(Aa.class));
      Assert.assertFalse(depTwoServiceRegistry.hasService(Aa.class.getName()));
      Assert.assertFalse(depOneServiceRegistry.hasService(loadClass(BB.class, depTwo.getClassLoader())));
      Assert.assertTrue(depOneServiceRegistry.hasService(BB.class.getName()));
      Assert.assertTrue(depTwoServiceRegistry.hasService(loadClass(BB.class, depTwo.getClassLoader())));
      Assert.assertTrue(depTwoServiceRegistry.hasService(BB.class.getName()));

      Assert.assertNotNull(depTwoServiceRegistry.getExportedInstance(BB.class.getName()));

      Imported<BB> services = addonRegistry.getServices(BB.class);
      Assert.assertFalse("Imported<BB> should have been satisfied", services.isUnsatisfied());
      Assert.assertFalse("Imported<BB> should not have been ambiguous", services.isAmbiguous());
      Iterator<BB> iterator2 = services.iterator();
      Assert.assertTrue(iterator2.hasNext());
      Assert.assertThat(iterator2.next(), is(instanceOf(BB.class)));
      Assert.assertFalse(iterator2.hasNext());
   }

   private Class<?> loadClass(Class<?> clazz, ClassLoader cl) throws Exception
   {
      return cl.loadClass(clazz.getName());
   }

}
