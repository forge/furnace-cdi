package test.org.jboss.forge.furnace.mocks;
/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


/**
 * Keep the class name. The Hashcode should be the same as BB
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class Aa
{
   public static void main(String[] args) throws Exception
   {
      System.out.println("test.Aa".hashCode());
      System.out.println("test.BB".hashCode());
   }
}
