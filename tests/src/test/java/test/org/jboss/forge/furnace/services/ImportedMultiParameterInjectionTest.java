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
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.TwoParameterGenericType;
import test.org.jboss.forge.furnace.mocks.services.TwoParameterGenericTypeImpl;

@RunWith(Arquillian.class)
public class ImportedMultiParameterInjectionTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(TwoParameterGenericType.class, TwoParameterGenericTypeImpl.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private Imported<TwoParameterGenericType<?, ?>> instances;

   @Test
   public void testImportedResolvesWildcardTypes() throws Exception
   {
      Assert.assertFalse(instances.isAmbiguous());
      for (TwoParameterGenericType<?, ?> instance : instances)
      {
         instances.release(instance);
      }
   }

   @Test
   @Ignore("FORGE-1263")
   public void testImportedResolvesWildcardTypesViaDirectAccess() throws Exception
   {
      instances.release(instances.get());
   }
}
