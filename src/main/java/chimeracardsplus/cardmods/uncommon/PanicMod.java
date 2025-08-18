package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.NoBlockPower;

public class PanicMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(PanicMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (PanicButton.ID.equals(card.cardID)) {
            return magic + 2.0F;
        }
        return magic;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.baseBlock >= 1 && c.cost >= -1 && (c.type == CardType.ATTACK || c.type == CardType.SKILL) && doesntUpgradeExhaust());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (PanicButton.ID.equals(card.cardID)) {
            return;
        }
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new NoBlockPower(AbstractDungeon.player, 2, false), 2));
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block * 4.0F;
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
        if (PanicButton.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PanicMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}