/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.mocks.services;

import javax.enterprise.inject.Produces;

import test.org.jboss.forge.furnace.mocks.PlainBean;
import test.org.jboss.forge.furnace.mocks.PlainInterface;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProducesPlainInterfaceService
{
   @Produces
   public PlainInterface producePlainInterface()
   {
      return new PlainBean("Produced");
   }
}
