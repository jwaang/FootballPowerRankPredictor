package app;

public interface ServiceProvider {
    Object[][] GetPowerRankData(int Season, int Week);
    boolean AI(Object[][] PowerRank, String[] Values);
}
