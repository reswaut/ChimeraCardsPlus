package chimeracardsplus.cardmods.common;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PointyMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(PointyMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean descriptionHack = false;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.type == CardType.ATTACK && abstractCard.baseDamage >= 1 && abstractCard.cost >= -1;
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
        String text = CARD_TEXT[0];
        if (descriptionHack) {
            int count = Math.toIntExact(AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().filter(c -> c != null && c.type == CardType.ATTACK).count());
            text += String.format(count == 1 ? CARD_TEXT[1] : CARD_TEXT[2], count);
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
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
    public float modifyDamageFinal(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        int count = Math.toIntExact(AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().filter(
                c -> c != null && c.type == CardType.ATTACK).count());
        if (AbstractDungeon.actionManager.cardQueue.stream().noneMatch(item -> item.card != null && item.card.uuid.equals(card.uuid))) {
            count += 1;
        }
        if (count == 10) {
            return damage * 2.0F;
        }
        return damage;
    }

    @Override
    public Color getGlow(AbstractCard card) {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().filter(c -> c != null && c.type == CardType.ATTACK).count() == 9L) {
            return Color.GOLD.cpy();
        }
        return null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PointyMod();
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