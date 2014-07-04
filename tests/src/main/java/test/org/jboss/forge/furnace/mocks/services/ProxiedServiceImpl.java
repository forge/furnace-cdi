/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.mocks.services;

import javax.enterprise.inject.Vetoed;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class ProxiedServiceImpl extends ProxiedServiceBaseClass implements ProxiedService
{
   public ProxiedServiceImpl()
   {
      super(ProxiedServiceImpl.class);
   }

   @Override
   public boolean getValue()
   {
      return true;
   }
}
