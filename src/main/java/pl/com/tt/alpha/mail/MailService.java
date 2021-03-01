package pl.com.tt.alpha.mail;

import pl.com.tt.alpha.user.domain.UserEntity;

public interface MailService {

	void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

	void sendEmailFromTemplate(UserEntity user, String templateName, String titleKey);

	void sendActivationEmail(UserEntity user);

	void sendPasswordResetMail(UserEntity user);

}
