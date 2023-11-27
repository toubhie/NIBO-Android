package com.nigeria.locateme.locateme.utils;

import android.os.Environment;

/**
 * Created by Theophilus on 12/8/2016.
 */
public class Constants {
    //All QREncoder Constants
    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;

    // Boolean Constants
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final int DELETED = 1;
    public static final int NOT_DELETED = 0;

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    //All preferences constants
    // Shared pref mode
    public static final int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String SHARED_PREFERENCE_NAME = "NIBO_SHARED_PREFERENCE";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String IS_REGISTERED = "IsRegistered";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_STORAGE_PATH = "storagePath";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public static final String KEY_IS_FIRST_TIME_LAUNCH_FOR_NIBO_CODE = "IsFirstTimeLaunchForNiboCode";
    public static final String KEY_ABSOLUTE_PATH = "absolutePath";
    public static final String KEY_PROFILE_PERCENTAGE = "profilePercentage";
    public static final String KEY_LAST_SYNC_DATE = "lastSyncDate";
    public static final String KEY_SYNC_FREQUENCY = "syncFrequency";


    ///PERMANENT CODE TAGS
    public static final String TAG_HOUSE_ADDRESS = "houseAddress";
    public static final String TAG_BUILDING_TYPE = "buildingType";
    public static final String TAG_STATE = "state";
    public static final String TAG_LOCAL_GOVERNMENT = "localGovernment";
    public static final String TAG_STREET = "street";
    public static final String TAG_LATLONG = "latLong";






    //All Tags
    public static final String TAG_GBACARD = "GBACARD";
    public static final String TAG_NIBO = "NIBO";
    public static final String TAG_NIBO_PREFIX = "Hi, this is my Nibo Number";
    public static final String TAG_GBACARD_PREFIX = "gbacard";
    public static final String TAG_MIMETYPE_RADUTOKEN 	= "vnd.android.cursor.item/my_contact";
    public static final String TAG_STORAGE_PATH = "storage_path";
    public static final String TAG_FILE = "file";
    public static final String TAG_IMAGE_BYTE_ARRAY = "imageByteArray";
    public static final String TAG_FIRSTNAME = "firstName";
    public static final String TAG_LASTNAME = "lastName";
    public static final String TAG_PHONE_NUMBER = "phoneNumber";
    public static final String TAG_OLD_PHONE_NUMBER = "oldPhoneNumber";
    public static final String TAG_PRIMARY_PHONE_NUMBER = "primaryPhoneNumber";
    public static final String TAG_SECONDARY_PHONE_NUMBER = "secondaryPhoneNumber";
    public static final String TAG_CONCATENATED_PHONE_NUMBERS = "concatenatedPhoneNumbers";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PRIMARY_EMAIL = "primaryEmail";
    public static final String TAG_SECONDARY_EMAIL = "secondaryEmail";
    public static final String TAG_CONCATENATED_EMAILS = "concatenatedEmails";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_LAST_CHECKIN = "lastCheckin";
    public static final String TAG_TIME_CHECKIN = "timeCheckin";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_NAME = "name";
    public static final String TAG_SENDER_PHONE_NUMBER = "senderPhoneNumber";
    public static final String TAG_RECEIVE_AND_SEND = "receiveAndSend";
    public static final String TAG_CONTACT_DETAIL = "contactDetail";
    public static final String TAG_USER_DETAIL = "userDetail";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_CODE = "code";
    public static final String UNIQUE_CODE = "uniqueCode";
    public static final String HOUSE_UNIQUE_CODE = "houseUniqueCode";
    public static final String TAG_COUNT = "count";
    public static final String TAG_USER_ID = "userId";
    public static final String TAG_FULL_NAME = "fullName";
    public static final String TAG_FEEDBACK_MESSAGE = "feedbackMessage";
    public static final String TAG_COMPANY_NAME = "companyName";
    public static final String TAG_JOB_TITLE = "jobTitle";
    public static final String TAG_COMPANY_WEBSITE = "companyWebsite";
    public static final String TAG_USER = "user";
    public static final String TAG_HOUSE = "house";
    public static final String TAG_ALL_USERS = "allUsers";
    public static final String TAG_ID = "id";
    public static final String TAG_DATE_CREATED = "dateCreated";
    public static final String TAG_DATE_UPDATED = "dateUpdated";
    public static final String TAG_CONCATENATED_RECEIVER_IDS = "concatenatedReceiverUserIds";
    public static final String TAG_SYNC_INTENT_DATA = "syncIntentData";
    public static final String TAG_CURRENT_VIEW = "currentView";
    public static final String TAG_SQLITE_ID = "sqliteId";
    public static final String TAG_CONFIRMATION_CODE = "confirmationCode";
    public static final String TAG_SAVED_LOCATIONS = "savedLocations";
    public static final String TAG_TEMPORARY_NIBO_CODE = "temporaryNiboCode";
    public static final String TAG_PERMANENT_NIBO_CODE = "permanentNiboCode";
    public static final String TAG_AGENT = "agent";
    public static final String TAG_NEAREST_BUSSTOP = "nearestBusstop";
    public static final String TAG_HOUSE_NUMBER = "houseNumber";
    public static final String TAG_NIBO_CODE = "niboCode";
    public static final String TAG_FULL_HOUSE_ADDRESS = "fullHouseAddress";
    public static final String TAG_CITY = "city";

