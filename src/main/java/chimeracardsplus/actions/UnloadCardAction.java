package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class UnloadCardAction extends AbstractGameAction {
    private final CardType type;

    public UnloadCardAction(CardType typeToKeep, AbstractCreature source) {
        type = typeToKeep;
        this.source = source;
        duration = Settings.ACTION_DUR_FAST;
    }

    @Override
    public void update() {
        if (duration >= Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type != type) {
                    addToTop(new DiscardSpecificCardAction(c));
                }
            }
            isDone = true;
        }
    }
}
