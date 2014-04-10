package org.jboss.forge.addon.mail.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.mail.Email;
import org.jboss.forge.addon.mail.EmailService;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

public class EmailSendCommand extends AbstractUICommand
{
   @Inject
   private UIInput<String> to;

   @Inject
   private UIInput<String> cc;

   @Inject
   private UIInput<String> bcc;

   @Inject
   @WithAttributes(label = "Subject")
   private UIInput<String> subject;

   @Inject
   @WithAttributes(label = "Message Body", type = InputType.TEXTAREA)
   private UIInput<String> message;

   @Inject
   private EmailService mailer;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(EmailSendCommand.class).name(context.getProvider().isGUI() ? "Send an Email Message" : "Email Send")
               .category(Categories.create("Communication", "Email"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(to).add(cc).add(bcc).add(subject).add(message);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      if (!(to.getValue() != null || cc.getValue() != null || bcc.getValue() != null))
      {
         validator.addValidationError(to, "Must send to at least one recipient.");
      }

      if (subject.getValue() == null)
      {
         validator.addValidationWarning(subject, "Sending email with blank subject.");
      }

   }

   @Override
   public Result execute(UIExecutionContext context)
   {
      try
      {
         mailer.send(Email.message()
                  .to(to.getValue())
                  .cc(cc.getValue())
                  .bcc(bcc.getValue())
                  .subject(subject.getValue())
                  .body(message.getValue()));
         return Results.success("Email sent.");
      }
      catch (Exception e)
      {
         return Results.fail("Failed to send Email.", e);
      }
   }
}