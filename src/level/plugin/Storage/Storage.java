package level.plugin.Storage;

public interface Storage {

    boolean setPoints(String uuid, int points);

    boolean setLevel(String uuid, int level);

    int getlevel(String uuid);

    int getPoints(String uuid);

}
