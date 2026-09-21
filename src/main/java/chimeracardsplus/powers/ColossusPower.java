package chimeracardsplus.powers;

import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.Matcher.MethodCallMatcher;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class ColossusPower extends AbstractPower {
    public static final String POWER_ID = ChimeraCardsPlus.makeID(ColossusPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ColossusPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        type = PowerType.BUFF;
        isTurnBased = true;
        updateDescription();
        Texture texture48 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/colossus32.png");
        Texture texture128 = ChimeraCardsPlus.resourceLoader.getModTexture("powers/colossus84.png");
        region48 = new AtlasRegion(texture48, 0, 0, texture48.getWidth(), texture48.getHeight());
        region128 = new AtlasRegion(texture128, 0, 0, texture128.getWidth(), texture128.getHeight());
    }

    public static float atDamageReceive(float damage, DamageType damageType, AbstractCreature source) {
        if (damageType != DamageType.NORMAL) {
            return damage;
        }
        AbstractPower power = source.getPower(VulnerablePower.POWER_ID);
        if (power == null || power.amount <= 0) {
            return damage;
        }
        return damage * 0.5F;
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
    public void updateDescription() {
        description = String.format(amount == 1 ? DESCRIPTIONS[0] : DESCRIPTIONS[1], amount);
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "calculateDamage"
    )
    public static class CalculateIntentDamagePatch {
        @SpireInsertPatch(
                locator = Locater.class,
                localvars = "tmp"
        )
        public static void Insert(AbstractMonster __instance, int dmg, @ByRef float[] tmp) {
            if (AbstractDungeon.player.hasPower(POWER_ID)) {
                tmp[0] = atDamageReceive(tmp[0], DamageType.NORMAL, __instance);
            }
        }
    }

    @SpirePatch(
            clz = DamageInfo.class,
            method = "applyPowers"
    )
    public static class CalculateRealDamagePatch {
        @SpireInsertPatch(
                locator = Locater.class,
                localvars = "tmp"
        )
        public static void Insert(DamageInfo __instance, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp) {
            if (target.hasPower(POWER_ID)) {
                tmp[0] = atDamageReceive(tmp[0], __instance.type, owner);
            }
        }
    }

    private static class Locater extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctBehavior) throws PatchingException, CannotCompileException {
            Matcher finalMatcher = new MethodCallMatcher(AbstractStance.class, "atDamageReceive");
            return LineFinder.findInOrder(ctBehavior, finalMatcher);
        }
    }
}
