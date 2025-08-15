package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;

public class FlowAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(FlowAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;

    public FlowAction() {
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = DEFAULT_DURATION;
    }

    public void update() {
        if (this.duration == DEFAULT_DURATION) {
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 99, true, true);
            this.addToBot(new WaitAction(Settings.ACTION_DUR_FAST));
        } else {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                if (!AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                    this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DrawCardNextTurnPower(AbstractDungeon.player, AbstractDungeon.handCardSelectScreen.selectedCards.group.size())));

                    for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                        AbstractDungeon.player.hand.moveToDiscardPile(c);
                        GameActionManager.incrementDiscard(false);
                        c.triggerOnManualDiscard();
                    }
                }
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
        }
        this.tickDuration();
    }
}
