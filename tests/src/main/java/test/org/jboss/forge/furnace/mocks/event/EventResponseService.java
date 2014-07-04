/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.mocks.event;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

public class EventResponseService
{
   @Inject
   @Named("2")
   private Event<Object> response;

   public void observeFirst(@Observes @Named("1") Object event)
   {
      response.fire(new EventPayload3());
   }
}
