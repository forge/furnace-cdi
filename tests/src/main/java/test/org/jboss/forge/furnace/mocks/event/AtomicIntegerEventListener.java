/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.mocks.event;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.event.Observes;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AtomicIntegerEventListener
{
   public void handleAtomicInteger(@Observes AtomicInteger event)
   {
      event.incrementAndGet();
   }
}
