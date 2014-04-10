package org.jboss.forge.addon;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.mail.Email;
import org.jboss.forge.addon.mail.EmailService;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EmailServiceTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:mail"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.addon:mail"))
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));
      return archive;
   }

   @Inject
   private EmailService mailer;

   @Test
   public void testEmailService() throws Exception
   {
      mailer.send(Email.message()
               .to("lincolnbaxter@gmail.com")
               .from("me@me.com")
               .subject("Hey! It's me.")
               .body("Ok this is the real stuff."));
   }
}