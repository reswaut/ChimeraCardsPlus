package chimeracardsplus.patches.cards;

import chimeracardsplus.ChimeraCardsPlus;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.cards.blue.Chaos;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

@SpirePatch(
        clz = Chaos.class,
        method = "use"
)
public class ChaosPatches {
    @SpirePrefixPatch
    public static SpireReturn<Void> Prefix(Chaos __instance, AbstractPlayer p, AbstractMonster m) {
        if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return SpireReturn.Continue();
        }
        for (int i = __instance.magicNumber; i > 0; --i) {
            AbstractDungeon.actionManager.addToBottom(new ChannelAction(AbstractOrb.getRandomOrb(true)));
        }
        return SpireReturn.Return();
    }
}
