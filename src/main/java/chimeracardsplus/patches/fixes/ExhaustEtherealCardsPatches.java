package chimeracardsplus.patches.fixes;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class ExhaustEtherealCardsPatches {
    @SpirePatch(
            clz = CardQueueItem.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, boolean.class}
    )
    public static class EndOfTurnEtherealPatches {
        @SpirePostfixPatch
        public static void Postfix(CardQueueItem __instance, AbstractCard card, boolean isEndTurnAutoPlay) {
            if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
                return;
            }
            if (isEndTurnAutoPlay && card.isEthereal) {
                card.exhaustOnUseOnce = true;
            }
        }
    }

    @SpirePatch(
            clz = DiscardAtEndOfTurnAction.class,
            method = "update"
    )
    public static class RetainEtherealPatches {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new SkipRetainEtherealCardsExpr();
        }

        private static class SkipRetainEtherealCardsExpr extends ExprEditor {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                if (!AbstractCard.class.getName().equals(f.getClassName())) {
                    return;
                }
                String fieldName = f.getFieldName();
                if ("retain".equals(fieldName) || "selfRetain".equals(fieldName)) {
                    f.replace("{ $_ = ($proceed($$)) && !(" + ChimeraCardsPlus.class.getName() + ".configs.enableBaseGameFixes() && $0.isEthereal); }");
                }
            }
        }
    }
}
