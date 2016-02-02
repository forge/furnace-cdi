/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.container.cdi.events;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InboundEvent
{
   private Object event;
   private Annotation[] qualifiers;

   public InboundEvent(Object event, Annotation[] qualifiers)
   {
      this.event = event;
      this.qualifiers = qualifiers;
   }

   public Object getEvent()
   {
      return event;
   }

   public Annotation[] getQualifiers()
   {
      return qualifiers;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((event == null) ? 0 : event.hashCode());
      result = prime * result + Arrays.hashCode(qualifiers);
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      InboundEvent other = (InboundEvent) obj;
      if (event == null)
      {
         if (other.event != null)
            return false;
      }
      else if (!event.equals(other.event))
         return false;
      if (!Arrays.equals(qualifiers, other.qualifiers))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "InboundEvent [event=" + event + ", qualifiers=" + Arrays.toString(qualifiers) + "]";
   }

}
