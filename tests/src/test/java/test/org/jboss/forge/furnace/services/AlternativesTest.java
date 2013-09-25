/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.AlternativeServiceBean;
import test.org.jboss.forge.furnace.mocks.ServiceBean;
import test.org.jboss.forge.furnace.mocks.ServiceInterface;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
@org.junit.Ignore("FORGE-1230")
public class AlternativesTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML(
                        new StringAsset(
                                 "<beans><alternatives>" + AlternativeServiceBean.class.getName()
                                          + "</alternatives></beans>"))
               .addClasses(ServiceInterface.class, ServiceBean.class, AlternativeServiceBean.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );
      return archive;
   }

   @Inject
   Imported<ServiceInterface> service;

   @Test
   public void testAlternativeInjectionGlobal() throws Exception
   {
      Assert.assertNotNull(service);
      Assert.assertEquals("Alternative", service.get().invoke());
      Assert.assertEquals("Alternative", service.iterator().next().invoke());
      Assert.assertEquals("Alternative", service.iterator().next().invoke());
      Assert.assertEquals("Alternative", service.iterator().next().invoke());
   }

}
