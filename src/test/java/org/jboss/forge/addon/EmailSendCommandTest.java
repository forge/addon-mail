package org.jboss.forge.addon;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.mail.commands.EmailSendCommand;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EmailSendCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:mail"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addClass(MockEmailProvider.class)
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.addon:mail"))
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"))
               .addAsAddonDependencies(AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"));
      return archive;
   }

   @Inject
   private UITestHarness harness;

   @Inject
   private MockEmailProvider provider;

   @Test
   public void testEmailService() throws Exception
   {
      String TO = "lincolnbaxter@gmail.com, lincoln@ocpsoft.org";
      String SUBJECT = "Hey! It's me.";
      String BODY = "Ok this is the real stuff.";
      Assert.assertNull(provider.getEmail());

      CommandController controller = harness.createCommandController(EmailSendCommand.class);
      controller.initialize();
      Assert.assertFalse(controller.isValid());
      controller.setValueFor("to", TO);
      Assert.assertTrue(controller.isValid());
      controller.setValueFor("subject", SUBJECT);
      controller.setValueFor("body", BODY);
      Result result = controller.execute();
      Assert.assertFalse(result instanceof Failed);

      Assert.assertNotNull(provider.getEmail());
      Assert.assertTrue(provider.getEmail().getTo().contains("lincolnbaxter@gmail.com"));
      Assert.assertTrue(provider.getEmail().getTo().contains("lincoln@ocpsoft.org"));
      Assert.assertEquals(2, provider.getEmail().getTo().size());
      Assert.assertEquals(0, provider.getEmail().getCc().size());
      Assert.assertEquals(0, provider.getEmail().getBcc().size());
      Assert.assertEquals(provider.getEmail().getSubject(), SUBJECT);
      Assert.assertEquals(provider.getEmail().getBody(), BODY);
   }

}