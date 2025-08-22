package chimeracardsplus.cardmods.rare;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.stream.Stream;

public class EdibleMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(EdibleMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return isNormalCard(abstractCard) && Stream.of(CardRarity.COMMON, CardRarity.UNCOMMON, CardRarity.RARE).anyMatch(cardRarity -> abstractCard.rarity == cardRarity);
    }

    @Override
    public boolean onObtain(AbstractCard card) {
        if (card.rarity == CardRarity.COMMON) {
            AbstractDungeon.player.increaseMaxHp(1, false);
        } else if (card.rarity == CardRarity.UNCOMMON) {
            AbstractDungeon.player.increaseMaxHp(2, false);
        } else if (card.rarity == CardRarity.RARE) {
            AbstractDungeon.player.increaseMaxHp(3, false);
        }
        return true;
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
        if (card.rarity == CardRarity.COMMON) {
            return insertAfterText(rawDescription, CARD_TEXT[0]);
        }
        if (card.rarity == CardRarity.UNCOMMON) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        if (card.rarity == CardRarity.RARE) {
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

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.HEALING;
    }
}