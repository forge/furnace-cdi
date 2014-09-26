/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.weld;

import org.jboss.forge.furnace.container.cdi.impl.PerformanceTunedBootstrapConfiguration;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.bootstrap.api.helpers.TCCLSingletonProvider;
import org.jboss.weld.bootstrap.spi.BootstrapConfiguration;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ModularWeld extends Weld
{
   static
   {
      /*
       * This must happen once per JVM
       */
      SingletonProvider.reset();
      SingletonProvider.initialize(new TCCLSingletonProvider());
   }

   private final ModuleScanResult scanResult;

   public ModularWeld(ModuleScanResult scanResult)
   {
      this.scanResult = scanResult;
   }

   @Override
   protected Deployment createDeployment(ResourceLoader loader, CDI11Bootstrap bootstrap)
   {
      Deployment deployment = super.createDeployment(scanResult.getResourceLoader(), bootstrap);
      deployment.getServices().add(BootstrapConfiguration.class, new PerformanceTunedBootstrapConfiguration());
      return deployment;
   }
}
