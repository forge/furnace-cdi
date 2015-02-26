/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.deployment;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
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
public class AddonMissingOptionalDependencyNotNull
{
   @Deployment(order = 3)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML();

      return archive;
   }

   @Deployment(name = "dep3,3", testable = false, order = 1)
   public static AddonArchive getDeploymentDep3()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create("dep4", "4", false, true));

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testHotSwap() throws Exception
   {
      AddonId dep3Id = AddonId.from("dep3", "3");
      AddonId dep4Id = AddonId.from("dep4", "4");
      AddonId asdf = AddonId.from("asdfasdf", "234");

      Addon dep3 = registry.getAddon(dep3Id);
      Addon dep4 = registry.getAddon(dep4Id);
      Addon asdfAddon = registry.getAddon(asdf);

      Assert.assertNotNull(dep3);
      Assert.assertNotNull(dep4);
      Assert.assertNotNull(asdfAddon);

   }

}