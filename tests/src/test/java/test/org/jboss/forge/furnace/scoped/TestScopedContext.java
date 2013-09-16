/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.furnace.scoped;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("unchecked")
public class TestScopedContext implements Context
{

   public static boolean ACTIVE = false;

   @Override
   public Class<? extends Annotation> getScope()
   {
      return TestScoped.class;
   }

   @Override
   public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext)
   {
      return (T) new TestScopedObject();
   }

   @Override
   public <T> T get(Contextual<T> contextual)
   {
      return (T) new TestScopedObject();
   }

   @Override
   public boolean isActive()
   {
      return ACTIVE;
   }
}
