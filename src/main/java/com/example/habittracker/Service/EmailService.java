package com.example.habittracker.Service;

import com.example.habittracker.Domain.User;
import com.example.habittracker.Domain.UserChallenge;
import com.example.habittracker.Domain.UserDaily;
import com.example.habittracker.Domain.UserHabit;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                + "Chúng tôi xin chúc mừng bạn đã hoàn thành thử thách \"" + userChallenge.getChallenge().getTitle() + "\" một cách xuất sắc!\n"
                + "Bạn đã trải qua một hành trình đáng nhớ từ ngày " + userChallenge.getStartDate().format(dateFormatter) + " đến ngày " + userChallenge.getEndDate().format(dateFormatter) + ".\n\n"
                + "Trong suốt thử thách, bạn đã đạt được những kết quả ấn tượng:\n"
                + "- Tổng số lần thực hiện thói quen thành công: " + userChallenge.getTotalCompletedTasks() + "\n"
                + "- Chuỗi thực hiện tốt nhất của bạn: " + userChallenge.getBestStreak() + " ngày liên tiếp\n"
                + "- Tổng số lần bỏ lỡ thói quen: " + userChallenge.getSkippedTasks() + "\n\n"
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
                + "Chúng tôi rất tiếc phải thông báo rằng chuỗi liên tiếp thực hiện thử thách \"" + userChallenge.getChallenge().getTitle() + "\" của bạn đã bị mất.\n\n"
                + "Bạn đã có một chuỗi ấn tượng là " + currentStreak + " ngày liên tiếp!\n"
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
        if (userDailyUnCompleteList.isEmpty() && userHabitUnCompleteList.isEmpty()) {
            return;
        }
        String recipientEmail = user.getEmail();
        String userName = user.getUserName();

        // Kiểm tra nếu người dùng chưa đăng nhập hôm nay
//        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
//        if (user.getLastLogin() != null && user.getLastLogin().isAfter(todayStart)) {
//            return;
//        }

        // Tạo nội dung email
        String subject = "Thông báo: Bạn có các thói quen chưa hoàn thành!";
        StringBuilder body = new StringBuilder();
        body.append("Chào bạn ").append(userName).append(",\n\n")
                .append("Hôm nay sắp kết thúc, nhưng bạn vẫn chưa hoàn thành một số thói quen \n\n")
                .append("Danh sách các nhiệm vụ chưa hoàn thành:\n");

        if (!userDailyUnCompleteList.isEmpty()) {
            body.append("- Thói quen hàng ngày:\n");
            for (UserDaily userDaily : userDailyUnCompleteList) {
                body.append("  + ").append(userDaily.getDaily().getTitle()).append("\n");
            }
        }

        if (!userHabitUnCompleteList.isEmpty()) {
            body.append("- Thói quen:\n");
            for (UserHabit userHabit : userHabitUnCompleteList) {
                body.append("  + ").append(userHabit.getHabit().getTitle()).append("\n");
            }
        }

        body.append("\nHãy đăng nhập ngay để hoàn thành các nhiệm vụ này trước khi ngày kết thúc nhé!\n\n")
                .append("Trân trọng,\n")
                .append("Đội ngũ ứng dụng của bạn\n")
                .append("Bebet");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bebet.habittracker@gmail.com");
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body.toString());

        mailSender.send(message);
    }
}
