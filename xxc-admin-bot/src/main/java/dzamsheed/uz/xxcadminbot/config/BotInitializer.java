package dzamsheed.uz.xxcadminbot.config;

import dzamsheed.uz.xxcadminbot.bot.XxcAdminBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BotInitializer {

    private final XxcAdminBot bot;

    @PostConstruct
    public void start() throws Exception {
        TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
        app.registerBot(bot.getBotToken(), bot);
        log.info("Telegram bot registered: @{}", bot.getBotToken().split(":")[0]);
    }
}
