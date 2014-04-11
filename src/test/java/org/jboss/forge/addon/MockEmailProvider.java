package org.jboss.forge.addon;

import javax.inject.Singleton;

import org.jboss.forge.addon.mail.Email;
import org.jboss.forge.addon.mail.spi.EmailProvider;

@Singleton
public class MockEmailProvider implements EmailProvider
{
   private Email email;

   @Override
   public void send(Email email)
   {
      System.out.println("Sent email: " + email);
      this.email = email;
   }

   public Email getEmail()
   {
      return email;
   }
}
