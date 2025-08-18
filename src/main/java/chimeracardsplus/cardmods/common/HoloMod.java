package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.common.BetterDiscardPileToHandAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.blue.Hologram;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HoloMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(HoloMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.timesUpgraded != 0 || card.upgraded) {
            addedExhaust = false;
        } else {
            addedExhaust = !card.exhaust;
            card.exhaust = true;
        }
        if (Hologram.ID.equals(card.cardID)) {
            InterceptUseField.interceptUse.set(card, Boolean.TRUE);
        }
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        if (card.exhaust && addedExhaust) {
            card.exhaust = false;
        }
        addedExhaust = false;
        card.initializeDescription();
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && (abstractCard.baseDamage >= 3 || abstractCard.baseBlock >= 3);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage / 3.0F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block / 3.0F : block;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (Hologram.ID.equals(card.cardID)) {
            addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, card.block));
            addToBot(new BetterDiscardPileToHandAction(2));
            return;
        }
        addToBot(new BetterDiscardPileToHandAction(1));
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
        if (Hologram.ID.equals(card.cardID)) {
            return rawDescription.replace(CARD_TEXT[2], CARD_TEXT[3]);
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HoloMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}