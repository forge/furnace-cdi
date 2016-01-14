/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.furnace.Furnace;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FurnaceProviderTest
{
   @Inject
   Furnace furnace;

   @Test
   public void testFurnaceInstance()
   {
      ClassLoader loader = getClass().getClassLoader();
      Assert.assertSame(furnace, Furnace.instance(loader));
   }
}
