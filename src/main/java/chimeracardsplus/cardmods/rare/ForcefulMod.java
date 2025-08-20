package chimeracardsplus.cardmods.rare;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.blue.ForceField;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ForcefulMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(ForcefulMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.cost += 2;
        card.costForTurn = card.cost;

        if (CardCrawlGame.dungeon != null && AbstractDungeon.currMapNode != null) {
            for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
                if (c.type == CardType.POWER) {
                    card.updateCost(-1);
                }
            }
        }
    }

    // Needs to be affordable
    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> (c.cost >= 1 || c.cost == 0 && (c.baseDamage >= 3 || c.baseBlock >= 3)) && doesntUpgradeCost()) && characterCheck(p -> abstractCard.cost + 2 <= p.masterDeck.group.stream().filter(card -> card.type == CardType.POWER).count() + 3);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 4.0F / 3.0F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 4.0F / 3.0F : block;
    }

    @Override
    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        if (otherCard.type == CardType.POWER) {
            card.updateCost(-1);
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
        if (ForceField.ID.equals(card.cardID)) {
            return rawDescription.replace(CARD_TEXT[1], CARD_TEXT[2]);
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ForcefulMod();
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