/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.impl;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a> 
 */
@SuppressWarnings("all")
final class ServiceLiteral implements Service
{
   private static int INSTANCE_COUNT = 0;

   private final int id;

   public ServiceLiteral()
   {
      this.id = uniqueId();
   }

   @Override
   public Class<? extends Annotation> annotationType()
   {
      return Service.class;
   }

   @Override
   public int id()
   {
      return id;
   }

   public static int uniqueId()
   {
      return INSTANCE_COUNT++;
   }
}