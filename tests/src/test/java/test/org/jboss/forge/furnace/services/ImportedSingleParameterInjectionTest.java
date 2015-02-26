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
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.ParameterGenericType;
import test.org.jboss.forge.furnace.mocks.services.ParameterGenericTypeImpl;

@RunWith(Arquillian.class)
public class ImportedSingleParameterInjectionTest
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addClasses(ParameterGenericType.class, ParameterGenericTypeImpl.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );

      return archive;
   }

   @Inject
   private Imported<ParameterGenericType<?>> instances;

   @Test
   public void testImportedResolvesWildcardTypesViaIteration() throws Exception
   {
      Assert.assertFalse(instances.isAmbiguous());
      for (ParameterGenericType<?> instance : instances)
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
