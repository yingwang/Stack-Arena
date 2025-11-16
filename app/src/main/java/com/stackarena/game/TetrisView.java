package com.stackarena.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TetrisView extends View {
    private TetrisGame game;
    private Paint paint;
    private Paint gridPaint;
    private Paint textPaint;
    private Paint highlightPaint;
    private Paint shadowPaint;
    private Paint borderPaint;
    private Paint flashPaint;
    private Paint ghostPaint;
    private float blockSize;
    private float offsetX;
    private float offsetY;
    private int[] clearingLines;
    private int flashAlpha = 0;
    private boolean isFlashing = false;

    // Star field animation
    private List<Star> stars;
    private List<Nebula> nebulas;
    private Random random;
    private Paint starPaint;
    private Paint nebulaPaint;

    public TetrisView(Context context) {
        super(context);
        init();
    }

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(Color.parseColor("#333333"));
        gridPaint.setStrokeWidth(2);
        gridPaint.setAntiAlias(true);

        highlightPaint = new Paint();
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setAntiAlias(true);

        shadowPaint = new Paint();
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#20000000"));
        shadowPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#4CAF50"));
        borderPaint.setStrokeWidth(4);
        borderPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        textPaint.setShadowLayer(5, 2, 2, Color.BLACK);

        flashPaint = new Paint();
        flashPaint.setStyle(Paint.Style.FILL);
        flashPaint.setAntiAlias(true);

        ghostPaint = new Paint();
        ghostPaint.setStyle(Paint.Style.STROKE);
        ghostPaint.setStrokeWidth(3);
        ghostPaint.setAntiAlias(true);
        ghostPaint.setAlpha(100); // 半透明虚线效果

        // Initialize star field
        starPaint = new Paint();
        starPaint.setAntiAlias(true);
        stars = new ArrayList<>();
        random = new Random();

        // Initialize nebulas (background space clouds)
        nebulaPaint = new Paint();
        nebulaPaint.setAntiAlias(true);
        nebulaPaint.setStyle(Paint.Style.FILL);
        nebulas = new ArrayList<>();

        // Stars and nebulas will be initialized in onSizeChanged when view dimensions are known
    }

    private void initStars() {
        // Create shooting stars/meteors
        for (int i = 0; i < 15; i++) {
            stars.add(new Star());
        }
    }

    private void initNebulas() {
        // Create a few large, slowly drifting nebula clouds
        for (int i = 0; i < 3; i++) {
            nebulas.add(new Nebula());
        }
    }

    private void updateStars() {
        // Update star positions
        for (Star star : stars) {
            star.update();
        }
        // Update nebula positions
        for (Nebula nebula : nebulas) {
            nebula.update();
        }
        invalidate();
        postDelayed(this::updateStars, 30); // 30ms = ~33fps
    }

    // Inner class for shooting stars (meteors)
    private class Star {
        float x, y;
        float speedX, speedY;
        float length;
        float thickness;
        int alpha;
        int color;
        long creationTime;
        long lifetime;

        Star() {
            reset();
        }

        void reset() {
            // Start from random position (usually top or left side)
            if (random.nextBoolean()) {
                x = random.nextFloat() * getWidth();
                y = -50;
            } else {
                x = -50;
                y = random.nextFloat() * getHeight() * 0.5f; // Upper half
            }

            // Diagonal movement (down-right direction mostly)
            float angle = (float) Math.toRadians(30 + random.nextFloat() * 60); // 30-90 degrees
            float speed = 8f + random.nextFloat() * 12f; // Fast meteors
            speedX = (float) Math.cos(angle) * speed;
            speedY = (float) Math.sin(angle) * speed;

            // Meteor tail length and thickness
            length = 40 + random.nextFloat() * 80; // 40-120 pixels tail
            thickness = 2f + random.nextFloat() * 3f; // 2-5 pixels thick
            alpha = 180 + random.nextInt(76); // 180-255 (bright)

            // Neon colors for meteors
            int colorChoice = random.nextInt(6);
            switch (colorChoice) {
                case 0: color = Color.parseColor("#00D9FF"); break; // Neon Cyan
                case 1: color = Color.parseColor("#B026FF"); break; // Neon Purple
                case 2: color = Color.parseColor("#FF006E"); break; // Neon Pink
                case 3: color = Color.parseColor("#00FFF5"); break; // Electric Cyan
                case 4: color = Color.parseColor("#FFFFFF"); break; // White
                default: color = Color.parseColor("#FFD700"); break; // Gold
            }

            creationTime = System.currentTimeMillis();
            lifetime = 2000 + random.nextInt(3000); // 2-5 seconds
        }

        void update() {
            x += speedX;
            y += speedY;

            // Reset when off screen or lifetime expired
            if (x > getWidth() + 100 || y > getHeight() + 100 ||
                System.currentTimeMillis() - creationTime > lifetime) {
                reset();
            }
        }

        void draw(Canvas canvas) {
            // Calculate tail end position
            float tailX = x - (speedX / Math.abs(speedX + speedY)) * length;
            float tailY = y - (speedY / Math.abs(speedX + speedY)) * length;

            // Draw meteor tail with gradient effect
            starPaint.setStrokeWidth(thickness);
            starPaint.setStyle(Paint.Style.STROKE);
            starPaint.setStrokeCap(Paint.Cap.ROUND);

            // Draw multiple tail segments for glow effect
            for (int i = 0; i < 3; i++) {
                float segmentAlpha = alpha * (1f - i * 0.3f);
                float segmentThickness = thickness * (1f - i * 0.2f);

                starPaint.setColor(color);
                starPaint.setAlpha((int) segmentAlpha);
                starPaint.setStrokeWidth(segmentThickness);

                if (i == 0) {
                    // Main tail with glow
                    starPaint.setShadowLayer(thickness * 2, 0, 0, color);
                } else {
                    starPaint.clearShadowLayer();
                }

                canvas.drawLine(x, y, tailX, tailY, starPaint);
            }

            // Draw bright head
            starPaint.setStyle(Paint.Style.FILL);
            starPaint.setColor(Color.WHITE);
            starPaint.setAlpha(alpha);
            starPaint.setShadowLayer(thickness * 3, 0, 0, color);
            canvas.drawCircle(x, y, thickness * 1.5f, starPaint);
        }
    }

    // Inner class for nebula clouds (background space atmosphere)
    private class Nebula {
        float x, y;
        float speed;
        float radius;
        int color;
        int alpha;

        Nebula() {
            reset();
        }

        void reset() {
            x = random.nextFloat() * getWidth();
            y = random.nextFloat() * getHeight();
            speed = 0.1f + random.nextFloat() * 0.3f; // Very slow drift
            radius = 80 + random.nextFloat() * 120; // Large radius (80-200)
            alpha = 15 + random.nextInt(25); // Very transparent (15-40)

            // Nebula colors - purple, cyan, pink tones
            int colorChoice = random.nextInt(4);
            switch (colorChoice) {
                case 0: color = Color.parseColor("#B026FF"); break; // Purple
                case 1: color = Color.parseColor("#00D9FF"); break; // Cyan
                case 2: color = Color.parseColor("#FF006E"); break; // Pink
                default: color = Color.parseColor("#00FFF5"); break; // Electric Cyan
            }
        }

        void update() {
            y += speed;
            // Reset when off screen
            if (y - radius > getHeight()) {
                x = random.nextFloat() * getWidth();
                y = -radius;
            }
        }

        void draw(Canvas canvas) {
            nebulaPaint.setColor(color);
            nebulaPaint.setAlpha(alpha);
            nebulaPaint.setShadowLayer(radius * 0.8f, 0, 0, color);
            canvas.drawCircle(x, y, radius, nebulaPaint);
        }
    }

    public void startLineClearAnimation(int[] lines) {
        this.clearingLines = lines;
        this.isFlashing = true;
        this.flashAlpha = 0;
        animateFlash();
    }

    private void animateFlash() {
        if (!isFlashing) return;

        flashAlpha += 40;
        if (flashAlpha > 255) {
            flashAlpha = 255;
            isFlashing = false;
            clearingLines = null;
        }

        invalidate();

        if (isFlashing) {
            postDelayed(this::animateFlash, 40);
        }
    }

    public void setGame(TetrisGame game) {
        this.game = game;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (game != null) {
            TetrisBoard board = game.getBoard();
            float boardWidth = w * 0.8f;
            float boardHeight = h * 0.9f;

            float blockWidth = boardWidth / board.getCols();
            float blockHeight = boardHeight / board.getRows();
            blockSize = Math.min(blockWidth, blockHeight);

            offsetX = (w - blockSize * board.getCols()) / 2;
            offsetY = (h - blockSize * board.getRows()) / 2;
        }

        // Initialize stars and nebulas now that we have view dimensions
        if (w > 0 && h > 0 && stars.isEmpty()) {
            initStars();
            initNebulas();
            // Start animation
            post(this::updateStars);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (game == null) return;

        // Draw background
        canvas.drawColor(Color.parseColor("#0F1419"));

        TetrisBoard board = game.getBoard();
        float boardLeft = offsetX;
        float boardTop = offsetY;
        float boardRight = offsetX + blockSize * board.getCols();
        float boardBottom = offsetY + blockSize * board.getRows();

        // Draw nebula clouds in background (except when centered on game board)
        for (Nebula nebula : nebulas) {
            // Nebulas are very transparent, so only skip if center is in game board
            boolean centerInGameBoard = (nebula.x >= boardLeft && nebula.x <= boardRight &&
                                        nebula.y >= boardTop && nebula.y <= boardBottom);
            if (!centerInGameBoard) {
                nebula.draw(canvas);
            }
        }

        // Draw animated star field across entire background (except game board area)
        for (Star star : stars) {
            // Draw stars everywhere except inside the game board rectangle
            boolean inGameBoard = (star.x >= boardLeft && star.x <= boardRight &&
                                  star.y >= boardTop && star.y <= boardBottom);
            if (!inGameBoard) {
                star.draw(canvas);
            }
        }

        int[][] boardState = board.getBoard();
        int[][] colors = board.getColors();

        // Draw neon glow border
        borderPaint.setColor(Color.parseColor("#00D9FF"));
        borderPaint.setShadowLayer(10, 0, 0, Color.parseColor("#5500D9FF"));
        float borderLeft = offsetX - 4;
        float borderTop = offsetY - 4;
        float borderRight = offsetX + blockSize * board.getCols() + 4;
        float borderBottom = offsetY + blockSize * board.getRows() + 4;
        canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, borderPaint);

        // Draw the board
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                float x = offsetX + j * blockSize;
                float y = offsetY + i * blockSize;

                // Draw grid
                canvas.drawRect(x, y, x + blockSize, y + blockSize, gridPaint);

                // Draw placed blocks with 3D effect
                if (boardState[i][j] != 0) {
                    draw3DBlock(canvas, x, y, blockSize, colors[i][j]);
                }
            }
        }

        // Draw ghost piece (shadow showing where piece will land)
        if (!game.isGameOver()) {
            TetrisPiece ghostPiece = game.getGhostPiece();
            if (ghostPiece != null) {
                int[][] ghostShape = ghostPiece.getShape();
                ghostPaint.setColor(game.getCurrentPiece().getColor());

                for (int i = 0; i < ghostShape.length; i++) {
                    for (int j = 0; j < ghostShape[i].length; j++) {
                        if (ghostShape[i][j] != 0) {
                            int boardX = ghostPiece.getX() + j;
                            int boardY = ghostPiece.getY() + i;

                            if (boardY >= 0) {
                                float x = offsetX + boardX * blockSize;
                                float y = offsetY + boardY * blockSize;
                                float inset = 4;
                                canvas.drawRect(x + inset, y + inset,
                                              x + blockSize - inset, y + blockSize - inset,
                                              ghostPaint);
                            }
                        }
                    }
                }
            }
        }

        // Draw current piece with 3D effect
        if (!game.isGameOver()) {
            TetrisPiece currentPiece = game.getCurrentPiece();
            int[][] shape = currentPiece.getShape();

            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] != 0) {
                        int boardX = currentPiece.getX() + j;
                        int boardY = currentPiece.getY() + i;

                        if (boardY >= 0) {
                            float x = offsetX + boardX * blockSize;
                            float y = offsetY + boardY * blockSize;
                            draw3DBlock(canvas, x, y, blockSize, currentPiece.getColor());
                        }
                    }
                }
            }
        }

        // Draw next piece preview
        drawNextPiece(canvas);

        // Draw line clear flash animation
        if (isFlashing && clearingLines != null) {
            flashPaint.setColor(Color.argb(flashAlpha, 255, 255, 255));
            for (int lineIndex : clearingLines) {
                float y = offsetY + lineIndex * blockSize;
                canvas.drawRect(offsetX, y, offsetX + blockSize * board.getCols(), y + blockSize, flashPaint);
            }
        }

        // Draw game over text
        if (game.isGameOver()) {
            textPaint.setTextSize(80);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(Color.parseColor("#FF5252"));
            canvas.drawText("GAME OVER", getWidth() / 2f, getHeight() / 2f, textPaint);
        }
    }

    private void draw3DBlock(Canvas canvas, float x, float y, float size, int color) {
        float inset = 3;
        float highlightInset = 5;

        // Draw shadow (bottom-right)
        canvas.drawRect(x + size - 4, y + 4, x + size, y + size, shadowPaint);
        canvas.drawRect(x + 4, y + size - 4, x + size, y + size, shadowPaint);

        // Main block with gradient
        int baseColor = color;
        int lightColor = lightenColor(baseColor, 0.3f);
        int darkColor = darkenColor(baseColor, 0.2f);

        LinearGradient gradient = new LinearGradient(
            x, y, x, y + size,
            lightColor, darkColor,
            Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        canvas.drawRect(x + inset, y + inset, x + size - inset, y + size - inset, paint);
        paint.setShader(null);

        // Highlight (top-left)
        highlightPaint.setColor(lightenColor(baseColor, 0.5f));
        canvas.drawRect(
            x + highlightInset,
            y + highlightInset,
            x + size - highlightInset,
            y + highlightInset + 2,
            highlightPaint
        );
        canvas.drawRect(
            x + highlightInset,
            y + highlightInset,
            x + highlightInset + 2,
            y + size - highlightInset,
            highlightPaint
        );

        // Dark edge (bottom-right)
        highlightPaint.setColor(darkenColor(baseColor, 0.4f));
        canvas.drawRect(
            x + highlightInset,
            y + size - highlightInset - 2,
            x + size - highlightInset,
            y + size - highlightInset,
            highlightPaint
        );
        canvas.drawRect(
            x + size - highlightInset - 2,
            y + highlightInset,
            x + size - highlightInset,
            y + size - highlightInset,
            highlightPaint
        );
    }

    private int lightenColor(int color, float factor) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        r = Math.min(255, (int) (r + (255 - r) * factor));
        g = Math.min(255, (int) (g + (255 - g) * factor));
        b = Math.min(255, (int) (b + (255 - b) * factor));

        return Color.rgb(r, g, b);
    }

    private int darkenColor(int color, float factor) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        r = Math.max(0, (int) (r * (1 - factor)));
        g = Math.max(0, (int) (g * (1 - factor)));
        b = Math.max(0, (int) (b * (1 - factor)));

        return Color.rgb(r, g, b);
    }

    private void drawNextPiece(Canvas canvas) {
        TetrisPiece nextPiece = game.getNextPiece();
        if (nextPiece == null) return;

        int[][] shape = nextPiece.getShape();
        float previewBlockSize = blockSize * 0.55f;
        float previewX = offsetX + (game.getBoard().getCols() * blockSize) + 15;
        float previewY = offsetY + 10;

        // Draw "Next:" label with background
        textPaint.setTextSize(24);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(Color.parseColor("#FFD700"));
        canvas.drawText("NEXT", previewX, previewY + 20, textPaint);

        // Draw next piece with 3D effect
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    float x = previewX + j * previewBlockSize;
                    float y = previewY + 30 + i * previewBlockSize;
                    draw3DBlock(canvas, x, y, previewBlockSize, nextPiece.getColor());
                }
            }
        }
    }

    public void refresh() {
        invalidate();
    }
}
