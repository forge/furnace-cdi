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
