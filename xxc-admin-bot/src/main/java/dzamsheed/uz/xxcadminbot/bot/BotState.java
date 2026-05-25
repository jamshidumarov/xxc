package dzamsheed.uz.xxcadminbot.bot;

public enum BotState {
    IDLE,

    // Product add
    PROD_ADD_NAME,
    PROD_ADD_CATEGORY,
    PROD_ADD_PRICE,
    PROD_ADD_AVAILABILITY,
    PROD_ADD_IMAGES,

    // Product edit
    PROD_EDIT_NAME,
    PROD_EDIT_PRICE,
    PROD_EDIT_CATEGORY,
    PROD_EDIT_AVAILABILITY,

    // Portfolio add
    PORT_ADD_TITLE,
    PORT_ADD_CATEGORY,
    PORT_ADD_YEAR,
    PORT_ADD_IMAGES,

    // Portfolio edit
    PORT_EDIT_TITLE,
    PORT_EDIT_CATEGORY,
    PORT_EDIT_YEAR
}
