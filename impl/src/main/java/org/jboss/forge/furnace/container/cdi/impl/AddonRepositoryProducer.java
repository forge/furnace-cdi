/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.impl;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Singleton;

import org.jboss.forge.furnace.repositories.AddonRepository;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a> 
 */
@Singleton
public class AddonRepositoryProducer
{
   private AddonRepository repository;

   @Produces
   @Singleton
   @Typed(AddonRepository.class)
   public AddonRepository produceGlobalAddonRepository()
   {
      return repository;
   }

   public void setRepository(AddonRepository repository)
   {
      this.repository = repository;
   }
}
