/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Iterators;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.furnace.mocks.AnotherServiceBean;
import test.org.jboss.forge.furnace.mocks.ServiceBean;
import test.org.jboss.forge.furnace.mocks.ServiceInterface;
import test.org.jboss.forge.furnace.mocks.SubServiceBean;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ServiceLookupTest
{
   @Deployment
   @AddonDependencies
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addClasses(ServiceInterface.class, ServiceBean.class, AnotherServiceBean.class, SubServiceBean.class)
               .addBeansXML();
   }

   @Inject
   private AddonRegistry registry;

   @Test
   public void shouldAssertExportedTypesSizeTo3()
   {
      Assert.assertThat(registry.getExportedTypes(ServiceInterface.class).size(), equalTo(3));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void shouldResolveImpls() throws Exception
   {
      Imported<ServiceInterface> imported = registry.getServices(ServiceInterface.class);
      Assert.assertTrue(imported.isAmbiguous());
      Assert.assertEquals(3, Iterators.asList(imported).size());
      Assert.assertThat(imported, CoreMatchers.<ServiceInterface> hasItems(
               instanceOf(ServiceBean.class),
               instanceOf(AnotherServiceBean.class),
               instanceOf(SubServiceBean.class)));
   }

}
