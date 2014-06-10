/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.mocks;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PlainBean implements PlainInterface
{
   private final String value;

   public PlainBean()
   {
      this.value = null;
   }

   public PlainBean(String value)
   {
      this.value = value;
   }

   @Override
   public String getValue()
   {
      return this.value;
   }

}
