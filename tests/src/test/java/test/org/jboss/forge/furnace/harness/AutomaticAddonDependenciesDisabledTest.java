/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.harness;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.arquillian.services.LocalServices;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AutomaticAddonDependenciesDisabledTest
{
    @Deployment
    @AddonDependencies(automatic = false)
    public static AddonArchive getDeployment() throws Exception
    {
        AddonArchive archive = ShrinkWrap.create(AddonArchive.class);
        archive.addAsLocalServices(AutomaticAddonDependenciesDisabledTest.class);
        return archive;
    }

    @Test
    public void testDependenciesAreAutomaticallyDeployedAndAssigned()
    {
        Addon addon = LocalServices.getAddon(getClass().getClassLoader());
        Set<org.jboss.forge.furnace.addons.AddonDependency> dependencies = addon.getDependencies();
        Assert.assertEquals(0, dependencies.size());
    }
}
