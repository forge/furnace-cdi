/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.event.AtomicIntegerEventListener;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class EventListenerTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addClass(AtomicIntegerEventListener.class)
               .addBeansXML();
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
