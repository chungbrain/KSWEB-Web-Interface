# KSWEB Standalone App (Android)

이 프로젝트는 `KSWEB-Web-Interface` PHP 파일들을 Android 앱 내부에 포함하여, 단독(Standalone)으로 실행할 수 있도록 구성된 **Android 프로젝트의 핵심 소스 코드**입니다.

## 🛠️ 앱 동작 원리
1. 앱이 실행되면 `assets/php` (PHP 바이너리)와 `assets/www` (KSWEB-Web-Interface 소스) 폴더를 안드로이드 내부 저장소로 복사합니다.
2. PHP 바이너리에 실행 권한(`chmod +x`)을 부여합니다.
3. PHP의 내장 웹 서버를 사용하여 백그라운드에서 `127.0.0.1:8080` 포트로 서버를 엽니다.
4. 앱의 메인 화면인 `WebView`가 `http://127.0.0.1:8080`에 접속하여 인터페이스를 보여줍니다.

## 🚀 빌드(APK 생성) 방법
현재 환경에는 Android 스튜디오(Gradle 및 Android SDK)가 설치되어 있지 않아 직접 APK를 추출할 수 없습니다. 대신, 본인의 PC에서 아래 단계를 따라 **직접 APK를 생성**할 수 있습니다.

### 1단계: Android Studio 프로젝트 준비
1. PC에서 **Android Studio**를 실행합니다.
2. `New Project` -> **Empty Views Activity** (Kotlin)를 선택하여 새 프로젝트를 생성합니다.
   - Package name: `com.example.ksweb` (중요: 소스 코드의 패키지명과 일치해야 함)
   - Minimum SDK: API 24 이상 추천

### 2단계: 코드 및 파일 복사
방금 생성된 이 폴더(`KSWEB-Standalone`)에 있는 파일들을 Android Studio 프로젝트의 해당 경로에 덮어씁니다.
- `MainActivity.kt` ➡️ `app/src/main/java/com/example/ksweb/MainActivity.kt`
- `activity_main.xml` ➡️ `app/src/main/res/layout/activity_main.xml`
- `AndroidManifest.xml` ➡️ `app/src/main/AndroidManifest.xml`
- `www/` 폴더 ➡️ `app/src/main/assets/www/` (assets 폴더가 없다면 `src/main` 아래에 직접 생성)

### 3단계: Android용 PHP 바이너리 추가 (핵심)
이 앱이 작동하려면 **안드로이드 환경(aarch64)에서 실행 가능한 PHP 실행 파일**이 필요합니다.
1. [PMMP PHP-Binaries GitHub](https://github.com/pmmp/PHP-Binaries/releases) 등에 방문하여 Android `arm64`용 PHP 바이너리(예: `PHP-8.2-Android-aarch64...`)를 다운로드하고 압축을 해제합니다.
2. 압축 해제된 파일 중 **`php`** 실행 파일(확장자 없음)을 찾습니다.
3. 이 `php` 파일을 Android Studio 프로젝트의 `app/src/main/assets/php` 위치에 넣습니다. (파일 이름이 정확히 `php`여야 합니다).

### 4단계: 빌드 및 실행
1. Android Studio 상단의 🟢 **Run(실행)** 버튼을 눌러 핸드폰에 직접 설치하거나,
2. `Build` 메뉴 ➡️ `Build Bundle(s) / APK(s)` ➡️ `Build APK(s)`를 클릭하여 `.apk` 파일을 추출합니다.

## 주의사항
* 이 코드는 단일 아키텍처(arm64) 기준이며, 구형 폰(32bit)을 지원하려면 armeabi-v7a 용 php 바이너리를 추가로 처리하는 로직이 필요할 수 있습니다.
* PHP 내장 서버는 가벼운 용도이므로 대규모 트래픽 처리에는 적합하지 않습니다.