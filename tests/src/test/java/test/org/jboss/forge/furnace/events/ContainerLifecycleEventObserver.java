/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.events;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.furnace.event.PostStartup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ContainerLifecycleEventObserver
{
   private boolean observedPerform;

   public void perform(@Observes PostStartup event)
   {
      this.observedPerform = true;
   }

   public boolean isObservedPerform()
   {
      return observedPerform;
   }
}
