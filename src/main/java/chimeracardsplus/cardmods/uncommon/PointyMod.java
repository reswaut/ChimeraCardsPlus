package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnDiscardMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class PointyMod extends AbstractAugment implements TriggerOnDiscardMod {
    public static final String ID = ChimeraCardsPlus.makeID(PointyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean descriptionHack = false;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.type == AbstractCard.CardType.ATTACK && c.baseDamage >= 1 && c.cost >= -1));
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
            int count = (int) AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().filter((c) -> c.type == AbstractCard.CardType.ATTACK).count();
            text += String.format((count == 1) ? CARD_TEXT[1] : CARD_TEXT[2], count);
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
    public void onManualDiscard(AbstractCard card) {
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        int count = (int) AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().filter(
                (c) -> c.type == AbstractCard.CardType.ATTACK).count();
        if (AbstractDungeon.actionManager.cardQueue.stream().noneMatch(item -> item.card.uuid.equals(card.uuid))) {
            count += 1;
        }
        if (count == 10) {
            return damage * 2.0F;
        }
        return damage;
    }

    @Override
    public Color getGlow(AbstractCard card) {
        if (AbstractDungeon.actionManager.cardsPlayedThisCombat.stream().filter((c) -> c.type == AbstractCard.CardType.ATTACK).count() == 9) {
            return Color.GOLD.cpy();
        }
        return null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PointyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}