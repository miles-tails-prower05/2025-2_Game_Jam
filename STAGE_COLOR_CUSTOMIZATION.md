# 스테이지별 색상 커스터마이징 기능

## 개요
각 스테이지마다 플랫폼, 가시, 부서지는 플랫폼, 스프링의 색상을 개별적으로 설정할 수 있는 시스템입니다.

## 기능 설명

### 지원되는 오브젝트
- **일반 플랫폼 (Platform)**: 기본 이동 플랫폼
- **가시 (Spike/Thorn)**: 플레이어에게 데미지를 주는 장애물
- **부서지는 플랫폼 (Breaking Platform)**: 플레이어가 밟으면 부서지는 플랫폼
- **스프링 (Spring)**: 플레이어를 튕겨주는 스프링보드

### 색상 지정 방법

스테이지 데이터 파일(`.txt`)에 `[COLORS]` 섹션을 추가하여 색상을 지정할 수 있습니다.

#### 지원되는 색상 형식

1. **Hex 형식**: `#RRGGBB`
   ```
   platformColor=#8B4513
   spikeColor=#FF4444
   ```

2. **RGB 형식**: `R,G,B` (0-255 범위)
   ```
   platformColor=139,69,19
   spikeColor=255,68,68
   ```

## 사용 예시

### Stage 2 (Hex 형식)
```
[PHYSICS]
gravity=0.6
jumpStrength=-12.0
speed=8.0
friction=0.8

[COLORS]
# Hex format: #RRGGBB or RGB format: R,G,B
# If not specified, default colors will be used
platformColor=#8B4513
spikeColor=#FF4444
breakablePlatformColor=#654321
springboardColor=#DAA520

[PLATFORMS]
# ... platform data ...
```

### Stage 3 (RGB 형식 - 수중 테마)
```
[PHYSICS]
gravity=0.35
jumpStrength=-9.5
speed=6.0
friction=0.9

[COLORS]
# RGB format for underwater stage theme
platformColor=70,130,180
spikeColor=255,69,0
breakablePlatformColor=100,149,237
springboardColor=0,191,255

[PLATFORMS]
# ... platform data ...
```

### Stage 4 (심해 테마)
```
[COLORS]
# Deep ocean theme with darker colors
platformColor=#2F4F4F
spikeColor=#DC143C
breakablePlatformColor=#1C3A5C
springboardColor=#4169E1
```

## 기본 색상

색상이 지정되지 않은 경우 다음 기본 색상이 사용됩니다:

| 오브젝트 | 기본 색상 | RGB 값 | Hex 값 |
|---------|---------|--------|--------|
| Platform | 갈색 (Brown) | RGB(180, 100, 50) | #B46432 |
| Spike | 밝은 회색 (Light Gray) | RGB(192, 192, 192) | #C0C0C0 |
| Breakable Platform | 어두운 갈색 (Dark Brown) | RGB(150, 80, 40) | #965028 |
| Springboard | 밝은 갈색 (Light Brown) | RGB(200, 120, 60) | #C8783C |

## 구현 세부 사항

### 클래스 구조

#### `StageColors.java`
- 스테이지별 색상 정보를 저장하는 클래스
- Hex 및 RGB 형식의 색상 파싱 지원
- 잘못된 색상 입력 시 기본 색상으로 자동 대체

#### `MapManager.java`
- 스테이지 데이터 파일에서 `[COLORS]` 섹션 파싱
- `StageColors` 객체 생성 및 관리
- `getStageColors()` 메서드를 통해 색상 정보 제공

#### `GamePanel.java`
- 렌더링 시 `MapManager`로부터 색상 정보를 가져와 적용
- 각 오브젝트 타입별로 적절한 색상 사용

## 특징

1. **하위 호환성**: `[COLORS]` 섹션이 없는 기존 스테이지 파일도 정상 작동 (기본 색상 사용)
2. **유연성**: Hex 또는 RGB 형식 중 선호하는 형식 선택 가능
3. **오류 처리**: 잘못된 색상 값 입력 시 자동으로 기본 색상 사용
4. **런타임 적용**: 스테이지 전환 시 자동으로 새로운 색상 설정 적용

## 테스트

구현된 기능은 다음 테스트 프로그램으로 검증되었습니다:

1. **StageColorsTest.java**: 색상 파싱 기능 테스트
2. **MapManagerColorTest.java**: 스테이지 파일에서 색상 로드 테스트

테스트 실행 방법:
```bash
javac -d bin -sourcepath src src/stage/StageColorsTest.java
java -cp bin stage.StageColorsTest

javac -d bin -sourcepath src src/stage/MapManagerColorTest.java
java -cp bin stage.MapManagerColorTest
```

## 향후 개선 가능 사항

1. 투명도(Alpha) 지원 추가
2. 그라데이션 효과 지원
3. 비주얼 에디터를 통한 색상 선택 UI
4. 색상 프리셋 라이브러리 제공
