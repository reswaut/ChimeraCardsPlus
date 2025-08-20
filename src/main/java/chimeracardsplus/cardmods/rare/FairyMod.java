package chimeracardsplus.cardmods.rare;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

public class FairyMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(FairyMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return isNormalCard(abstractCard) && abstractCard.rarity != CardRarity.BASIC;
    }

    @Override
    public boolean preDeath(AbstractCard card) {
        if (AbstractDungeon.player.currentHealth > 0) {
            return false;
        }

        CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
        group.group.add(card);
        // It is intended to be able to remove bottled card.
        CardGroup retGroup = group.getPurgeableCards();
        if (retGroup.isEmpty()) {
            return false;
        }

        AbstractDungeon.player.currentHealth = 0;
        AbstractDungeon.player.heal(Math.max(1, (int) (AbstractDungeon.player.maxHealth / 10.0F)), true);

        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float) Settings.WIDTH / 2, (float) Settings.HEIGHT / 2));
        AbstractDungeon.player.masterDeck.removeCard(card);

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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FairyMod();
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