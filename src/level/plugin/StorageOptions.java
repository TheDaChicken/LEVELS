package level.plugin;

public enum StorageOptions {

    FILE, MYSQL;

    public static StorageOptions state;

    public static boolean isStorageOption(StorageOptions state) {
        return StorageOptions.state == state;
    }

    public static StorageOptions getStorageOption() {
        return state;
    }

    public static void setStorageOption(StorageOptions state) {
        StorageOptions.state = state;
    }

}
