package dzamsheed.uz.xxcadminbot.bot;

import dzamsheed.uz.xxcadminbot.config.BotConfig;
import dzamsheed.uz.xxcadminbot.domain.entity.*;
import dzamsheed.uz.xxcadminbot.repository.*;
import dzamsheed.uz.xxcadminbot.service.ImageUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class XxcAdminBot implements LongPollingUpdateConsumer {

    private static final int PAGE_SIZE = 5;
    private static final DateTimeFormatter FMT = DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.of("Asia/Tashkent"));

    private static final List<String> CATEGORIES =
            List.of("LEPKA", "TYAGA", "KERAMIKA", "SUVENIR", "KULOLCHILIK", "BUYURTMA");
    private static final List<String> AVAILABILITIES =
            List.of("AVAILABLE", "ORDER", "SOLD_OUT");

    private final BotConfig botConfig;
    private final ContactBotRepository contactRepo;
    private final ProductBotRepository productRepo;
    private final PortfolioBotRepository portfolioRepo;
    private final ProductImageBotRepository productImageRepo;
    private final PortfolioImageBotRepository portfolioImageRepo;
    private final ImageUploadService imageUploadService;
    private final TelegramClient telegramClient;

    // per-user state
    private final Map<Long, BotState> states = new HashMap<>();
    private final Map<Long, Map<String, String>> drafts = new HashMap<>();  // temp data while adding
    private final Map<Long, String> editTarget = new HashMap<>();           // UUID of item being edited

    public XxcAdminBot(BotConfig botConfig,
                       ContactBotRepository contactRepo,
                       ProductBotRepository productRepo,
                       PortfolioBotRepository portfolioRepo,
                       ProductImageBotRepository productImageRepo,
                       PortfolioImageBotRepository portfolioImageRepo,
                       ImageUploadService imageUploadService) {
        this.botConfig = botConfig;
        this.contactRepo = contactRepo;
        this.productRepo = productRepo;
        this.portfolioRepo = portfolioRepo;
        this.productImageRepo = productImageRepo;
        this.portfolioImageRepo = portfolioImageRepo;
        this.imageUploadService = imageUploadService;
        this.telegramClient = new OkHttpTelegramClient(botConfig.getToken());
    }

    public String getBotToken() { return botConfig.getToken(); }

    // ─── entry ───────────────────────────────────────────────────────────────

    @Override
    public void consume(List<Update> updates) { updates.forEach(this::process); }

    @Transactional
    public void process(Update u) {
        if (u.hasMessage() && u.getMessage().hasText()) {
            long chatId = u.getMessage().getChatId();
            if (!isAdmin(chatId)) { sendText(chatId, "⛔ Ruxsat yo'q."); return; }
            handleText(chatId, u.getMessage().getText().trim());
        } else if (u.hasMessage() && u.getMessage().hasPhoto()) {
            long chatId = u.getMessage().getChatId();
            if (!isAdmin(chatId)) return;
            handlePhoto(chatId, u.getMessage().getPhoto());
        } else if (u.hasCallbackQuery()) {
            long chatId = u.getCallbackQuery().getMessage().getChatId();
            if (!isAdmin(chatId)) return;
            handleCallback(chatId, u.getCallbackQuery().getData());
        }
    }

    // ─── text handler ────────────────────────────────────────────────────────

    private void handleText(long chatId, String text) {
        BotState state = states.getOrDefault(chatId, BotState.IDLE);

        // Global cancel
        if (text.equalsIgnoreCase("❌ Bekor qilish")) {
            resetState(chatId);
            sendMainMenu(chatId);
            return;
        }

        switch (state) {
            case PROD_ADD_NAME -> { draft(chatId).put("name", text); askProductCategory(chatId); states.put(chatId, BotState.PROD_ADD_CATEGORY); }
            case PROD_ADD_CATEGORY -> {
                if (!CATEGORIES.contains(text)) { sendText(chatId, "Kategoriyani ro'yxatdan tanlang."); return; }
                draft(chatId).put("category", text); askProductPrice(chatId); states.put(chatId, BotState.PROD_ADD_PRICE);
            }
            case PROD_ADD_PRICE -> {
                if (text.equals("⏭ O'tkazib yuborish")) { draft(chatId).put("price", ""); askProductAvailability(chatId); states.put(chatId, BotState.PROD_ADD_AVAILABILITY); return; }
                try { Integer.parseInt(text.replace(" ", "")); draft(chatId).put("price", text.replace(" ", "")); askProductAvailability(chatId); states.put(chatId, BotState.PROD_ADD_AVAILABILITY); }
                catch (NumberFormatException e) { sendText(chatId, "Faqat raqam kiriting yoki o'tkazib yuboring."); }
            }
            case PROD_ADD_AVAILABILITY -> {
                if (!AVAILABILITIES.contains(text)) { sendText(chatId, "Holati ro'yxatdan tanlang."); return; }
                saveNewProduct(chatId, text);
            }
            case PROD_EDIT_NAME -> { editProductField(chatId, "name", text); }
            case PROD_EDIT_PRICE -> {
                try { editProductField(chatId, "price", text.replace(" ", "")); }
                catch (NumberFormatException e) { sendText(chatId, "Faqat raqam kiriting."); }
            }
            case PROD_EDIT_CATEGORY -> {
                if (!CATEGORIES.contains(text)) { sendText(chatId, "Kategoriyani ro'yxatdan tanlang."); return; }
                editProductField(chatId, "category", text);
            }
            case PROD_EDIT_AVAILABILITY -> {
                if (!AVAILABILITIES.contains(text)) { sendText(chatId, "Holati ro'yxatdan tanlang."); return; }
                editProductField(chatId, "availability", text);
            }
            case PORT_ADD_TITLE -> { draft(chatId).put("title", text); askPortfolioCategory(chatId); states.put(chatId, BotState.PORT_ADD_CATEGORY); }
            case PORT_ADD_CATEGORY -> {
                if (!CATEGORIES.contains(text)) { sendText(chatId, "Kategoriyani ro'yxatdan tanlang."); return; }
                draft(chatId).put("category", text); sendTextWithCancel(chatId, "📅 Yilni kiriting (masalan: 2024) yoki o'tkazib yuboring:", true); states.put(chatId, BotState.PORT_ADD_YEAR);
            }
            case PORT_ADD_YEAR -> {
                if (text.equals("⏭ O'tkazib yuborish")) { draft(chatId).put("year", ""); saveNewPortfolio(chatId); return; }
                try { Integer.parseInt(text); draft(chatId).put("year", text); saveNewPortfolio(chatId); }
                catch (NumberFormatException e) { sendText(chatId, "Faqat raqam kiriting yoki o'tkazib yuboring."); }
            }
            case PORT_EDIT_TITLE -> editPortfolioField(chatId, "title", text);
            case PORT_EDIT_CATEGORY -> {
                if (!CATEGORIES.contains(text)) { sendText(chatId, "Kategoriyani ro'yxatdan tanlang."); return; }
                editPortfolioField(chatId, "category", text);
            }
            case PORT_EDIT_YEAR -> {
                try { editPortfolioField(chatId, "year", text); }
                catch (NumberFormatException e) { sendText(chatId, "Faqat raqam kiriting."); }
            }
            case PROD_ADD_IMAGES -> {
                if (text.equals("✅ Tayyor")) {
                    UUID id = UUID.fromString(editTarget.getOrDefault(chatId, ""));
                    long count = productImageRepo.countByProductId(id);
                    resetState(chatId);
                    sendMainMenu(chatId);
                    sendText(chatId, "✅ Mahsulot saqlandi. Rasmlar: " + count + " ta.");
                }
            }
            case PORT_ADD_IMAGES -> {
                if (text.equals("✅ Tayyor")) {
                    UUID id = UUID.fromString(editTarget.getOrDefault(chatId, ""));
                    long count = portfolioImageRepo.countByPortfolioId(id);
                    resetState(chatId);
                    sendMainMenu(chatId);
                    sendText(chatId, "✅ Ish saqlandi. Rasmlar: " + count + " ta.");
                }
            }
            default -> handleCommand(chatId, text);
        }
    }

    private void handleCommand(long chatId, String text) {
        switch (text) {
            case "/start", "/help", "🏠 Asosiy menyu" -> sendMainMenu(chatId);
            case "/products", "📦 Mahsulotlar" -> sendProductList(chatId, 0);
            case "/portfolio", "🖼 Qilingan ishlar" -> sendPortfolioList(chatId, 0);
            case "/contacts", "📨 Murojaatlar" -> sendContactList(chatId, 0);
            case "/stats", "📊 Statistika" -> sendStats(chatId);
            case "➕ Mahsulot qo'shish" -> startAddProduct(chatId);
            case "➕ Ish qo'shish" -> startAddPortfolio(chatId);
            default -> sendMainMenu(chatId);
        }
    }

    // ─── callback handler ────────────────────────────────────────────────────

    private void handleCallback(long chatId, String data) {
        String[] p = data.split(":", 3);
        String action = p[0];
        switch (action) {
            case "prod_page"   -> sendProductList(chatId, Integer.parseInt(p[1]));
            case "prod_view"   -> sendProductDetail(chatId, UUID.fromString(p[1]));
            case "prod_active" -> toggleProductActive(chatId, UUID.fromString(p[1]));
            case "prod_delete" -> confirmDeleteProduct(chatId, UUID.fromString(p[1]));
            case "prod_del_ok" -> deleteProduct(chatId, UUID.fromString(p[1]));
            case "prod_edit"   -> sendProductEditMenu(chatId, UUID.fromString(p[1]));
            case "prod_edit_name" -> { editTarget.put(chatId, p[1]); sendTextWithCancel(chatId, "Yangi nomi (UZ):", false); states.put(chatId, BotState.PROD_EDIT_NAME); }
            case "prod_edit_price" -> { editTarget.put(chatId, p[1]); sendTextWithCancel(chatId, "Yangi narx (UZS):", false); states.put(chatId, BotState.PROD_EDIT_PRICE); }
            case "prod_edit_cat"  -> { editTarget.put(chatId, p[1]); askProductCategory(chatId); states.put(chatId, BotState.PROD_EDIT_CATEGORY); editTarget.put(chatId, p[1]); }
            case "prod_edit_avail"-> { editTarget.put(chatId, p[1]); askProductAvailability(chatId); states.put(chatId, BotState.PROD_EDIT_AVAILABILITY); editTarget.put(chatId, p[1]); }

            case "port_page"   -> sendPortfolioList(chatId, Integer.parseInt(p[1]));
            case "port_view"   -> sendPortfolioDetail(chatId, UUID.fromString(p[1]));
            case "port_active" -> togglePortfolioActive(chatId, UUID.fromString(p[1]));
            case "port_featured"-> togglePortfolioFeatured(chatId, UUID.fromString(p[1]));
            case "port_delete" -> confirmDeletePortfolio(chatId, UUID.fromString(p[1]));
            case "port_del_ok" -> deletePortfolio(chatId, UUID.fromString(p[1]));
            case "port_edit"   -> sendPortfolioEditMenu(chatId, UUID.fromString(p[1]));
            case "port_edit_title"-> { editTarget.put(chatId, p[1]); sendTextWithCancel(chatId, "Yangi sarlavha (UZ):", false); states.put(chatId, BotState.PORT_EDIT_TITLE); }
            case "port_edit_cat"  -> { editTarget.put(chatId, p[1]); askPortfolioCategory(chatId); states.put(chatId, BotState.PORT_EDIT_CATEGORY); editTarget.put(chatId, p[1]); }
            case "port_edit_year" -> { editTarget.put(chatId, p[1]); sendTextWithCancel(chatId, "Yangi yil:", false); states.put(chatId, BotState.PORT_EDIT_YEAR); }
            case "prod_images"    -> startAddImages(chatId, p[1], BotState.PROD_ADD_IMAGES, "mahsulot");
            case "port_images"    -> startAddImages(chatId, p[1], BotState.PORT_ADD_IMAGES, "ish");

            case "contact_page"       -> sendContactList(chatId, Integer.parseInt(p[1]));
            case "contact_view"       -> sendContactDetail(chatId, UUID.fromString(p[1]));
            case "contact_viewed"     -> updateContactStatus(chatId, UUID.fromString(p[1]), "VIEWED");
            case "contact_processing" -> updateContactStatus(chatId, UUID.fromString(p[1]), "PROCESSING");
            case "contact_completed"  -> updateContactStatus(chatId, UUID.fromString(p[1]), "COMPLETED");
        }
    }

    // ─── PRODUCT: list ───────────────────────────────────────────────────────

    private void sendProductList(long chatId, int page) {
        List<Product> products = productRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, PAGE_SIZE));
        long total = productRepo.count();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        if (products.isEmpty()) { sendText(chatId, "📦 Mahsulotlar yo'q."); return; }

        StringBuilder sb = new StringBuilder(String.format("📦 *Mahsulotlar* (%d ta):\n\n", total));
        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (Product pr : products) {
            String status = Boolean.TRUE.equals(pr.getIsActive()) ? "✅" : "❌";
            String price = pr.getPrice() != null ? formatPrice(pr.getPrice()) + " UZS" : "—";
            sb.append(String.format("%s *%s*\n   %s | %s\n\n",
                    status, escape(pr.getNameUz()), pr.getCategory(), price));
            rows.add(new InlineKeyboardRow(
                    btn("📋 " + pr.getNameUz(), "prod_view:" + pr.getId())));
        }

        List<InlineKeyboardButton> nav = new ArrayList<>();
        if (page > 0) nav.add(btn("⬅", "prod_page:" + (page - 1)));
        if (page < totalPages - 1) nav.add(btn("➡", "prod_page:" + (page + 1)));
        if (!nav.isEmpty()) rows.add(new InlineKeyboardRow(nav));

        send(chatId, sb.toString(), InlineKeyboardMarkup.builder().keyboard(rows).build());
    }

    // ─── PRODUCT: detail ─────────────────────────────────────────────────────

    private void sendProductDetail(long chatId, UUID id) {
        productRepo.findById(id).ifPresent(pr -> {
            String activeLabel = Boolean.TRUE.equals(pr.getIsActive()) ? "✅ Faol" : "❌ Nofaol";
            String avail = switch (pr.getAvailability() != null ? pr.getAvailability() : "") {
                case "AVAILABLE" -> "Mavjud";
                case "ORDER"     -> "Buyurtma asosida";
                case "SOLD_OUT"  -> "Sotilgan";
                default -> "—";
            };
            String text = String.format("""
                    📦 *%s*

                    📂 Kategoriya: %s
                    💰 Narx: %s
                    🏷 Holati: %s
                    📌 Status: %s
                    📅 Qo'shilgan: %s
                    """,
                    escape(pr.getNameUz()),
                    pr.getCategory(),
                    pr.getPrice() != null ? formatPrice(pr.getPrice()) + " UZS" : "—",
                    avail,
                    activeLabel,
                    pr.getCreatedAt() != null ? FMT.format(pr.getCreatedAt()) : "—");

            String sid = id.toString();
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✏️ Tahrirlash", "prod_edit:" + sid),
                            btn(Boolean.TRUE.equals(pr.getIsActive()) ? "🔴 O'chirish" : "🟢 Yoqish", "prod_active:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("📸 Rasm qo'shish", "prod_images:" + sid),
                            btn("🗑 O'chirish", "prod_delete:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("⬅ Orqaga", "prod_page:0")))
                    .build();
            send(chatId, text, kb);
        });
    }

    // ─── PRODUCT: add ────────────────────────────────────────────────────────

    private void startAddProduct(long chatId) {
        drafts.put(chatId, new HashMap<>());
        sendTextWithCancel(chatId, "📦 Yangi mahsulot nomi (UZ):", false);
        states.put(chatId, BotState.PROD_ADD_NAME);
    }

    private void askProductCategory(long chatId) {
        sendKeyboard(chatId, "📂 Kategoriyani tanlang:", CATEGORIES, true);
    }

    private void askProductPrice(long chatId) {
        sendSkippableText(chatId, "💰 Narxni kiriting (UZS):");
    }

    private void askProductAvailability(long chatId) {
        sendKeyboard(chatId, "🏷 Mavjudligini tanlang:", AVAILABILITIES, true);
    }

    private void saveNewProduct(long chatId, String availability) {
        Map<String, String> d = draft(chatId);
        Product p = new Product();
        p.setId(UUID.randomUUID());
        p.setNameUz(d.get("name"));
        p.setCategory(d.get("category"));
        p.setAvailability(availability);
        p.setIsActive(true);
        p.setIsCustomOrder(false);
        p.setCurrency("UZS");
        String priceStr = d.getOrDefault("price", "");
        if (!priceStr.isBlank()) p.setPrice(Integer.parseInt(priceStr));
        productRepo.save(p);
        drafts.remove(chatId);
        editTarget.put(chatId, p.getId().toString());
        states.put(chatId, BotState.PROD_ADD_IMAGES);
        sendText(chatId, "✅ Mahsulot qo'shildi: *" + escape(p.getNameUz()) + "*");
        sendImagePrompt(chatId);
    }

    // ─── PRODUCT: edit ───────────────────────────────────────────────────────

    private void sendProductEditMenu(long chatId, UUID id) {
        String sid = id.toString();
        productRepo.findById(id).ifPresent(pr -> {
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✏️ Nomi", "prod_edit_name:" + sid),
                            btn("💰 Narxi", "prod_edit_price:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("📂 Kategoriya", "prod_edit_cat:" + sid),
                            btn("🏷 Holati", "prod_edit_avail:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("⬅ Orqaga", "prod_view:" + sid)))
                    .build();
            send(chatId, "✏️ *" + escape(pr.getNameUz()) + "* — nimani o'zgartirasiz?", kb);
        });
    }

    private void editProductField(long chatId, String field, String value) {
        UUID id = UUID.fromString(editTarget.getOrDefault(chatId, ""));
        switch (field) {
            case "name"         -> productRepo.updateNameUz(id, value);
            case "price"        -> productRepo.updatePrice(id, Integer.parseInt(value));
            case "category"     -> productRepo.updateCategory(id, value);
            case "availability" -> productRepo.updateAvailability(id, value);
        }
        resetState(chatId);
        sendText(chatId, "✅ Yangilandi.");
        sendProductDetail(chatId, id);
    }

    // ─── PRODUCT: toggle / delete ────────────────────────────────────────────

    private void toggleProductActive(long chatId, UUID id) {
        productRepo.findById(id).ifPresent(pr -> {
            boolean newActive = !Boolean.TRUE.equals(pr.getIsActive());
            productRepo.updateActive(id, newActive);
            sendText(chatId, newActive ? "🟢 Mahsulot yoqildi." : "🔴 Mahsulot o'chirildi.");
            sendProductDetail(chatId, id);
        });
    }

    private void confirmDeleteProduct(long chatId, UUID id) {
        productRepo.findById(id).ifPresent(pr -> {
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✅ Ha, o'chir", "prod_del_ok:" + id),
                            btn("❌ Yo'q", "prod_view:" + id)))
                    .build();
            send(chatId, "🗑 *" + escape(pr.getNameUz()) + "* ni o'chirishni tasdiqlaysizmi?", kb);
        });
    }

    private void deleteProduct(long chatId, UUID id) {
        productRepo.deleteById(id);
        sendText(chatId, "🗑 Mahsulot o'chirildi.");
        sendProductList(chatId, 0);
    }

    // ─── PORTFOLIO: list ─────────────────────────────────────────────────────

    private void sendPortfolioList(long chatId, int page) {
        List<PortfolioItem> items = portfolioRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, PAGE_SIZE));
        long total = portfolioRepo.count();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        if (items.isEmpty()) { sendText(chatId, "🖼 Qilingan ishlar yo'q."); return; }

        StringBuilder sb = new StringBuilder(String.format("🖼 *Qilingan ishlar* (%d ta):\n\n", total));
        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (PortfolioItem it : items) {
            String status = Boolean.TRUE.equals(it.getIsActive()) ? "✅" : "❌";
            String featured = Boolean.TRUE.equals(it.getIsFeatured()) ? "⭐" : "";
            sb.append(String.format("%s%s *%s* | %s%s\n\n",
                    status, featured, escape(it.getTitleUz()),
                    it.getCategory(), it.getYear() != null ? " | " + it.getYear() : ""));
            rows.add(new InlineKeyboardRow(btn("📋 " + it.getTitleUz(), "port_view:" + it.getId())));
        }

        List<InlineKeyboardButton> nav = new ArrayList<>();
        if (page > 0) nav.add(btn("⬅", "port_page:" + (page - 1)));
        if (page < totalPages - 1) nav.add(btn("➡", "port_page:" + (page + 1)));
        if (!nav.isEmpty()) rows.add(new InlineKeyboardRow(nav));

        send(chatId, sb.toString(), InlineKeyboardMarkup.builder().keyboard(rows).build());
    }

    // ─── PORTFOLIO: detail ───────────────────────────────────────────────────

    private void sendPortfolioDetail(long chatId, UUID id) {
        portfolioRepo.findById(id).ifPresent(it -> {
            String activeLabel = Boolean.TRUE.equals(it.getIsActive()) ? "✅ Faol" : "❌ Nofaol";
            String featuredLabel = Boolean.TRUE.equals(it.getIsFeatured()) ? "⭐ Ha" : "Yo'q";
            String text = String.format("""
                    🖼 *%s*

                    📂 Kategoriya: %s
                    📅 Yil: %s
                    ⭐ Featured: %s
                    📌 Status: %s
                    🗓 Qo'shilgan: %s
                    """,
                    escape(it.getTitleUz()),
                    it.getCategory(),
                    it.getYear() != null ? it.getYear() : "—",
                    featuredLabel,
                    activeLabel,
                    it.getCreatedAt() != null ? FMT.format(it.getCreatedAt()) : "—");

            String sid = id.toString();
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✏️ Tahrirlash", "port_edit:" + sid),
                            btn(Boolean.TRUE.equals(it.getIsActive()) ? "🔴 O'chirish" : "🟢 Yoqish", "port_active:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("📸 Rasm qo'shish", "port_images:" + sid),
                            btn(Boolean.TRUE.equals(it.getIsFeatured()) ? "⭐ Featured olib tashlash" : "⭐ Featured qilish", "port_featured:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("🗑 O'chirish", "port_delete:" + sid),
                            btn("⬅ Orqaga", "port_page:0")))
                    .build();
            send(chatId, text, kb);
        });
    }

    // ─── PORTFOLIO: add ──────────────────────────────────────────────────────

    private void startAddPortfolio(long chatId) {
        drafts.put(chatId, new HashMap<>());
        sendTextWithCancel(chatId, "🖼 Yangi ish sarlavhasi (UZ):", false);
        states.put(chatId, BotState.PORT_ADD_TITLE);
    }

    private void askPortfolioCategory(long chatId) {
        sendKeyboard(chatId, "📂 Kategoriyani tanlang:", CATEGORIES, true);
    }

    private void saveNewPortfolio(long chatId) {
        Map<String, String> d = draft(chatId);
        PortfolioItem it = new PortfolioItem();
        it.setId(UUID.randomUUID());
        it.setTitleUz(d.get("title"));
        it.setCategory(d.get("category"));
        it.setIsActive(true);
        it.setIsFeatured(false);
        String yearStr = d.getOrDefault("year", "");
        if (!yearStr.isBlank()) it.setYear(Integer.parseInt(yearStr));
        portfolioRepo.save(it);
        drafts.remove(chatId);
        editTarget.put(chatId, it.getId().toString());
        states.put(chatId, BotState.PORT_ADD_IMAGES);
        sendText(chatId, "✅ Ish qo'shildi: *" + escape(it.getTitleUz()) + "*");
        sendImagePrompt(chatId);
    }

    // ─── PORTFOLIO: edit ─────────────────────────────────────────────────────

    private void sendPortfolioEditMenu(long chatId, UUID id) {
        String sid = id.toString();
        portfolioRepo.findById(id).ifPresent(it -> {
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✏️ Sarlavha", "port_edit_title:" + sid),
                            btn("📂 Kategoriya", "port_edit_cat:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(
                            btn("📅 Yil", "port_edit_year:" + sid),
                            btn("⬅ Orqaga", "port_view:" + sid)))
                    .build();
            send(chatId, "✏️ *" + escape(it.getTitleUz()) + "* — nimani o'zgartirasiz?", kb);
        });
    }

    private void editPortfolioField(long chatId, String field, String value) {
        UUID id = UUID.fromString(editTarget.getOrDefault(chatId, ""));
        switch (field) {
            case "title"    -> portfolioRepo.updateTitle(id, value);
            case "category" -> portfolioRepo.updateCategory(id, value);
            case "year"     -> portfolioRepo.updateYear(id, Integer.parseInt(value));
        }
        resetState(chatId);
        sendText(chatId, "✅ Yangilandi.");
        sendPortfolioDetail(chatId, id);
    }

    // ─── PORTFOLIO: toggle / delete ──────────────────────────────────────────

    private void togglePortfolioActive(long chatId, UUID id) {
        portfolioRepo.findById(id).ifPresent(it -> {
            boolean newActive = !Boolean.TRUE.equals(it.getIsActive());
            portfolioRepo.updateActive(id, newActive);
            sendText(chatId, newActive ? "🟢 Yoqildi." : "🔴 O'chirildi.");
            sendPortfolioDetail(chatId, id);
        });
    }

    private void togglePortfolioFeatured(long chatId, UUID id) {
        portfolioRepo.findById(id).ifPresent(it -> {
            boolean newFeatured = !Boolean.TRUE.equals(it.getIsFeatured());
            portfolioRepo.updateFeatured(id, newFeatured);
            sendText(chatId, newFeatured ? "⭐ Featured qilindi." : "⭐ Featured olib tashlandi.");
            sendPortfolioDetail(chatId, id);
        });
    }

    private void confirmDeletePortfolio(long chatId, UUID id) {
        portfolioRepo.findById(id).ifPresent(it -> {
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✅ Ha, o'chir", "port_del_ok:" + id),
                            btn("❌ Yo'q", "port_view:" + id)))
                    .build();
            send(chatId, "🗑 *" + escape(it.getTitleUz()) + "* ni o'chirishni tasdiqlaysizmi?", kb);
        });
    }

    private void deletePortfolio(long chatId, UUID id) {
        portfolioRepo.deleteById(id);
        sendText(chatId, "🗑 Ish o'chirildi.");
        sendPortfolioList(chatId, 0);
    }

    // ─── CONTACTS ────────────────────────────────────────────────────────────

    private void sendContactList(long chatId, int page) {
        List<Contact> contacts = contactRepo.findByStatusOrderByCreatedAtDesc("NEW", PageRequest.of(page, PAGE_SIZE));
        long newCount = contactRepo.countByStatus("NEW");

        if (contacts.isEmpty()) { sendText(chatId, "📭 Yangi murojaatlar yo'q."); return; }

        StringBuilder sb = new StringBuilder(String.format("📨 *Yangi murojaatlar* (%d ta):\n\n", newCount));
        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (Contact c : contacts) {
            sb.append(String.format("👤 *%s* | 📞 %s\n", escape(c.getName()), c.getPhone()));
            rows.add(new InlineKeyboardRow(btn("📖 " + c.getName(), "contact_view:" + c.getId())));
        }

        long total = contactRepo.count();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        List<InlineKeyboardButton> nav = new ArrayList<>();
        if (page > 0) nav.add(btn("⬅", "contact_page:" + (page - 1)));
        if (page < totalPages - 1) nav.add(btn("➡", "contact_page:" + (page + 1)));
        if (!nav.isEmpty()) rows.add(new InlineKeyboardRow(nav));

        send(chatId, sb.toString(), InlineKeyboardMarkup.builder().keyboard(rows).build());
    }

    private void sendContactDetail(long chatId, UUID id) {
        contactRepo.findById(id).ifPresent(c -> {
            String typeUz = switch (c.getType() != null ? c.getType() : "") {
                case "PURCHASE"    -> "Mahsulot sotib olish";
                case "ORDER"       -> "Buyurtma berish";
                case "PARTNERSHIP" -> "Hamkorlik";
                default            -> "Boshqa";
            };
            String text = String.format("""
                    🔔 *Murojaat tafsilotlari*

                    👤 Ism: *%s*
                    📞 Telefon: *%s*
                    📧 Email: %s
                    📂 Turi: %s
                    💬 Xabar: %s
                    📅 Sana: %s
                    """,
                    escape(c.getName()), c.getPhone(),
                    c.getEmail() != null ? c.getEmail() : "—",
                    typeUz,
                    c.getMessage() != null ? escape(c.getMessage()) : "—",
                    c.getCreatedAt() != null ? FMT.format(c.getCreatedAt()) : "—");

            String sid = id.toString();
            InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                    .keyboardRow(new InlineKeyboardRow(
                            btn("✅ Ko'rildi",  "contact_viewed:" + sid),
                            btn("➡ Jarayonda", "contact_processing:" + sid),
                            btn("🏁 Yakunlandi","contact_completed:" + sid)))
                    .keyboardRow(new InlineKeyboardRow(btn("⬅ Orqaga", "contact_page:0")))
                    .build();
            send(chatId, text, kb);
        });
    }

    private void updateContactStatus(long chatId, UUID id, String status) {
        contactRepo.updateStatus(id, status);
        String label = switch (status) {
            case "VIEWED"     -> "Ko'rildi ✅";
            case "PROCESSING" -> "Jarayonda ➡";
            case "COMPLETED"  -> "Yakunlandi 🏁";
            default -> status;
        };
        sendText(chatId, "Holat yangilandi: " + label);
    }

    // ─── STATS ───────────────────────────────────────────────────────────────

    private void sendStats(long chatId) {
        long totalProd  = productRepo.count();
        long activeProd = productRepo.countByIsActiveTrue();
        long totalPort  = portfolioRepo.count();
        long allCont    = contactRepo.count();
        long newCont    = contactRepo.countByStatus("NEW");
        long doneCont   = contactRepo.countByStatus("COMPLETED");

        String text = String.format("""
                📊 *X&X Ceramics statistikasi*

                📦 Mahsulotlar: *%d* (faol: %d)
                🖼 Qilingan ishlar: *%d*
                📨 Jami murojaatlar: *%d*
                🆕 Yangi: *%d*
                ✅ Yakunlangan: *%d*
                """, totalProd, activeProd, totalPort, allCont, newCont, doneCont);
        sendMarkdown(chatId, text);
    }

    // ─── notify (called externally) ──────────────────────────────────────────

    public void notifyNewContact(Contact contact) {
        String typeUz = switch (contact.getType() != null ? contact.getType() : "") {
            case "PURCHASE"    -> "Mahsulot sotib olish";
            case "ORDER"       -> "Buyurtma berish";
            case "PARTNERSHIP" -> "Hamkorlik";
            default            -> "Boshqa";
        };
        String text = String.format("""
                🔔 *Yangi murojaat!*

                👤 Ism: *%s*
                📞 Telefon: *%s*
                📧 Email: %s
                📂 Turi: %s
                💬 Xabar: %s
                """,
                escape(contact.getName()), contact.getPhone(),
                contact.getEmail() != null ? contact.getEmail() : "—",
                typeUz,
                contact.getMessage() != null && !contact.getMessage().isBlank() ? escape(contact.getMessage()) : "—");

        InlineKeyboardMarkup kb = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(
                        btn("📖 Ko'rish", "contact_view:" + contact.getId()),
                        btn("✅ Ko'rildi", "contact_viewed:" + contact.getId())))
                .build();

        botConfig.getAdminChatIds().forEach(adminId -> {
            try { telegramClient.execute(SendMessage.builder().chatId(adminId).text(text).parseMode("Markdown").replyMarkup(kb).build()); }
            catch (Exception e) { log.error("notify failed for admin {}", adminId, e); }
        });
    }

    // ─── IMAGE UPLOAD ────────────────────────────────────────────────────────

    private void startAddImages(long chatId, String entityId, BotState state, String label) {
        editTarget.put(chatId, entityId);
        states.put(chatId, state);
        sendImagePrompt(chatId);
    }

    private void sendImagePrompt(long chatId) {
        ReplyKeyboardMarkup kb = ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow(KeyboardButton.builder().text("✅ Tayyor").build()))
                .keyboardRow(new KeyboardRow(KeyboardButton.builder().text("❌ Bekor qilish").build()))
                .resizeKeyboard(true).build();
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("📸 Rasmlarni yuboring (bir yoki bir nechta).\nYuborib bo'lgach *✅ Tayyor* tugmasini bosing.")
                    .parseMode("Markdown").replyMarkup(kb).build());
        } catch (Exception e) { log.error("sendImagePrompt failed", e); }
    }

    private void handlePhoto(long chatId, List<PhotoSize> photos) {
        BotState state = states.getOrDefault(chatId, BotState.IDLE);
        if (state != BotState.PROD_ADD_IMAGES && state != BotState.PORT_ADD_IMAGES) {
            sendText(chatId, "ℹ️ Rasm qo'shish uchun mahsulot yoki ish sahifasidan '📸 Rasm qo'shish' tugmasini bosing.");
            return;
        }

        if (!imageUploadService.isConfigured()) {
            sendText(chatId, "⚠️ Cloudinary sozlanmagan. application.properties ga cloud-name, api-key, api-secret kiriting.");
            return;
        }

        PhotoSize largest = photos.stream()
                .max(Comparator.comparingInt(p -> p.getFileSize() != null ? p.getFileSize() : 0))
                .orElse(null);
        if (largest == null) return;

        try {
            File tgFile = telegramClient.execute(new GetFile(largest.getFileId()));
            ImageUploadService.UploadResult result = imageUploadService.uploadFromTelegram(
                    tgFile.getFilePath(),
                    state == BotState.PROD_ADD_IMAGES ? "products" : "portfolio");

            UUID entityId = UUID.fromString(editTarget.getOrDefault(chatId, ""));
            if (state == BotState.PROD_ADD_IMAGES) {
                long existing = productImageRepo.countByProductId(entityId);
                ProductImage img = new ProductImage();
                img.setProductId(entityId);
                img.setUrl(result.url());
                img.setCloudinaryId(result.publicId());
                img.setPrimary(existing == 0);
                img.setSortOrder((int) existing);
                productImageRepo.save(img);
                sendText(chatId, "✅ Rasm qo'shildi (" + (existing + 1) + "-rasm). Yana yuborishingiz mumkin.");
            } else {
                long existing = portfolioImageRepo.countByPortfolioId(entityId);
                PortfolioImage img = new PortfolioImage();
                img.setPortfolioId(entityId);
                img.setUrl(result.url());
                img.setCloudinaryId(result.publicId());
                img.setPrimary(existing == 0);
                img.setSortOrder((int) existing);
                portfolioImageRepo.save(img);
                sendText(chatId, "✅ Rasm qo'shildi (" + (existing + 1) + "-rasm). Yana yuborishingiz mumkin.");
            }
        } catch (Exception e) {
            log.error("Photo upload failed", e);
            sendText(chatId, "❌ Rasmni yuklashda xatolik: " + e.getMessage());
        }
    }

    // ─── menus ───────────────────────────────────────────────────────────────

    private void sendMainMenu(long chatId) {
        ReplyKeyboardMarkup kb = ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow(
                        KeyboardButton.builder().text("📦 Mahsulotlar").build(),
                        KeyboardButton.builder().text("🖼 Qilingan ishlar").build()))
                .keyboardRow(new KeyboardRow(
                        KeyboardButton.builder().text("➕ Mahsulot qo'shish").build(),
                        KeyboardButton.builder().text("➕ Ish qo'shish").build()))
                .keyboardRow(new KeyboardRow(
                        KeyboardButton.builder().text("📨 Murojaatlar").build(),
                        KeyboardButton.builder().text("📊 Statistika").build()))
                .resizeKeyboard(true)
                .build();
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId).text("🏺 *X&X Ceramics Admin Panel*")
                    .parseMode("Markdown").replyMarkup(kb).build());
        } catch (Exception e) { log.error("sendMainMenu failed", e); }
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private void sendKeyboard(long chatId, String prompt, List<String> options, boolean withCancel) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder kb = ReplyKeyboardMarkup.builder().resizeKeyboard(true);
        options.forEach(o -> kb.keyboardRow(new KeyboardRow(KeyboardButton.builder().text(o).build())));
        if (withCancel) kb.keyboardRow(new KeyboardRow(KeyboardButton.builder().text("❌ Bekor qilish").build()));
        try {
            telegramClient.execute(SendMessage.builder().chatId(chatId).text(prompt).replyMarkup(kb.build()).build());
        } catch (Exception e) { log.error("sendKeyboard failed", e); }
    }

    private void sendTextWithCancel(long chatId, String prompt, boolean withSkip) {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder kb = ReplyKeyboardMarkup.builder().resizeKeyboard(true);
        if (withSkip) kb.keyboardRow(new KeyboardRow(KeyboardButton.builder().text("⏭ O'tkazib yuborish").build()));
        kb.keyboardRow(new KeyboardRow(KeyboardButton.builder().text("❌ Bekor qilish").build()));
        try {
            telegramClient.execute(SendMessage.builder().chatId(chatId).text(prompt).replyMarkup(kb.build()).build());
        } catch (Exception e) { log.error("sendTextWithCancel failed", e); }
    }

    private void sendSkippableText(long chatId, String prompt) { sendTextWithCancel(chatId, prompt, true); }

    private void send(long chatId, String text, InlineKeyboardMarkup kb) {
        try { telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).parseMode("Markdown").replyMarkup(kb).build()); }
        catch (Exception e) { log.error("send failed", e); }
    }

    private void sendText(long chatId, String text) {
        try { telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).parseMode("Markdown").build()); }
        catch (Exception e) { log.error("sendText failed", e); }
    }

    private void sendMarkdown(long chatId, String text) {
        try { telegramClient.execute(SendMessage.builder().chatId(chatId).text(text).parseMode("Markdown").build()); }
        catch (Exception e) { log.error("sendMarkdown failed", e); }
    }

    private InlineKeyboardButton btn(String label, String data) {
        return InlineKeyboardButton.builder().text(label).callbackData(data).build();
    }

    private boolean isAdmin(long chatId) { return botConfig.getAdminChatIds().contains(chatId); }

    private Map<String, String> draft(long chatId) { return drafts.computeIfAbsent(chatId, k -> new HashMap<>()); }

    private void resetState(long chatId) {
        states.remove(chatId);
        drafts.remove(chatId);
        editTarget.remove(chatId);
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId).text("✔️").replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build()).build());
        } catch (Exception ignored) {}
    }

    private String formatPrice(int price) {
        return String.format("%,d", price).replace(",", " ");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("_", "\\_").replace("*", "\\*").replace("[", "\\[").replace("`", "\\`");
    }
}
