# Stack Arena - Android Tetris Game Project Documentation

**Project**: Stack Arena (Custom Tetris Implementation)  
**Language**: Java  
**Platform**: Android 7.0+ (API 24, targeting API 35)  
**Build System**: Gradle (Android Gradle Plugin 8.5.1)  
**Package**: com.stackarena.game  
**Version**: 2.0 (versionCode: 2)

---

## 1. PROJECT OVERVIEW

Stack Arena is a feature-rich Android Tetris variant with modern graphics, adaptive music, and neon cyberpunk styling. The game implements classic Tetris mechanics with 7 custom tetromino shapes, 9-level difficulty scaling, and persistence features.

### Key Features
- 10×20 game board (standard Tetris)
- 7 tetromino types (A-G, custom shapes)
- Adjustable difficulty (9 speed levels, 0-9 pre-filled starting lines)
- Procedural audio synthesis (no external sound files)
- Adaptive background music that changes tempo with game stress
- High score persistence (top 10 with timestamps)
- Neon cyberpunk visual theme
- Starfield and nebula background animations
- 3D-style block rendering with shadows and gradients

---

## 2. DIRECTORY STRUCTURE

```
Stack-Arena/
├── build.gradle                    # Root build config (AGP 8.5.1)
├── settings.gradle                 # Project settings & repositories
├── gradle/                         # Gradle wrapper files
└── app/
    ├── build.gradle               # App build config (compileSdk 35, minSdk 24)
    ├── src/main/
    │   ├── AndroidManifest.xml    # 2 activities, portrait orientation
    │   ├── java/com/stackarena/game/
    │   │   ├── MainActivity.java              (466 lines) - Game controller
    │   │   ├── TetrisGame.java                (292 lines) - Game logic
    │   │   ├── TetrisBoard.java               (209 lines) - Board state (10×20)
    │   │   ├── TetrisView.java                (550 lines) - Custom rendering
    │   │   ├── TetrisPiece.java               (180 lines) - Piece model (7 types)
    │   │   ├── SoundManager.java              (312 lines) - Audio synthesis
    │   │   ├── HighScoreManager.java          (113 lines) - Score persistence
    │   │   ├── HighScoresActivity.java        (128 lines) - Scores display
    │   │   └── CustomToast.java               (34 lines)  - Toast utility
    │   └── res/
    │       ├── layout/             (3 XML layouts)
    │       │   ├── activity_main.xml          (305 lines)
    │       │   ├── activity_high_scores.xml   (60 lines)
    │       │   └── custom_toast.xml
    │       ├── drawable/           (17 SVG vector XMLs)
    │       │   ├── Game controls: ic_arrow_left/right, ic_rotate, ic_drop
    │       │   ├── Buttons: button_background*, close_button_background
    │       │   ├── UI: score_background, high_score_entry*
    │       │   ├── Background: game_background
    │       │   └── Launcher: ic_launcher_* variants
    │       ├── menu/
    │       │   └── game_menu.xml    (4 menu items)
    │       ├── values/
    │       │   ├── colors.xml       (Neon palette)
    │       │   ├── strings.xml      (28 string resources)
    │       │   └── themes.xml       (Dark theme with neon accents)
    │       ├── mipmap-*/            (Icon DPI variants: xxhdpi, xhdpi, hdpi, mdpi, anydpi-v26, xxxhdpi)
    │       └── layout/custom_toast.xml
    └── build/                      (Compiled output - ignored in git)
```

---

## 3. JAVA CLASSES & ARCHITECTURE

### Core Game Engine

#### **TetrisGame.java** (292 lines)
**Role**: Game state machine and logic engine

**Key Responsibilities**:
- Manage current and next piece (TetrisPiece instances)
- Handle piece movements (moveLeft, moveRight, rotate, moveDown)
- Collision detection via TetrisBoard
- Score calculation (100-800 points per line based on quantity and level)
- Level progression (1 level per 1000 points)
- Game over detection
- Piece placement and line clearing

