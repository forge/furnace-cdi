/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.mocks.services;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class NonService
{
   private boolean performObserved;
   private boolean preShutdownObserved;

   public void perform(@Observes PostStartup event) throws IOException
   {
      performObserved = true;
   }

   public void preShutdown(@Observes PreShutdown event)
   {
      preShutdownObserved = true;
   }

   public boolean isPerformObserved()
   {
      return performObserved;
   }

   public boolean isPreShutdownObserved()
   {
      return preShutdownObserved;
   }
}
