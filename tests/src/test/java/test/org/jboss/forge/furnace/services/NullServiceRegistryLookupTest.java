package test.org.jboss.forge.furnace.services;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.lifecycle.AddonLifecycleProvider;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.extension.TestExtension;
import test.org.jboss.forge.furnace.mocks.services.ConsumingService;
import test.org.jboss.forge.furnace.mocks.services.PublishedService;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NullServiceRegistryLookupTest
{
   @Deployment(order = 2)
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace:container-cdi", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(ConsumingService.class, TestExtension.class)
               .addBeansXML()
               .addAsServiceProvider(Extension.class, TestExtension.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace:container-cdi", "2.0.0-SNAPSHOT"),
                        AddonDependencyEntry.create("dependency", "1")
               );

      return archive;
   }

   @Deployment(name = "dependency,1", testable = false, order = 1)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(PublishedService.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("nullcontainer", "1")
               );

      return archive;
   }

   @Deployment(name = "nullcontainer,1", testable = false, order = 1)
   public static ForgeArchive getContainerDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClass(NullAddonLifecycleProvider.class)
               .addAsServiceProvider(AddonLifecycleProvider.class, NullAddonLifecycleProvider.class);

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test(expected = ContainerException.class)
   public void testServiceRegistryNotNull() throws Exception
   {
      Imported<PublishedService> instance = registry.getInstance(PublishedService.class);
      Assert.assertNotNull(instance);
      instance.get();
   }

}