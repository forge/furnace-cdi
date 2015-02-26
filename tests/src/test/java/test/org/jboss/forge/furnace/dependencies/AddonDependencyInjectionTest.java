/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.dependencies;

import javax.enterprise.inject.spi.Extension;
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

import test.org.jboss.forge.furnace.mocks.extension.TestExtension;
import test.org.jboss.forge.furnace.mocks.services.ConsumingService;
import test.org.jboss.forge.furnace.mocks.services.PublishedService;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonDependencyInjectionTest
{
   @Deployment(order = 2)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(ConsumingService.class, TestExtension.class)
               .addBeansXML()
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("dependency", "2")
               );

      return archive;
   }

   @Deployment(name = "dependency,2", testable = false, order = 1)
   public static AddonArchive getDependencyDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class, "dependency.jar")
               .addClasses(PublishedService.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private ConsumingService consuming;

   @Inject
   private PublishedService remote;

   @Test
   public void testRemoteServiceInjection() throws Exception
   {
      Assert.assertNotNull(consuming);
      Assert.assertNotNull(remote);
      Assert.assertEquals("I am ConsumingService. Remote service says [I am PublishedService.]",
               consuming.getMessage());
      Assert.assertEquals(remote.hashCode(), consuming.getRemoteHashCode());
      Assert.assertNotSame(consuming, remote);
      Assert.assertNotSame(consuming.getClassLoader(), remote.getClassLoader());
   }

}