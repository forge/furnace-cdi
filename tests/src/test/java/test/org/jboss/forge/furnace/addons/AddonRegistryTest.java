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
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.BB;
import test.org.jboss.forge.furnace.mocks.PlainBean;
import test.org.jboss.forge.furnace.mocks.Aa;
import test.org.jboss.forge.furnace.mocks.PlainInterface;
import test.org.jboss.forge.furnace.mocks.PlainQualifier;
import test.org.jboss.forge.furnace.mocks.QualifiedPlainBean;
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
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(PlainInterface.class, PlainBean.class,
                        PlainQualifier.class, QualifiedPlainBean.class, ServiceInterface.class, ServiceBean.class,
                        Aa.class, BB.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private AddonRegistry addonRegistry;

   @Test
   public void testImported() throws Exception
   {
      Imported<ServiceInterface> services = addonRegistry.getServices(ServiceInterface.class);
      Assert.assertFalse(services.isUnsatisfied());
      Assert.assertFalse(services.isAmbiguous());
      Assert.assertNotNull(services.get());
      Assert.assertNotNull(services.iterator().next());
      Assert.assertTrue(services.iterator().hasNext());
   }

   @Test
   public void testImportedWithQualifiers() throws Exception
   {
      Imported<PlainInterface> services = addonRegistry.getServices(PlainInterface.class);
      Assert.assertTrue(services.isAmbiguous());
      Assert.assertFalse(services.isUnsatisfied());
      Assert.assertTrue(services.iterator().hasNext());
      Assert.assertNotNull(services.iterator().next());
   }

   @Test
   public void testImportedWithExpectedObjectsSameClassLoader() throws Exception
   {
      Imported<Aa> services = addonRegistry.getServices(Aa.class);
      Assert.assertFalse(services.isUnsatisfied());
      Assert.assertFalse(services.isAmbiguous());
      Iterator<Aa> iterator = services.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertThat(iterator.next(), is(instanceOf(Aa.class)));
      Assert.assertFalse(iterator.hasNext());

      Imported<BB> services2 = addonRegistry.getServices(BB.class);
      Assert.assertFalse(services2.isUnsatisfied());
      Assert.assertFalse(services2.isAmbiguous());
      Iterator<BB> iterator2 = services2.iterator();
      Assert.assertTrue(iterator2.hasNext());
      Assert.assertThat(iterator2.next(), is(instanceOf(BB.class)));
      Assert.assertFalse(iterator2.hasNext());
   }

}
