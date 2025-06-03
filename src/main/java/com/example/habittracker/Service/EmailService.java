package com.example.habittracker.Service;

import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender1) {
        this.mailSender = mailSender1;
    }

    public void sendEmailCompleteChallenge(UserChallenge userChallenge){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String recipientEmail = userChallenge.getUser().getEmail();
        String emailSubject = "Chúc mừng bạn đã hoàn thành thử thách: " + userChallenge.getChallenge().getTitle();
        String emailBody = "Chào " + (userChallenge.getUser() != null ? userChallenge.getUser().getUserName() : "") + ",\n\n"
                + "Chúng tôi xin chúc mừng bạn đã **hoàn thành thử thách \"" + userChallenge.getChallenge().getTitle() + "\"** một cách xuất sắc!\n"
                + "Bạn đã trải qua một hành trình đáng nhớ từ ngày **" + userChallenge.getStartDate().format(dateFormatter) + "** đến ngày **" + userChallenge.getEndDate().format(dateFormatter) + "**.\n\n"
                + "Trong suốt thử thách, bạn đã đạt được những kết quả ấn tượng:\n"
                + "- Tổng số lần thực hiện thói quen thành công: **" + userChallenge.getTotalCompletedTasks() + "**\n"
                + "- Chuỗi thực hiện tốt nhất của bạn: **" + userChallenge.getBestStreak() + "** ngày liên tiếp\n"
                + "- Tổng số lần bỏ lỡ thói quen: **" + userChallenge.getSkippedTasks() + "**\n\n"
                + "Đây là một thành tựu đáng tự hào! Bạn có thể chia sẻ hành trình và kết quả này với bạn bè và cộng đồng.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ ứng dụng của bạn\n"
                + "Bebet";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bebet.habittracker@gmail.com");
        message.setTo(recipientEmail);
        message.setSubject(emailSubject);
        message.setText(emailBody);

        mailSender.send(message);

    }

    public void sendStreakLostNotification(UserChallenge userChallenge, long currentStreak) {

        String subject = "Thông báo: Chuỗi của thử thách \"" + userChallenge.getChallenge().getTitle() + "\" của bạn đã bị mất!";
        String body = "Chào bạn " + userChallenge.getUser().getUserName() + ",\n\n"
                + "Chúng tôi rất tiếc phải thông báo rằng chuỗi liên tiếp thực hiện thử thách **\"" + userChallenge.getChallenge().getTitle() + "\"** của bạn đã bị mất.\n\n"
                + "Bạn đã có một chuỗi ấn tượng là **" + currentStreak + "** ngày liên tiếp!\n"
                + "Đừng nản lòng nhé! Hãy bắt đầu lại từ hôm nay và xây dựng một chuỗi mới.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ ứng dụng của bạn\n"
                + "Bebet";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bebet.habittracker@gmail.com");
        message.setTo(userChallenge.getUser().getEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendEmailTaskUnComplete(List<UserDaily> userDailyUnCompleteList, List<UserHabit> userHabitUnCompleteList, User user){

    }
}
