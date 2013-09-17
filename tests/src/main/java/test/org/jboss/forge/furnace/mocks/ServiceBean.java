package test.org.jboss.forge.furnace.mocks;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServiceBean implements ServiceInterface
{
   @Override
   public Object invoke()
   {
      return "Yay!";
   }
}
