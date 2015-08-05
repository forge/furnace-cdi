/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.weld;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ModuleScanResult
{
   private final ResourceLoader loader;
   private final List<String> resourceUrls;
   private final Collection<String> classes;

   public ModuleScanResult(ResourceLoader loader, List<String> discoveredResourceUrls,
            Collection<String> discoveredClasses)
   {
      this.loader = loader;
      this.resourceUrls = discoveredResourceUrls;
      this.classes = discoveredClasses;
   }

   public Collection<String> getDiscoveredClasses()
   {
      return classes;
   }

   public List<String> getDiscoveredResourceUrls()
   {
      return resourceUrls;
   }

   public ResourceLoader getResourceLoader()
   {
      return loader;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      if (resourceUrls != null)
      {
         Iterator<String> iterator = resourceUrls.iterator();
         while (iterator.hasNext())
         {
            String url = iterator.next();
            result.append(url).append("\n");
         }
      }
      if (classes != null)
      {
         result.append("\n");
         Iterator<String> iterator = classes.iterator();
         while (iterator.hasNext())
         {
            String type = iterator.next();
            result.append(type).append("\n");
         }
      }
      return result.toString();
   }
}
