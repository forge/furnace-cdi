package test.org.jboss.forge.furnace.events;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.lifecycle.AddonLifecycleProvider;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.PublishedService;
import test.org.jboss.forge.furnace.services.NullAddonLifecycleProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NullEventManagerTest
{
   @Deployment(order = 2)
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
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
   private Addon addon;

   @Test
   public void testPublishEventWorksIfEventManagerFromProviderWasNull() throws Exception
   {
      addon.getEventManager().fireEvent(addon);
   }

}