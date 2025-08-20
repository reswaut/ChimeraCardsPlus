package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CapaciousMod extends AbstractAugmentPlus implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(CapaciousMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private boolean addedExhaust = true;

    private static int getBaseValDamage(AbstractCard card) {
        return Math.min(Math.max(card.baseDamage - 1, 0) / 5, 5);
    }

    private static int getBaseValBlock(AbstractCard card) {
        return Math.min(Math.max(card.baseBlock - 1, 0) / 5, 5);
    }

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
    public void onUpgradeCheck(AbstractCard card) {
        card.initializeDescription();
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? Math.max(block - getBaseValBlock(card) * 5.0F, 0.0F) : block;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return allowOrbMods() && cardCheck(abstractCard, c -> c.cost >= -1 && (c.baseDamage >= 6 || c.baseBlock >= 6) && doesntUpgradeExhaust());
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? Math.max(damage - getBaseValDamage(card) * 5.0F, 0.0F) : damage;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new IncreaseMaxOrbAction(baseVal(card)));
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
        return getBaseValDamage(abstractCard) + getBaseValBlock(abstractCard);
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
        int val = baseVal(card);
        if (val <= 0) {
            return rawDescription;
        }
        String text;
        if (val == 1) {
            text = addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1];
        } else {
            text = addedExhaust ? CARD_TEXT[2] : CARD_TEXT[3];
        }
        return insertAfterText(rawDescription, String.format(text, DESCRIPTION_KEY));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CapaciousMod();
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