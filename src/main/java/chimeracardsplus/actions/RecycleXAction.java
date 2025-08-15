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
    private final AbstractCreature target;

    public RecycleXAction(AbstractCard card, AbstractCreature target) {
        this.hiddenCard = card;
        this.target = target;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (this.p.hand.isEmpty()) {
                this.isDone = true;
            } else {
                if (this.p.hand.size() == 1) {
                    if (this.p.hand.getBottomCard().costForTurn == -1) {
                        hiddenCard.energyOnUse = EnergyPanel.getCurrentEnergy();
                    } else {
                        hiddenCard.energyOnUse = Math.max(0, this.p.hand.getBottomCard().costForTurn);
                    }
                    hiddenCard.freeToPlayOnce = true;
                    hiddenCard.use(p, target instanceof AbstractMonster ? (AbstractMonster) target : null);

                    this.p.hand.moveToExhaustPile(this.p.hand.getBottomCard());
                } else {
                    AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false);
                }
                this.tickDuration();
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
                    this.p.hand.moveToExhaustPile(c);
                }

                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            }

            this.tickDuration();
        }
    }
}
