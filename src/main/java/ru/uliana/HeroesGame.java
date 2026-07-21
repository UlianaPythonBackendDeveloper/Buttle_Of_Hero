package ru.uliana;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class HeroesGame extends ApplicationAdapter {
    private enum GameState { MENU, PLAYING }
    private GameState currentState = GameState.MENU;

    private SpriteBatch batch;
    private Hero hero;
    private Texture spritesheet;
    private TextureRegion heroRegion;
    private TextureRegion enemyRegion;
    private TextureRegion tileRegion;
    private Texture whiteTexture;
    private Texture mapSpritesheet;

    private BitmapFont font;
    private BitmapFont titleFont;
    private Array<Enemy> enemies;

    private Rectangle playButtonRect;
    private Rectangle exitButtonRect;

    private int waveNumber = 1;

    private final int MAP_SIZE = 10;
    private final int TILE_WIDTH = 64;
    private final int TILE_HEIGHT = 32;
    private final int SPRITE_SIZE = 16;
    private final float RENDER_SCALE = 2.0f;
    private Animation<TextureRegion> heroWalkAnim;
    private TextureRegion heroIdleRegion;
    private TextureRegion swordRegion;
    @Override
    public void create() {
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.0f);
        titleFont.setColor(Color.GOLD);

        hero = new Hero("Hero", 600, 300);

        // 1. Загрузка персонажей
        spritesheet = new Texture(Gdx.files.internal("roguelikeChar_transparent.png"));
        spritesheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Подготовка кадров для анимации героя
        heroIdleRegion = new TextureRegion(spritesheet, 0, 0, 16, 16);

        TextureRegion[] walkFrames = new TextureRegion[2];
        walkFrames[0] = new TextureRegion(spritesheet, 0, 0, 16, 16);  // Первый кадр (стоя)
        walkFrames[1] = new TextureRegion(spritesheet, 18, 0, 16, 16); // Второй кадр (шаг)

        // Анимация ходьбы (кадры меняются каждые 0.15 секунды)
        heroWalkAnim = new Animation<TextureRegion>(0.15f, walkFrames);

        // Спрайт врага
        enemyRegion = new TextureRegion(spritesheet, 0, 51, 16, 16);

        // 2. Загрузка карты из RPGpack_sheet.png
        mapSpritesheet = new Texture(Gdx.files.internal("RPGpack_sheet.png"));
        mapSpritesheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        tileRegion = new TextureRegion(mapSpritesheet, 64, 0, 64, 64);

        // 3. Белая текстура для HP-баров и кнопок
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        whiteTexture = new Texture(whitePixmap);
        whitePixmap.dispose();

        // 4. Кнопки и враги
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float btnWidth = 200;
        float btnHeight = 50;

        playButtonRect = new Rectangle((screenWidth - btnWidth) / 2f, screenHeight / 2f, btnWidth, btnHeight);
        exitButtonRect = new Rectangle((screenWidth - btnWidth) / 2f, screenHeight / 2f - 70, btnWidth, btnHeight);
        swordRegion = new TextureRegion(spritesheet, 0, 153, 16, 16);
        initEnemies();
    }

    private void initEnemies() {
        enemies = new Array<>();
        spawnWave(1);
    }

    private void spawnWave(int wave) {
        enemies.clear();
        this.waveNumber = wave;
        for (int i = 0; i < 3 + wave; i++) {
            float spawnX = MathUtils.random(400, 700);
            float spawnY = MathUtils.random(150, 400);
            int enemyHp = 30 + wave * 10;
            int enemyDmg = 5 + wave * 2;
            int enemyXp = 40 + wave * 10;
            int enemyGold = 15 + wave * 5;
            enemies.add(new Enemy(spawnX, spawnY, enemyHp, enemyDmg, enemyXp, enemyGold));
        }
    }

    private void generateGrassTexture() {
        Pixmap tilePixmap = new Pixmap(TILE_WIDTH, TILE_HEIGHT, Pixmap.Format.RGBA8888);
        tilePixmap.setColor(0, 0, 0, 0);
        tilePixmap.fill();
        tilePixmap.setColor(new Color(0.2f, 0.6f, 0.2f, 1f));
        int halfW = TILE_WIDTH / 2;
        int halfH = TILE_HEIGHT / 2;
        for (int y = 0; y < TILE_HEIGHT; y++) {
            for (int x = 0; x < TILE_WIDTH; x++) {
                if (Math.abs(x - halfW) * halfH + Math.abs(y - halfH) * halfW <= halfW * halfH) {
                    tilePixmap.drawPixel(x, y);
                }
            }
        }
        tilePixmap.setColor(new Color(0.15f, 0.45f, 0.15f, 1f));
        tilePixmap.drawRectangle(0, halfH, TILE_WIDTH, 1);
        tileRegion = new TextureRegion(new Texture(tilePixmap));
        tilePixmap.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (currentState == GameState.MENU) {
            updateMenu();
            drawMenu();
        } else if (currentState == GameState.PLAYING) {
            updateGame();
            drawGame();
        }
    }

    private void updateMenu() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (playButtonRect.contains(mouseX, mouseY)) {
                currentState = GameState.PLAYING;
            } else if (exitButtonRect.contains(mouseX, mouseY)) {
                Gdx.app.exit();
            }
        }
    }

    private void drawMenu() {
        batch.begin();

        titleFont.draw(batch, "HEROES BATTLE", Gdx.graphics.getWidth() / 2f - 110, Gdx.graphics.getHeight() - 150);

        batch.setColor(Color.DARK_GRAY);
        batch.draw(whiteTexture, playButtonRect.x, playButtonRect.y, playButtonRect.width, playButtonRect.height);
        batch.setColor(Color.WHITE);
        font.draw(batch, "PLAY", playButtonRect.x + 80, playButtonRect.y + 30);

        batch.setColor(Color.FIREBRICK);
        batch.draw(whiteTexture, exitButtonRect.x, exitButtonRect.y, exitButtonRect.width, exitButtonRect.height);
        batch.setColor(Color.WHITE);
        font.draw(batch, "EXIT", exitButtonRect.x + 82, exitButtonRect.y + 30);

        batch.end();
    }

    private void updateGame() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = GameState.MENU;
            return;
        }

        // --- МАГАЗИН ПРОКАЧКИ ЗА ЗОЛОТО ---
        // Лечение: H (20 Gold)
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (hero.getGold() >= 20) {
                hero.spendGold(20);
                hero.heal(30);
            }
        }

        // Урон: B (30 Gold)
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            if (hero.getGold() >= 30) {
                hero.spendGold(30);
                hero.increaseDamage(5);
            }
        }

        // --- СПАВН СЛЕДУЮЩЕЙ ВОЛНЫ ВРАГОВ ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            spawnWave(waveNumber + 1);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            float clickX = Gdx.input.getX();
            float clickY = Gdx.graphics.getHeight() - Gdx.input.getY();
            hero.setTarget(clickX - (SPRITE_SIZE * RENDER_SCALE) / 2f, clickY - (SPRITE_SIZE * RENDER_SCALE) / 2f);
        }

        hero.update(deltaTime);

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) continue;
            enemy.update(deltaTime);

            float spriteCenterX = SPRITE_SIZE * RENDER_SCALE / 2f;
            float dx = (hero.getX() + spriteCenterX) - (enemy.getX() + spriteCenterX);
            float dy = (hero.getY() + spriteCenterX) - (enemy.getY() + spriteCenterX);
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance <= 40f) {
                if (hero.canAttack()) {
                    enemy.takeDamage(hero.getDamage());
                    hero.resetAttackCooldown();

                    // Включаем визуальный эффект взмаха!
                    hero.triggerAttackVisual();

                    if (!enemy.isAlive()) {
                        hero.addReward(enemy.getXpReward(), enemy.getGoldReward());
                    }
                }
                if (enemy.isAlive() && enemy.canAttack()) {
                    hero.takeDamage(enemy.getDamage());
                    enemy.resetAttackCooldown();
                }
            }
        }
    }

    private void drawGame() {
        batch.begin();

        // 1. Карта
        float offsetX = 550;
        float offsetY = 200;
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                float isoX = (x - y) * (TILE_WIDTH / 2f) + offsetX;
                float isoY = (x + y) * (TILE_HEIGHT / 2f) + offsetY;
                batch.draw(tileRegion, isoX, isoY);
            }
        }

        // 2. Враги
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                batch.draw(enemyRegion, enemy.getX(), enemy.getY(), 0, 0, SPRITE_SIZE, SPRITE_SIZE, RENDER_SCALE, RENDER_SCALE, 0);
                drawHealthBar(enemy.getX(), enemy.getY() + (SPRITE_SIZE * RENDER_SCALE) + 4, enemy.getHp(), enemy.getMaxHp(), SPRITE_SIZE * RENDER_SCALE, 4);
            }
        }

        // 3. Герой
        TextureRegion currentHeroFrame;
        if (hero.isMoving()) {
            currentHeroFrame = heroWalkAnim.getKeyFrame(hero.getStateTime(), true);
        } else {
            currentHeroFrame = heroIdleRegion;
        }

