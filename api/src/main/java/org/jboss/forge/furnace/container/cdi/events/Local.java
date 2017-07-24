/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.events;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.event.PostStartup;
import org.jboss.forge.furnace.event.PreShutdown;

/**
 * This qualifier should be used when you want to listen for {@link PostStartup} and {@link PreShutdown} events of the
 * {@link Addon} this observer is on.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.PARAMETER })
public @interface Local
{
   /**
    * Supports inline instantiation of the {@link Local} qualifier.
    */
   public static final class Literal extends AnnotationLiteral<Local> implements Local
   {
      public static final Literal INSTANCE = new Literal();

      private static final long serialVersionUID = 1L;

   }
}
