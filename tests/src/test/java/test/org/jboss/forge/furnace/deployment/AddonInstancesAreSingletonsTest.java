/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.deployment;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FIXME This test needs to be refactored to be a bit less brittle. It breaks when addon POMs change.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonInstancesAreSingletonsTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Inject
   private AddonRepository repository;

   @Test
   public void testInstallingAddonWithSingleOptionalAddonDependency() throws InterruptedException, TimeoutException
   {
      int addonCount = registry.getAddons().size();
      final AddonId mockAddon = AddonId.fromCoordinates("org.jboss.forge.addon:mock-addon,2.0.0-SNAPSHOT");

      /*
       * Ensure that the Addon instance we receive is requested before configuration is rescanned.
       */
      Addon example = registry.getAddon(mockAddon);
      Assert.assertTrue(((MutableAddonRepository) repository).deploy(mockAddon, Arrays.asList(AddonDependencyEntry
               .create("org.jboss.forge.furnace.container:cdi", false)), null));
      Assert.assertTrue(((MutableAddonRepository) repository).enable(mockAddon));
      Addons.waitUntilStarted(example, 10, TimeUnit.SECONDS);
      Assert.assertEquals(addonCount + 1, registry.getAddons().size());
   }

}