// Рисуем самого героя
        batch.draw(currentHeroFrame, hero.getX(), hero.getY(), 0, 0, SPRITE_SIZE, SPRITE_SIZE, RENDER_SCALE, RENDER_SCALE, 0);

// --- ДОБАВЛЯЕМ ВЗМАХ МЕЧОМ ---
        if (hero.isAttackingVisual()) {
            // Рисуем меч слегка выше и правее героя с небольшим поворотом
            batch.draw(
                    swordRegion,
                    hero.getX() + 16, hero.getY() + 16, // Позиция (чуть в стороне от героя)
                    8, 8,                              // Точка вращения (центр спрайта 16x16)
                    16, 16,                            // Ширина и высота спрайта
                    RENDER_SCALE, RENDER_SCALE,        // Масштаб
                    -45                                // Угол поворота (наклон меча)
            );
        }
        // 4. HUD (Интерфейс)
        font.draw(batch, "LVL: " + hero.getLevel(), 20, 700);
        font.draw(batch, "HP: " + hero.getHp() + " / " + hero.getMaxHp(), 20, 680);
        font.draw(batch, "DMG: " + hero.getDamage(), 20, 660);
        font.draw(batch, "Gold: " + hero.getGold(), 20, 640);
        font.draw(batch, "Wave: " + waveNumber, 20, 620);

        // Панель магазина и управления
        font.draw(batch, "--- SHOP & CONTROLS ---", 20, 110);
        font.draw(batch, "[H] Heal (+30 HP) - 20 Gold", 20, 90);
        font.draw(batch, "[B] Upgrade DMG (+5) - 30 Gold", 20, 70);
        font.draw(batch, "[N] Spawn Next Wave", 20, 50);
        font.draw(batch, "[ESC] Main Menu", 20, 30);

        batch.end();
    }

    private void drawHealthBar(float x, float y, int currentHp, int maxHp, float width, float height) {
        float hpPercent = (float) currentHp / maxHp;
        batch.setColor(Color.BLACK);
        batch.draw(whiteTexture, x - 1, y - 1, width + 2, height + 2);
        if (hpPercent > 0.4f) batch.setColor(Color.GREEN);
        else batch.setColor(Color.RED);
        batch.draw(whiteTexture, x, y, width * hpPercent, height);
        batch.setColor(Color.WHITE);
    }


    @Override
    public void dispose() {
        spritesheet.dispose();
        whiteTexture.dispose();
        font.dispose();
        titleFont.dispose();
    }
}