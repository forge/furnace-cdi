/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.furnace.container.cdi.arquillian;

import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.arquillian.spi.TestClassRegistrationStrategy;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CDITestClassRegistrationStrategy implements TestClassRegistrationStrategy
{
   @Override
   public boolean handles(TestClass testClass, AddonArchive addonArchive)
   {
      for (AddonDependencyEntry entry : addonArchive.getAddonDependencies())
      {
         if (entry.isFurnaceContainer())
         {
            return entry.getName().endsWith(":cdi");
         }
      }
      return false;
   }

   @Override
   public void register(TestClass testClass, AddonArchive addonArchive)
   {
      addonArchive.addBeansXML();
   }
}
