/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.forge.furnace.container.cdi.events.CrossContainerObserverMethod;
import org.jboss.forge.furnace.container.cdi.events.EventManagerProducer;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ContainerBeanRegistrant implements Extension
{
   public void registerWeldSEBeans(@Observes BeforeBeanDiscovery event, BeanManager manager)
   {
      event.addAnnotatedType(manager.createAnnotatedType(AddonProducer.class), AddonProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(AddonRegistryProducer.class),
               AddonRegistryProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(AddonRepositoryProducer.class),
               AddonRepositoryProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(CrossContainerObserverMethod.class),
               CrossContainerObserverMethod.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(EventManagerProducer.class),
               EventManagerProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(FurnaceProducer.class), FurnaceProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(ImportedProducer.class), ImportedProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryProducer.class),
               ServiceRegistryProducer.class.getName());
      event.addAnnotatedType(manager.createAnnotatedType(WeldContainer.class), WeldContainer.class.getName());
   }
}
