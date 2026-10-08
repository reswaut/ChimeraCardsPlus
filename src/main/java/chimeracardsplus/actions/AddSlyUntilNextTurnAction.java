package chimeracardsplus.actions;

import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.SlyKeywordMod;
import chimeracardsplus.helpers.Constants;
import chimeracardsplus.patches.SlyFieldPatches.SlyFieldPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Collection;

public class AddSlyUntilNextTurnAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(AddSlyUntilNextTurnAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    private final Collection<AbstractCard> cannotChoose = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
    private final CardType cardType;
    private boolean first = true;

    public AddSlyUntilNextTurnAction(CardType cardType) {
        this.cardType = cardType;
    }

    private static void addSlyUntilNextTurn(AbstractCard card) {
        SlyKeywordMod mod = new SlyKeywordMod();
        CardModifierManager.addModifier(card, mod);
        ChimeraCardsPlus.gameActionInfoManager.queuePlayerTurnStartAction(new RemoveModifierAction(card, mod, true));
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type != cardType || SlyFieldPatch.sly.get(c)) {
                    cannotChoose.add(c);
                }
            }

            if (cannotChoose.size() == AbstractDungeon.player.hand.group.size()) {
                isDone = true;
                return;
            }

            if (AbstractDungeon.player.hand.group.size() - cannotChoose.size() == 1) {
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c.type == cardType && !SlyFieldPatch.sly.get(c)) {
                        addSlyUntilNextTurn(c);
                        AbstractDungeon.player.hand.refreshHandLayout();
                        isDone = true;
                        return;
                    }
                }
            }

            AbstractDungeon.player.hand.group.removeAll(cannotChoose);
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
            return;
        }

        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                addSlyUntilNextTurn(c);
                AbstractDungeon.player.hand.addToHand(c);
            }
            for (AbstractCard c : cannotChoose) {
                AbstractDungeon.player.hand.addToTop(c);
            }
            AbstractDungeon.player.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
        }

        isDone = true;
    }
}
