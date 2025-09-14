package chimeracardsplus.powers;

import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.effects.BetterSilentGainPowerEffect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.List;

public class LetterOpenerPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(LetterOpenerPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int img_width, img_height;
    private List<AbstractGameEffect> effect = null;

    public LetterOpenerPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        updateDescription();
        img = ImageMaster.loadImage("images/relics/letterOpener.png");
        img_width = img.getWidth();
        img_height = img.getHeight();
    }

    private void updateEffect() {
        if (effect == null) {
            effect = ReflectionHacks.getPrivate(this, AbstractPower.class, "effect");
        }
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount);
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.type == CardType.SKILL) {
            addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(amount, true), DamageType.THORNS, AttackEffect.SLASH_HEAVY));
            updateEffect();
            if (effect != null) {
                effect.add(new BetterSilentGainPowerEffect(img, img_width, img_height));
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
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
