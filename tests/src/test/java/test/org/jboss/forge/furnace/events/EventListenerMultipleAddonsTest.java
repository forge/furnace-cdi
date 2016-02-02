/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.events;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.event.EventManager;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.event.AtomicIntegerEventListener;
import test.org.jboss.forge.furnace.mocks.services.PublishedService;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
@Ignore("FURNACE-107: This test reproduces the bug")
public class EventListenerMultipleAddonsTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClass(AtomicIntegerEventListener.class);
   }

   @Deployment(name = "test:dep1,3", testable = false, order = 3)
   public static AddonArchive getDeploymentDep3()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addClass(PublishedService.class)
               // FURNACE-107: If you remove the line below, testFireEvent will fail with a different error
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));
   }

   @Deployment(name = "test:dep2,2", testable = false, order = 2)
   public static AddonArchive getDeploymentDep2()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addClass(PublishedService.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("test:dep1"));
   }

   @Deployment(name = "test:dep1,1", testable = false, order = 1)
   public static AddonArchive getDeploymentDep1()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addClass(PublishedService.class)
               // FURNACE-107: If you remove the line below, testFireEvent will fail with a different error
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));
   }

   @Inject
   AddonRegistry addonRegistry;

   @Inject
   Addon addon;

   @Test
   public void testFireEvent()
   {
      AtomicInteger atomicInteger = new AtomicInteger();
      addonRegistry.getEventManager().fireEvent(atomicInteger);
      Assert.assertEquals(1, atomicInteger.intValue());
   }

   @Test
   public void testFireEventFromAddon()
   {
      EventManager eventManager = addon.getEventManager();
      AtomicInteger atomicInteger = new AtomicInteger();
      eventManager.fireEvent(atomicInteger);
      Assert.assertEquals(1, atomicInteger.intValue());
   }
}