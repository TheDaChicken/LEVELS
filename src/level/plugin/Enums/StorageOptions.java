package level.plugin.Enums;

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

    public static boolean parseStorage(String text) {
        for (StorageOptions storageOptions : StorageOptions.values()) {
            if (storageOptions.name().equalsIgnoreCase(text)) {
                StorageOptions.state = storageOptions;
                return true;
            }
        }
        return false;
    }
}
