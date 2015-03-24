/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.org.jboss.forge.furnace.events;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.furnace.event.PostStartup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ContainerLifecycleEventObserver
{
   private boolean observedPerform;
   private Map<String, Integer> postStartupMap = new HashMap<>();

   public void perform(@Observes PostStartup event)
   {
      String addonName = event.getAddon().getId().getName().toString();
      if (postStartupMap.containsKey(addonName))
      {
         Integer myInt = postStartupMap.get(addonName);
         postStartupMap.put(addonName, myInt+1);
      }
      else
      {
         postStartupMap.put(addonName, 1);
      }
      this.observedPerform = true;
   }

   public boolean isObservedPerform()
   {
      return observedPerform;
   }

   public Map<String, Integer> getPostStartupMap()
   {
      return postStartupMap;
   }

   public int getSetSize()
   {
      return postStartupMap.size();
   }
}
