package org.jboss.forge.addon.mail;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.furnace.util.Assert;

public class Email
{
   private List<String> to = new ArrayList<>();
   private List<String> cc = new ArrayList<>();
   private List<String> bcc = new ArrayList<>();
   private String from;
   private String subject;
   private String body;

   protected Email()
   {
   }

   public List<String> getTo()
   {
      return to;
   }

   public Email to(String to)
   {
      if (to != null)
         this.to.add(to);
      return this;
   }

   public List<String> getCc()
   {
      return cc;
   }

   public Email cc(String cc)
   {
      if (cc != null)
         this.cc.add(cc);
      return this;
   }

   public List<String> getBcc()
   {
      return bcc;
   }

   public Email bcc(String bcc)
   {
      if (bcc != null)
         this.bcc.add(bcc);
      return this;
   }

   public String getFrom()
   {
      return from;
   }

   public Email from(String from)
   {
      Assert.notNull(from, "Sender must not be null");
      this.from = from;
      return this;
   }

   public String getSubject()
   {
      return subject;
   }

   public Email subject(String subject)
   {
      this.subject = subject;
      return this;
   }

   public String getBody()
   {
      return body;
   }

   public Email body(String body)
   {
      this.body = body;
      return this;
   }

   public static Email message()
   {
      return new Email();
   }

   public Email to(Iterable<String> to)
   {
      if (to != null)
      {
         for (String address : to)
         {
            this.to.add(address);
         }
      }
      return this;
   }

   public Email cc(Iterable<String> cc)
   {
      if (cc != null)
      {
         for (String address : cc)
         {
            this.cc.add(address);
         }
      }
      return this;
   }

   public Email bcc(Iterable<String> bcc)
   {
      if (bcc != null)
      {
         for (String address : bcc)
         {
            this.bcc.add(address);
         }
      }
      return this;
   }

   @Override
   public String toString()
   {
      return "Email [to=" + to + ", cc=" + cc + ", bcc=" + bcc + ", from=" + from + ", subject=" + subject + ", body="
               + body + "]";
   }

}
