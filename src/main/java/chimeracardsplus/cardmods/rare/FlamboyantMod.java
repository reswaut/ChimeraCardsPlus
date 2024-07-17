package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class FlamboyantMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(FlamboyantMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean descriptionHack = false;

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return AbstractDungeon.actionManager.cardsPlayedThisTurn.size() > 5 ? (damage * 2.0F) : damage;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> c.baseDamage >= 1);
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
            int count = AbstractDungeon.actionManager.cardsPlayedThisTurn.size();
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

    // The following are the best that I can do without discard hooks.
    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        descriptionHack = group.type == CardGroup.CardGroupType.HAND;
        card.initializeDescription();
    }

    @Override
    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        descriptionHack = group.type == CardGroup.CardGroupType.HAND;
        card.initializeDescription();
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return AbstractDungeon.actionManager.cardsPlayedThisTurn.size() >= 5 ? Color.GOLD : null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FlamboyantMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}