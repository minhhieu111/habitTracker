package com.example.habittracker.Service;

import com.example.habittracker.Domain.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${SPRING_MAIL_USERNAME}")
    private String senderEmail;

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
        message.setFrom(senderEmail);
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
        message.setFrom(senderEmail);
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
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body.toString());

        mailSender.send(message);
    }

    @Async
    public void sendEmailReceiveAchievement(UserAchievement userAchievement) {
        String recipientEmail = userAchievement.getUser().getEmail();

        String subject = "Bạn đã nhận được Thành tựu mới!";
        StringBuilder body = new StringBuilder();

        body.append("Chào ").append(userAchievement.getUser().getUserName()).append(",\n\n");
        body.append("Tuyệt vời!\n");
        body.append("Chúng tôi rất vui mừng thông báo rằng bạn vừa mở khóa một thành tựu mới: **\"").append(userAchievement.getAchievement().getTitle()).append("\"**.\n\n");

        // Thêm phần mô tả về thành tựu (nếu Achievement có trường description)
        // if (achievement.getDescription() != null && !achievement.getDescription().isEmpty()) {
        //     body.append(achievement.getDescription()).append("\n\n");
        // }

        body.append("Để vinh danh thành tích xuất sắc này, bạn nhận được:\n");
        if (userAchievement.getAchievement().getChallengeBonus() != null && userAchievement.getAchievement().getChallengeBonus() > 0) {
            body.append("- Thêm ").append(userAchievement.getAchievement().getChallengeBonus()).append(" lượt tạo thử thách mới (challenge limit).\n");
        }
        if (userAchievement.getAchievement().getTaskBonus() != null && userAchievement.getAchievement().getTaskBonus() > 0) {
            body.append("- Thêm ").append(userAchievement.getAchievement().getTaskBonus()).append(" lượt tạo nhiệm vụ mới (task limit).\n");
        }

        if (userAchievement.getAchievement().getCoinBonus() != null && userAchievement.getAchievement().getCoinBonus() > 0) {
            body.append("- ").append(userAchievement.getAchievement().getCoinBonus()).append(" xu vào tài khoản của bạn.\n");
        }

        body.append("\n");
        body.append("Hãy tiếp tục hành trình rèn luyện thói quen và chinh phục những đỉnh cao mới nhé!\n\n");
        body.append("Trân trọng,\n");
        body.append("Đội ngũ ứng dụng Bebet");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body.toString());
    }

    public void sendWelcomeEmail(User newUser) {

        String recipientEmail = newUser.getEmail();
        String subject = "Chào mừng bạn đến với Bebet - Hành trình rèn luyện thói quen của bạn!";

        StringBuilder body = new StringBuilder();
        body.append("Chào ").append(newUser.getUserName()).append(",\n\n");
        body.append("Chào mừng bạn đã gia nhập cộng đồng Bebet! Chúng tôi rất vui mừng khi bạn quyết định cùng chúng tôi xây dựng những thói quen tốt và chinh phục các thử thách.\n\n");

        body.append("Tại Bebet, bạn có thể:\n");
        body.append("- Tạo và theo dõi các nhiệm vụ hàng ngày (Dailies).\n");
        body.append("- Xây dựng và duy trì những thói quen tích cực (Habits) hoặc từ bỏ những thói quen tiêu cực.\n");
        body.append("- Quản lý các công việc cần làm (To-Dos).\n");
        body.append("- Tham gia vào các thử thách thú vị và kết nối với cộng đồng.\n");
        body.append("- Ghi lại hành trình của bạn với nhật ký cá nhân.\n\n");

        body.append("Hãy bắt đầu hành trình của bạn ngay hôm nay bằng cách tạo thử thách đầu tiên hoặc thêm một thói quen mới.\n");
        body.append("Nếu có bất kỳ câu hỏi nào, đừng ngần ngại liên hệ với chúng tôi nhé!\n\n");

        body.append("Chúc bạn có những trải nghiệm tuyệt vời với Bebet!\n\n");
        body.append("Trân trọng,\n");
        body.append("Đội ngũ Bebet\n");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body.toString());
    }
}
