/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.mocks.services;

import test.org.jboss.forge.furnace.mocks.PlainInterface;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProducedPlainBean implements PlainInterface
{
   private String value;

   public ProducedPlainBean(String value)
   {
      this.value = value;
   }

   @Override
   public String getValue()
   {
      return value;
   }

}
