package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class EdibleMod extends AbstractAugment implements TriggerOnObtainMod {
    public static final String ID = ChimeraCardsPlus.makeID(EdibleMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> isNormalCard(c)
                && (c.rarity == AbstractCard.CardRarity.COMMON || c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE));
    }

    @Override
    public void onObtain(AbstractCard card) {
        if (card.rarity == AbstractCard.CardRarity.COMMON) {
            AbstractDungeon.player.increaseMaxHp(1, false);
        } else if (card.rarity == AbstractCard.CardRarity.UNCOMMON) {
            AbstractDungeon.player.increaseMaxHp(2, false);
        } else if (card.rarity == AbstractCard.CardRarity.RARE) {
            AbstractDungeon.player.increaseMaxHp(3, false);
        }
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
        if (card.rarity == AbstractCard.CardRarity.COMMON) {
            return insertAfterText(rawDescription, CARD_TEXT[0]);
        }
        if (card.rarity == AbstractCard.CardRarity.UNCOMMON) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        if (card.rarity == AbstractCard.CardRarity.RARE) {
            return insertAfterText(rawDescription, CARD_TEXT[2]);
        }
        return rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new EdibleMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}