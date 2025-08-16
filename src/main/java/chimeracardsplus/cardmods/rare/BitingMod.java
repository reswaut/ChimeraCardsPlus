package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.HealingMod;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Bite;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BitingMod extends AbstractAugment implements HealingMod {
    public static final String ID = ChimeraCardsPlus.makeID(BitingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> ((c.cost >= 1 || c.cost == -1) && c.type == AbstractCard.CardType.ATTACK && c.baseDamage >= 2 && c.rarity != AbstractCard.CardRarity.BASIC && doesntUpgradeCost()));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.8F;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (Bite.ID.equals(card.cardID)) {
            return;
        }
        if (card.cost > 0) {
            this.addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, card.cost));
        } else if (card.cost == -1) {
            this.addToBot(new HealAction(AbstractDungeon.player, AbstractDungeon.player, card.energyOnUse));
        }
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (Bite.ID.equals(card.cardID)) {
            return magic + card.cost;
        }
        return magic;
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
        if (Bite.ID.equals(card.cardID)) {
            return rawDescription;
        }
        if (card.cost == -1) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        return insertAfterText(rawDescription, String.format(CARD_TEXT[0], card.cost));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BitingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}