/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.harness;

import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AutomaticAddonDependenciesTest
{
    private static final String CONTAINER_CDI_DEP = "org.jboss.forge.furnace.container:cdi";

    @Deployment
    @AddonDependencies
    public static AddonArchive getDeployment() throws Exception
    {
        AddonArchive archive = ShrinkWrap.create(AddonArchive.class).addBeansXML();
        return archive;
    }

    @Inject
    private Furnace furnace;

    @Inject
    private Addon addon;

    @Test
    public void testDependenciesAreAutomaticallyDeployedAndAssigned()
    {
        Assert.assertNotNull(furnace);
        Set<org.jboss.forge.furnace.addons.AddonDependency> dependencies = addon.getDependencies();
        Assert.assertEquals(1, dependencies.size());
        Iterator<org.jboss.forge.furnace.addons.AddonDependency> iterator = dependencies.iterator();
        Assert.assertEquals(CONTAINER_CDI_DEP, iterator.next().getDependency().getId().getName());
    }
}
