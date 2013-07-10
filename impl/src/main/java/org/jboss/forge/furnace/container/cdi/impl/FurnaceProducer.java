package org.jboss.forge.furnace.container.cdi.impl;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.forge.furnace.Furnace;

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
