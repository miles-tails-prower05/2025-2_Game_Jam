package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class eventScenePanel extends JPanel {
    // --- 레이아웃 관련 컴포넌트 ---
    private JLabel mainImageLabel;   // 상단 컷씬 이미지
    private JLabel faceImageLabel;   // 하단 왼쪽 캐릭터 얼굴
    private JLabel nameLabel;        // 하단 캐릭터 이름
    private JTextArea dialogArea;    // 하단 대사 텍스트
    private JPanel textPanel;        // 텍스트와 이름을 담을 패널
    private JButton skipButton;      // 스킵 버튼
    private JButton nextButton;      // 다음(투명 버튼, 전체 화면 클릭용)

    // --- 데이터 및 상태 변수 ---
    private Container frame;
    private CardLayout cards;
    
    // 컷씬 데이터 구조체
    private class SceneData {
        String mainImg;
        String faceImg;
        String name;
        String dialogue;
        
        public SceneData(String m, String f, String n, String d) {
            this.mainImg = m; this.faceImg = f; this.name = n; this.dialogue = d;
        }
    }
    
    private ArrayList<SceneData> sceneList; // 파싱된 대사 리스트
    private int currentIndex = 0;           // 현재 대사 인덱스
    private String nextViewName;            // 컷씬 종료 후 이동할 화면 이름 (예: "GAME", "SELECT")

    // --- 생성자 ---
    public eventScenePanel(Container frame, CardLayout cards) {
        this.frame = frame;
        this.cards = cards;
        this.sceneList = new ArrayList<>();

        setLayout(null); // 자유 배치를 위해 null layout 사용
        setPreferredSize(new Dimension(1280, 720));
        setBackground(Color.BLACK);

        initUI();
    }

    // UI 초기화
    private void initUI() {
        // 1. 메인 이미지 영역 (상단)
        mainImageLabel = new JLabel();
        mainImageLabel.setBounds(0, 0, 1280, 520);
        mainImageLabel.setOpaque(true);
        mainImageLabel.setBackground(new Color(20, 30, 40)); // 이미지 없을 때 기본색
        mainImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(mainImageLabel);

        // 2. 하단 UI 영역 배경 (얼굴 + 텍스트)
        JPanel bottomPanel = new JPanel(null);
        bottomPanel.setBounds(0, 520, 1280, 200);
        bottomPanel.setBackground(new Color(0, 50, 80)); // 짙은 청록색 (이미지 참고)
        add(bottomPanel);

        // 3. 얼굴 이미지 영역 (하단 왼쪽)
        faceImageLabel = new JLabel();
        faceImageLabel.setBounds(0, 0, 200, 200);
        faceImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        faceImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(faceImageLabel);

        // 4. 이름 영역 (얼굴 우측 상단)
        nameLabel = new JLabel("캐릭터 이름");
        nameLabel.setBounds(210, 10, 1000, 40);
        nameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        bottomPanel.add(nameLabel);

        // 5. 대사 영역 (얼굴 우측 하단)
        dialogArea = new JTextArea("대사가 출력되는 공간입니다.");
        dialogArea.setBounds(210, 60, 1020, 130);
        dialogArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 20));
        dialogArea.setForeground(Color.WHITE);
        dialogArea.setBackground(new Color(0, 0, 0, 0)); // 배경 투명
        dialogArea.setOpaque(false);
        dialogArea.setLineWrap(true);       // 자동 줄바꿈
        dialogArea.setWrapStyleWord(true);  // 단어 단위 줄바꿈
        dialogArea.setEditable(false);      // 수정 불가
        bottomPanel.add(dialogArea);

        // 6. 스킵 버튼 (우측 상단)
        skipButton = new JButton("SKIP >>");
        skipButton.setBounds(1150, 20, 100, 40);
        skipButton.setBackground(new Color(0, 0, 0, 150));
        skipButton.setForeground(Color.WHITE);
        skipButton.setFocusable(false);
        skipButton.addActionListener(e -> endCutscene());
        add(skipButton);
        
        // 7. 화면 전체 클릭 시 다음 대사로 넘어가는 투명 버튼 (또는 MouseListener)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showNextLine();
            }
        });

        // 8. 키보드(스페이스바) 입력 처리
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    showNextLine();
                }
            }
        });
        
        // 컴포넌트 순서 정리 (버튼이 위에 오도록)
        setComponentZOrder(skipButton, 0);
        setComponentZOrder(mainImageLabel, 1);
    }

    // --- 외부 호출 메서드: 컷씬 시작 ---
    // scriptFileName: ui 패키지 안의 txt 파일 이름 (예: "intro_script.txt")
    // nextView: 컷씬 종료 후 이동할 카드의 이름 (예: "GAME", "SELECT")
    public void startCutscene(String scriptFileName, String nextView) {
        this.nextViewName = nextView;
        this.currentIndex = 0;
        this.sceneList.clear();
        
        loadScriptFile("eventSceneData/" + scriptFileName); // 파일 로딩
        
        if (!sceneList.isEmpty()) {
            updateUI(sceneList.get(0)); // 첫 장면 표시
            cards.show(frame, "CUTSCENE"); // CardLayout 이름은 GameMain에서 등록한 이름이어야 함
            this.requestFocus(); // 키 입력을 위해 포커스
        } else {
            System.out.println("스크립트 파일이 비어있거나 로드 실패: " + scriptFileName);
            endCutscene(); // 데이터 없으면 바로 종료
        }
    }

    // 텍스트 파일 파싱
    // 파일 형식: 메인이미지|얼굴이미지|이름|대사
    private void loadScriptFile(String fileName) {
        try {
            // src/ui/ 폴더 내의 파일을 읽음
            InputStream is = getClass().getResourceAsStream("/ui/" + fileName);
            if (is == null) {
                System.out.println("파일을 찾을 수 없습니다: /ui/" + fileName);
                return;
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue; // 공백이나 주석(#) 무시
                
                String[] parts = line.split("\\|"); // 파이프(|)로 구분
                if (parts.length >= 4) {
                    sceneList.add(new SceneData(parts[0], parts[1], parts[2], parts[3]));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 다음 대사 출력
    private void showNextLine() {
        currentIndex++;
        if (currentIndex < sceneList.size()) {
            updateUI(sceneList.get(currentIndex));
        } else {
            endCutscene();
        }
    }

    // UI 업데이트 (이미지 및 텍스트 변경)
    private void updateUI(SceneData data) {
        nameLabel.setText(data.name);
        dialogArea.setText(data.dialogue);

        // 이미지 로드 (경로는 프로젝트 상황에 맞게 조정 필요, 여기선 src/images/ 라고 가정하거나 루트 등)
        // 실제로는 getClass().getResource("/images/" + data.mainImg) 방식을 추천
        setAttributesImage(mainImageLabel, data.mainImg);
        setAttributesImage(faceImageLabel, data.faceImg);
    }

    // 이미지 설정 헬퍼 메서드
    private void setAttributesImage(JLabel label, String imageName) {
        if (imageName == null || imageName.equals("null") || imageName.isEmpty()) {
            label.setIcon(null);
            label.setText("No Image");
            return;
        }
        
        // 이미지 파일 경로: src/images 폴더 안에 있다고 가정 (필요시 경로 수정)
        // 예: /images/bg_01.png
        java.net.URL imgUrl = getClass().getResource("/ui/images/" + imageName);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // 라벨 크기에 맞게 이미지 리사이징
            Image img = icon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
            label.setText("");
        } else {
            // 이미지를 못 찾았을 때 텍스트 표시
            label.setIcon(null);
            label.setText(imageName); 
        }
    }

    // 컷씬 종료
    private void endCutscene() {
        // 타이머나 애니메이션이 있다면 여기서 정지
        cards.show(frame, nextViewName);
        
        // 만약 게임 화면으로 돌아간다면 게임에 포커스를 줘야 함
        // 이 부분은 구조상 GameMain 등에서 처리되거나, 
        // Panel 전환 후 해당 패널이 requestFocusInWindow()를 호출해야 함.
    }
}