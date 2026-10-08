package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ShadowmeldPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(ShadowmeldPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ShadowmeldPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        updateDescription();
        Texture texture48 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/shadowmeld32.png");
        Texture texture128 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/shadowmeld84.png");
        region48 = new AtlasRegion(texture48, 0, 0, texture48.getWidth(), texture48.getHeight());
        region128 = new AtlasRegion(texture128, 0, 0, texture128.getWidth(), texture128.getHeight());
    }

    @Override
    public void updateDescription() {
        description = amount == 1 ? DESCRIPTIONS[0] : String.format(DESCRIPTIONS[1], amount);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
    }

    @SpirePatch(
            clz = AbstractCreature.class,
            method = "addBlock"
    )
    public static class DoubleBlockPatch {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature __instance, @ByRef int[] blockAmount) {
            if (blockAmount[0] <= 0) {
                return;
            }
            AbstractPower power = __instance.getPower(POWER_ID);
            if (power == null) {
                return;
            }
            int powerAmount = power.amount;
            if (powerAmount >= 10) {
                blockAmount[0] = 999;
                return;
            }
            blockAmount[0] <<= powerAmount;
            if (blockAmount[0] >= 999) {
                blockAmount[0] = 999;
            }
        }
    }
}
