package at.qe.sepm.skeleton.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for sending emails
 *
 * @author Johannes Spies
 */
@Component
@Scope("application")
public class MailSenderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MailSender mailSender;

    @Autowired
    public MailSenderService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Creates a mail object an sends an email to the given address
     * @param address
     * @param subject
     * @param content
     */
    public void sendUserMail(String address, String subject, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("Quizconnect <mail@quizconnect.rocks>");
        mail.setTo(address);
        mail.setSubject(subject);
        mail.setText(content);
        logger.info("sending user mail");

        ExecutorService executor = Executors.newWorkStealingPool();
        executor.execute(() -> mailSender.send(mail));
    }

}