**Key Methods**:
- `moveLeft()`, `moveRight()` - Movement with collision checking
- `rotate()` - 5-offset wall kick system (attempts offsets: 0, -1, 1, -2, 2)
- `moveDown()` - Gravity-based movement, returns piece placement
- `updateScore(int linesCleared)` - Multiplies by level
- `getGhostPiece()` - Calculates landing position
- `updateMusicSpeed()` - Adapts music tempo based on board fill level

**GameListener Interface**:
```java
public interface GameListener {
    void onScoreChanged(int score);
    void onLevelChanged(int level);
    void onGameOver();
    void onBoardChanged();
    void onLinesClearing(int[] lines);
}
```

**Data Fields**:
- `TetrisBoard board` - Game board state
- `TetrisPiece currentPiece`, `nextPiece` - Active pieces
- `int score`, `level`, `speed` - Game state
- `boolean gameOver`, `paused` - Game flags
- `SoundManager soundManager` - Audio callbacks

---

#### **TetrisBoard.java** (209 lines)
**Role**: Board state management and collision detection

**Responsibilities**:
- Maintain 10×20 grid of blocks and colors
- Validate piece positions (boundary checks, collision detection)
- Place pieces on board
- Detect and clear full lines
- Add pre-filled starting lines with random gaps
- Calculate board fill level for music adaptation

**Key Methods**:
- `isValidPosition(TetrisPiece)` - Checks boundaries and collisions
- `placePiece(TetrisPiece)` - Locks piece onto board
- `clearLines()` - Removes full rows and shifts down
- `addStartingLines(int numLines)` - Pre-fills board with random gaps
- `getBoardFillLevel()` - Returns 0.0-1.0 based on highest block position

**Data Structure**:
```java
private int[][] board;    // ROWS=20, COLS=10 (0=empty, 1=filled)
private int[][] colors;   // Parallel color array for rendering
```

**Constants**:
- `ROWS = 20` (standard Tetris height)
- `COLS = 10` (standard Tetris width)

---

#### **TetrisPiece.java** (180 lines)
**Role**: Tetromino piece definition and manipulation

**7 Piece Types (A-G)** - Custom shapes (not standard Tetris):
- **A**: 2-block line (XX)
- **B**: 3-block L (XX / .X)
- **C**: 5-block (XX / XX / .X)
- **D**: 5-block zigzag (XX / .X / XX)
- **E**: 5-block line (XXXXX)
- **F**: 5-block inverted L (XXX / ..X / ..X)
- **G**: 5-block plus/cross (. X . / X X X / . X .)

