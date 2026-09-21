package chimeracardsplus.patches.fixes;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;

@SpirePatch(
        clz = CardQueueItem.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = {AbstractCard.class, boolean.class}
)
public class ExhaustEtherealEndOfTurnCardPatches {
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
