package level.plugin.Enums;

public enum LevelUpTypeOptions {

    SPECIFIC, MULTIPLIER;

    public static LevelUpTypeOptions state;

    public static boolean isLevelUpType(LevelUpTypeOptions state) {
        return LevelUpTypeOptions.state == state;
    }

    public static LevelUpTypeOptions getLevelUpType() {
        return state;
    }

    public static void setLevelUpType(LevelUpTypeOptions state) {
        LevelUpTypeOptions.state = state;
    }

    public static boolean parseLevelUpType(String text) {
        for (LevelUpTypeOptions levelUpOption : LevelUpTypeOptions.values()) {
            if (levelUpOption.name().equalsIgnoreCase(text)) {
                LevelUpTypeOptions.state = levelUpOption;
                return true;
            }
        }
        return false;
    }
}
