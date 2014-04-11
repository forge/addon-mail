package org.jboss.forge.addon.mail.spi;

import org.jboss.forge.addon.mail.Email;

public interface EmailProvider
{
   void send(Email email);
}
