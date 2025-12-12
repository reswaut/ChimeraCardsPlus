package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class RecycleXAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(RecycleXAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private final AbstractCard hiddenCard;
    private boolean first = true;

    public RecycleXAction(AbstractCard card, AbstractCreature target) {
        hiddenCard = card;
        this.target = target;
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (first) {
            first = false;
            if (AbstractDungeon.player.hand.isEmpty()) {
                isDone = true;
                return;
            }
            if (AbstractDungeon.player.hand.size() == 1) {
                if (AbstractDungeon.player.hand.getBottomCard().costForTurn == -1) {
                    hiddenCard.energyOnUse = EnergyPanel.getCurrentEnergy();
                } else {
                    hiddenCard.energyOnUse = Math.max(0, AbstractDungeon.player.hand.getBottomCard().costForTurn);
                }
                hiddenCard.freeToPlayOnce = true;
                hiddenCard.use(AbstractDungeon.player, target instanceof AbstractMonster ? (AbstractMonster) target : null);

                AbstractDungeon.player.hand.moveToExhaustPile(AbstractDungeon.player.hand.getBottomCard());

                isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                if (c.costForTurn == -1) {
                    hiddenCard.energyOnUse = EnergyPanel.getCurrentEnergy();
                } else {
                    hiddenCard.energyOnUse = Math.max(0, c.costForTurn);
                }
                hiddenCard.freeToPlayOnce = true;
                hiddenCard.use(AbstractDungeon.player, target instanceof AbstractMonster ? (AbstractMonster) target : null);
                AbstractDungeon.player.hand.moveToExhaustPile(c);
            }

            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
        }
        isDone = true;
    }
}
