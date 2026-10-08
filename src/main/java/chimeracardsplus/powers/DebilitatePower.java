package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class DebilitatePower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(DebilitatePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DebilitatePower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.DEBUFF;
        isTurnBased = true;
        priority = 99;
        updateDescription();
        Texture texture48 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/debilitate32.png");
        Texture texture128 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/debilitate84.png");
        region48 = new AtlasRegion(texture48, 0, 0, texture48.getWidth(), texture48.getHeight());
        region128 = new AtlasRegion(texture128, 0, 0, texture128.getWidth(), texture128.getHeight());
    }

    @Override
    public void updateDescription() {
        description = String.format(amount == 1 ? DESCRIPTIONS[0] : DESCRIPTIONS[1], amount);
    }

    @Override
    public void atEndOfRound() {
        if (amount == 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
        } else {
            addToBot(new ReducePowerAction(owner, owner, POWER_ID, 1));
        }
    }

    @Override
    public float atDamageReceive(float damage, DamageType damageType) {
        if (damage <= 0.0F) {
            return damage;
        }
        AbstractPower vulnerablePower = owner.getPower(VulnerablePower.POWER_ID);
        if (vulnerablePower == null) {
            return damage;
        }
        float effectiveMultiplier = vulnerablePower.atDamageReceive(damage, damageType) / damage;
        return Math.max(0.0F, damage / effectiveMultiplier * (effectiveMultiplier * 2.0F - 1.0F));
    }

    @Override
    public float atDamageGive(float damage, DamageType type) {
        if (damage <= 0.0F) {
            return damage;
        }
        AbstractPower weakPower = owner.getPower(WeakPower.POWER_ID);
        if (weakPower == null) {
            return damage;
        }
        float effectiveMultiplier = weakPower.atDamageGive(damage, type) / damage;
        return Math.max(0.0F, damage / effectiveMultiplier * (effectiveMultiplier * 2.0F - 1.0F));
    }
}
