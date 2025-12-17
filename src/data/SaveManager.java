package data;
import java.io.*;
import java.util.Properties;

public class SaveManager {
    private static SaveManager instance;
    private Properties prop;
    private final String FILE_PATH = "save_data.properties"; // 프로젝트 루트에 저장됨

    public SaveManager() {
        prop = new Properties();
        load();
    }

    // 파일에서 데이터 불러오기
    public void load() {
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            prop.load(fis);
        } catch (IOException e) {
            // 파일이 없으면 새로 생성되므로 무시
            System.out.println("기존 세이브 파일이 없습니다. 새로 생성합니다.");
        }
    }

    // 파일에 데이터 저장하기
    public void save() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            prop.store(fos, "Game Save Data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 최고 기록 가져오기 (없으면 -1 반환)
    public long getBestTime(String stageName) {
        String val = prop.getProperty(stageName);
        if (val == null) return -1;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // 최고 기록 저장하기 (기존 기록보다 빠를 경우에만 갱신)
    public void setBestTime(String stageName, long time) {
        long currentBest = getBestTime(stageName);
        // 기록이 없거나(-1), 새로운 기록이 더 짧을 때 저장
        if (currentBest == -1 || time < currentBest) {
            prop.setProperty(stageName, String.valueOf(time));
            save();
            System.out.println(stageName + " 신기록 저장됨: " + time + "ms");
        }
    }
}