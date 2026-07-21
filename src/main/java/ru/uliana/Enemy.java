package ru.uliana;

public class Enemy {
    private float x, y;
    private int hp;
    private int maxHp;
    private int damage;
    private int xpReward;
    private int goldReward;
    private boolean alive = true;

    private float attackCooldown = 0f;
    private final float ATTACK_SPEED = 1.2f; // Враг бьет раз в 1.2 сек

    public Enemy(float x, float y, int hp, int damage, int xpReward, int goldReward) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.damage = damage;
        this.xpReward = xpReward;
        this.goldReward = goldReward;
    }

    public void update(float deltaTime) {
        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
    }

    public boolean canAttack() {
        return attackCooldown <= 0;
    }

    public void resetAttackCooldown() {
        this.attackCooldown = ATTACK_SPEED;
    }

    public void takeDamage(int amount) {
        if (!alive) return;
        this.hp -= amount;
        System.out.println("⚔️ Враг получил урон: -" + amount + " | Осталось HP врага: " + hp + "/" + maxHp);

        if (this.hp <= 0) {
            this.hp = 0;
            this.alive = false;
        }
    }

    // Геттеры
    public float getX() { return x; }
    public float getY() { return y; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getDamage() { return damage; }
    public int getXpReward() { return xpReward; }
    public int getGoldReward() { return goldReward; }
    public boolean isAlive() { return alive; }
}
