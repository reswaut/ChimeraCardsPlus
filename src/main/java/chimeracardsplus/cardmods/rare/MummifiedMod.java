package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MummifiedMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(MummifiedMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && abstractCard.type == CardType.POWER;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        List<AbstractCard> groupCopy = AbstractDungeon.player.hand.group.stream().filter(c -> c.cost > 0 && c.costForTurn > 0 && !c.freeToPlayOnce).collect(Collectors.toCollection(() -> new ArrayList<>(16)));
        for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
            if (i.card != null) {
                groupCopy.remove(i.card);
            }
        }
        if (!groupCopy.isEmpty()) {
            groupCopy.get(AbstractDungeon.cardRandomRng.random(0, groupCopy.size() - 1)).setCostForTurn(0);
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
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new MummifiedMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}