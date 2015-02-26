/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.scoped;

import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class TestScopedTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addClasses(TestScoped.class, TestScopedContext.class, TestScopedExtension.class, TestScopedObject.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi")
               )
               .addBeansXML()
               .addAsManifestResource(new StringAsset(TestScopedExtension.class.getName()),
                        "services/javax.enterprise.inject.spi.Extension");

      return archive;
   }

   @Inject
   private Imported<TestScopedObject> modelInstance;

   @Test(expected = ContextNotActiveException.class)
   public void testContextNotActive() throws Exception
   {
      modelInstance.get().doSomething();
   }

   @Test
   @Ignore("FORGE-1209")
   public void testSatisfiedImportedWithCustomScope() throws Exception
   {
      Assert.assertTrue("Should not be satisfied since there is no Context in scope", modelInstance.isUnsatisfied());
      TestScopedContext.ACTIVE = true;
      Assert.assertFalse("Should be satisfied since there command context was initialized",
               modelInstance.isUnsatisfied());
      modelInstance.get().doSomething();
      TestScopedContext.ACTIVE = false;
      Assert.assertTrue("Should not be satisfied since there is no Context in scope after finish is called",
               modelInstance.isUnsatisfied());
   }

}
