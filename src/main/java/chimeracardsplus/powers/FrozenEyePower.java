package chimeracardsplus.powers;

import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.FrozenEye;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.List;

public class FrozenEyePower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(FrozenEyePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private final int img_width, img_height;
    private List<AbstractGameEffect> effect = null;

    public FrozenEyePower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        updateDescription();
        img = ImageMaster.loadImage("images/relics/frozenEye.png");
        img_width = img.getWidth();
        img_height = img.getHeight();
    }

    private void updateEffect() {
        if (effect == null) {
            effect = ReflectionHacks.getPrivate(this, AbstractPower.class, "effect");
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (amount == 0) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
        } else {
            addToBot(new ReducePowerAction(owner, owner, POWER_ID, 1));
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void renderIcons(SpriteBatch sb, float x, float y, Color c) {
        sb.setColor(c);
        sb.draw(img, x - 12.0F, y - 12.0F, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale * 1.5F, Settings.scale * 1.5F, 0.0F, 0, 0, img_width, img_height, false, false);
        updateEffect();
        if (effect != null) {
            for (AbstractGameEffect e : effect) {
                e.render(sb, x, y);
            }
        }
    }

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "hasRelic"
    )
    public static class EquivalentFrozenEyePatches {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Prefix(AbstractPlayer __instance, String targetID) {
            if (targetID.equals(FrozenEye.ID) && __instance.hasPower(POWER_ID)) {
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }
}
