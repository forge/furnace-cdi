/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.scoped;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TestScopedExtension implements Extension
{
   public void registerContext(@Observes final AfterBeanDiscovery event)
   {
      TestScopedContext context = new TestScopedContext();
      event.addContext(context);
   }

}
