package test.org.jboss.forge.furnace;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.AbstractImplementation;
import test.org.jboss.forge.furnace.mocks.ExportedInterface;
import test.org.jboss.forge.furnace.mocks.ImplementingClass1;
import test.org.jboss.forge.furnace.mocks.ImplementingClass2;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@RunWith(Arquillian.class)
public class ContainerServiceMultipleInstanceTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace:container-cdi", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(
                        AbstractImplementation.class,
                        ExportedInterface.class,
                        ImplementingClass1.class,
                        ImplementingClass2.class
               )
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace:container-cdi", "2.0.0-SNAPSHOT")
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private Instance<ExportedInterface> instanceInterfaceInstance;

   @Test
   public void testRegisteredServices()
   {
      Assert.assertNotNull(instanceInterfaceInstance.get());
      for (ExportedInterface instance : instanceInterfaceInstance)
      {
         Assert.assertNotNull(instance);
      }
      Instance<ImplementingClass1> implementation = instanceInterfaceInstance.select(ImplementingClass1.class);
      Assert.assertTrue(implementation.get() instanceof AbstractImplementation);
   }
}