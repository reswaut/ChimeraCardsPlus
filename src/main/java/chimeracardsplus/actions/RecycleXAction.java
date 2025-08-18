package chimeracardsplus.actions;

import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class RecycleXAction extends AbstractGameAction {
    private static final String ID = ChimeraCardsPlus.makeID(RecycleXAction.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private final AbstractPlayer p;
    private final AbstractCard hiddenCard;

    public RecycleXAction(AbstractCard card, AbstractCreature target) {
        hiddenCard = card;
        this.target = target;
        actionType = ActionType.CARD_MANIPULATION;
        p = AbstractDungeon.player;
        duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (duration >= Settings.ACTION_DUR_FAST) {
            if (p.hand.isEmpty()) {
                isDone = true;
            } else {
                if (p.hand.size() == 1) {
                    if (p.hand.getBottomCard().costForTurn == -1) {
                        hiddenCard.energyOnUse = EnergyPanel.getCurrentEnergy();
                    } else {
                        hiddenCard.energyOnUse = Math.max(0, p.hand.getBottomCard().costForTurn);
                    }
                    hiddenCard.freeToPlayOnce = true;
                    hiddenCard.use(p, target instanceof AbstractMonster ? (AbstractMonster) target : null);

                    p.hand.moveToExhaustPile(p.hand.getBottomCard());
                } else {
                    AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
                }
                tickDuration();
            }
        } else {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                    if (c.costForTurn == -1) {
                        hiddenCard.energyOnUse = EnergyPanel.getCurrentEnergy();
                    } else {
                        hiddenCard.energyOnUse = Math.max(0, c.costForTurn);
                    }
                    hiddenCard.freeToPlayOnce = true;
                    hiddenCard.use(p, target instanceof AbstractMonster ? (AbstractMonster) target : null);
                    p.hand.moveToExhaustPile(c);
                }

                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            }

            tickDuration();
        }
    }
}
