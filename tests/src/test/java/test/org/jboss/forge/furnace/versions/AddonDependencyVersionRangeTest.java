/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.versions;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonDependencyVersionRangeTest
{
   @Deployment(order = 1)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("A", "[1,2]", true),
                        AddonDependencyEntry.create("B", "1", true),
                        AddonDependencyEntry.create("C", "2", true)
               );

      return archive;
   }

   @Deployment(name = "A,1", testable = false, order = 2)
   public static AddonArchive getDeploymentA1()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Deployment(name = "A,2", testable = false, order = 3)
   public static AddonArchive getDeploymentA2()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Deployment(name = "A,3", testable = false, order = 3)
   public static AddonArchive getDeploymentA3()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Deployment(name = "B,1", testable = false, order = 4)
   public static AddonArchive getDeploymentB1()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("A", "2,3", true)
               );

      return archive;
   }

   @Deployment(name = "C,1", testable = false, order = 4)
   public static AddonArchive getDeploymentC1()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("A", "1", false)
               );

      return archive;
   }

   @Deployment(name = "C,2", testable = false, order = 4)
   public static AddonArchive getDeploymentC2()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("A", "2", true)
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testBuildGraphs() throws Exception
   {
      Assert.assertTrue(registry.getName().startsWith("ROOT"));
      Assert.assertEquals(8, registry.getAddons().size());
   }
}