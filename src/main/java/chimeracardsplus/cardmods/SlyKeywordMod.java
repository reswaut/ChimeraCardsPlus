package chimeracardsplus.cardmods;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.patches.SlyFieldPatches.SlyFieldPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class SlyKeywordMod extends AbstractCardModifier {
    public static final String ID = ChimeraCardsPlus.makeID(SlyKeywordMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return AbstractAugment.insertBeforeText(rawDescription, uiStrings.TEXT[0]);
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !SlyFieldPatch.sly.get(card);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        SlyFieldPatch.sly.set(card, true);
    }

    @Override
    public void onRemove(AbstractCard card) {
        SlyFieldPatch.sly.set(card, false);
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SlyKeywordMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
