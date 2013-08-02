package test.org.jboss.forge.furnace.events;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.event.EventPayload1;
import test.org.jboss.forge.furnace.mocks.event.EventPayload3;
import test.org.jboss.forge.furnace.mocks.event.EventResponseService;
import test.org.jboss.forge.furnace.mocks.event.EventService;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonEventPropagationRemoteTest
{
   @Deployment(order = 1)
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(EventService.class, EventPayload1.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("dependencyA", "1")
               );

      return archive;
   }

   @Deployment(name = "dependencyA,1", testable = false, order = 2)
   public static ForgeArchive getDependencyDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class, "dependencyA.jar")
               .addClasses(EventResponseService.class, EventPayload3.class)
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi", false)
               )
               .addBeansXML();

      return archive;
   }

   @Inject
   private EventService sender;

   @Test
   public void testEventPropagationAcrossContainers() throws Exception
   {
      Assert.assertFalse(sender.isLocalRequestRecieved());
      Assert.assertFalse(sender.isWrongResponseRecieved());
      Assert.assertFalse(sender.isRemoteResponseRecieved());
      sender.fire();
      Assert.assertTrue(sender.isLocalRequestRecieved());
      Assert.assertTrue(sender.isRemoteResponseRecieved());
      Assert.assertFalse(sender.isWrongResponseRecieved());
   }

}