package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;

public class DexterousMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(DexterousMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust && card.type != CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        } else {
            addedExhaust = false;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && c.baseBlock >= 5 && doesntUpgradeExhaust());
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block - (int) (block / 5.0F) * 4;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DexterityPower(AbstractDungeon.player, baseVal(card)), baseVal(card)));
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
        return abstractCard.baseBlock / 5;
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
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DexterousMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}