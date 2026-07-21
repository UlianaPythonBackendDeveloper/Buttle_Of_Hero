package ru.uliana;

public class Hero {
    private String name;
    private float x, y;
    private float targetX, targetY;
    private float speed = 250f;

    // Характеристики
    private int level = 1;
    private final int maxLevel = 100;
    private long xp = 0;
    private int hp = 150;
    private int maxHp = 150;
    private int damage = 25;
    private int gold = 0;

    // Боевой таймер (чтобы не бить 60 раз в секунду)
    private float attackCooldown = 0f;
    private final float ATTACK_SPEED = 0.8f; // Атакует каждые 0.8 сек
    private float stateTime = 0f;
    private boolean isMoving = false;
    private float attackVisualTimer = 0f;
    public Hero(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }

    public void setTarget(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;

        // Перемещение
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 3f) {
            isMoving = true; // Герой идет!

            float moveX = (dx / distance) * speed * deltaTime;
            float moveY = (dy / distance) * speed * deltaTime;

            if (Math.abs(moveX) > distance) x = targetX;
            else x += moveX;

            if (Math.abs(moveY) > distance) y = targetY;
            else y += moveY;
        } else {
            isMoving = false; // Герой пришел и стоит на месте
        }

        // Кулдаун атаки
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
        if (attackVisualTimer > 0) {
            attackVisualTimer -= deltaTime;
        }
    }

    public boolean canAttack() {
        return attackCooldown <= 0;
    }

    public void resetAttackCooldown() {
        this.attackCooldown = ATTACK_SPEED;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
        if (this.hp < 0) this.hp = 0;
        System.out.println("⚠️ Герой получил урон: -" + amount + " | Осталось HP: " + hp + "/" + maxHp);
    }

    // Прокачка
    public long getXpForNextLevel() {
        return (long) (80 * Math.pow(level, 1.25));
    }

    public void addReward(int xpAmount, int goldAmount) {
        this.gold += goldAmount;
        this.xp += xpAmount;
        System.out.println("🎉 Враг повержен! + " + xpAmount + " XP | +" + goldAmount + " Золота");

        while (level < maxLevel && xp >= getXpForNextLevel()) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        maxHp += 30;
        hp = maxHp; // Полный хил при левелапе
        damage += 8;
        System.out.println("✨ УРОВЕНЬ ПОВЫШЕН! Текущий LVL: " + level + " | Атака: " + damage + " | Max HP: " + maxHp);
    }
    public void spendGold(int amount){
        this.gold -= amount;
    }
    public  void heal(int amount){
        this.hp = Math.min(this.maxHp, this.hp + amount);
    }
    public void increaseDamage(int amount){
        this.damage += amount;
    }
    public float getStateTime(){
        return stateTime;
    }
    public boolean isMoving(){
        return isMoving;
    }
    public void triggerAttackVisual() {
        this.attackVisualTimer = 0.15f; // Меч будет виден 0.15 секунды
    }

    // Проверяет, активен ли эффект прямо сейчас
    public boolean isAttackingVisual() {
        return attackVisualTimer > 0;
    }
    // Геттеры
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public int getLevel() { return level; }
    public int getGold(){return gold;}
}