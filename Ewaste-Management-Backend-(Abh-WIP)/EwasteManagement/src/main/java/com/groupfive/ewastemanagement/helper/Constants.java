package com.groupfive.ewastemanagement.helper;

public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String VIEW_ITEMS_ON_SALE = "View Items On Sale :: ";
    public static final String NO_DRIVES_AVAILABLE = "No Drives Available";
    public static final String API_HAS_STARTED_SUCCESSFULLY = "Api has started successfully";
    public static final String ANALYTICS_FETCHED_SUCCESSFULLY = "Analytics fetched successfully";
    public static final String FETCHED_SUCCESSFULLY = "Fetched Successfully";
    public static final String NOTIFICATION_CREATED_WITH_ALL_THE_DETAILS_SUCCESSFULLY = "Notification created with all the details successfully";
    public static final String NOTIFICATION_SAVED_TO_DATABASE_WITH_ALL_DETAILS_SUCCESSFULLY = "Notification saved to database with all details successfully";
    public static final String GET_ANALYTICS = "Get Analytics ::";
    public static final String ANALYTICS = "Analytics ";
    public static final String IS_COLLECTOR = "isCollector";
    public static final String IS_CUSTOMER = "isCustomer";
    public static final String IS_VENDOR = "isVendor";
    public static final String PICK_UP = "PickUp";

    public static final String EMAIL = "EMAIL";
    public static final String PENDING = "pending";
    public static final String DROP_OFF = "DropOff";
    public static final String COMPLETED = "completed";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String EXPIRED = "Expired";
    public static final String AVAILABLE = "Available";
    public static final String UPCOMING = "Upcoming";
    public static final String NO_USER_FOUND = "No user Found";
    public static final String BEARER = "Bearer";
    public static final String RESET_PASSWORD = "Reset Password";
    public static final String E_WASTE_MANAGEMENT = "E-Waste Management";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String COLLECTOR = "COLLECTOR";
    public static final String URL_EMAIL_RESET_PASSWORD = "http://localhost:3000/password/save/";
    public static final String NO_REQUEST_PENDING = "No Request Pending";
    public static final String NO_SUCH_ORDER_EXIST = "No such order exist";
    public static final String SCHEDULED = "Scheduled";
    public static final String NO_DATA_PROVIDED = "No data provided";
    public static final String ENTER_ALL_DETAILS = "Enter all Details";
    public static final String NO_SUCH_USER_FOUND = "No Such User Found";
    public static final String NO_ITEMS_IN_SALE = "No items in Sale";
    public static final String DRIVE_NOT_FOUND = "Drive Not Found";
    public static final String PROFILE_NOT_FOUND = "Profile Not Found";
    public static final String E_WASTE_DRIVE_COLLECTOR = "EWasteDriveCollector";
    public static final String E_WASTE_DRIVE_CITY = "EWasteDriveCity";
    public static final String NO_NEW_NOTIFICATION = "No New Notification";
    public static final String VENDOR_CITY = "vendorCity";
    public static final String VENDOR_ALL_CITY = "vendorAllCity";
    public static final String CUSTOMER_CITY = "customerCity";
    public static final String CUSTOMER_ALL_CITY = "customerAllCity";

    //Collector service implementation analytics
    public static final String TEMP_COLLECTED = "TempCollected";
    public static final String LAMPS_COLLECTED = "LampsCollected";
    public static final String LARGE_EQUIP_COLLECTED = "LargeEquipCollected";
    public static final String SMALL_EQUIP_COLLECTED = "SmallEquipCollected";
    public static final String SMALL_IT_COLLECTED = "SmallITCollected";
    public static final String SCREENS_COLLECTED = "ScreensCollected";
    public static final String TEMP_SELL = "TempSell";
    public static final String LAMPS_SELL = "LampsSell";
    public static final String LARGE_EQUIP_SELL = "LargeEquipSell";
    public static final String SMALL_EQUIP_SELL = "SmallEquipSell";
    public static final String SMALL_IT_SELL = "SmallITSell";
    public static final String SCREENS_SELL = "ScreensSell";
    public static final String TEMP = "Temp";
    public static final String LAMPS = "Lamps";
    public static final String LARGE_EQUIP = "LargeEquip";
    public static final String SMALL_EQUIP = "SmallEquip";
    public static final String SMALL_IT = "SmallIT";
    public static final String SCREENS = "Screens";
    public static final String NO_ORDERS = "No orders";
    public static final String REDUCE_QUANTITY = "Reduce Quantity";
    public static final String OUT_OF_STOCK = "Out of Stock";
    public static final String NO_ITEM_FOUND = "No Item Found";
    public static final String NO_COLLECTOR_FOUND = "No Collector Found";
    public static final String VENDOR_IN_CITY = "vendorInCity";
    public static final String ALL_VENDOR = "allVendor";
    public static final String COLLECTOR_IN_CITY = "collectorInCity";
    public static final String ALL_COLLECTOR = "allCollector";


    //Vendor service implementation
    public static final String TEMP_COLLECTOR_SALE = "TempCollectorSale";
    public static final String LAMPS_COLLECTOR_SALE = "LampsCollectorSale";
    public static final String LARGE_EQUIP_COLLECTOR_SALE = "LargeEquipCollectorSale";
    public static final String SMALL_EQUIP_COLLECTOR_SALE = "SmallEquipCollectorSale";
    public static final String SMALL_IT_COLLECTOR_SALE = "SmallITCollectorSale";
    public static final String SCREENS_COLLECTOR_SALE = "ScreensCollectorSale";
    public static final String TEMP_VENDOR = "TempVendor";
    public static final String LAMPS_VENDOR = "LampsVendor";
    public static final String LARGE_EQUIP_VENDOR = "LargeEquipVendor";
    public static final String SMALL_EQUIP_VENDOR = "SmallEquipVendor";
    public static final String SMALL_IT_VENDOR = "SmallITVendor";
    public static final String SCREENS_VENDOR = "ScreensVendor";
    public static final String TEMP_CITY = "TempCity";
    public static final String LAMPS_CITY = "LampsCity";
    public static final String LARGE_EQUIP_CITY = "LargeEquipCity";
    public static final String SMALL_EQUIP_CITY = "SmallEquipCity";
    public static final String SMALL_IT_CITY = "SmallITCity";
    public static final String SCREENS_CITY = "ScreensCity";

    //Customer Implementation
    public static final String TEMP_TOTAL = "TempTotal";
    public static final String LAMPS_TOTAL = "LampsTotal";
    public static final String LARGE_EQUIP_TOTAL = "LargeEquipTotal";
    public static final String SMALL_EQUIP_TOTAL = "SmallEquipTotal";
    public static final String SMALL_IT_TOTAL = "SmallITTotal";
    public static final String SCREENS_TOTAL = "ScreensTotal";
    public static final String ORDER_NOT_ACCEPTED_BY_COLLECTOR = "Order Not Accepted By Collector";
    public static final String NO_UNREAD_NOTIFICATION = "No Unread Notification";
    public static final String ORDER_IN_CITY = "orderInCity";
    public static final String ORDER_CUSTOMER = "orderCustomer";
    public static final String E_WASTE_DRIVE_LIST_CITY = "eWasteDriveListCity";
    public static final String E_WASTE_DRIVE_LIST_ALL = "eWasteDriveListAll";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String VENDOR = "VENDOR";
    public static final String SOLD = "Sold";
    public static final String TRACE_ID = "TRACE_ID";
    public static final String EMPTY_EMAIL = "Empty email";
    public static final String EMAIL_CANNOT_BE_EMPTY = "Email cannot be empty";
    public static final String USER_DOES_NOT_EXIST = "The user does not exist";
    public static final String USER_WITH_ID_VIEWED_HIS_ANALYTICS = "User with id '{}' viewed his analytics";
    public static final String AUTHORIZATION = "Authorization";
}