    public static final String TAG_LOCATION_ID = "locationId";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_IS_DELETED = "isDeleted";

    public static final String TAG_FULL_NAME_UPPER = "FULL_NAME";
    public static final String TAG_EMAIL_UPPER = "EMAIL";
    public static final String TAG_PHONE_NUMBER_UPPER = "PHONE_NUMBER";
    public static final String TAG_ADDRESS_UPPER = "ADDRESS";
    public static final String TAG_LAST_CHECK_IN_UPPER = "EMAIL";
    public static final String TAG_TIME_CHECK_IN_UPPER = "PHONE_NUMBER";

    public static final String TAG_LOCATION_ID_UPPER = "LOCATION_ID";
    public static final String TAG_USER_ID_UPPER = "USER_ID";
    public static final String TAG_SQLITE_ID_UPPER = "SQLITE_ID";
    public static final String TAG_LONGITUDE_UPPER = "LONGITUDE";
    public static final String TAG_LATITUDE_UPPER = "LATITUDE";
    public static final String TAG_DESCRIPTION_UPPER = "DESCRIPTION";
    public static final String TAG_IS_DELETED_UPPER = "IS_DELETED";

    public static final String TAG_SAVED_LOCATION_MYSQL_ID = "savedLocationMysqlId";

    // Sync Constants
    public static String TAG_SYNC_FREQUENCY_POSITION = "syncFrequencyPosition";

    //Animation Constants
    public static final int PROGRESS_ANIM_TIME = 1000;

    // View Tags
    public static final String VIEW_CONTACT_PROFILE = "contactProfile";
    public static final String VIEW_SETTINGS = "settings";
    public static final String VIEW_PROFILE = "profile";

    public static final String TAG_SZ_TOKEN = String.format("MY_CONTACT", System.currentTimeMillis());

    //Parse Constants
    public static final String TAG_PARSE_NAME = "GBACARD_PARSE";
    public static final String KEY_PARSE_OBJECT_ID = "objectId";
    public static final String KEY_PARSE_FIRST_NAME = "firstName";
    public static final String KEY_PARSE_LAST_NAME = "lastName";
    public static final String KEY_PARSE_EMAIL = "email";
    public static final String KEY_PARSE_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_PARSE_ADDRESS = "address";
    public static final String KEY_PARSE_PASSWORD = "password";

    // Permission Constants
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 120;
    public static final int REQUEST_CODE_CAMERA_AND_GALLERY_PERMISSIONS = 121;
    public static final int REQUEST_CODE_ASK_CALL_PHONE_PERMISSIONS = 122;
    public static final int REQUEST_CODE_ASK_SMS_PHONE_PERMISSIONS = 123;
    public static final int REQUEST_CODE_ASK_CONTACTS_PERMISSIONS = 124;
    public static final int REQUEST_CODE_ASK_CAMERA_PERMISSIONS = 125;


