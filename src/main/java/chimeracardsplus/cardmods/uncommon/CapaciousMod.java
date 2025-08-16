package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CapaciousMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(CapaciousMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public boolean modified, upgraded;
    private boolean addedExhaust = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.exhaust && card.type != AbstractCard.CardType.POWER) {
            addedExhaust = true;
            card.exhaust = true;
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return allowOrbMods() && cardCheck(card, (c) -> c.cost >= -1 && (c.baseDamage >= 6 || c.baseBlock >= 6) && doesntUpgradeExhaust());
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? Math.max(damage - getBaseValDamage(card) * 5.0F, 0.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? Math.max(block - getBaseValBlock(card) * 5.0F, 0.0F) : block;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new IncreaseMaxOrbAction(getBaseVal(card)));
    }

    private int getBaseValDamage(AbstractCard card) {
        return Math.min(Math.max(card.baseDamage - 1, 0) / 5, 5);
    }

    private int getBaseValBlock(AbstractCard card) {
        return Math.min(Math.max(card.baseBlock - 1, 0) / 5, 5);
    }

    public int getBaseVal(AbstractCard card) {
        return getBaseValDamage(card) + getBaseValBlock(card);
    }

    public String key() {
        return ID;
    }

    public int val(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public int baseVal(AbstractCard card) {
        return this.getBaseVal(card);
    }

    public boolean modified(AbstractCard card) {
        return this.modified;
    }

    public boolean upgraded(AbstractCard card) {
        this.modified = card.timesUpgraded != 0 || card.upgraded;
        this.upgraded = card.timesUpgraded != 0 || card.upgraded;
        return this.upgraded;
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
        String text = "";
        if (getBaseVal(card) <= 0) {
            return rawDescription;
        } else if (getBaseVal(card) == 1) {
            text = addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1];
        } else if (getBaseVal(card) >= 2) {
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
}