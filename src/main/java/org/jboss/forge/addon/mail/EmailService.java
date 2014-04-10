package org.jboss.forge.addon.mail;

import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.ConfigurationFactory;
import org.jboss.forge.furnace.util.Strings;

public class EmailService
{
   public static final String CONFIG_SUBSET_KEY = "org.jboss.forge.addon.mail";
   public static final String CONFIG_MAIL_SMTP_PASSWORD = "mail.smtp.password";
   public static final String CONFIG_MAIL_SMTP_USER = "mail.smtp.user";
   public static final String CONFIG_MAIL_SMTP_PORT = "mail.smtp.port";
   public static final String CONFIG_MAIL_SMTP_HOST = "mail.smtp.host";
   public static final String CONFIG_MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
   public static final String CONFIG_MAIL_SMTP_AUTH_ENABLED = "mail.smtp.auth";

   @Inject
   private ConfigurationFactory configFactory;

   public void send(Email email)
   {
      Session session = getEmailSession();

      try
      {
         Message message = new MimeMessage(session);

         for (String to : email.getTo())
         {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
         }
         for (String cc : email.getCc())
         {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
         }
         for (String bcc : email.getBcc())
         {
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
         }

         if (!Strings.isNullOrEmpty(email.getFrom()))
            message.setFrom(new InternetAddress(email.getFrom()));

         message.setSubject(email.getSubject());
         message.setText(email.getBody());

         Transport.send(message);
      }
      catch (MessagingException e)
      {
         throw new RuntimeException(e);
      }
   }

   private Session getEmailSession()
   {
      Configuration configuration = configFactory.getUserConfiguration();
      final Configuration mailConfig = configuration.subset(CONFIG_SUBSET_KEY);

      Properties props = new Properties();
      props.put(CONFIG_MAIL_SMTP_AUTH_ENABLED, mailConfig.getProperty(CONFIG_MAIL_SMTP_AUTH_ENABLED));
      props.put(CONFIG_MAIL_SMTP_STARTTLS_ENABLE, mailConfig.getProperty(CONFIG_MAIL_SMTP_STARTTLS_ENABLE));
      props.put(CONFIG_MAIL_SMTP_HOST, mailConfig.getProperty(CONFIG_MAIL_SMTP_HOST));
      props.put(CONFIG_MAIL_SMTP_PORT, mailConfig.getProperty(CONFIG_MAIL_SMTP_PORT));

      javax.mail.Authenticator authenticator = new javax.mail.Authenticator()
      {
         protected PasswordAuthentication getPasswordAuthentication()
         {
            return new PasswordAuthentication(mailConfig.getString(CONFIG_MAIL_SMTP_USER),
                     mailConfig.getString(CONFIG_MAIL_SMTP_PASSWORD));
         }
      };

      Session session = Session.getInstance(props, authenticator);
      return session;
   }
}