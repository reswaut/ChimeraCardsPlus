package chimeracardsplus.patches;

import CardAugments.CardAugmentsMod;
import CardAugments.screens.ModifierScreen;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.HashSet;

public class WhitelistModePatches {
    public static final String ID = ChimeraCardsPlus.makeID(WhitelistModePatches.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @SpirePatch(
            clz = CardAugmentsMod.class,
            method = "isAugmentEnabled"
    )
    public static class InvertWhitelistDisablePatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new InvertContainsExpr();
        }

        private static class InvertContainsExpr extends ExprEditor {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (HashSet.class.getName().equals(m.getClassName()) && "contains".equals(m.getMethodName())) {
                    m.replace("{ $_ = (" + ChimeraCardsPlus.class.getName() + ".configs.enableWhitelist()) ^ ($proceed($$)); }");
                }
            }
        }
    }

    @SpirePatch(
            clz = ModifierScreen.class,
            method = "renderUpgradeViewToggle"
    )
    public static class ChangeWhitelistDisableTextPatch {
        @SpirePrefixPatch
        public static void Prefix() {
            if (ChimeraCardsPlus.configs.enableWhitelist()) {
                ModifierScreen.TEXT[8] = TEXT[1];
            } else {
                ModifierScreen.TEXT[8] = TEXT[0];
            }
        }
    }
}
