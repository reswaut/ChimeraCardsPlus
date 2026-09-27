package chimeracardsplus.cardmods.uncommon;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WindfallMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(WindfallMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    private static void collectFreeCards(Collection<AbstractCard> freeCards, CardGroup pool) {
        for (AbstractCard c : pool.group) {
            if (c.cost == 0 && !c.hasTag(CardTags.HEALING)) {
                freeCards.add(c);
            }
        }
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && (c.type == CardType.ATTACK || c.type == CardType.SKILL) && notExhaust(c) && doesntUpgradeExhaust());
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        List<AbstractCard> freeCards = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
        collectFreeCards(freeCards, AbstractDungeon.srcCommonCardPool);
        collectFreeCards(freeCards, AbstractDungeon.srcUncommonCardPool);
        collectFreeCards(freeCards, AbstractDungeon.srcRareCardPool);
        if (freeCards.isEmpty()) {
            return;
        }
        AbstractCard reward = freeCards.get(AbstractDungeon.cardRandomRng.random(freeCards.size() - 1)).makeCopy();
        if ((card.timesUpgraded != 0 || card.upgraded) && reward.canUpgrade()) {
            reward.upgrade();
        }
        addToBot(new MakeTempCardInHandAction(reward, 1));
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
        return insertAfterText(rawDescription, card.timesUpgraded != 0 || card.upgraded ? CARD_TEXT[1] : CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new WindfallMod();
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
