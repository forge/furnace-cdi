/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.events;

import java.util.Map.Entry;

import javax.inject.Inject;
import javax.xml.xpath.XPathFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matej Briškár
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ContainerLifecycleEventsTest
{

   @Deployment(order = 3)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment1()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("dep2"),
                        AddonDependencyEntry.create("dep3"),
                        AddonDependencyEntry.create("dep4")
               ).addBeansXML();
      return archive;
   }

   @Deployment(name = "dep2,1", testable = false, order = 2)
   public static AddonArchive getDeploymentDep2()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();
      return archive;
   }

   @Deployment(name = "dep3,1", testable = false, order = 1)
   public static AddonArchive getDeploymentDep3()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();
      return archive;
   }

   @Deployment(name = "dep4,1", testable = false, order = 0)
   public static AddonArchive getDeploymentDep4()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClass(ContainerLifecycleEventObserver.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();
      return archive;
   }

   @Inject
   private ContainerLifecycleEventObserver observer;

   @Test
   public void testContainerStartup()
   {
      Assert.assertTrue(observer.isObservedPerform());
      Assert.assertTrue("PostStartup should be called for each installed addon. Only "
               + observer.getPostStartupMap().size() + " calls were registered.",
               observer.getPostStartupMap().size() == 4);
      for (Entry<String, Integer> entry : observer.getPostStartupMap().entrySet())
      {
         if (entry.getValue() > 1)
         {
            Assert.fail("Multiple PostStartup events for a single addon");
         }
      }
   }

   @Test
   public void testContainerSupportsXPath()
   {
      Assert.assertNotNull(XPathFactory.newInstance().newXPath());
   }

}