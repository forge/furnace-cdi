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
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.AbstractImportedBaseConsumer;
import test.org.jboss.forge.furnace.mocks.services.ConcreteImportedConsumer;
import test.org.jboss.forge.furnace.mocks.services.ExportedService;

@RunWith(Arquillian.class)
public class InheritedServiceRemoteImportedInjectionTest
{
   @Deployment(order = 0)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClass(ConcreteImportedConsumer.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("dependency")
               );

      return archive;
   }

   @Deployment(name = "dependency,2", testable = false, order = 1)
   public static AddonArchive getDependencyDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class, "dependency.jar")
               .addClass(AbstractImportedBaseConsumer.class)
               .addClasses(ExportedService.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private ConcreteImportedConsumer consumer;

   @Test
   public void testInheritedServiceInjectionsResolveSuccessfully() throws Exception
   {
      Assert.assertNotNull(consumer);
      Assert.assertNotNull(consumer.getService());
   }
}
