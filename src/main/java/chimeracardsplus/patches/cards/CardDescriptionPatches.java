package chimeracardsplus.patches.cards;

import basemod.ReflectionHacks;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.util.Map;

public class CardDescriptionPatches {
    private static final Map<String, CardStrings> cards = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards");

    public static void rewriteDescriptions(Class<? extends AbstractCard> cardClass) {
        if (!ChimeraCardsPlus.configs.enableBaseGameFixes()) {
            return;
        }
        String id = ChimeraCardsPlus.makeID(cardClass.getSimpleName());
        CardStrings cardStrings = cards.get(id);
        if (cardStrings == null) {
            return;
        }
        CardStrings oldCardStrings = ReflectionHacks.getPrivateStatic(cardClass, "cardStrings");
        if (oldCardStrings == null) {
            ChimeraCardsPlus.logger.error("-- No static cardStrings found in {}.", cardClass.getName());
            return;
        }
        ChimeraCardsPlus.logger.info("-- Rewritten descriptions of {}.", cardClass.getName());
        if (cardStrings.DESCRIPTION != null) {
            oldCardStrings.DESCRIPTION = cardStrings.DESCRIPTION;
        }
        if (cardStrings.UPGRADE_DESCRIPTION != null) {
            oldCardStrings.UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
        }
    }
}
