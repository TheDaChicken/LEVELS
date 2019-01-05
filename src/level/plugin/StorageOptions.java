package level.plugin;


public enum StorageOptions {

	MYSQL, FILE;

	public static StorageOptions state;

	public static void setStorageOption(StorageOptions state)
		{
			StorageOptions.state = state;
		}

	public static boolean isStorageOption(StorageOptions state)
		{
			return StorageOptions.state == state;
		}

	public static StorageOptions getStorageOption()
		{
			return state;
		}

}
