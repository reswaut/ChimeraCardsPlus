package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CardModifierMultiBaseDamagePatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = "calculateCardDamage"
    )
    public static class ModifyMultiBaseDamageHook {
        @SpireInsertPatch(
                locator = Locator1.class,
                localvars = {"tmp", "m", "i"}
        )
        public static void Insert1(AbstractCard __instance, AbstractMonster mo, float[] tmp, ArrayList<AbstractMonster> m, int i) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof MultiBaseDamageMod) {
                    tmp[i] = ((MultiBaseDamageMod) mod).modifyMultiBaseDamage(tmp[i], m.get(i), __instance, mo);
                }
            }
        }

        @SpireInsertPatch(
                locator = Locator2.class,
                localvars = {"isMultiDamage"}
        )
        public static void Insert2(AbstractCard __instance, AbstractMonster mo, @ByRef boolean[] isMultiDamage) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(__instance)) {
                if (mod instanceof MultiBaseDamageMod) {
                    isMultiDamage[0] = true;
                    break;
                }
            }
        }

        @SpireInsertPatch(
                locator = Locator3.class,
                localvars = {"isMultiDamage"}
        )
        public static void Insert3(AbstractCard __instance, AbstractMonster mo, boolean isMultiDamage) {
            if (!isMultiDamage) {
                return;
            }
            for (int i = 0; i < __instance.multiDamage.length; ++i) {
                if (AbstractDungeon.getCurrRoom().monsters.monsters.get(i) == mo) {
                    __instance.damage = __instance.multiDamage[i];
                    break;
                }
            }
        }
    }

    private static class Locator1 extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1]};
        }
    }

    private static class Locator2 extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "isDamageModified");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static class Locator3 extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "damage");
            int[] tmp = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{tmp[1] + 1};
        }
    }
}
