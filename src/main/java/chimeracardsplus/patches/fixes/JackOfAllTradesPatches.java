package chimeracardsplus.patches.fixes;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.JackOfAllTrades;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@SpirePatch(
        clz = JackOfAllTrades.class,
        method = "use"
)
public class JackOfAllTradesPatches {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(JackOfAllTrades __instance, AbstractPlayer p, AbstractMonster m) {
        if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return SpireReturn.Continue();
        }
        for (int i = __instance.magicNumber; i > 0; --i) {
            AbstractCard c = AbstractDungeon.returnTrulyRandomColorlessCardInCombat(AbstractDungeon.cardRandomRng).makeCopy();
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(c, 1));
        }
        return SpireReturn.Return();
    }
}
