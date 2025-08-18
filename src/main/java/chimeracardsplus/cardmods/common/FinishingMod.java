package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnDiscardMod;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FinishingMod extends AbstractAugment implements TriggerOnDiscardMod {
    public static final String ID = ChimeraCardsPlus.makeID(FinishingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean descriptionHack = false;

    private static int computeHits(CardType type) {
        return (int) AbstractDungeon.actionManager.cardsPlayedThisTurn.stream().filter(c -> c.type == type).count();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        InterceptUseField.interceptUse.set(card, Boolean.TRUE);
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 2.0F / 3.0F : block;
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 2.0F / 3.0F : damage;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> noShenanigans(c)
                && c.cost >= 0
                && (c.baseDamage >= 2 || c.baseBlock >= 2)
                && (c.type == CardType.ATTACK || c.type == CardType.SKILL)
                && customCheck(c, check ->
                    noCardModDescriptionChanges(check)
                            && check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1L));
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
        if (card.type == CardType.ATTACK) {
            text = CARD_TEXT[0];
            if (descriptionHack) {
                int count = computeHits(card.type);
                text += String.format(count == 1 ? CARD_TEXT[1] : CARD_TEXT[2], count);
            }
        } else if (card.type == CardType.SKILL) {
            text = CARD_TEXT[3];
            if (descriptionHack) {
                int count = computeHits(card.type);
                text += String.format(count == 1 ? CARD_TEXT[4] : CARD_TEXT[5], count);
            }
        }
        return rawDescription.replaceFirst("[.。]", text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        int hits = computeHits(card.type) - 1;
        for (int i = 0; i < hits; ++i) {
            card.use(AbstractDungeon.player, (AbstractMonster) target);
        }
        descriptionHack = false;
        card.initializeDescription();
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        descriptionHack = true;
        card.initializeDescription();
    }

    @Override
    public void onMoveToDiscard(AbstractCard card) {
        descriptionHack = false;
        card.initializeDescription();
    }

    @Override
    public void onManualDiscard(AbstractCard card) {
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FinishingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}