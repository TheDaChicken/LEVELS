package level.plugin.Storage;

public class MySQL implements Storage {

    @Override
    public boolean setPoints(String uuid, int points) {
        return false;
    }

    @Override
    public boolean setLevel(String uuid, int level) {
        return false;
    }

    @Override
    public int getlevel(String uuid) {
        return 0;
    }

    @Override
    public int getPoints(String uuid) {
        return 0;
    }
}
