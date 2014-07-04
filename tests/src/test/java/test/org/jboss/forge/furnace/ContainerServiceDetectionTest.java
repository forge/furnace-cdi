/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.spi.ExportedInstance;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.AbstractImplementation;
import test.org.jboss.forge.furnace.mocks.ExportedAbstractClass;
import test.org.jboss.forge.furnace.mocks.ExportedConcreteClass;
import test.org.jboss.forge.furnace.mocks.ExportedInterface;
import test.org.jboss.forge.furnace.mocks.ImplementingClass1;
import test.org.jboss.forge.furnace.mocks.ImplementingClass2;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ContainerServiceDetectionTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(ExportedAbstractClass.class,
                        ExportedConcreteClass.class,
                        AbstractImplementation.class,
                        ExportedInterface.class,
                        ImplementingClass1.class,
                        ImplementingClass2.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private Addon addon;

   @Test
   public void testRegisteredServices()
   {
      Assert.assertNotNull(addon.getServiceRegistry().getExportedInstance(ExportedConcreteClass.class));
      Assert.assertNull(addon.getServiceRegistry().getExportedInstance(ExportedAbstractClass.class));

      Assert.assertNotNull(addon.getServiceRegistry().getExportedInstance(ImplementingClass1.class));
      Assert.assertNotNull(addon.getServiceRegistry().getExportedInstance(ImplementingClass2.class));
   }

   @Test
   public void testGetExportedInstances()
   {
      Assert.assertNotNull(addon.getServiceRegistry().getExportedInstance(ExportedConcreteClass.class).get());
      Assert.assertNotNull(addon.getServiceRegistry().getExportedInstance(ImplementingClass1.class).get());
      Assert.assertNotNull(addon.getServiceRegistry().getExportedInstance(ImplementingClass2.class).get());
   }

   @Test(expected = Exception.class)
   public void testGetExportedInstanceBySharedAbstractClass()
   {
      addon.getServiceRegistry().getExportedInstance(AbstractImplementation.class);
   }

   @Test(expected = Exception.class)
   public void testGetExportedInstanceBySharedInterface()
   {
      addon.getServiceRegistry().getExportedInstance(ExportedInterface.class);
   }

   @Test
   public void testGetExportedInstancesByBaseType()
   {
      Set<ExportedInstance<ExportedInterface>> byInterface = addon.getServiceRegistry().getExportedInstances(ExportedInterface.class);
      Set<ExportedInstance<AbstractImplementation>> byAbstractBaseClass = addon.getServiceRegistry()
               .getExportedInstances(AbstractImplementation.class);

      Assert.assertEquals(2, byInterface.size());
      Assert.assertEquals(2, byAbstractBaseClass.size());

      for (ExportedInstance<ExportedInterface> instance : byInterface)
      {
         instance.get();
      }

      for (ExportedInstance<AbstractImplementation> instance : byAbstractBaseClass)
      {
         instance.get();
      }
   }
}