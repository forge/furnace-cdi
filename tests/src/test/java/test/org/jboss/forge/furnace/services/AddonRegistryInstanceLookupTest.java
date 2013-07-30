package test.org.jboss.forge.furnace.services;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.services.PublishedService;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AddonRegistryInstanceLookupTest
{
   @Deployment(order = 2)
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace:container-cdi", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
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
               .addAsLocalServices(PublishedService.class);

      return archive;
   }

   @Deployment(name = "other,1", testable = false, order = 0)
   public static ForgeArchive getContainerDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(PublishedService.class)
               .addAsLocalServices(PublishedService.class);

      return archive;
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void testMissingNamedLookupReturnsEmptyInstance() throws Exception
   {
      Imported<PublishedService> instance = registry.getServices("org.example.blah.NotExistsBadClassThing");
      Assert.assertNotNull(instance);
      Assert.assertFalse(instance.isAmbiguous());
      Assert.assertFalse(instance.isSatisfied());
   }

   @Test
   public void testMissingTypedLookupReturnsEmptyInstance() throws Exception
   {
      Imported<AddonDependencyEntry> instance = registry.getServices(AddonDependencyEntry.class);
      Assert.assertNotNull(instance);
      Assert.assertFalse(instance.isAmbiguous());
      Assert.assertFalse(instance.isSatisfied());
   }

   @Test
   public void testTypedLookupReturnsProperType() throws Exception
   {
      Imported<PublishedService> instance = registry.getServices(PublishedService.class);
      Assert.assertNotNull(instance);
      PublishedService service = instance.get();
      Assert.assertNotNull(service);
   }

   @Test
   public void testTypedLookupCanBeIterated() throws Exception
   {
      Imported<PublishedService> instance = registry.getServices(PublishedService.class);
      Assert.assertFalse(instance.isAmbiguous());
      Assert.assertTrue(instance.isSatisfied());
      Iterator<PublishedService> iterator = instance.iterator();
      Assert.assertTrue(iterator.hasNext());
      Assert.assertNotNull(iterator.next());
      Assert.assertFalse(iterator.hasNext());
   }

   @Test
   public void testNameLookupReturnsAllMatches() throws Exception
   {
      Imported<PublishedService> instance = registry.getServices(PublishedService.class.getName());
      Assert.assertTrue(instance.isAmbiguous());
      Assert.assertTrue(instance.isSatisfied());

      Assert.assertNotNull(instance);
      Iterator<PublishedService> iterator = instance.iterator();
      Assert.assertTrue(iterator.hasNext());
      Object first = iterator.next();
      Assert.assertNotNull(first);
      Assert.assertTrue(iterator.hasNext());
      Assert.assertTrue(iterator.hasNext());
      Object second = iterator.next();
      Assert.assertNotNull(second);
      Assert.assertFalse(iterator.hasNext());

      boolean typeMatchFound = false;
      if (first instanceof PublishedService)
         typeMatchFound = true;
      if (second instanceof PublishedService)
         typeMatchFound = true;

      Assert.assertTrue(typeMatchFound);
   }

}