**Colors** (Neon palette):
- A: Neon Cyan (#00D9FF)
- B: Neon Green (#39FF14)
- C: Neon Purple (#B026FF)
- D: Neon Pink (#FF006E)
- E: Neon Orange (#FF9E00)
- F: Electric Cyan (#00FFF5)
- G: Deep Pink (#FF146B)

**Key Methods**:
- `rotate()` - Clockwise rotation via matrix transpose
- `moveLeft()`, `moveRight()`, `moveDown()` - Position adjustment
- `copy()` - Create independent copy (for validation without side effects)

**Data Fields**:
- `int[][] shape` - 2D array representing piece blocks
- `int x, y` - Board position (y=-1 at spawn, above visible board)
- `int color` - RGB color value

---

#### **TetrisView.java** (550 lines)
**Role**: Custom Canvas-based renderer for game graphics

**Rendering Features**:
- **Starfield**: 200+ parallax stars with 3 speed categories (distant/medium/fast)
- **Nebulas**: 3 large slowly-drifting translucent clouds
- **Board**: 10×20 grid with neon border glow
- **Blocks**: 3D effect with gradient fill, highlight, shadow
- **Pieces**: Current piece + ghost piece preview
- **Next Piece**: Preview panel to the right of board
- **Animations**: Line clear flash, continuous starfield motion

**Inner Classes**:
```java
private class Star {
    float x, y, speed, size;
    int alpha, color;
    // Depth effect: slow/medium/fast speeds, varying brightness
    void update() { y += speed; }  // Scrolls down
}

private class Nebula {
    float x, y, speed, radius;
    int color, alpha;
    // Very transparent, slowly drifting background
}
```

**Key Methods**:
- `onDraw(Canvas)` - Main render method
- `draw3DBlock()` - Renders individual block with gradient and 3D effects
- `drawNextPiece()` - Renders piece preview
- `startLineClearAnimation()` - Triggers white flash effect
- `setGame()` - Connects to TetrisGame instance

**Paint Objects**:
- `paint`, `gridPaint`, `borderPaint` - Basic shapes
- `ghostPaint` - Semi-transparent outline for ghost piece
- `starPaint`, `nebulaPaint` - Animation effects
- `flashPaint` - Line clear animation

**Dimensions**:
- Board occupies center ~80% width, ~90% height of view
- Block size auto-calculated to fit board
- Margins calculated for centering

---

### Activity Classes

#### **MainActivity.java** (466 lines)
**Role**: Primary game controller, UI hub, lifecycle manager

**Features**:
1. **Speed Selection Panel** (visible at start)
   - SeekBar: 1-9 speed levels (1000ms → 200ms game tick)
   - SeekBar: 0-9 starting lines (pre-filled difficulty)
   - Start Game button

2. **Game Play Panel** (visible during game)
   - Score display (dynamic, updated via listener)
   - Level display (dynamic)
   - Control buttons: New Game, Pause/Resume, High Scores
   - TetrisView custom canvas
   - 4 game control buttons in RelativeLayout

3. **Game Loop**
   - Handler-based timer (not frame-locked)
   - Speed-dependent delay: `Math.max(200, 1100 - (speed × 100))`
   - Called via `gameRunnable` Runnable

4. **Down Button Hold-to-Accelerate**
   - Progressive acceleration based on hold duration
   - Initial: 100ms between moves
   - After 30+ presses: 20ms (max speed)
   - Uses separate `downHandler` with `downRunnable`

5. **Preferences Persistence**
   - SharedPreferences: `StackArenaPrefs`
   - Keys: `speed`, `startingLines`
   - Loaded on init, saved on user change

6. **Audio Control**
   - SoundManager instance for sound effects
   - Background music starts/stops with game
   - Music paused on app pause, resumed on app resume
   - Mute toggle via options menu

7. **Options Menu** (4 items)
   - New Game
   - Pause/Resume (text changes dynamically)
   - High Scores (launches HighScoresActivity)
   - Mute/Unmute (text changes dynamically)

**Lifecycle Hooks**:
```java
onCreate()         → Initialize views, setup listeners
onResume()        → Resume music if game is playing
onPause()         → Auto-pause game, pause music
onDestroy()       → Stop game loop, release audio resources
```

**Key Methods**:
- `startGame()` - Transition from selection to gameplay
- `startGameLoop()` - Begin Handler-based game ticking
- `startNewGame()` - Create new TetrisGame instance
- `togglePause()` - Toggle paused state, show toast
- `showSpeedSelection()` - Transition back to selection screen
- `onScoreChanged()`, `onLevelChanged()`, etc. - GameListener callbacks

**GameListener Callbacks**:
```java
onScoreChanged()   → Update tvScore TextView
onLevelChanged()   → Update tvLevel TextView
onGameOver()       → Stop game loop, save score, show toast
onBoardChanged()   → Call tetrisView.refresh() for re-render
onLinesClearing()  → Start line clear animation
```

---

#### **HighScoresActivity.java** (128 lines)
**Role**: Display-only high scores screen

**Features**:
- Fetch top 10 scores via HighScoreManager
- Display ranking (#1, #2, etc.)
- Color coding:
  - Top 3 (gold rank number): #FFD700
  - Remaining: Green rank (#4CAF50)
- Score formatted with thousands separator
- Level and timestamp for each entry
- Close button (✕) or back navigation
- Empty state message if no scores

**Layout**:
```
[HIGH SCORES]                [✕]
#1  Score: 5,000  Level: 8  Nov 15, 2024
#2  Score: 4,200  Level: 7  Nov 14, 2024
...
[ScrollView for many entries]
```

---

### Utility Classes

#### **SoundManager.java** (312 lines)
**Role**: Procedural audio synthesis and music management

**Audio Synthesis**:
- No external sound files
- All sounds generated programmatically
- **Sample Rate**: 22,050 Hz (low-quality retro game audio)
- **Format**: PCM 16-bit mono
- **Wave Types**: Square wave (SFX), Sine wave (music)

**Sound Effects**:
```java
playMove()      → 1200 Hz, 30ms (simple blip)
playRotate()    → 1400 Hz, 35ms (higher blip)
playDrop()      → 880-660 Hz descending, 60-80ms
playLineClear() → C-E-G-C-E ascending arpeggio, 500+ ms total
playGameOver()  → 523-440-349-262 Hz descending, 150-400ms
playLevelUp()   → 523-659-784-1047-1319 Hz ascending, 600ms
```

**Background Music**:
- Energetic arcade-style 33-note loop (~8.5 seconds)
- Runs in background thread
- Tempo controlled by `musicSpeed` (1.0x = normal, 1.5x = fast)
- **Music Speed Adaptation**:
  - Board empty: 1.0x
  - Board 40%+ full: 1.0-1.5x (interpolated)
  - Board 80%+ full: 1.5x (max speed)

**Key Methods**:
- `playSound(double[] freqs, int[] durations)` - Generic SFX
- `playTone(double frequency, int durationMs)` - Single tone synthesis
- `startBackgroundMusic()` - Start music thread
- `stopBackgroundMusic()` - Stop thread and release audio
- `pauseMusic()`, `resumeMusic()` - Pause without stopping
- `setMusicSpeed(float speed)` - Adjust music tempo
- `toggleMute()` - Switch mute state
- `release()` - Cleanup on activity destroy

**Audio Properties**:
- SFX volume: 12% of max (0.12 × 32767)
- Music volume: 18% of max (0.18 × 32767)
- SFX wave: Square (harsh, retro)
- Music wave: Sine (smooth)
- Envelope: 70% fade-out (SFX), 5% fade-in + 20% fade-out (music)

**Threading**:
- Separate thread for music generation
- Each tone blocks until completion (synchronized playback)
- Handler posts delayed cleanup for audio tracks

---

#### **HighScoreManager.java** (113 lines)
**Role**: High score persistence using SharedPreferences

**Storage**:
- SharedPreferences key: `TetrisHighScores`
- Format: Serialized strings (score,level,timestamp separated by semicolons)
- Max scores: Top 10 only

**Nested Class**:
```java
public static class ScoreEntry implements Comparable<ScoreEntry> {
    public int score;
    public int level;
    public long timestamp;
    
    // Comparator: sorts by score descending
    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }
}
```

**Key Methods**:
- `addScore(int score, int level)` - Add new score, maintain top 10
- `getHighScores()` - Fetch all scores, auto-sorted
- `isHighScore(int score)` - Check if score qualifies for top 10
- `getRank(int score)` - Get rank position if score added now
- `saveScores()` - Serialize to SharedPreferences
- Constructor: Takes Context, initializes SharedPreferences

---

#### **CustomToast.java** (34 lines)
**Role**: Centralized toast notification utility

**Features**:
- Custom layout-based toast (not system default)
- Centered on screen
- Custom styling via `custom_toast.xml`
- Methods: `show()`, `showShort()`, `showLong()`
- Note: Both short and long use SHORT duration (1s) by design

---

## 4. RESOURCES & STYLING

### Colors (neon_*.xml)
```xml
neon_blue       #00D9FF
neon_purple     #B026FF
neon_pink       #FF006E
neon_cyan       #00FFF5 (accent color)
neon_green      #39FF14
neon_orange     #FF9E00

dark_bg_primary    #0A0E27
dark_bg_secondary  #1A1F3A
dark_bg_tertiary   #2A2F4A
overlay_dark       #AA000000
overlay_glow       #33FFFFFF
```

### Theme (themes.xml)
- **Base**: Theme.AppCompat.NoActionBar (no action bar)
- **Primary**: neon_blue (#00D9FF)
- **Accent**: neon_cyan (#00FFF5)
- **Status Bar**: dark_bg_primary, no light text
- **Navigation Bar**: dark_bg_primary
- **Text Primary**: neon_cyan
- **Text Secondary**: neon_purple

### Strings (strings.xml)
28 string resources for all UI text, menu items, and messages.

### Layouts

**activity_main.xml** (305 lines):
```
LinearLayout (vertical, dark background)
├── Speed Selection Panel (initially visible)
│   ├── "GAME SETTINGS" title (cyan, shadow glow)
│   ├── Speed section (1-9, SeekBar, large display)
│   ├── Starting Lines section (0-9, SeekBar, large display)
│   └── "Start Game" button
└── Game Play Panel (initially gone)
    ├── Score & Level display (2-column layout with backgrounds)
    ├── Control buttons: New Game, Pause, High Scores
    ├── TetrisView (flex-grow, main canvas)
    └── Game controls in RelativeLayout
        ├── Left button (far left)
        ├── Rotate button (after left)
        ├── Right button (far right)
        └── Drop button (before right)
```

**activity_high_scores.xml** (60 lines):
```
LinearLayout (vertical, dark background)
├── Title bar
│   ├── "HIGH SCORES" (gold, centered)
│   └── Close button (✕)
└── ScrollView
    └── scoresContainer LinearLayout (dynamic entries)
```

### Drawables (17 XML vector files)

**Icons** (all SVG-like path definitions):
- `ic_arrow_left.xml`, `ic_arrow_right.xml` - D-pad arrows
- `ic_rotate.xml` - Curved arrow
- `ic_drop.xml` - Down arrow
- `ic_pause.xml`, `ic_play.xml` - Media controls
- `ic_launcher_foreground.xml`, `ic_launcher_background.xml` - App icon layers

**Buttons**:
- `button_background.xml` - Basic button style
- `button_background_enhanced.xml` - Enhanced with more effects
- `button_background_selector.xml` - State-based selector
- `close_button_background.xml` - Close button styling

**Game UI**:
- `score_background.xml` - Semi-transparent box for score/level
- `high_score_entry.xml` - Standard score entry background
- `high_score_entry_top.xml` - Top 3 entry background (gold tint)
- `game_background.xml` - Game field background
- `toast_background.xml` - Toast notification styling

---

## 5. BUILD CONFIGURATION

### Root build.gradle
```gradle
plugins {
    id 'com.android.application' version '8.5.1'
}
```

### app/build.gradle
```gradle
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.stackarena.game'
    compileSdk 35                          # Target Android 15
    
    defaultConfig {
        applicationId "com.stackarena.game"
        minSdk 24                          # Android 7.0
        targetSdk 35                       # Android 15
        versionCode 2
        versionName "2.0"
    }
    
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles 'proguard-android-optimize.txt'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

**Key Settings**:
- **Gradle Plugin**: 8.5.1 (latest stable)
- **Compile SDK**: 35 (Android 15, latest)
- **Min SDK**: 24 (Android 7.0 - older devices supported)
- **Target SDK**: 35 (Android 15)
- **Java**: Version 11 (modern Java features)
- **Proguard**: Disabled (no code obfuscation)

---

## 6. MANIFEST

### AndroidManifest.xml
```xml
<application>
    <activity android:name=".MainActivity"
        android:exported="true"
        android:screenOrientation="portrait">
        <!-- LAUNCHER intent filter -->
    </activity>
    
    <activity android:name=".HighScoresActivity"
        android:exported="false"
        android:screenOrientation="portrait"
        android:parentActivityName=".MainActivity"/>
</application>
```

**Key Configuration**:
- No permissions required (no internet, camera, GPS, etc.)
- Both activities portrait-only
- MainActivity is launcher (exported)
- HighScoresActivity is child activity (back button support)
- Supports API 31+
- Dark theme applied

---

## 7. GAME MECHANICS

### Scoring System
```
Lines Cleared  Points (before level multiplier)
    1          100 × level
    2          300 × level
    3          500 × level
    4          800 × level
```

### Difficulty Levels
```
Speed Setting  Game Tick Delay
    1 (slowest)  1000 ms
    2            900 ms
    3            800 ms
    4            700 ms
    5            600 ms
    6            500 ms
    7            400 ms
    8            300 ms
    9 (fastest)  200 ms
```

### Starting Difficulty
- **Starting Lines**: 0-9 (pre-fills board with gaps)
- Generated with 1-2 random gaps per line
- Uses tetromino colors (mixed)
- Forces faster skill requirement from start

### Level Progression
```
Levels increase every 1,000 points
Level = (score / 1,000) + 1
Max level theoretically unlimited
```

### Music Adaptation
```
Board Fill Level (0.0 = empty, 1.0 = top-to-bottom)
is calculated from: 1.0 - (highestBlockRow / 20)

Music Speed = 1.0 + ((fillLevel - 0.4) / 0.4) * 0.5
         = 1.0 at 0% fill
         = 1.25 at 40% fill (threshold)
         = 1.5 at 80%+ fill (max speed)

Effect: Music speeds up as board fills, increasing tension
```

---

## 8. GAME FLOW

### Entry Point
1. **App Launch** → MainActivity.onCreate()
2. **Speed Selection** → User selects speed (1-9) and starting lines (0-9)
3. **Start Game** → Transition to game play panel

### Game Loop (Active)
```
Handler posts gameRunnable every N milliseconds (speed-dependent)
  ↓
TetrisGame.moveDown()
  ↓
  IF collision:
    - Place piece on board
    - Check for full lines
    - Clear lines if found (notify view for animation)
    - Add next piece to current
    - Generate new next piece
    - Check game over (new piece valid?)
    IF game over:
      - Stop loop
      - Calculate final score
      - Save to high scores
      - Show game over toast
    ELSE:
      - Continue loop
  ELSE (no collision):
    - Move piece down
    - Notify view to redraw
```

### Control Flow
```
User Input (Button/Touch)
  ↓
MainActivity event handler
  ↓
TetrisGame.moveLeft/Right/Rotate/moveDown()
  ↓
TetrisBoard.isValidPosition(tempPiece)
  ↓
IF valid: execute move, notify listener
  ↓
MainActivity.onBoardChanged()
  ↓
TetrisView.refresh()
  ↓
onDraw(Canvas) with updated state
```

### Pause Behavior
- Game state frozen (TetrisGame.paused = true)
- Game loop continues but doesn't call moveDown()
- Music paused (SoundManager.pauseMusic())
- Can resume without losing state

---

## 9. ARCHITECTURE PATTERNS

### 1. **MVC Pattern**
- **Model**: TetrisGame, TetrisBoard, TetrisPiece (data & logic)
- **View**: TetrisView (rendering), Layout XMLs (UI)
- **Controller**: MainActivity (input handling & lifecycle)

### 2. **Observer Pattern**
```java
TetrisGame.GameListener interface
  ↑
  implements
  ↓
MainActivity listens for:
- onScoreChanged() → update tvScore
- onLevelChanged() → update tvLevel
- onGameOver() → stop loop, show toast
- onBoardChanged() → refresh TetrisView
- onLinesClearing() → start animation
```

### 3. **Immutability for Validation**
```java
// Before moving, validate with a copy
TetrisPiece temp = currentPiece.copy();
temp.moveLeft();
if (board.isValidPosition(temp)) {
    currentPiece.moveLeft();  // Apply
}
```

### 4. **State Machine**
- Game states: Playing, Paused, GameOver
- UI states: SpeedSelection, GamePlay
- Transitions managed by MainActivity

### 5. **Adapter Pattern** (implicit)
- TetrisView adapts game data to canvas rendering
- HighScoresActivity adapts ScoreEntry objects to UI

### 6. **Strategy Pattern**
- Different piece shapes (A-G) with different rotation mechanics
- Difficulty levels (1-9) with different tick speeds

---

## 10. KEY CODING CONVENTIONS

### Naming
- **Classes**: PascalCase (`MainActivity`, `TetrisGame`)
- **Methods**: camelCase (`moveLeft()`, `startGameLoop()`)
- **Variables**: camelCase (`currentPiece`, `blockSize`)
- **Constants**: UPPER_SNAKE_CASE (`ROWS`, `MAX_SCORES`, `SAMPLE_RATE`)
- **UI Elements**: Descriptive with prefix (`btnStart`, `tvScore`, `seekBarSpeed`)

### Organization
1. **Class Members** (top to bottom)
   - Static constants
   - Instance fields
   - Constructors
   - Public methods (grouped by functionality)
   - Private/protected methods

2. **MainActivity Organization**
   - Initialization block
   - Setup methods (UI, listeners)
   - Game control methods
   - GameListener implementations
   - Menu handlers
   - Lifecycle hooks (onPause, onResume, onDestroy)

### Android-Specific Conventions
- Activities inherit from AppCompatActivity
- SharedPreferences accessed via getSharedPreferences()
- Listeners implement interfaces (not lambda callbacks)
- View initialization in onCreate()
- Lifecycle-safe operations in appropriate callbacks

---

## 11. THREADING MODEL

### Main Thread
- UI operations (view updates, event handling)
- Handler posts to main looper
- View rendering (Canvas.onDraw runs on main)

### Game Loop Thread
- Handler-based, runs on main thread
- Handler.postDelayed() schedules next tick
- Non-blocking (unlike old frame-locked loops)

### Audio Threads
- SoundManager spawns background thread for music
- Each tone synthesis blocks the audio thread
- Music loop thread waits for tone playback to finish
- Volatile flags for thread-safe pause/mute control

### No Explicit Thread Management
- Handlers manage timing (cleaner than Thread.sleep)
- Background music loop runs in dedicated thread
- No thread pooling (single music thread per game)

---

## 12. PERSISTENCE

### SharedPreferences Storage

**Preferences (StackArenaPrefs)**
- `speed` (int): 1-9, default 1
- `startingLines` (int): 0-9, default 0

**High Scores (TetrisHighScores)**
- `high_scores` (String): Semicolon-separated entries
- Format: `score,level,timestamp;score2,level2,timestamp2;...`
- Auto-sorted by score descending
- Limited to top 10

### No Database
- Lightweight SharedPreferences sufficient for small data
- No SQL, no external DB
- Automatic persistence on apply()

---

## 13. PERFORMANCE CONSIDERATIONS

### Optimization Techniques
1. **Lazy Rendering**: Only redraw on state change (invalidate())
2. **Non-blocking Game Loop**: Handler-based, not frame-locked
3. **Efficient Board Checks**: 2D array lookups O(1) per cell
4. **Ghost Piece Cached**: Only recalculated during collision
5. **No Garbage**: Minimal allocations in game loop (reuse arrays)
6. **Canvas Painting**: Grouped draw operations, no excessive invalidation

### Potential Bottlenecks
- Starfield animation (200+ stars updated each frame ~33fps)
- Audio synthesis (real-time PCM generation, thread-blocking)
- Large string operations in score serialization (minor, only top 10)

### Battery Considerations
- Handler delays prevent CPU spinning
- Sleep between audio synthesis
- Pause music when app paused
- No wake locks held

---

## 14. ERROR HANDLING

### Try-Catch Blocks
- Audio thread InterruptedException handling
- SharedPreferences NumberFormatException on corrupt data
- AudioTrack release() exception suppression

### Null Checks
```java
if (game != null) { ... }
if (soundManager != null) { ... }
if (listener != null) { listener.onScoreChanged(...); }
```

### Resource Cleanup
```java
onDestroy() {
    stopGame();
    soundManager.release();
}
```

---

## 15. TESTING RECOMMENDATIONS

### Unit Tests
- TetrisBoard collision detection
- Piece rotation logic
- Score calculation formula
- Line clearing algorithm
- HighScoreManager serialization

### Integration Tests
- Game loop tick sequence
- GameListener callback order
- Piece-to-board interaction
- Audio synthesis output (optional)

### UI Tests
- MainActivity state transitions
- Button click handling
- SeekBar value changes
- HighScoresActivity display

### Manual Testing
- Long play sessions (memory leaks?)
- Speed 1 vs Speed 9 gameplay feel
- Pause/resume state persistence
- High score ranking accuracy

---

## 16. KNOWN BEHAVIORS & QUIRKS

1. **Custom Toast Duration**: Both SHORT and LONG use 1-second duration (not standard)
2. **Wall Kick Rotation**: 5-offset system allows rotation near walls (non-standard Tetris)
3. **Starting Position**: Pieces spawn at y=-1 (above board) for proper game over detection
4. **Music During Pause**: Stops, not paused (resumption restarts music)
5. **Down Button Acceleration**: Progressive, not uniform (UX improvement)
6. **No Hard Drop**: Drop button accelerates gravity, not instant placement

---

## 17. EXTENSION POINTS

### Easy to Add
- New piece types (extend TetrisPiece.PieceType enum)
- Different block visual styles (modify draw3DBlock)
- Additional sound effects (add method to SoundManager)
- More musical notes (extend playMusicLoop)
- Difficulty presets (speed + starting lines combos)

### Moderate Effort
- Piece hold feature (modify TetrisGame)
- Line clear animations (TetrisView already supports)
- Combo multipliers (enhance score calculation)
- Leaderboard backend (add network layer)
- Sound file support (replace synthesis with MediaPlayer)

### Difficult
- Multiplayer (requires network sync)
- Procedural level generation
- AI opponent
- 3D isometric view (major graphics rewrite)

---

## 18. DEPENDENCIES & LIBRARIES

### Core Android
- `android.app.Activity`
- `android.view.View`, `Canvas`
- `android.graphics.*` (Paint, Color, Gradient)
- `android.widget.*` (Button, SeekBar, etc.)
- `android.content.SharedPreferences`
- `android.media.AudioTrack`
- `android.os.Handler`, `Looper`

### AndroidX
- `androidx.appcompat.app.AppCompatActivity`
- `androidx.appcompat.app.ActionBarDrawerToggle` (if used)

### Material Design (transitive from AppCompat)
- Color palette, Material Design principles

### None Custom / Open Source
- **No external game engines** (Unity, Unreal)
- **No audio libraries** (OpenAL, libGDX, ExoPlayer)
- **No networking** (Retrofit, OkHttp)
- **No persistence** (Room, Firebase)
- **No JSON parsing** (Gson, Moshi)

---

## 19. QUICK START FOR DEVELOPERS

### Build & Run
```bash
./gradlew build                 # Compile
./gradlew installDebug          # Install on device
```

### Key Files to Edit
- Game logic changes → `TetrisGame.java`
- Graphics changes → `TetrisView.java`
- New sounds → `SoundManager.java`
- UI layout → `activity_main.xml`
- Colors → `res/values/colors.xml`

### Common Tasks
- **Adjust game speed**: Edit `SAMPLE_RATE` in SoundManager, delay in MainActivity.startGameLoop()
- **Change piece colors**: Edit TetrisPiece.initializeColor()
- **Modify board size**: Change `ROWS`, `COLS` in TetrisBoard
- **Add new piece type**: Add to TetrisPiece.PieceType enum, define shape and color
- **New sound effect**: Add method to SoundManager with frequency array

---

## 20. PROJECT STATISTICS

| Metric | Value |
|--------|-------|
| Total Java Code | ~2,100 lines |
| Classes | 9 |
| Activities | 2 |
| XML Resources | ~400 lines (layout + drawable + menu) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |
| Dependencies | 3 (AppCompat, Material, ConstraintLayout) |
| Permissions | 0 (no special permissions) |
| Audio Files | 0 (synthesized) |
| Board Size | 10 × 20 (standard) |
| Piece Types | 7 (custom) |
| Speed Levels | 9 |
| Starting Difficulty | 10 (0-9 lines) |
| High Score Slots | 10 |

---

## CONCLUSION

Stack Arena is a well-architected Android Tetris variant demonstrating:
- Clean separation of concerns
- Observer pattern for reactive UI updates
- Custom graphics rendering with Canvas
- Procedural audio synthesis
- Persistent state management
- Modern Android development practices
- Responsive game loop without frame-locking

The codebase is maintainable, extensible, and suitable for educational purposes or as a foundation for enhanced Tetris variants.
