package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;

public class PerseverantMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(PerseverantMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private int turnsRetained;

    public PerseverantMod() {
        turnsRetained = 0;
    }
    public PerseverantMod(int turnsRetained) {
        this.turnsRetained = turnsRetained;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.selfRetain = true;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && c.baseBlock >= 4 && notRetain(c) && notEthereal(c) && doesntOverride(c, "triggerOnEndOfTurnForPlayingCard"));
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return Math.max(block + baseVal(card) * (turnsRetained - 2), 0.0F);
    }

    @Override
    public void onRetained(AbstractCard card) {
        turnsRetained += 1;
        card.applyPowers();
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return baseVal(abstractCard);
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        return abstractCard.baseBlock / 4;
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return false;
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        return abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return insertAfterText(insertBeforeText(rawDescription, CARD_TEXT[0]), String.format(CARD_TEXT[1], DESCRIPTION_KEY));
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        card.initializeDescription();
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        card.initializeDescription();
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PerseverantMod(turnsRetained);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}