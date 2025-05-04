package chimeracardsplus.patches;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.interfaces.TriggerOnUpdateObjectsMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class CardModifierOnUpdateObjectsPatch {
    @SpirePatch(
            clz = AbstractRoom.class,
            method = "updateObjects"
    )
    public static class UpdateObjectsHook {
        @SpirePostfixPatch
        public static void Postfix(AbstractRoom __instance) {
            boolean updated = true;
            while (updated) {
                updated = false;
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                        if (mod instanceof TriggerOnUpdateObjectsMod) {
                            if (((TriggerOnUpdateObjectsMod) mod).onUpdateObjects(card)) {
                                updated = true;
                                break;
                            }
                        }
                    }
                    if (updated) {
                        break;
                    }
                }
            }
        }
    }
}
