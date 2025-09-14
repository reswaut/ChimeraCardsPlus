package chimeracardsplus.powers;

import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.effects.BetterSilentGainPowerEffect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.List;

public class TungstenRodPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(TungstenRodPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int img_width, img_height;
    private List<AbstractGameEffect> effect = null;

    public TungstenRodPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        updateDescription();
        img = ImageMaster.loadImage("images/relics/tungsten.png");
        img_width = img.getWidth();
        img_height = img.getHeight();
    }

    private void updateEffect() {
        if (effect == null) {
            effect = ReflectionHacks.getPrivate(this, AbstractPower.class, "effect");
        }
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
    }

    @Override
    public int onLoseHp(int damageAmount) {
        if (damageAmount > 0) {
            updateEffect();
            if (effect != null) {
                effect.add(new BetterSilentGainPowerEffect(img, img_width, img_height));
            }
            return Math.max(damageAmount - amount, 0);
        }
        return damageAmount;
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount);
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        sb.setColor(c);
        sb.draw(img, x - 16.0F, y - 16.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale * 1.875F, Settings.scale * 1.875F, 0.0F, 0, 0, img_width, img_height, false, false);
        updateEffect();
        if (effect != null) {
            for (AbstractGameEffect e : effect) {
                e.render(sb, x, y);
            }
        }
    }
}