    //Image Upload Selection
    public static final int TAKE_PICTURE = 1;
    public static final int ACTIVITY_SELECT_IMAGE = 2;

    //Select Country
    public static final int SELECT_COUNTRY = 3;

    public static String TAKE_PHOTO = "Take Photo";
    public static String CHOOSE_FROM_GALLERY = "Choose from Gallery";
    public static String CANCEL = "Cancel";

    //GbaCard Path on SD Card
    public static final String GBACARD = "/Gbacard/";
    // public static String GBACARD_ABSOLUTE_PATH = "";
    public static final String GBACARD_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + GBACARD;
    public static final String GBACARD_INTERNAL_SD_CARD_PATH = "";

    //Images Path on SD Card
    public static final String CAPTURED_IMAGES = "Captured Images/";
    public static final String CAPTURED_IMAGES_PATH = GBACARD_ABSOLUTE_PATH + CAPTURED_IMAGES;
    public static final String SAVED_IMAGE = "Saved Images/";
    public static final String SAVED_IMAGES_PATH = GBACARD_ABSOLUTE_PATH + SAVED_IMAGE;

    //Tesseract Data Path on SD Card
    public static final String TESSERACT = "Tesseract/";
    public static final String TESSERACT_MAIN_DIR = GBACARD_ABSOLUTE_PATH + TESSERACT;
    public static final String TESSERACT_SUB_DIR = "Tessdata";
    public static final String TESSERACT_PATH = GBACARD_ABSOLUTE_PATH + TESSERACT_SUB_DIR;

    public static final String TESSERACT_ENGLISH_TRAINED_DATA = "eng.traineddata";

    //Contacts Path on SD Card
    public static final String CONTACTS = "Contacts/";
    public static final String CONTACTS_PATH = GBACARD_ABSOLUTE_PATH + CONTACTS;

    //Bluetooth Tags
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 90;

    //Notification Tags
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    //All SMS Tags
    public static final String SENT = "sent";
    public static final String DELIVERED = "delivered";

