package chimeracardsplus.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.function.Supplier;

public class UseCardMultipleTimesAction extends AbstractGameAction {
    private final AbstractCard card;
    private final Supplier<Integer> computeUses;

    public UseCardMultipleTimesAction(AbstractCard card, AbstractCreature target, Supplier<Integer> computeUses) {
        this.card = card;
        this.target = target;
        this.computeUses = computeUses;
    }

    @Override
    public void update() {
        int hits = computeUses.get();
        for (int i = 0; i < hits; ++i) {
            card.use(AbstractDungeon.player, (AbstractMonster) target);
        }
        isDone = true;
    }
}