/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.impl;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.forge.furnace.Furnace;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a> 
 */
@Singleton
public class FurnaceProducer
{
   private Furnace furnace;

   @Produces
   public Furnace getFurnace()
   {
      return furnace;
   }

   public void setFurnace(Furnace furnace)
   {
      this.furnace = furnace;
   }
}