    //All EndPoints
    //public static final String BASE_ENDPOINT = "http://192.168.8.101/locateme/";
    public static final String BASE_ENDPOINT = "http://niboapp.com/nibo_mobile/";
    //public static final String BASE_ENDPOINT = "http://www.gbacard.com/mobile_app/";
    public static final String REGISTER_URL = BASE_ENDPOINT + "register.php";
    public static final String EDIT_PROFILE_URL = BASE_ENDPOINT + "edit_profile.php";
    public static final String EDIT_PROFILE_USER_URL = BASE_ENDPOINT + "edit_profile_user.php";
    public static final String EDIT_NIBO_HOUSE_CODE = BASE_ENDPOINT + "edit_nibo_house_code.php";
    public static final String REQUEST_HOUSE_CODE_URL = BASE_ENDPOINT + "request_house_code.php";
    public static final String ACTIVATE_HOUSE_CODE_URL = BASE_ENDPOINT + "activate_house_code.php";
    public static final String EDIT_PROFILE_URL_AFTER_DELETE = BASE_ENDPOINT + "edit_profile_after_delete.php";
    public static final String SEND_FEEDBACK_URL = BASE_ENDPOINT + "send_feedback.php";
    public static final String GET_CONTACT_BY_PHONE_NUMBER_URL = BASE_ENDPOINT + "get_user_contact.php";
    public static final String GET_HOUSE_BY_PERMANENT_CODE_URL = BASE_ENDPOINT + "get_house_location.php";
    public static final String GET_HOUSE_BY_SPECIAL_CODE_URL = BASE_ENDPOINT + "get_special_house_location.php";
    public static final String GET_USER_BY_EMAIL = BASE_ENDPOINT + "forgot_password.php";
    public static final String UPDATE_PASSWORD = BASE_ENDPOINT + "update_password.php";
    public static final String ADD_CONTACT_TO_SERVER_URL = BASE_ENDPOINT + "add_contact.php";
    public static final String LOGIN_URL = BASE_ENDPOINT + "login.php";
    public static final String GET_COUNT_OF_UNSYNCED_ROWS_URL = BASE_ENDPOINT + "get_unsynced_row_count.php";
    public static final String GET_MYSQL_DB_USERS_URL = BASE_ENDPOINT + "get_all_users.php";
    public static final String UPDATE_SYNC_STATUS_URL = BASE_ENDPOINT + "update_sync_status.php";
    public static final String DELETE_CONTACT_URL = BASE_ENDPOINT + "delete_contact.php";
    public static final String CHECK_PRIMARY_PHONE_NUMBER_URL = BASE_ENDPOINT + "check_primary_phone_number.php";
    public static final String RETRIEVE_PASSWORD_URL = BASE_ENDPOINT + "forgot_password.php";
    public static final String CONFIRM_CONFIRMATION_CODE_URL = BASE_ENDPOINT + "confirm_confirmation_code.php";
    public static final String CHANGE_PASSWORD_URL = BASE_ENDPOINT + "change_password.php";
    public static final String GET_ALL_USER_SAVED_LOCATIONS_URL = BASE_ENDPOINT + "get_all_user_saved_locations.php";
    public static final String INSERT_NEW_SAVED_LOCATION_TO_SYNC = BASE_ENDPOINT + "insert_new_saved_location_to_sync.php";
    public static final String UPDATE_SAVED_LOCATION_TO_SYNC = BASE_ENDPOINT + "update_saved_location_to_sync.php";
    public static final String SEARCH_TEMPORARY_NIBO_CODE_URL = BASE_ENDPOINT + "search_temporary_nibo_code.php";
    public static final String SEARCH_PERMANENT_NIBO_CODE_URL = BASE_ENDPOINT + "search_permanent_nibo_code.php";
    public static final String SEARCH_SPECIAL_NIBO_CODE_URL = BASE_ENDPOINT + "search_special_nibo_code.php";

    //Scan Constants
    public static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    public static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    public static final String[] ZXING_URLS = { "http://zxing.appspot.com/scan", "zxing://scan/" };

    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    // SQLite Constants
    // Database Version
    public static final int DATABASE_VERSION = 3;

    // Database Name
    public static final String DATABASE_NAME = "gbacardContacts";

    // Contacts table name
    public static final String TABLE_CONTACTS = "contacts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "fullName";
    public static final String COLUMN_PRIMARY_PHONE_NUMBER = "primaryPhoneNumber";
    public static final String COLUMN_SECONDARY_PHONE_NUMBER = "secondaryPhoneNumber";
    public static final String COLUMN_CONCATENATED_PHONE_NUMBERS = "concatenatedPhoneNumbers";
    public static final String COLUMN_PRIMARY_EMAIL = "primaryEmail";
    public static final String COLUMN_SECONDARY_EMAIL = "secondaryEmail";
    public static final String COLUMN_CONCATENATED_EMAILS = "concatenatedEmails";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_COMPANY_NAME = "companyName";
    public static final String COLUMN_JOB_TITLE = "jobTitle";
    public static final String COLUMN_COMPANY_WEBSITE = "companyWebsite";
    public static final String COLUMN_DATE_CREATED = "dateCreated";
    public static final String COLUMN_DATE_UPDATED = "dateUpdated";
    public static final String COLUMN_CREATED_BY = "createdBy";
    public static final String COLUMN_IS_DELETED = "isDeleted";
    public static final String COLUMN_USER_PHOTO = "userPhoto";

    //Urls
    public static final String HELP_AND_FAQ_URL = "http://gbacard.com/faqs/";
    public static final String TERMS_OF_USE_URL = "http://gbacard.com/security/";
    public static final String PRIVACY_POLICY_URL = "http://gbacard.com/security/";
}
