/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.mocks;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Priority(Integer.MAX_VALUE)
@Alternative
public class AlternativeServiceBean implements ServiceInterface
{
   @Override
   public Object invoke()
   {
      return "Alternative";
   }
}
