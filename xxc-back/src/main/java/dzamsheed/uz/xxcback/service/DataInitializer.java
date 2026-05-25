package dzamsheed.uz.xxcback.service;

import dzamsheed.uz.xxcback.domain.entity.*;
import dzamsheed.uz.xxcback.domain.enums.*;
import dzamsheed.uz.xxcback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final CertificateRepository certificateRepository;
    private final ContactRepository contactRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createAdmin();
        if (productRepository.count() == 0) seedProducts();
        if (portfolioItemRepository.count() == 0) seedPortfolio();
        if (certificateRepository.count() == 0) seedCertificates();
        if (contactRepository.count() == 0) seedContacts();
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private static String img(int seed) {
        return "https://picsum.photos/seed/xxc" + seed + "/800/600";
    }

    private void saveProd(Product p, int... seeds) {
        Product saved = productRepository.save(p);
        for (int i = 0; i < seeds.length; i++) {
            saved.getImages().add(ProductImage.builder()
                    .product(saved).url(img(seeds[i])).isPrimary(i == 0).sortOrder(i).build());
        }
        if (seeds.length > 0) productRepository.save(saved);
    }

    private void savePort(PortfolioItem p, int... seeds) {
        PortfolioItem saved = portfolioItemRepository.save(p);
        for (int i = 0; i < seeds.length; i++) {
            saved.getImages().add(PortfolioImage.builder()
                    .portfolioItem(saved).url(img(seeds[i])).isPrimary(i == 0).sortOrder(i).build());
        }
        if (seeds.length > 0) portfolioItemRepository.save(saved);
    }

    private void save(Certificate c) { certificateRepository.save(c); }
    private void save(Contact c)     { contactRepository.save(c); }

    // ─── Admin ───────────────────────────────────────────────────────────────

    private void createAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build());
            log.info("Default admin created — parolni o'zgartiring!");
        }
    }

    // ─── Products ─────────────────────────────────────────────────────────────

    private void seedProducts() {
        log.info("Mahsulotlar seed qilinmoqda...");

        // LEPKA
        saveProd(Product.builder()
                .nameUz("Qo'lda yasalgan kosa").nameRu("Миска ручной работы").nameEn("Handmade Bowl")
                .descriptionUz("Tabiiy loydan qo'lda shakllantirilib, ekologik bo'yoq bilan sirlangan kosa.")
                .descriptionRu("Миска из натуральной глины, вылепленная вручную и покрытая экологичной глазурью.")
                .descriptionEn("A bowl hand-shaped from natural clay and coated with eco-friendly glaze.")
                .category(Category.LEPKA).price(85000).availability(Availability.AVAILABLE).build(),
                10, 11);

        saveProd(Product.builder()
                .nameUz("Ko'k sirli vaza").nameRu("Ваза с синей глазурью").nameEn("Blue Glazed Vase")
                .descriptionUz("Nozik bo'yin va keng qorinli, ko'k tusli sir bilan qoplangan an'anaviy vaza.")
                .descriptionRu("Традиционная ваза с тонкой шейкой и широким туловом, покрытая синей глазурью.")
                .descriptionEn("A traditional vase with a slender neck and wide body, covered in blue glaze.")
                .category(Category.LEPKA).price(120000).availability(Availability.AVAILABLE).build(),
                12, 13, 14);

        saveProd(Product.builder()
                .nameUz("Piyolalar to'plami (6 dona)").nameRu("Набор пиал (6 шт.)").nameEn("Piala Set (6 pcs)")
                .descriptionUz("Choy ichish uchun mo'ljallangan, bir-biriga mos bezak bilan qoplangan 6 ta piyola.")
                .descriptionRu("Набор из 6 пиал для чая с согласованным орнаментом.")
                .descriptionEn("Set of 6 matching tea bowls with coordinated decorative pattern.")
                .category(Category.LEPKA).price(250000).availability(Availability.AVAILABLE).build(),
                15, 16);

        saveProd(Product.builder()
                .nameUz("Buyurtma asosida idish").nameRu("Посуда на заказ").nameEn("Custom Dinnerware")
                .descriptionUz("Siz tanlagan rang va naqsh asosida tayyorlanadigan individual idish to'plami.")
                .descriptionRu("Индивидуальный набор посуды по выбранному цвету и узору.")
                .descriptionEn("Individual dinnerware set made to your chosen color and pattern.")
                .category(Category.LEPKA).availability(Availability.ORDER).isCustomOrder(true).build(),
                17);

        // TYAGA
        saveProd(Product.builder()
                .nameUz("Nozik tyaga idish").nameRu("Тонкий тянутый сосуд").nameEn("Delicate Pull Vessel")
                .descriptionUz("Tyaga texnikasida yaratilgan, devorlar juda nozik bo'lgan badiiy idish.")
                .descriptionRu("Художественный сосуд, созданный техникой тяги с очень тонкими стенками.")
                .descriptionEn("Artistic vessel created with the pull technique, featuring very thin walls.")
                .category(Category.TYAGA).price(180000).availability(Availability.AVAILABLE).build(),
                20, 21);

        saveProd(Product.builder()
                .nameUz("Tyaga qadah").nameRu("Тянутый бокал").nameEn("Pull Goblet")
                .descriptionUz("Baland oyoqli, tyaga usulida yasalgan estetik qadah.")
                .descriptionRu("Изящный бокал на высокой ножке, выполненный техникой тяги.")
                .descriptionEn("Elegant tall-stemmed goblet crafted using the pull technique.")
                .category(Category.TYAGA).price(95000).availability(Availability.AVAILABLE).build(),
                22, 23);

        // KERAMIKA
        saveProd(Product.builder()
                .nameUz("Sirlangan dekorativ panel").nameRu("Глазурованное декоративное панно").nameEn("Glazed Decorative Panel")
                .descriptionUz("Devorga osilishi uchun mo'ljallangan, geometrik naqshli keramika panel.")
                .descriptionRu("Керамическое панно для стены с геометрическим орнаментом.")
                .descriptionEn("Wall-mounted ceramic panel with geometric ornamental pattern.")
                .category(Category.KERAMIKA).price(350000).availability(Availability.AVAILABLE)
                .dimensions("30x30 sm").material("Keramika, tabiiy sir").build(),
                30, 31, 32);

        saveProd(Product.builder()
                .nameUz("Ko'za (an'anaviy)").nameRu("Кувшин (традиционный)").nameEn("Traditional Jug")
                .descriptionUz("O'zbek an'anaviy uslubida yasalgan, suv yoki meva sharbati uchun mo'ljallangan ko'za.")
                .descriptionRu("Кувшин в традиционном узбекском стиле для воды или сока.")
                .descriptionEn("A jug in traditional Uzbek style, suitable for water or fruit juice.")
                .category(Category.KERAMIKA).price(145000).availability(Availability.AVAILABLE)
                .dimensions("25 sm balandlik").weight("800 g").material("Keramika").build(),
                33, 34);

        saveProd(Product.builder()
                .nameUz("Gul uchun keramika qozon").nameRu("Керамический горшок для цветов").nameEn("Ceramic Flower Pot")
                .descriptionUz("O'simliklar uchun nafis keramika qozon, tabiiy ranglar bilan bezatilgan.")
                .descriptionRu("Изящный керамический горшок для растений, украшенный натуральными цветами.")
                .descriptionEn("Elegant ceramic flower pot decorated with natural colors.")
                .category(Category.KERAMIKA).price(75000).availability(Availability.AVAILABLE).build(),
                35, 36);

        // SUVENIR
        saveProd(Product.builder()
                .nameUz("Keramik magnet (Toshkent)").nameRu("Керамический магнит (Ташкент)").nameEn("Ceramic Magnet (Tashkent)")
                .descriptionUz("Toshkent shahri ramzlari bilan bezatilgan sovg'a magniti.")
                .descriptionRu("Сувенирный магнит с символами города Ташкент.")
                .descriptionEn("Gift magnet decorated with symbols of Tashkent city.")
                .category(Category.SUVENIR).price(25000).availability(Availability.AVAILABLE).build(),
                40);

        saveProd(Product.builder()
                .nameUz("Qo'chqor haykali").nameRu("Фигурка барана").nameEn("Ram Figurine")
                .descriptionUz("An'anaviy O'zbek mifologiyasiga asoslangan, qo'lda yasalgan qo'chqor haykali.")
                .descriptionRu("Фигурка барана ручной работы, основанная на традиционной узбекской мифологии.")
                .descriptionEn("Handcrafted ram figurine based on traditional Uzbek mythology.")
                .category(Category.SUVENIR).price(65000).availability(Availability.AVAILABLE)
                .dimensions("15x10 sm").weight("400 g").build(),
                41, 42);

        saveProd(Product.builder()
                .nameUz("Mini samovoy").nameRu("Мини-самовар").nameEn("Mini Samovar")
                .descriptionUz("Keramikadan ishlangan dekorativ mini samovoy, sovg'a uchun ideal.")
                .descriptionRu("Декоративный мини-самовар из керамики, идеальный подарок.")
                .descriptionEn("Decorative ceramic mini samovar, ideal as a gift.")
                .category(Category.SUVENIR).price(110000).availability(Availability.AVAILABLE).build(),
                43, 44);

        // KULOLCHILIK
        saveProd(Product.builder()
                .nameUz("Kulolchilik ko'za (katta)").nameRu("Гончарный кувшин (большой)").nameEn("Pottery Jug (Large)")
                .descriptionUz("Kulolchilik charxida yasalgan, 3 litrli an'anaviy suv ko'zasi.")
                .descriptionRu("Традиционный кувшин для воды объёмом 3 литра, сделанный на гончарном круге.")
                .descriptionEn("Traditional 3-litre water jug made on a pottery wheel.")
                .category(Category.KULOLCHILIK).price(195000).availability(Availability.AVAILABLE)
                .dimensions("40 sm balandlik").weight("1.5 kg").material("Tabiiy loy").build(),
                50, 51);

        saveProd(Product.builder()
                .nameUz("Kulolchilik kosachasi").nameRu("Гончарная чашка").nameEn("Pottery Cup")
                .descriptionUz("Charxda yasalgan, qo'lda sirlangan oddiy va go'zal choy kosasi.")
                .descriptionRu("Простая и красивая чайная чашка, сделанная на круге и вручную покрытая глазурью.")
                .descriptionEn("Simple and beautiful tea cup made on a wheel and hand-glazed.")
                .category(Category.KULOLCHILIK).price(55000).availability(Availability.AVAILABLE).build(),
                52, 53);

        // BUYURTMA
        saveProd(Product.builder()
                .nameUz("Individual to'y to'plami").nameRu("Индивидуальный свадебный набор").nameEn("Custom Wedding Set")
                .descriptionUz("To'y marosimi uchun maxsus tayyorlanadigan, logotipin yoki ismi yozilgan keramika to'plami.")
                .descriptionRu("Специальный керамический набор для свадьбы с логотипом или именем.")
                .descriptionEn("Custom ceramic set for weddings with personalized logo or name.")
                .category(Category.BUYURTMA).availability(Availability.ORDER).isCustomOrder(true).build(),
                60, 61);

        log.info("Mahsulotlar seed qilindi.");
    }

    // ─── Portfolio ────────────────────────────────────────────────────────────

    private void seedPortfolio() {
        log.info("Portfolio seed qilinmoqda...");

        savePort(PortfolioItem.builder()
                .titleUz("Rishton uslubidagi idishlar").titleRu("Посуда в стиле Риштан").titleEn("Rishtan-Style Dinnerware")
                .descriptionUz("Rishton kulolchilik an'anasiga asoslangan, ko'k-oq ranglar bilan bezatilgan to'liq dasturxon to'plami.")
                .descriptionRu("Полный обеденный сервиз, украшенный в традициях риштанской керамики в сине-белых тонах.")
                .descriptionEn("A complete dinnerware set decorated in the Rishtan ceramic tradition in blue and white tones.")
                .category(Category.KERAMIKA).year(2024).isFeatured(true).build(),
                70, 71, 72);

        savePort(PortfolioItem.builder()
                .titleUz("Restoran uchun xizmat to'plami").titleRu("Сервировочный набор для ресторана").titleEn("Restaurant Service Set")
                .descriptionUz("Toshkentdagi premium restoran uchun 200 ta idishdan iborat maxsus buyurtma. Restoranning logotipi har bir idishga nozik tarzda tushirilgan.")
                .descriptionRu("Специальный заказ из 200 предметов для премиального ресторана в Ташкенте. Логотип ресторана нанесён на каждый предмет.")
                .descriptionEn("A special order of 200 pieces for a premium Tashkent restaurant. The restaurant logo is delicately imprinted on each piece.")
                .category(Category.BUYURTMA).year(2023).isFeatured(true).build(),
                73, 74);

        savePort(PortfolioItem.builder()
                .titleUz("Ko'rgazma uchun haykaltaroshlik").titleRu("Скульптура для выставки").titleEn("Exhibition Sculpture")
                .descriptionUz("Toshkent xalqaro san'at ko'rgazmasi uchun maxsus tayyorlangan, 80 sm balandlikdagi abstrakt keramika haykali.")
                .descriptionRu("Абстрактная керамическая скульптура высотой 80 см, специально созданная для Ташкентской международной выставки искусств.")
                .descriptionEn("An abstract ceramic sculpture 80 cm tall, specially created for the Tashkent International Art Exhibition.")
                .category(Category.LEPKA).year(2023).isFeatured(true).build(),
                75, 76, 77);

        savePort(PortfolioItem.builder()
                .titleUz("Mehmonxona interyeri uchun panellar").titleRu("Панели для интерьера отеля").titleEn("Hotel Interior Panels")
                .descriptionUz("5 yulduzli mehmonxonaning lobby xonasi uchun 12 ta devoriy keramika panel. Har biri O'zbekistonning tarixiy shaharlarini tasvirlaydi.")
                .descriptionRu("12 настенных керамических панелей для лобби пятизвёздочного отеля. Каждая изображает исторические города Узбекистана.")
                .descriptionEn("12 wall ceramic panels for the lobby of a five-star hotel. Each depicts a historical city of Uzbekistan.")
                .category(Category.KERAMIKA).year(2024).isFeatured(false).build(),
                78, 79, 80);

        savePort(PortfolioItem.builder()
                .titleUz("Tyaga texnikasida guldonlar seriyasi").titleRu("Серия ваз техникой тяги").titleEn("Pull Technique Vase Series")
                .descriptionUz("O'zaro bog'liq 7 ta guldon: har biri turli o'simliklarni ramziy tasvirlaydi. Tyaga texnikasida yaratilgan.")
                .descriptionRu("Серия из 7 связанных между собой ваз: каждая символически изображает разные растения. Выполнены техникой тяги.")
                .descriptionEn("A series of 7 interconnected vases, each symbolically depicting different plants. Created using the pull technique.")
                .category(Category.TYAGA).year(2022).isFeatured(true).build(),
                81, 82);

        savePort(PortfolioItem.builder()
                .titleUz("Bolalar bog'chasi uchun bezaklar").titleRu("Декорации для детского сада").titleEn("Kindergarten Decorations")
                .descriptionUz("Toshkentdagi xususiy bolalar bog'chasi uchun 30 ta rangli keramika hayvallar va ertaklar qahramonlari.")
                .descriptionRu("30 цветных керамических фигурок для частного детского сада в Ташкенте — животные и сказочные персонажи.")
                .descriptionEn("30 colorful ceramic figurines for a private kindergarten in Tashkent — animals and fairy tale characters.")
                .category(Category.SUVENIR).year(2022).isFeatured(false).build(),
                83, 84);

        savePort(PortfolioItem.builder()
                .titleUz("Kulolchilik master-klass natijalari").titleRu("Результаты мастер-класса по гончарству").titleEn("Pottery Master Class Results")
                .descriptionUz("25 ishtirokchi bilan o'tkazilgan kulolchilik master-klassida yaratilgan ishlar namoyishi.")
                .descriptionRu("Выставка работ, созданных на мастер-классе по гончарству с участием 25 человек.")
                .descriptionEn("Exhibition of works created during a pottery master class with 25 participants.")
                .category(Category.KULOLCHILIK).year(2021).isFeatured(false).build(),
                85, 86);

        savePort(PortfolioItem.builder()
                .titleUz("Bayram kolleksiyasi 2024").titleRu("Праздничная коллекция 2024").titleEn("Festive Collection 2024")
                .descriptionUz("Navro'z bayrami munosabati bilan tayyorlangan maxsus kolleksiya: 15 ta noyob buyum.")
                .descriptionRu("Специальная коллекция к празднику Навруз: 15 уникальных предметов.")
                .descriptionEn("A special collection for Navruz celebration: 15 unique items.")
                .category(Category.LEPKA).year(2024).isFeatured(true).build(),
                87, 88, 89);

        log.info("Portfolio seed qilindi.");
    }

    // ─── Certificates ─────────────────────────────────────────────────────────

    private void seedCertificates() {
        log.info("Sertifikatlar seed qilinmoqda...");

        // ACHIEVEMENT (no image)
        save(Certificate.builder()
                .titleUz("Hunarmandchilik sohasida 10 yillik faoliyat").titleRu("10 лет деятельности в сфере ремёсел").titleEn("10 Years of Craft Activity")
                .descriptionUz("X&X Ceramics ustaxonasining tashkil etilganidan beri o'tgan 10 yillik muvaffaqiyatli faoliyati.")
                .descriptionRu("10 лет успешной деятельности мастерской X&X Ceramics с момента основания.")
                .descriptionEn("10 years of successful activity of the X&X Ceramics studio since its founding.")
                .year(2024).type(CertificateType.ACHIEVEMENT).sortOrder(1).build());

        save(Certificate.builder()
                .titleUz("500 dan ortiq bajarilgan buyurtmalar").titleRu("Более 500 выполненных заказов").titleEn("Over 500 Completed Orders")
                .descriptionUz("Mahalliy va xalqaro mijozlar uchun 500 dan ortiq muvaffaqiyatli buyurtmani bajarish.")
                .descriptionRu("Выполнение более 500 успешных заказов для местных и международных клиентов.")
                .descriptionEn("Completion of over 500 successful orders for local and international clients.")
                .year(2023).type(CertificateType.ACHIEVEMENT).sortOrder(2).build());

        save(Certificate.builder()
                .titleUz("Xalqaro ko'rgazmada ishtirok").titleRu("Участие в международной выставке").titleEn("International Exhibition Participation")
                .descriptionUz("Dubai Expo 2023 xalqaro ko'rgazmasida O'zbekiston pavilyonida ishtirok etish.")
                .descriptionRu("Участие в международной выставке Dubai Expo 2023 в павильоне Узбекистана.")
                .descriptionEn("Participation in Dubai Expo 2023 international exhibition at the Uzbekistan pavilion.")
                .year(2023).issuedBy("Dubai Expo 2023").type(CertificateType.ACHIEVEMENT).sortOrder(3).build());

        // CERTIFICATE (with images)
        save(Certificate.builder()
                .titleUz("O'zbekiston hunarmandlar uyushmasi sertifikati").titleRu("Сертификат Ассоциации ремесленников Узбекистана").titleEn("Uzbekistan Craftsmen Association Certificate")
                .descriptionUz("O'zbekiston hunarmandlar uyushmasining a'zosi sifatida berilgan rasmiy sertifikat.")
                .descriptionRu("Официальный сертификат, выданный как члену Ассоциации ремесленников Узбекистана.")
                .descriptionEn("Official certificate issued as a member of the Uzbekistan Craftsmen Association.")
                .year(2022).issuedBy("O'zbekiston Hunarmandlar Uyushmasi")
                .imageUrl("https://picsum.photos/seed/xxc-cert1/600/800")
                .type(CertificateType.CERTIFICATE).sortOrder(4).build());

        save(Certificate.builder()
                .titleUz("Sifat menejmenti ISO 9001 sertifikati").titleRu("Сертификат системы менеджмента качества ISO 9001").titleEn("ISO 9001 Quality Management Certificate")
                .descriptionUz("Ishlab chiqarish jarayonlarining xalqaro sifat standartlariga muvofiqligi.")
                .descriptionRu("Соответствие производственных процессов международным стандартам качества.")
                .descriptionEn("Conformity of production processes to international quality standards.")
                .year(2023).issuedBy("Bureau Veritas")
                .imageUrl("https://picsum.photos/seed/xxc-cert2/600/800")
                .type(CertificateType.CERTIFICATE).sortOrder(5).build());

        save(Certificate.builder()
                .titleUz("Ekologik mahsulot sertifikati").titleRu("Сертификат экологичной продукции").titleEn("Eco-Friendly Product Certificate")
                .descriptionUz("Mahsulotlar tabiiy xom ashyodan tayyorlanishi va ekologik standartlarga muvofiqligi.")
                .descriptionRu("Изготовление продукции из натурального сырья и соответствие экологическим стандартам.")
                .descriptionEn("Products are made from natural raw materials and comply with environmental standards.")
                .year(2022).issuedBy("EcoCert Uzbekistan")
                .imageUrl("https://picsum.photos/seed/xxc-cert3/600/800")
                .type(CertificateType.CERTIFICATE).sortOrder(6).build());

        // AWARD (with images)
        save(Certificate.builder()
                .titleUz("\"Yilning eng yaxshi hunarmandi\" mukofoti").titleRu("Награда «Лучший мастер года»").titleEn("\"Best Craftsman of the Year\" Award")
                .descriptionUz("O'zbekiston Madaniyat vazirligi tomonidan berilgan yilning eng yaxshi hunarmandi unvoni.")
                .descriptionRu("Звание лучшего мастера года, присвоенное Министерством культуры Узбекистана.")
                .descriptionEn("The title of Best Craftsman of the Year awarded by the Ministry of Culture of Uzbekistan.")
                .year(2023).issuedBy("O'zbekiston Madaniyat vazirligi")
                .imageUrl("https://picsum.photos/seed/xxc-award1/600/800")
                .type(CertificateType.AWARD).sortOrder(7).build());

        save(Certificate.builder()
                .titleUz("\"Milliy meros\" ko'rgazmasi g'olibi").titleRu("Победитель выставки «Национальное наследие»").titleEn("Winner of \"National Heritage\" Exhibition")
                .descriptionUz("Toshkent shahrida bo'lib o'tgan \"Milliy Meros\" ko'rgazmasida birinchi o'rinni egallash.")
                .descriptionRu("Завоевание первого места на выставке «Национальное наследие» в Ташкенте.")
                .descriptionEn("Winning first place at the National Heritage exhibition held in Tashkent.")
                .year(2024).issuedBy("Toshkent shahar hokimligi")
                .imageUrl("https://picsum.photos/seed/xxc-award2/600/800")
                .type(CertificateType.AWARD).sortOrder(8).build());

        log.info("Sertifikatlar seed qilindi.");
    }

    // ─── Contacts ─────────────────────────────────────────────────────────────

    private void seedContacts() {
        log.info("Murojaatlar seed qilinmoqda...");

        save(Contact.builder()
                .name("Alisher Toshmatov").phone("+998901234567").email("alisher@example.com")
                .type(ContactType.ORDER).status(ContactStatus.NEW)
                .message("Assalomu alaykum! To'yimiz uchun 50 ta piyola va 10 ta kosa buyurtma bermoqchimiz. Ular ustiga familiyamizni yozib berishingiz mumkinmi?")
                .build());

        save(Contact.builder()
                .name("Malika Yusupova").phone("+998935678901").email("malika.y@gmail.com")
                .type(ContactType.PURCHASE).status(ContactStatus.VIEWED)
                .message("Ko'k sirli vazalardan birini sotib olmoqchiman. Narxi qancha?")
                .build());

        save(Contact.builder()
                .name("Bobur Karimov").phone("+998971112233")
                .type(ContactType.ORDER).status(ContactStatus.PROCESSING)
                .message("Restoran uchun 100 ta idish kerak. Har bir idishga logotipimizni tushirib bera olasizmi? Termini 2 oy.")
                .build());

        save(Contact.builder()
                .name("Dilnoza Xasanova").phone("+998909876543").email("dilnoza@xasanova.uz")
                .type(ContactType.PARTNERSHIP).status(ContactStatus.COMPLETED)
                .message("Biz xalqaro savdo kompaniyasimiz. Mahsulotlaringizni Dubayga eksport qilish haqida gaplashmoqchimiz.")
                .build());

        save(Contact.builder()
                .name("Sardor Mirzayev").phone("+998885551122")
                .type(ContactType.PURCHASE).status(ContactStatus.NEW)
                .message("Ona kuniga sovg'a uchun chiroyli idish izlayapman. Narx diapazoni 100-200 ming so'm orasida.")
                .build());

        save(Contact.builder()
                .name("Gulnora Rahimova").phone("+998914443355").email("gulnora.r@mail.ru")
                .type(ContactType.OTHER).status(ContactStatus.NEW)
                .message("Master-klass o'tkazishingiz mumkinmi? Do'stlarim bilan kulolchilik o'rganmoqchimiz.")
                .build());

        log.info("Murojaatlar seed qilindi.");
    }
}
