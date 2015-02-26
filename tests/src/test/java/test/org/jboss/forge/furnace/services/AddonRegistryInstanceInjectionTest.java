/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.services;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.PublishedService;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonRegistryInstanceInjectionTest
{
   @Deployment(order = 2)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("dependency", "1")
               );

      return archive;
   }

   @Deployment(name = "dependency,1", testable = false, order = 1)
   public static AddonArchive getDependencyDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(PublishedService.class)
               .addAsLocalServices(PublishedService.class);

      return archive;
   }

   @Deployment(name = "other,1", testable = false, order = 0)
   public static AddonArchive getContainerDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(PublishedService.class)
               .addAsLocalServices(PublishedService.class);

      return archive;
   }

   @Inject
   private Imported<PublishedService> instance;
   @Inject
   private Imported<AddonDependencyEntry> missing;

   @Test
   public void testInjectionTypedLookupMissing() throws Exception
   {
      Assert.assertNotNull(missing);
      Assert.assertFalse(missing.isAmbiguous());
      Assert.assertTrue(missing.isUnsatisfied());
   }

   @Test(expected = ContainerException.class)
   public void testInjectionTypedLookupMissingGetThrowsException() throws Exception
   {
      missing.get();
   }

   @Test
   public void testInjectionTypedLookupMissingIteratorIsEmpty() throws Exception
   {
      Iterator<AddonDependencyEntry> iterator = missing.iterator();
      Assert.assertNotNull(iterator);
      Assert.assertFalse(iterator.hasNext());
   }

   @Test
   public void testInjectionTypedLookup() throws Exception
   {
      Assert.assertNotNull(instance);
      Assert.assertFalse(instance.isAmbiguous());
      Assert.assertFalse(instance.isUnsatisfied());
   }

   @Test
   public void testTypedLookupReturnsProperType() throws Exception
   {
      Assert.assertNotNull(instance);
      PublishedService service = instance.get();
      Assert.assertNotNull(service);
   }

   @Test
   public void testTypedLookupCanBeIterated() throws Exception
   {
      Assert.assertFalse(instance.isAmbiguous());
      Assert.assertFalse(instance.isUnsatisfied());
      Iterator<PublishedService> iterator = instance.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertNotNull(iterator.next());
      Assert.assertFalse(iterator.hasNext());
   }

}