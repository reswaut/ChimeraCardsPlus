package chimeracardsplus.actions;

import basemod.cardmods.RetainMod;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.helpers.Constants;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.util.ArrayList;
import java.util.Collection;

public class AddRetainAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(AddRetainAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    private final Collection<AbstractCard> cannotChoose = new ArrayList<>(Constants.DEFAULT_LIST_SIZE);
    private boolean first = true;

    @Override
    public void update() {
        if (first) {
            first = false;
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.selfRetain) {
                    cannotChoose.add(c);
                }
            }

            if (cannotChoose.size() == AbstractDungeon.player.hand.group.size()) {
                isDone = true;
                return;
            }

            if (AbstractDungeon.player.hand.group.size() - cannotChoose.size() == 1) {
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (!c.selfRetain) {
                        CardModifierManager.addModifier(c, new RetainMod());
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
                CardModifierManager.addModifier(c, new RetainMod());
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
