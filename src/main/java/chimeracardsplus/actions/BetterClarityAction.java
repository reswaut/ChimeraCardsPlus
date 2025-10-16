package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class BetterClarityAction extends AbstractGameAction {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ClarityAction");
    private static final String[] TEXT = uiStrings.TEXT;
    private boolean first;

    public BetterClarityAction(int numCards) {
        amount = numCards;
        actionType = ActionType.CARD_MANIPULATION;
        duration = Settings.ACTION_DUR_FAST;
        first = true;
    }

    @Override
    public void update() {
        if (first) {
            int realAmount = Math.min(amount, AbstractDungeon.player.drawPile.size());
            if (realAmount == 0) {
                isDone = true;
                return;
            }
            if (realAmount == 1) {
                AbstractDungeon.player.drawPile.moveToHand(AbstractDungeon.player.drawPile.getBottomCard(), AbstractDungeon.player.drawPile);
                isDone = true;
                return;
            }

            CardGroup tmpGroup = new CardGroup(CardGroupType.UNSPECIFIED);
            for (int i = 0; i < realAmount; ++i) {
                tmpGroup.addToTop(AbstractDungeon.player.drawPile.group.get(AbstractDungeon.player.drawPile.size() - i - 1));
            }
            AbstractDungeon.gridSelectScreen.open(tmpGroup, amount, false, TEXT[0]);
            first = false;
        } else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.targetGroup.group) {
                if (AbstractDungeon.gridSelectScreen.selectedCards.contains(c)) {
                    AbstractDungeon.player.drawPile.moveToHand(c, AbstractDungeon.player.drawPile);
                } else {
                    AbstractDungeon.player.drawPile.moveToExhaustPile(c);
                }
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        tickDuration();
    }
}