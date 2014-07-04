/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.mocks.services;

import javax.inject.Inject;

public class ConsumingService
{
   @Inject
   private PublishedService service;

   public String getMessage()
   {
      return "I am ConsumingService. Remote service says [" + service.getMessage() + "]";
   }

   public ClassLoader getClassLoader()
   {
      return getClass().getClassLoader();
   }

   public int getRemoteHashCode()
   {
      return service.hashCode();
   }
}
