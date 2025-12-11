package chimeracardsplus.actions;

import basemod.cardmods.EtherealMod;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class AddEtherealAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(AddEtherealAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private boolean first = true;

    public AddEtherealAction() {
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            if (AbstractDungeon.player.hand.isEmpty()) {
                isDone = true;
            } else if (AbstractDungeon.player.hand.size() == 1) {
                AbstractCard c = AbstractDungeon.player.hand.getTopCard();
                CardModifierManager.addModifier(c, new EtherealMod());
                AbstractDungeon.player.hand.refreshHandLayout();
                isDone = true;
            } else {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
            }
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                CardModifierManager.addModifier(c, new EtherealMod());
                AbstractDungeon.player.hand.addToHand(c);
            }
            AbstractDungeon.player.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        tickDuration();
    }
}
