package org.jboss.forge.addon.mail.impl;

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
import org.jboss.forge.addon.mail.Email;
import org.jboss.forge.addon.mail.spi.EmailProvider;
import org.jboss.forge.furnace.util.Strings;

public class SmtpEmailProvider implements EmailProvider
{
   public static final String CONFIG_SUBSET_KEY = "smtp";
   public static final String CONFIG_MAIL_SMTP_PASSWORD = "password";
   public static final String CONFIG_MAIL_SMTP_USER = "user";
   public static final String CONFIG_MAIL_SMTP_PORT = "port";
   public static final String CONFIG_MAIL_SMTP_HOST = "host";
   public static final String CONFIG_MAIL_SMTP_STARTTLS = "starttls";
   public static final String CONFIG_MAIL_SMTP_AUTH_ENABLED = "auth";

   @Inject
   private ConfigurationFactory configFactory;

   @Override
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

         message.setSubject(email.getSubject() == null ? "" : email.getSubject());
         message.setText(email.getBody() == null ? "" : email.getBody());

         Transport.send(message);
      }
      catch (MessagingException e)
      {
         throw new RuntimeException(e);
      }
   }

   private Session getEmailSession()
   {
      final Configuration config = configFactory.getUserConfiguration().subset(CONFIG_SUBSET_KEY);

      Properties props = new Properties();
      if (config.containsKey(CONFIG_MAIL_SMTP_AUTH_ENABLED))
      {
         props.put("mail.smtp.auth", config.getProperty(CONFIG_MAIL_SMTP_AUTH_ENABLED));
      }
      if (config.containsKey(CONFIG_MAIL_SMTP_HOST))
      {
         props.put("mail.smtp.host", config.getProperty(CONFIG_MAIL_SMTP_HOST));
      }
      if (config.containsKey(CONFIG_MAIL_SMTP_PORT))
      {
         props.put("mail.smtp.port", config.getProperty(CONFIG_MAIL_SMTP_PORT));
      }
      if (config.containsKey(CONFIG_MAIL_SMTP_STARTTLS))
      {
         props.put("mail.smtp.starttls.enable", config.getProperty(CONFIG_MAIL_SMTP_STARTTLS));
      }

      javax.mail.Authenticator authenticator = new javax.mail.Authenticator()
      {
         protected PasswordAuthentication getPasswordAuthentication()
         {
            return new PasswordAuthentication(config.getString(CONFIG_MAIL_SMTP_USER),
                     config.getString(CONFIG_MAIL_SMTP_PASSWORD));
         }
      };

      Session session = Session.getInstance(props, authenticator);
      return session;
   }

}
