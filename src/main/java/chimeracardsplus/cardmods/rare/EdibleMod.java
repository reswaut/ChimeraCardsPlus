package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class EdibleMod extends AbstractAugment implements TriggerOnObtainMod {
    public static final String ID = ChimeraCardsPlus.makeID(EdibleMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return isNormalCard(card) && (card.rarity == AbstractCard.CardRarity.COMMON || card.rarity == AbstractCard.CardRarity.UNCOMMON || card.rarity == AbstractCard.CardRarity.RARE);
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