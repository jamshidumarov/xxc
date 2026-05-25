package dzamsheed.uz.xxcadminbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Getter
public class BotConfig {

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Value("${bot.admin-chat-ids}")
    private String adminChatIdsRaw;

    public Set<Long> getAdminChatIds() {
        return Arrays.stream(adminChatIdsRaw.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }
}
