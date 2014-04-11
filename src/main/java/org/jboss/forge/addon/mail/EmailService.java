package org.jboss.forge.addon.mail;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.addon.mail.spi.EmailProvider;
import org.jboss.forge.furnace.services.Imported;

public class EmailService
{
   private static final Logger log = Logger.getLogger(EmailService.class.getName());

   @Inject
   private Imported<EmailProvider> providers;

   public void send(Email email)
   {
      boolean sent = false;
      for (EmailProvider provider : providers)
      {
         try
         {
            provider.send(email);
            sent = true;
            break;
         }
         catch (Exception e)
         {
            log.log(Level.SEVERE, "Could not send email via provider [" + provider + "]", e);
         }
         finally
         {
            providers.release(provider);
         }
      }

      if (!sent)
      {
         throw new IllegalStateException("Could not send email. See log for details.");
      }
   }

}
