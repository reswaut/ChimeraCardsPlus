package chimeracardsplus.patches;

import CardAugments.cardmods.AbstractAugment;
import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Blizzard;
import com.megacrit.cardcrawl.cards.blue.Chaos;
import com.megacrit.cardcrawl.cards.colorless.JackOfAllTrades;
import com.megacrit.cardcrawl.cards.purple.Wish;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SpirePatch(
        clz = AbstractAugment.class,
        method = "usesMagic"
)
public class UsesMagicOverridePatches {
    private static final Collection<String> trueInstances = Arrays.asList(Chaos.ID, JackOfAllTrades.ID, Wish.ID);
    private static final Collection<String> falseInstances = Collections.singletonList(Blizzard.ID);

    @SpirePrefixPatch
    public static SpireReturn<Boolean> Prefix(AbstractCard card) {
        if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return SpireReturn.Continue();
        }
        if (trueInstances.contains(card.cardID)) {
            return SpireReturn.Return(true);
        }
        if (falseInstances.contains(card.cardID)) {
            return SpireReturn.Return(false);
        }
        return SpireReturn.Continue();
    }
}
