package org.jboss.forge.addon.mail.commands;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.ConfigurationFactory;
import org.jboss.forge.addon.mail.EmailService;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class EmailSetupCommand extends AbstractUICommand
{
   @Inject
   @WithAttributes(label = "Use SMTP Authentication?")
   private UIInput<Boolean> smtpAuth;

   @Inject
   @WithAttributes(label = "Username")
   private UIInput<String> smtpUsername;

   @Inject
   @WithAttributes(label = "Password", type = InputType.SECRET)
   private UIInput<String> smtpPassword;

   @Inject
   @WithAttributes(label = "Host")
   private UIInput<String> smtpHost;

   @Inject
   @WithAttributes(label = "Port")
   private UIInput<Integer> smtpPort;

   @Inject
   @WithAttributes(label = "Use TLS/SSL", description = "Use TLS/SSL Protocol")
   private UIInput<Boolean> smtpUseTlsSsl;

   @Inject
   private ConfigurationFactory configFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(EmailSetupCommand.class)
               .name(context.getProvider().isGUI() ? "Configure SMTP" : "Email SMTP Setup")
               .category(Categories.create("Communication", "Email"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Callable<Boolean> authEnabled = new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return smtpAuth.getValue();
         }
      };
      smtpUsername.setEnabled(authEnabled);
      smtpPassword.setEnabled(authEnabled);

      Configuration configuration = configFactory.getUserConfiguration();
      Configuration mailConfig = configuration.subset(EmailService.CONFIG_SUBSET_KEY);
      smtpAuth.setDefaultValue(mailConfig.getBoolean(EmailService.CONFIG_MAIL_SMTP_AUTH_ENABLED, false));
      smtpUsername.setDefaultValue(mailConfig.getString(EmailService.CONFIG_MAIL_SMTP_USER, ""));
      smtpPassword.setDefaultValue(mailConfig.getString(EmailService.CONFIG_MAIL_SMTP_PASSWORD, ""));
      smtpHost.setDefaultValue(mailConfig.getString(EmailService.CONFIG_MAIL_SMTP_HOST, "localhost"));
      smtpPort.setDefaultValue(mailConfig.getInteger(EmailService.CONFIG_MAIL_SMTP_PORT, 25));
      smtpUseTlsSsl.setDefaultValue(mailConfig.getBoolean(EmailService.CONFIG_MAIL_SMTP_STARTTLS_ENABLE, false));

      builder.add(smtpUsername).add(smtpPassword).add(smtpAuth).add(smtpHost).add(smtpPort).add(smtpUseTlsSsl);
   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      Configuration configuration = configFactory.getUserConfiguration();
      Configuration mailConfig = configuration.subset(EmailService.CONFIG_SUBSET_KEY);

      mailConfig.setProperty(EmailService.CONFIG_MAIL_SMTP_AUTH_ENABLED, smtpAuth.getValue());
      mailConfig.setProperty(EmailService.CONFIG_MAIL_SMTP_USER,
               smtpUsername.isEnabled() ? smtpUsername.getValue() : "");
      mailConfig.setProperty(EmailService.CONFIG_MAIL_SMTP_PASSWORD,
               smtpUsername.isEnabled() ? smtpPassword.getValue() : "");
      mailConfig.setProperty(EmailService.CONFIG_MAIL_SMTP_HOST, smtpHost.getValue());
      mailConfig.setProperty(EmailService.CONFIG_MAIL_SMTP_PORT, smtpPort.getValue());
      mailConfig.setProperty(EmailService.CONFIG_MAIL_SMTP_STARTTLS_ENABLE, smtpUseTlsSsl.getValue());

      return Results.success("SMTP configuration complete.");
   }
}