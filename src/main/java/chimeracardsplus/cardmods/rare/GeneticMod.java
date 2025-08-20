package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.localization.UIStrings;

public class GeneticMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(GeneticMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private boolean addedExhaust = true;
    private int block;

    public GeneticMod() {
        block = 1;
    }
    public GeneticMod(int baseBlock) {
        block = baseBlock;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 0 && c.baseBlock >= 4 && c.rarity != CardRarity.BASIC && doesntUpgradeExhaust());
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return this.block;
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
        return insertAfterText(rawDescription, String.format(addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1], DESCRIPTION_KEY));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int increment = baseVal(card);
        do {
            AbstractCard c = StSLib.getMasterDeckEquivalent(card);
            if (c != null && CardModifierManager.hasModifier(c, ID)) {
                GeneticMod modifier = (GeneticMod) CardModifierManager.getModifiers(c, ID).get(0);
                modifier.block += increment;
                c.applyPowers();
            }
        } while (false);
        for (AbstractCard c : GetAllInBattleInstances.get(card.uuid)) {
            if (CardModifierManager.hasModifier(c, ID)) {
                GeneticMod modifier = (GeneticMod) CardModifierManager.getModifiers(c, ID).get(0);
                modifier.block += increment;
                c.applyPowers();
            }
        }
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        card.initializeDescription();
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GeneticMod(block);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }
}