package utils;

public class Variables {

    private static String clientName = "Client_" + System.currentTimeMillis();
    private static String clientEmail = "email_" + System.currentTimeMillis() + "@gmail.com";
    public static String updatedCustomerName = clientName + "_updated";
    private static String accessToken;

    private static String cartId;
    private static int productId;

    private static String itemId;
    private static String orderId;


    // Client Name
    public static void setClientName(String clientName) {
        Variables.clientName = clientName;
    }

    public static String getClientName() {
        return clientName;
    }

    public static void setUpdatedCustomerName(String updatedCustomerName){
        Variables.updatedCustomerName = updatedCustomerName;
    }

    public static String getUpdatedCustomerName(){
        return updatedCustomerName;
    }
    // Client Email
    public static void setClientEmail(String clientEmail) {
        Variables.clientEmail = clientEmail;
    }

    public static String getClientEmail() {
        return clientEmail;
    }


    // Access Token
    public static void setAccessToken(String accessToken) {
        Variables.accessToken = accessToken;
    }

    public static String getAccessToken() {
        return accessToken;
    }


    // Cart ID
    public static void setCartId(String cartId) {
        Variables.cartId = cartId;
    }

    public static String getCartId() {
        return cartId;
    }


    // Product ID
    public static void setProductId(int productId) {
        Variables.productId = productId;
    }

    public static int getProductId() {
        return productId;
    }




    // Item ID
    public static void setItemId(String itemId) {
        Variables.itemId = itemId;
    }

    public static String getItemId() {
        return itemId;
    }


    // Order ID
    public static void setOrderId(String orderId) {
        Variables.orderId = orderId;
    }

    public static String getOrderId() {
        return orderId;
    }
}
