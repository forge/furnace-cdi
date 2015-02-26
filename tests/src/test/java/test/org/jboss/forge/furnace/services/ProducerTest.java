/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.spi.ServiceRegistry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.PlainBean;
import test.org.jboss.forge.furnace.mocks.PlainInterface;
import test.org.jboss.forge.furnace.mocks.services.ProducesPlainInterfaceService;

/**
 * In this test, the interface is in the API addon and the implementation is provided in a JAR (not managed by CDI) in
 * this addon, which declares a single @Produces for the API interface returning an instance of the implementation in
 * the JAR
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ProducerTest
{
   @Deployment(order = 1)
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      JavaArchive implJar = ShrinkWrap.create(JavaArchive.class).addClass(PlainBean.class);

      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClasses(ProducesPlainInterfaceService.class)
               .addAsLibraries(implJar)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("API", "1"),
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );
      return archive;
   }

   @Deployment(name = "API,1", testable = false, order = 2)
   public static AddonArchive getDeploymentDep1()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addClass(PlainInterface.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               );
      return archive;
   }

   @Inject
   AddonRegistry addonRegistry;

   @Inject
   ServiceRegistry serviceRegistry;

   @Test
   public void testProducer()
   {
      Assert.assertTrue(serviceRegistry.hasService(PlainInterface.class));
      Imported<PlainInterface> services = addonRegistry.getServices(PlainInterface.class);
      Assert.assertFalse(services.isUnsatisfied());
      Assert.assertFalse(services.isAmbiguous());
      PlainInterface pi = services.get();
      Assert.assertEquals("Produced", pi.getValue());
   }
}
