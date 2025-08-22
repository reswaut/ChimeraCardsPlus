package chimeracardsplus.powers;

import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.effects.BetterSilentGainPowerEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashPowerEffect;

import java.util.List;

public class VelvetChokerPower extends TwoAmountPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(VelvetChokerPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int img_width, img_height;
    private final List<AbstractGameEffect> effect;
    private float flashTimer = -1.0F;

    public VelvetChokerPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.DEBUFF;
        isTurnBased = true;
        updateDescription();
        img = ImageMaster.loadImage("images/relics/redChoker.png");
        img_width = img.getWidth();
        img_height = img.getHeight();
        updateCardsUsedThisTurn();
        effect = ReflectionHacks.getPrivate(this, AbstractPower.class, "effect");
    }

    private void updateCardsUsedThisTurn() {
        amount2 = AbstractDungeon.actionManager.cardsPlayedThisTurn.size();
        updateDescription();
        if (amount2 >= 6 && flashTimer < 0.0F && effect != null) {
            flashTimer = 1.0F;
            effect.add(new BetterSilentGainPowerEffect(img, img_width, img_height));
            AbstractDungeon.effectList.add(new FlashPowerEffect(this));
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        flashTimer -= Gdx.graphics.getDeltaTime();
    }

    @Override
    public void atStartOfTurn() {
        if (amount == 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
        } else {
            addToBot(new ReducePowerAction(owner, owner, POWER_ID, 1));
        }
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        updateCardsUsedThisTurn();
        if (amount2 >= 6) {
            card.cantUseMessage = DESCRIPTIONS[1];
            return false;
        }
        return true;
    }

    @Override
    public void updateDescription() {
        description = String.format(DESCRIPTIONS[0], amount2);
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        sb.setColor(c);
        sb.draw(img, x - 12.0F, y - 12.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale * 1.5F, Settings.scale * 1.5F, 0.0F, 0, 0, img_width, img_height, false, false);
        for (AbstractGameEffect e : effect) {
            e.render(sb, x, y);
        }
    }
}
